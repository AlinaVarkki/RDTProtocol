public class Receiver extends TransportLayer {


    private byte[] data;
    private byte[] NAK;
    private byte[] ACK;

    public Receiver(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }
    private TransportLayerPacket currentPacket = null;

    @Override
    public void init() {
        NAK = "NAK".getBytes();
        ACK = "ACK".getBytes();
    }

    @Override
    public void rdt_send(byte[] data) {
        makePacket(data);
        udt_send();
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        if(isCorrupt(pkt)) {
            System.out.println("Corrupt packet detected!");
            rdt_send(NAK);
        }
        else {
            rdt_send(ACK);
            simulator.sendToApplicationLayer(this, extract(pkt));
        }
    }

    @Override
    public void timerInterrupt() {

    }

    public byte[] extract(TransportLayerPacket receivedPacket){
        return receivedPacket.getData();
    }

    private boolean isCorrupt (TransportLayerPacket receivedPacket){
        byte compareChecksum = 0;
        for(byte bit : receivedPacket.getData()){
            compareChecksum += bit;
        }
        compareChecksum += receivedPacket.getChecksum();

        if (compareChecksum == -1) return false;
        else return true;
    }

    private TransportLayerPacket makePacket(byte[] data){
        currentPacket = new TransportLayerPacket(data);
        return currentPacket;
    }

    private void udt_send(){
        simulator.sendToNetworkLayer(this,currentPacket);
    }
}