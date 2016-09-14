package server.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import echowand.objects.EchonetDataObject;
import echowand.objects.EchonetDevice;
import echowand.objects.EchonetProfileObject;
import echowand.objects.HumidityDeviceObject;
import echowand.objects.TemperatureDeviceObject;
import echowand.objects.EchonetDevice.DeviceType;

/**
 * @author Cu Pham
 *
 */
public class EchonetDeviceEx {	
	// logger
	final static Logger logger = Logger.getLogger(EchonetDeviceEx.class);
	
	/**
	 * profile object
	 */
	private EchonetProfileObject profile;
	/**
	 * list data object
	 */
	private List<Map<String, String>> eObjList;
	
	private DeviceType deviceType;
	
	public EchonetDeviceEx() {
	}

	public EchonetProfileObject getProfile() {
		return profile;
	}

	public void setProfile(EchonetProfileObject profile) {
		this.profile = profile;
	}

	public List<Map<String, String>> geteObjList() {
		return eObjList;
	}

	public void seteObjList(List<Map<String, String>> eObjList) {
		this.eObjList = eObjList;
	}

	
	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * Convert EchonetDeviceEx to EchonetDevice
	 */
	public EchonetDevice toEchonetDevice() {
		// create a new EchonetDevice
		EchonetDevice eDevice = new EchonetDevice();
		// set device type
		eDevice.setDeviceType(this.deviceType);
		// set profile for EchonetDevice
		eDevice.setProfile(this.profile);
		// convert installation location to lower
		if (eDevice.getProfile() != null && eDevice.getProfile().getInstallLocation() != null)
			eDevice.getProfile().setInstallLocation(eDevice.getProfile().getInstallLocation().toLowerCase());
		// create objList
		ArrayList<EchonetDataObject> objList = new ArrayList<EchonetDataObject>();

		// browse eObjList
		for (Map<String, String> map : eObjList) {
			try {
				// check if groupCpde and classCode are not null
				if (map.containsKey("groupCode") && map.containsKey("classCode") && map.containsKey("instanceCode")) {
					try {
						// parse groupCode, classCode, instance to byte
						byte groupCode = Byte.parseByte(map.get("groupCode"));
						byte classCode = Byte.parseByte(map.get("classCode"));
						byte instance = Byte.parseByte(map.get("instanceCode"));

						switch (groupCode) {
						// List of Objects of Sensor-related Device Class Group
						case 0x00:
							switch (classCode) {
							// Temperature sensor
							case 0x11:
								TemperatureDeviceObject temperatureObj = new TemperatureDeviceObject();
								temperatureObj.setGroupCode(groupCode);
								temperatureObj.setClassCode(classCode);
								temperatureObj.setInstanceCode(instance);
								
								if (map.containsKey("operationStatus")) {
									boolean operationStatus = Boolean.parseBoolean(map.get("operationStatus"));
									temperatureObj.setOperationStatus(operationStatus);
								}
								if (map.containsKey("temperature")) {
									try {
										int temperature = (int)Double.parseDouble(map.get("temperature"));
										temperatureObj.setTemperature(temperature);
									} catch (NumberFormatException ex) {
										logger.error(ex);
									}
								}
								objList.add(temperatureObj);
								break;
							// Humidity sensor
							case 0x12:
								HumidityDeviceObject humidityObj = new HumidityDeviceObject();
								humidityObj.setGroupCode(groupCode);
								humidityObj.setClassCode(classCode);
								humidityObj.setInstanceCode(instance);
								if (map.containsKey("operationStatus")) {
									boolean operationStatus = Boolean.parseBoolean(map.get("operationStatus"));
									humidityObj.setOperationStatus(operationStatus);
								}
								if (map.containsKey("humidity")) {
									try {
										double humidity = Double.parseDouble(map.get("humidity"));
										humidityObj.setHumidity(humidity);
									} catch (NumberFormatException ex) {
										logger.error(ex);
									}
								}
								objList.add(humidityObj);
								break;
							}
							break;
						}
					} catch (NumberFormatException ex) {
						// print exception if parse failed
						logger.error(ex);
					}
				}
			} catch (Exception ex) {
				logger.error(ex);
			}
		}

		// set eObjList for EchonetDevice object
		eDevice.seteObjList(objList);

		return eDevice;
	}
}
