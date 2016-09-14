package dto;

/**
 * @author Cu Pham
 *
 */
public class DirectAttributeObject {
	/**
	 * device ip
	 */
	private String ip;
	/**
	 * attribute name
	 */
	private String attributeName;
	/**
	 * attribute value
	 */
	private String value;
	
	public DirectAttributeObject(){
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
