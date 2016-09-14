package server.objects;

import server.config.TAG_NAME;

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
	 * attribute name
	 */
	private String attributeName;
	/**
	 * attribute value
	 */
	private String value;

	public DirectAttributeObject() {
	}

	public String getIp() {
		return ip;
	}

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
	 * @param groupCode the groupCode to set
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
	 * @param classCode the classCode to set
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
	 * @param instanceCode the instanceCode to set
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
	 * @param epc the epc to set
	 */
	public void setEpc(byte epc) {
		this.epc = epc;
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

	/**
	 * convert to json
	 * 
	 * @return DirectAttributeObject in json format
	 */
	public String toJson() {
		StringBuilder json = new StringBuilder();
		json.append("{\"ip\":\"");
		json.append(this.ip);
		json.append("\", \"attributeName\":\"");
		json.append(this.attributeName);
		json.append("\", \"value\":\"");

		switch (this.attributeName) {
		case TAG_NAME.OPERATION_STATUS:
		case TAG_NAME.POWER_SAVING_OPERATION_SETTING:
			boolean b = Boolean.parseBoolean(value);
			if (b)
				json.append("ON");
			else
				json.append("OFF");
			break;
		case TAG_NAME.REMOTE_CONTROL_SETTING:
			b = Boolean.parseBoolean(value);
			if (b)
				json.append("YES");
			else
				json.append("NO");
			break;
		default:
			json.append(this.value);
			break;
		}
		json.append("\"}");

		return json.toString();
	}

	/**
	 * Compare two DirectAttributeObject
	 * 
	 * @param obj
	 *            another DirectAttributeObject
	 * @return true if each pair attributes of two objects is equal (except
	 *         attribute value)
	 */
	public boolean equals(DirectAttributeObject obj) {
		if (this.ip.equals(obj.ip) && this.attributeName.equals(obj.attributeName))
			return true;

		return false;
	}
}
