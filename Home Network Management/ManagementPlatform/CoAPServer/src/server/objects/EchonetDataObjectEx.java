package server.objects;

import java.util.List;

/**
 * @author Cu Pham
 *
 */
public class EchonetDataObjectEx {
	/**
	 * device ip
	 */
	private String deviceIP;
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
	 * operation status
	 */
	private boolean operationStatus;
	/**
	 * list extended attributes of EchonetDataObject
	 */
	private List<AttributeDataObject> listAttributes;
	
	public String getDeviceIP() {
		return deviceIP;
	}
	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}
	
	public byte getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(byte groupCode) {
		this.groupCode = groupCode;
	}
	
	public byte getClassCode() {
		return classCode;
	}
	public void setClassCode(byte classCode) {
		this.classCode = classCode;
	}
	
	public byte getInstanceCode() {
		return instanceCode;
	}
	public void setInstanceCode(byte instanceCode) {
		this.instanceCode = instanceCode;
	}
	
	public boolean isOperationStatus() {
		return operationStatus;
	}
	public void setOperationStatus(boolean operationStatus) {
		this.operationStatus = operationStatus;
	}
	
	public List<AttributeDataObject> getListAttributes() {
		return listAttributes;
	}
	public void setListAttributes(List<AttributeDataObject> listAttributes) {
		this.listAttributes = listAttributes;
	}
}
