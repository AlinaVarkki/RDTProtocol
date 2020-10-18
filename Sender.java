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
        if(isCorrupt(pkt) || pkt.getSeqnum() != seqnum){
            udt_send();
        }
        else{
            currentPacket = pktQ.poll();
            seqnum = 1 - seqnum;
            if(currentPacket != null){
                currentPacket.setSeqnum(seqnum);
                udt_send();
            }
        }
    }

    @Override
    public void timerInterrupt() {

    }

    private void udt_send(){

        simulator.sendToNetworkLayer(this, currentPacket);
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
