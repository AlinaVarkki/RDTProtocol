import java.util.*;

public class Sender extends TransportLayer {

    private TransportLayerPacket currentPacket;
    private int seqnum;
    private Queue<TransportLayerPacket> pktQ;

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
    }

    @Override
    public void rdt_send(byte[] data) {
            pktQ.add(new TransportLayerPacket (data, 0, 0));
            udt_send();
    }

    @Override
    public void rdt_receive(TransportLayerPacket pkt) {
        //System.out.println("stopped");
        simulator.stopTimer(this);
        if(isCorrupt(pkt) || pkt.getSeqnum() < seqnum){

            udt_send();

        }
        else{
            TransportLayerPacket nowACKedPacked = null;
            
            for(TransportLayerPacket p: sentNotACKed){
                if(p.getSeqnum() == pkt.getSeqnum()){
                    nowACKedPacked = p;
                    break;
                }
            }
            sentNotACKed.remove(nowACKedPacked);
            
                if(pkt.getSeqnum() == nextACKedSeqNum) {
                    nextACKedSeqNum++;
                    while (ACKedSeqNumbers.contains(nextACKedSeqNum)){
                        ACKedSeqNumbers.remove(nextACKedSeqNum);
                        nextACKedSeqNum++;
                    }
                    udt_send();
                }else{
                    //if we cannot move the window yet because seqNum is not next, store it
                    ACKedSeqNumbers.add(pkt.getSeqnum());
                }
        }
    }

    @Override
    public void timerInterrupt() {
        /* called when timer is finished and not stopped and then resends packet*/
        //System.out.println("timer done resending packet");
        udt_send();
    }

    private void udt_send(){

        while (nextACKedSeqNum + windowSize > lastSentSeqNum) {

            currentPacket = pktQ.poll();

            seqnum ++;

            if(currentPacket != null){
                currentPacket.setSeqnum(seqnum);
            }else{
                //in case there are no packets in the queue
                break;
            }

            if (currentPacket != null) {
                /* timer started */
                simulator.startTimer(this, 50.0);
                simulator.sendToNetworkLayer(this, new TransportLayerPacket(currentPacket));
                
                sentNotACKed.add(currentPacket);
                
                lastSentSeqNum++;
            }
        }
    }



}
