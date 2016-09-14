package echowand.common;

import java.util.Arrays;

/*
 * @author HiepNguyen
 */

public class PropertyMap {
	
	private byte[] map;
	
	public PropertyMap() {
        this.map = new byte[17];
    }
	
	public PropertyMap(byte[] newMap) {
        this();
        
        if (newMap.length == 0) {
            return;
        }
        
        if (newMap.length >= 17) {
            System.arraycopy(newMap, 0, this.map, 0, 17);
            this.map[0] = countEPC();
        } else {
            int len = byteToInt(newMap[0]);
            
            if (len > newMap.length-1) {
                len = newMap.length - 1;
            }

            for (int i = 1; i <= len; i++) {
                setBit(EPC.fromByte(newMap[i]), true);
            }
        }
    }
	
	private int byteToInt(byte b) {
        return 0xff & (int)b;
    }
	
	private int epcToBitPos(EPC epc) {
        int upper = byteToInt(epc.toByte()) >> 4;
        if (upper < 8) {
            return -1;
        }
        return upper & 0x07;
    }
    
    private int epcToBytePos(EPC epc) {
        int lower = 0x0f & byteToInt(epc.toByte());
        return lower+1;
    }
    
    private boolean isSet(int bitPos, int bytePos) {
        if (bitPos < 0 || 7 < bitPos) {
            return false;
        }
        
        if (bytePos < 1 || 16 < bytePos) {
            return false;
        }
        
        byte bitMap = this.map[bytePos];
        byte bitFilter = (byte)(0x01 << bitPos);
        return (bitMap & bitFilter) != 0;
    }
    
    public boolean isSet(EPC epc) {
        int bitPos = epcToBitPos(epc);
        int bytePos = epcToBytePos(epc);
        return isSet(bitPos, bytePos);
    }
    
    private boolean setBit(int bitPos, int bytePos, boolean b) {
        if (bitPos < 0 || 7 < bitPos) {
            return false;
        }
        
        if (bytePos < 1 || 16 < bytePos) {
            return false;
        }
        
        boolean cur = isSet(bitPos, bytePos);
        if (!cur && b) {
            this.map[0]++;
            this.map[bytePos] |= (byte)(0x01 << bitPos);
        } else if (cur && !b) {
            this.map[0]--;
            this.map[bytePos] &= (byte)(0xff ^ (0x01 << bitPos));
        }
        return true;
    }
    
    private boolean setBit(EPC epc, boolean b) {
        int bitPos = epcToBitPos(epc);
        int bytePos = epcToBytePos(epc);
        return setBit(bitPos, bytePos, b);
    }
    
    public boolean set(EPC epc) {
        return setBit(epc, true);
    }
    
    public boolean unset(EPC epc) {
        return setBit(epc, false);
    }
    
    public int count() {
        return byteToInt(map[0]);
    }
    
    private byte countEPC() {
        int count = 0;
        for (int i = 0x80; i <= 0xff; i++) {
            if (isSet(EPC.fromByte((byte) i))) {
                count++;
            }
        }
        return (byte)count;
    }
    
    public byte[] toBytes() {
        int len = count();
        if (len < 16) {
            byte[] data = new byte[len+1];
            data[0] = (byte)len;
            int index = 1;
            for (int i = 0x80; i <= 0xff; i++) {
                if (isSet(EPC.fromByte((byte)i))) {
                    data[index++] = (byte) i;
                    if (index > len) {
                        break;
                    }
                }
            }
            return data;
        } else {
            return Arrays.copyOf(map, map.length);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PropertyMap)) {
            return false;
        }
        
        PropertyMap other = (PropertyMap)o;
        
        return Arrays.equals(other.toBytes(), toBytes());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(toBytes());
    }
}
