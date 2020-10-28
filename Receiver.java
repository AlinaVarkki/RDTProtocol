import java.util.LinkedList;

public class Receiver extends TransportLayer {

    private TransportLayerPacket currentPacket;
    private int seqnum;
    private LinkedList<TransportLayerPacket> buffer;

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
            currentPacket = new TransportLayerPacket("NAK".getBytes() , -1, 1);
            //currentPacket = new TransportLayerPacket("NAK".getBytes() , 1 - seqnum, 1);
            udt_send();
        }
        else{
            simulator.sendToApplicationLayer(this,pkt.getData());
            currentPacket = new TransportLayerPacket("ACK".getBytes(), seqnum, 1);
            udt_send();
            //seqnum = 1 - seqnum;
            seqnum++;
        }
    }
    @Override
    public void timerInterrupt() {

    }

    private void udt_send(){
        simulator.sendToNetworkLayer(this,currentPacket);
    }
}