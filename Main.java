public class Main {

    public static void main(String[] args) {
        NetworkSimulator sim = new NetworkSimulator(5, 0.0, 0.1, 10.0, false, 2);

        Sender sender = new Sender("Sender", sim);
        sim.setSender(sender);

        TransportLayer receiver = new Receiver("Receiver", sim);
        sim.setReceiver(receiver);

        sim.runSimulation();

    }

}
