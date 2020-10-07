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
        data = receivedPacket.getData();
        return data;
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        extract(pkt);
        simulator.sendToApplicationLayer(this, data);
    }

    @Override
    public void timerInterrupt() {

    }
}