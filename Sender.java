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
            /* timer started */
            simulator.startTimer(this, 10.0);

        }
        else{
            pktQ.add(new TransportLayerPacket (data, seqnum, 0));
        }
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        if(isCorrupt(pkt) || pkt.getSeqnum() != seqnum){
            udt_send();

        }
        else{
            currentPacket = pktQ.poll();
            seqnum = 1 - seqnum;
            /* will stop the current timer if there is one running */
            simulator.stopTimer(this);
            System.out.println("stopped");
            if(currentPacket != null){
                currentPacket.setSeqnum(seqnum);
                udt_send();

            }
        }
    }

    @Override
    public void timerInterrupt() {
        /* called when timer is finished and not stopped and then resends packet*/
     System.out.println("timer done resending packet");

            udt_send();
            simulator.startTimer(this, 10.0);

    }

    private void udt_send(){
        if(currentPacket != null) {
            simulator.sendToNetworkLayer(this, currentPacket);
        }
    }

    private boolean isCorrupt (TransportLayerPacket receivedPacket){
        if(receivedPacket.getSeqnum() <= 1 && receivedPacket.getSeqnum() >= 0 && receivedPacket.getAcknum() <= 1 && receivedPacket.getAcknum() >= 0) {
            byte compareChecksum = 0;
            for (byte bit : receivedPacket.getData()) {
                compareChecksum += bit;
            }
            compareChecksum += receivedPacket.getChecksum();

            if (compareChecksum == -1) return false;
            else return true;
        }
        else return false;
    }

}
