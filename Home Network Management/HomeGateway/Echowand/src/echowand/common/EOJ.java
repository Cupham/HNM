package echowand.common;

/*
 * @author HiepNguyen
 */

public class EOJ {
	private ClassEOJ classEOJ;
    private byte instanceCode;
    
    public EOJ(byte classGroupCode, byte classCode, byte instanceCode) {
        this.classEOJ = new ClassEOJ(classGroupCode, classCode);
        this.instanceCode = instanceCode;
    }
    
    public EOJ(String eoj) throws IllegalArgumentException {
        if (eoj.length() != 6) {
            throw new IllegalArgumentException("Invalid EOJ: " + eoj);
        }
        try {
            this.classEOJ = new ClassEOJ(eoj.substring(0,4));
            this.instanceCode = (byte) Integer.parseInt(eoj.substring(4, 6), 16);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid EOJ: " + eoj, ex);
        }
    }
    
    public EOJ(byte[] bytes) {
        this(bytes, 0);
    }
    
    public EOJ(byte[] bytes, int offset) {
        this.classEOJ = new ClassEOJ(bytes[offset], bytes[offset+1]);
        this.instanceCode = bytes[offset+2];
    }
    
    public byte getClassGroupCode() {
        return this.classEOJ.getClassGroupCode();
        
    }
    
    public byte getClassCode() {
        return this.classEOJ.getClassCode();
    }
    
    public byte getInstanceCode() {
        return this.instanceCode;
    }
    
    public ClassEOJ getClassEOJ() {
        return classEOJ;
    }
    
    public EOJ getEOJWithInstanceCode(byte newInstanceCode) {
        return classEOJ.getEOJWithInstanceCode(newInstanceCode);
    }
    
    public EOJ getAllInstanceEOJ() {
        return getEOJWithInstanceCode((byte)0x00);
    }
    
    public boolean isMemberOf(ClassEOJ ceoj) {
        return getClassEOJ().equals(ceoj);
    }
    
    public boolean isAllInstance() {
        return instanceCode == 0x00;
    }
    
    public boolean isDeviceObject() {
        return getClassEOJ().isDeviceObject();
    }
    
    public boolean isProfileObject() {
        return getClassEOJ().isProfileObject();
    }
    
    public boolean isNodeProfileObject() {
        return getClassEOJ().isNodeProfileObject();
    }
    
    public int intValue() {
        return (classEOJ.intValue() << 8) | (0xff & (int)instanceCode);
    }
    
    public byte[] toBytes() {
        byte[] bytes = new byte[3];
        bytes[0] = classEOJ.getClassGroupCode();
        bytes[1] = classEOJ.getClassCode();
        bytes[2] = instanceCode;
        return bytes;
    }
    
    @Override
    public String toString() {
        return String.format("%06x", intValue());
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj instanceof EOJ) {
            EOJ other = (EOJ)otherObj;
            return     this.classEOJ.equals(other.classEOJ)
                    && this.getInstanceCode() == other.getInstanceCode();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return intValue();
    }
}
