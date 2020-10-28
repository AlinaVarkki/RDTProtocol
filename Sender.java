import java.util.*;

public class Sender extends TransportLayer {

    private TransportLayerPacket currentPacket;
    private int seqnum;
    private Queue<TransportLayerPacket> pktQ;
    private double timeoutDelay = 50;
    private boolean timerGoing = false;
    private LinkedList<double[]> timerQ;

    private int windowSize = 4;
    private int nextACKedSeqNum = 0;
    private int lastSentSeqNum = 0;
    //only the ones not in sequence
    private ArrayList<Integer> ACKedSeqNumbers = new ArrayList<>();
    private ArrayList<TransportLayerPacket> sentNotACKed = new ArrayList<>();

    public Sender(String name, NetworkSimulator simulator) {
        super(name, simulator);
    }

    @Override
    public void init() {
        seqnum = -1;
        currentPacket = null;
        pktQ = new LinkedList<>();
        timerQ = new LinkedList<>();
    }

    @Override
    public void rdt_send(byte[] data) {
        pktQ.add(new TransportLayerPacket(data, 0, 0));
        udt_send();
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        //System.out.println("stopped");
        if (!isCorrupt(pkt)) {

            stopTimer(pkt.getSeqnum());

            for (TransportLayerPacket p : sentNotACKed) {
                if (p.getSeqnum() == pkt.getSeqnum()) {
                    sentNotACKed.remove(p);
                    break;
                }
            }

            if (pkt.getSeqnum() == nextACKedSeqNum) {
                nextACKedSeqNum++;
                while (ACKedSeqNumbers.contains(nextACKedSeqNum)) {
                    ACKedSeqNumbers.removeAll(Arrays.asList(nextACKedSeqNum));
                    nextACKedSeqNum++;
                }
                udt_send();
            } else {
                //if we cannot move the window yet because seqNum is not next, store it
                ACKedSeqNumbers.add(pkt.getSeqnum());
            }
        }
    }

    @Override
    public void timerInterrupt() {
        // figure out what packet has been dropped
        // resend that packet

        boolean keepInterrupting = true;
        while(keepInterrupting || timerQ.peek() == null) {
            if (timerQ.peek()[1] <= simulator.simulationTime) {
                for (TransportLayerPacket p : sentNotACKed) {
                    if (p.getSeqnum() == timerQ.peek()[0]) {
                        timerQ.poll();
                        simulator.sendToNetworkLayer(this,p);
                        timerQ.add(new double[] {(double) p.getSeqnum(), simulator.simulationTime + timeoutDelay});
                        break;
                    }
                }


            } else {
                keepInterrupting = false;
            }
        }
        if (timerQ.peek() != null) {
            simulator.startTimer(this, timerQ.peek()[1] - simulator.simulationTime);
            timerGoing = true;
        }
    }

    private void udt_send() {

        while (nextACKedSeqNum + windowSize > lastSentSeqNum) {
            currentPacket = pktQ.poll();
            if (currentPacket != null) {
                seqnum++;
                currentPacket.setSeqnum(seqnum);
                sentNotACKed.add(currentPacket);
                lastSentSeqNum++;
                startTimer();
                simulator.sendToNetworkLayer(this, new TransportLayerPacket(currentPacket));
            }
            else break;
        }
    }

    private void startTimer(){
        if(!timerGoing) {
            simulator.startTimer(this, timeoutDelay);
        }
        timerQ.add(new double[] {Double.valueOf(seqnum), simulator.simulationTime + timeoutDelay});
        timerGoing = true;
    }

    private void stopTimer(double pktSeqnum){

        simulator.stopTimer(this);
        timerGoing = false;
        for(double[] query : timerQ){
            if(query[0] == pktSeqnum){
                timerQ.remove(query);
                break;
            }
        }
        if (timerQ.peek()!=null){
           timerInterrupt();

        }
    }
}
