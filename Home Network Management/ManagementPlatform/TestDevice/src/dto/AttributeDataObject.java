package dto;

/**
 * @author Cu Pham
 *
 */
public class AttributeDataObject {
	/**
	 * attribute name
	 */
	private String attributeName;
	/**
	 * epc
	 */
	private byte epc;
	/**
	 * value
	 */
	private String value;
	/**
	 * set: yes/no
	 */
	private boolean changeable;
	
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	public byte getEpc() {
		return epc;
	}
	public void setEpc(byte epc) {
		this.epc = epc;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isChangeable() {
		return changeable;
	}
	public void setChangeable(boolean changeable) {
		this.changeable = changeable;
	}
	
}
