package server.objects;

/**
 * @author Cu Pham
 *
 */
public class AttributeObject {
	/**
	 * device ip
	 */
	private String ip;
	/**
	 * group code
	 */
	private byte groupCode;
	/**
	 * class code
	 */
	private byte classCode;
	/**
	 * instance code
	 */
	private byte instanceCode;
	/**
	 * epc
	 */
	private byte epc;
	/**
	 * value
	 */
	private String value;

	public AttributeObject() {
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the groupCode
	 */
	public byte getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode
	 *            the groupCode to set
	 */
	public void setGroupCode(byte groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the classCode
	 */
	public byte getClassCode() {
		return classCode;
	}

	/**
	 * @param classCode
	 *            the classCode to set
	 */
	public void setClassCode(byte classCode) {
		this.classCode = classCode;
	}

	/**
	 * @return the instanceCode
	 */
	public byte getInstanceCode() {
		return instanceCode;
	}

	/**
	 * @param instanceCode
	 *            the instanceCode to set
	 */
	public void setInstanceCode(byte instanceCode) {
		this.instanceCode = instanceCode;
	}

	/**
	 * @return the epc
	 */
	public byte getEpc() {
		return epc;
	}

	/**
	 * @param epc
	 *            the epc to set
	 */
	public void setEpc(byte epc) {
		this.epc = epc;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Compare two AttributeObject
	 * 
	 * @param obj
	 *            another AttributeObject
	 * @return true if each pair attributes of two objects is equal (except attribute value)
	 */
	public boolean equals(AttributeObject obj) {
		if (this.ip.equals(obj.ip)
				&& this.groupCode == obj.groupCode
				&& this.classCode == obj.classCode
				&& this.instanceCode == obj.instanceCode
				&& this.epc == obj.epc)
			return true;

		return false;
	}
}
