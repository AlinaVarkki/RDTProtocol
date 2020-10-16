public class TransportLayerPacket {

    // Maybe remove these
    // You may need extra fields
    private int seqnum;
    private int acknum;
    private byte checksum;

    byte[] data;

    // You may need extra methods

    public TransportLayerPacket(byte[] data) {
        this.data = data;
        makeChecksum();
    }

    public TransportLayerPacket(TransportLayerPacket pkt) {
        this.data = pkt.getData();
        makeChecksum();

    }

    public void setSeqnum(int seqnum) {
        this.seqnum = seqnum;
    }

    public void setAcknum(int acknum) {
        this.acknum = acknum;
    }

    public void setChecksum(byte checksum){ this.checksum = checksum; }

    public int getSeqnum() {return seqnum; }

    public int getAcknum() {return acknum; }

    public byte getChecksum() {return checksum; }

    private void makeChecksum(){
        //add all bytes from data together, allowing for overflow.
        for(byte bit : data){
            checksum += bit;
        }
        //flip all the bits of the checksum
        checksum *= (-1);
        checksum -= 1;
    }

    public byte[] getData() {
        return data;
    }

}