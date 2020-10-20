import java.util.*;

public class Sender extends TransportLayer {

    private TransportLayerPacket currentPacket;
    private int seqnum;
    private Queue<TransportLayerPacket> pktQ;

    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    @Override
    public void init() {
        seqnum = 0;
        currentPacket = null;
        pktQ = new LinkedList<>();
    }

    @Override
    public void rdt_send(byte[] data) {
        if(currentPacket == null) {
            currentPacket = new TransportLayerPacket(data, seqnum, 0);
            udt_send();
        }
        else{
            pktQ.add(new TransportLayerPacket (data, seqnum, 0));
        }
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        
        simulator.stopTimer(this);
        if(isCorrupt(pkt) || pkt.getSeqnum() != seqnum){
            udt_send();

        }
        else{
            currentPacket = pktQ.poll();
            seqnum = 1 - seqnum;
            /* will stop the current timer if there is one running */


            if(currentPacket != null){
                currentPacket.setSeqnum(seqnum);
                udt_send();

            }
        }
    }

    @Override
    public void timerInterrupt() {
        /* called when timer is finished and not stopped and then resends packet*/

        udt_send();
    }

    private void udt_send(){
        if(currentPacket != null) {
            /* timer started */
            simulator.startTimer(this, 50.0);
            simulator.sendToNetworkLayer(this, currentPacket);
        }
    }



}
