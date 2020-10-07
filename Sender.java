public class Sender extends TransportLayer {

    private TransportLayerPacket packetToSend;

    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    @Override
    public void init() {

    }

    @Override
    public void rdt_send(byte[] data) {
        make_pkt(data);
    }

    public void make_pkt(byte[] data){
        packetToSend = new TransportLayerPacket(data);
    }

    public void udt_send(TransportLayerPacket packetToSend){
        simulator.sendToNetworkLayer(this, packetToSend);
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {

    }

    @Override
    public void timerInterrupt() {

    }

    public TransportLayerPacket getPacketToSend(){
        return packetToSend;
    }
}
