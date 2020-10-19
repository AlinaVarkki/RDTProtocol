public class Receiver extends TransportLayer {

    private TransportLayerPacket currentPacket;
    private int seqnum;

    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    @Override
    public void init() {
        currentPacket = null;
        seqnum = 0;
    }

    @Override
    public void rdt_send(byte[] data) {

    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        if(isCorrupt(pkt) || pkt.getSeqnum() != seqnum){
            currentPacket = new TransportLayerPacket("NAK".getBytes() , 1 - seqnum, 1);
            udt_send();
        }
        else{
            simulator.sendToApplicationLayer(this,pkt.getData());
            currentPacket = new TransportLayerPacket("ACK".getBytes(), seqnum, 1);
            udt_send();
            seqnum = 1 - seqnum;
        }
    }
    @Override
    public void timerInterrupt() {

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

    private void udt_send(){
        simulator.sendToNetworkLayer(this,currentPacket);
    }
}