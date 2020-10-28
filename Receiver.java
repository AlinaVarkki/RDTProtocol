import java.util.ArrayList;
import java.util.LinkedList;

public class Receiver extends TransportLayer {

    private TransportLayerPacket currentPacket;
    private int seqnum;
    private ArrayList<TransportLayerPacket> buffer = new ArrayList<>();

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
        if(isCorrupt(pkt)){
            currentPacket = new TransportLayerPacket("NAK".getBytes() , seqnum - 1, 1);
            udt_send();
        }
        else if (pkt.getSeqnum() < seqnum){
            currentPacket = new TransportLayerPacket("NXT".getBytes(), seqnum - 1, 1);
            udt_send();
        }else if(pkt.getSeqnum() > seqnum){
            buffer.add(pkt);
            currentPacket = new TransportLayerPacket("ACK".getBytes(), pkt.getSeqnum(), 1);
            udt_send();
        }
        else{
            simulator.sendToApplicationLayer(this,pkt.getData());
            currentPacket = new TransportLayerPacket("ACK".getBytes(), seqnum, 1);
            udt_send();
            seqnum++;

            while (checkIfContainsNextPkt()) {
                for (TransportLayerPacket p : buffer) {
                    if (p.getSeqnum() == seqnum) {
                        simulator.sendToApplicationLayer(this, p.getData());
                        seqnum++;

                    }
                }
            }
        }
    }

    public Boolean checkIfContainsNextPkt(){
        for(TransportLayerPacket p: buffer){
            if(p.getSeqnum() == seqnum){
                return true;
            }
        }
        return false;
    }

    @Override
    public void timerInterrupt() {

    }

    private void udt_send(){
        simulator.sendToNetworkLayer(this,currentPacket);
    }
}