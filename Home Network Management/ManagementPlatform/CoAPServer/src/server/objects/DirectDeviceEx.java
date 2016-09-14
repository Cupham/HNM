package server.objects;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import echowand.objects.EchonetProfileObject;
import echowand.objects.EchonetDevice.DeviceType;
import CoAP.Client.objects.DirectDataObject;
import CoAP.Client.objects.LED_Sensor;
import CoAP.Client.objects.TCN75_Temperature;

/**
 * @author Cu Pham
 *
 */
public class DirectDeviceEx {

	// logger
	final static Logger logger = Logger.getLogger(DirectDeviceEx.class);
	
	/**
	 * profile object
	 */
	private EchonetProfileObject profile;
	/**
	 * list data object
	 */
	private List<Map<String, String>> eObjList;
	/**
	 * devie type
	 */
	private DeviceType deviceType;
	
	public DirectDeviceEx(){
	}

	/**
	 * @return the profile
	 */
	public EchonetProfileObject getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(EchonetProfileObject profile) {
		this.profile = profile;
	}

	/**
	 * @return the eObjList
	 */
	public List<Map<String, String>> geteObjList() {
		return eObjList;
	}

	/**
	 * @param eObjList the eObjList to set
	 */
	public void seteObjList(List<Map<String, String>> eObjList) {
		this.eObjList = eObjList;
	}

	/**
	 * @return the deviceType
	 */
	public DeviceType getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	
	/**
	 * Convert DirectDeviceEx to DirectDevice
	 */
	public DirectDevice toDirectDevice(){
		// create a new DirectDevice
		DirectDevice dDevice = new DirectDevice();
		// set device type
		dDevice.setDeviceType(this.deviceType);
		// set profile for DeviceDevice
		dDevice.setProfile(this.profile);
		// convert installation location to lower
		if (dDevice.getProfile() != null && dDevice.getProfile().getInstallLocation() != null)
			dDevice.getProfile().setInstallLocation(dDevice.getProfile().getInstallLocation().toLowerCase());
		// create objList
		ArrayList<DirectDataObject> objList = new ArrayList<DirectDataObject>();
		// browse eObjList
		for (Map<String, String> map : eObjList){
			try{
				// check if groupCpde and classCode are not null
				if (map.containsKey("groupCode") && map.containsKey("classCode") && map.containsKey("instanceCode")){
					try {
						// parse groupCode, classCode, instance to byte
						byte groupCode = Byte.parseByte(map.get("groupCode"));
						byte classCode = Byte.parseByte(map.get("classCode"));
						byte instance = Byte.parseByte(map.get("instanceCode"));

						switch (groupCode) {
						// List of Objects of Sensor-related Device Class Group
						case 0x00:
							switch (classCode) {
							// LED sensor
							case 0x00:
								LED_Sensor led = new LED_Sensor();
								led.setGroupCode(groupCode);
								led.setClassCode(classCode);
								led.setInstanceCode(instance);
								if (map.containsKey("operationStatus")) {
									boolean operationStatus = Boolean.parseBoolean(map.get("operationStatus"));
									led.setOperationStatus(operationStatus);
								}
								if (map.containsKey("ledON")) {
									boolean ledON = Boolean.parseBoolean(map.get("ledON"));
									led.setLedON(ledON);
								}
								objList.add(led);
								break;
							// TCN75 Temperature
							case 0x11:
								TCN75_Temperature tcn75Temperature = new TCN75_Temperature();
								tcn75Temperature.setGroupCode(groupCode);
								tcn75Temperature.setClassCode(classCode);
								tcn75Temperature.setInstanceCode(instance);
								
								if (map.containsKey("operationStatus")) {
									boolean operationStatus = Boolean.parseBoolean(map.get("operationStatus"));
									tcn75Temperature.setOperationStatus(operationStatus);
								}
								if (map.containsKey("temperature")) {
									try {
										int temperature = Integer.parseInt(map.get("temperature"));
										tcn75Temperature.setTemperature(temperature);
									} catch (NumberFormatException ex) {
										logger.error(ex);
									}
								}
								objList.add(tcn75Temperature);
								break;
							}
							break;
						}
					} catch (NumberFormatException ex) {
						// print exception if parse failed
						logger.error(ex);
					}
				}
			} catch (Exception ex){
				logger.error(ex);
			}
		}
		dDevice.seteObjList(objList);
		
		return dDevice;
	}
}
