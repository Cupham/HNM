package server.objects;

import echowand.objects.EchonetDevice.DeviceType;

/**
 * @author Cu Pham
 *
 */
public class EchonetDeviceStatus {
	/**
	 * device ip
	 */
	private String deviceIP;
	/**
	 * device status
	 */
	private boolean deviceStatus;
	/**
	 * variable define that device is echonet lite device
	 *  or CoAP enable device
	 *  or gateway
	 */
	private DeviceType deviceType;
	
	/**
	 * Constructor
	 */
	public EchonetDeviceStatus(){
	}

	public String getDeviceIP() {
		return deviceIP;
	}

	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	public boolean isDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(boolean deviceStatus) {
		this.deviceStatus = deviceStatus;
	}
	

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * convert EchonetDeviceStatus to XML
	 * @return
	 */
	public String toXML(){
		StringBuilder xml = new StringBuilder();
		switch(this.deviceType){
		case EchonetLiteDevice:
			xml.append("\n\t<ECHONET_Lite>");
			break;
		case DirectDevice:
			xml.append("\n\t<CoAP_enabled_device>");
			break;
		case HomeGateway:
			xml.append("\n\t<Home_Gateway>");
			break;
		default:
			xml.append("\n\t<Unknown>");
			break;
		}
		
		xml.append("\n\t\t<Device_IP>");
		xml.append(this.deviceIP);
		xml.append("</Device_IP>");
		xml.append("\n\t\t<Device_Status>");
		xml.append((this.deviceStatus ? "ON": "OFF"));
		xml.append("</Device_Status>");
		
		switch(this.deviceType){
		case EchonetLiteDevice:
			xml.append("\n\t</ECHONET_Lite>");
			break;
		case DirectDevice:
			xml.append("\n\t</CoAP_enabled_device>");
			break;
		case HomeGateway:
			xml.append("\n\t</Home_Gateway>");
			break;
		default:
			xml.append("\n\t</Unknown>");
			break;
		}
		return xml.toString();
	}
}
