import java.util.*;

public class Sender extends TransportLayer {


    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }
    private TransportLayerPacket currentPacket = null;
    private byte[] NAK;
    private byte[] ACK;
    private Queue<TransportLayerPacket> packetQueue;
    public int state;

    @Override
    public void init() {
        NAK = "NAK".getBytes();
        ACK = "ACK".getBytes();
        state = 0;
        packetQueue = new LinkedList<TransportLayerPacket>();
    }

    @Override
    public void rdt_send(byte[] data) {
        if(state == 0) {
            //System.out.println("Not waiting, sending packet");
            currentPacket = makePacket(data);
            udt_send();
        }
        else{
            //System.out.println("Currently waiting! Saving packet");
            packetQueue.add(makePacket(data));
        }
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        simulator.stopTimer(this);
        if(extract(pkt).equals("ACK")){
            if(packetQueue.isEmpty()){
                state = 0;
                //System.out.println("Recieved ACK, waiting for next packet");
            }
            else {
                //System.out.println("Recieved ACK, sending next packet");
                currentPacket = packetQueue.remove();
                udt_send();
            }
        }
        else if(extract(pkt).equals("NAK")){
            //System.out.println("Corrupted Packet, resending");
            resendPacket();
        }
    }

    @Override
    public void timerInterrupt() {

    }

    private void udt_send(){
        state = 1;
        simulator.startTimer(this,100);
        simulator.sendToNetworkLayer(this,currentPacket);

    }

    private TransportLayerPacket makePacket(byte[] data){
        return new TransportLayerPacket(data);

    }

    private String extract(TransportLayerPacket pkt){
        String msg = new String(pkt.getData());
        return msg;
    }

    private void resendPacket(){
        udt_send();
    }

}
