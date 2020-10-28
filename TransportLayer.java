public abstract class TransportLayer {

    String name;
    NetworkSimulator simulator;

    public TransportLayer(String name, NetworkSimulator simulator) {
        this.name = name;
        this.simulator = simulator;
    }

    public abstract void init();

    public abstract void rdt_send(byte[] data);

    public abstract void rdt_receive(TransportLayerPacket pkt);

    public abstract void timerInterrupt();

    public String getName() {
        return this.name;
    }

    public boolean isCorrupt (TransportLayerPacket receivedPacket){
        if(receivedPacket.getSeqnum() >= -1 && receivedPacket.getAcknum() <= 1 && receivedPacket.getAcknum() >= 0) {
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