package echowand.common;


import java.util.Arrays;

/*
 * @author HiepNguyen
 */

public class Data {

    private byte[] data;
    
    public Data(byte... data) {
        this.data = Arrays.copyOf(data, data.length);
    }
    
    public Data(byte[] data, int offset, int length) {
        this.data = new byte[length];
        System.arraycopy(data, offset, this.data, 0, length);
    }
    
    public byte get(int index) {
        return data[index];
    }
    
    public int size() {
        return data.length;
    }
    
    public boolean isEmpty() {
        return data.length == 0;
    }

    public byte[] toBytes() {
        return Arrays.copyOf(data, data.length);
    }
    
    public byte[] toBytes(int offset, int length) {
        byte[] newData = new byte[length];
        System.arraycopy(this.data, offset, newData, 0, length);
        return newData;
    }
    
    public void copyBytes(int srcOffset, byte[] destData, int destOffset, int length) {
            System.arraycopy(data, srcOffset, destData, destOffset, length);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<data.length; i++) {
            builder.append(String.format("%02x", data[i]));
        }
        return builder.toString();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Arrays.hashCode(this.data);
        return hash;
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof Data)) {
            return false;
        }
        
        Data other = (Data)otherObj;
        return Arrays.equals(data, other.data);
    }
}
