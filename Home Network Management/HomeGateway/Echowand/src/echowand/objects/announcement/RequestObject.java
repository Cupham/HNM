/**
 * Announcement change status object
 */
package echowand.objects.announcement;

import com.google.gson.Gson;

/**
 * @author Cu Pham
 *
 */
public class RequestObject {

	private String ip;
	private byte groupCode;
	private byte classCode;
	private byte instanceCode;
	private byte epc;
	private String value;
	
	public RequestObject(){
		
	}
	
	public RequestObject(String ip, byte groupCode, byte classCode, byte instanceCode,
			byte epc, String value){
		this.ip = ip;
		this.groupCode = groupCode;
		this.classCode = classCode;
		this.instanceCode = instanceCode;
		this.epc = epc;
		this.value = value;
	}

	public String toJson(){
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * @param ip the ip to set
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
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
