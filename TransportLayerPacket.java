public class TransportLayerPacket {

    // Maybe remove these
    // You may need extra fields
    private int seqnum;
    private int acknum;
    private byte checksum;

    byte[] data;

    // You may need extra methods

    public TransportLayerPacket(byte[] data, int seqnum, int acknum) {
        this.data = data;
        this.seqnum = seqnum;
        this.acknum = acknum;
        makeChecksum();
    }

    public TransportLayerPacket(TransportLayerPacket pkt) {
        this.data = pkt.getData();
        this.seqnum = pkt.getSeqnum();
        this.acknum = pkt.getAcknum();
        makeChecksum();
    }

    public void setSeqnum(int new_seqnum) {
        seqnum = new_seqnum;
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