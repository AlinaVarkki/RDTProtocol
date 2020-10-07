public class Receiver extends TransportLayer {


private byte[] data;

    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }


    @Override
    public void init() {

    }

    @Override
    public void rdt_send(byte[] data) {

    }

    public byte[] extract(TransportLayerPacket receivedPacket){
        return receivedPacket.getData();
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        simulator.sendToApplicationLayer(this, extract(pkt));
    }

    @Override
    public void timerInterrupt() {

    }
}