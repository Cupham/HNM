package echowand.common;

/*
 * @author HiepNguyen
 */

public class ClassEOJ {
	private byte classGroupCode;
    private byte classCode;
    private static ClassEOJ nodeClassEOJ = new ClassEOJ((byte) 0x0E, (byte) 0xF0);
    private static byte[] deviceCodes = new byte[]{(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06};
    private static byte profileCode = (byte) 0x0e;
    
    /*
     * @param classGroupCode
     * @param classCode
     */
    public ClassEOJ(byte classGroupCode, byte classCode) {
        this.classGroupCode = classGroupCode;
        this.classCode = classCode;
    }
    
    /*
     * @param ceoj
     * @exception IllegalArgumentException
     */
    public ClassEOJ(String ceoj) throws IllegalArgumentException {
        if (ceoj.length() != 4) {
            throw new IllegalArgumentException("Invalid ClassEOJ: " + ceoj);
        }
        try {
            this.classGroupCode = (byte) Integer.parseInt(ceoj.substring(0, 2), 16);
            this.classCode = (byte) Integer.parseInt(ceoj.substring(2, 4), 16);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid ClassEOJ: " + ceoj, ex);
        }
    }
    /*
     * @return
     */
    public byte getClassGroupCode() {
        return this.classGroupCode;
        
    }
    
    public byte getClassCode() {
        return this.classCode;
    }
    
    public byte[] toBytes() {
        byte[] bytes = new byte[2];
        bytes[0] = classGroupCode;
        bytes[1] = classCode;
        return bytes;
    }
    
    public boolean isNodeProfileObject() {
        return this.equals(nodeClassEOJ);
    }
    
    public EOJ getEOJWithInstanceCode(byte instanceCode) {
        return new EOJ(classGroupCode, classCode, instanceCode);
    }
    
    public EOJ getAllInstanceEOJ() {
        return getEOJWithInstanceCode((byte)0x00);
    }
    
    public boolean isDeviceObject() {
        for (byte code: deviceCodes) {
            if (code == classGroupCode) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isProfileObject() {
        return profileCode == classGroupCode;
    }
    
    public int intValue() {
        return ((0xff & (int)classGroupCode) << 8)
                | (0xff & (int)classCode);
    }
    
    @Override
    public String toString() {
        return String.format("%04x", intValue());
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof ClassEOJ) {
            ClassEOJ other = (ClassEOJ)otherObj;
            return     this.getClassGroupCode() == other.getClassGroupCode()
                    && this.getClassCode() == other.getClassCode();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return intValue();
    }

}
