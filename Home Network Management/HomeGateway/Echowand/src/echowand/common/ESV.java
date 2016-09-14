package echowand.common;

public enum ESV {
	Invalid((byte)0x00),
	
	SetI((byte)0x60),
	
	SetC((byte)0x61),
	
	Get((byte)0x62),
	
	INF_REQ((byte)0x63),
	
	SetGet((byte)0x6e),
	
	Set_Res((byte)0x71),
	
	Get_Res((byte)0x72),
	
	INF((byte)0x73),
	
	INFC((byte)0x74),
	
	INFC_Res((byte)0x7a),
	
	SetGet_Res((byte)0x7e),
	
	SetI_SNA((byte)0x50),
	
	SetC_SNA((byte)0x51),
	
	Get_SNA((byte)0x52),
	
	INF_SNA((byte)0x53),
	
	SetGet_SNA((byte)0x5E);
	
	public byte toByte() {
	    return code;
	}

    public boolean isInfo() {
        switch (this) {
            case INF_REQ:
            case INF:
            case INFC:
            case INFC_Res:
            case INF_SNA:
                return true;
            default:
                return false;
        }
    }
    
    public boolean isSetGet() {
        switch (this) {
            case SetGet:
            case SetGet_Res:
            case SetGet_SNA:
                return true;
            default:
                return false;
        }
    }
	
    public boolean isInvalid() {
        return this == Invalid;
    }
    
    private ESV(byte code) {
        this.code = code;
    }
    
    private byte code;
    
    public static ESV fromByte(byte code) {
        for (ESV esv : ESV.values()) {
            if (esv.code == code) {
                return esv;
            }
        }
        return Invalid;
    }
}
