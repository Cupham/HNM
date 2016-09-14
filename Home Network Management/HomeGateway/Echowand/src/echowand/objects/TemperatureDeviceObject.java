/**
 * Device object for temperature sensor
 */
package echowand.objects;

import echowand.common.EPC;
import echowand.object.EchonetObjectException;
import echowand.object.RemoteObject;
import echowand.util.ConvertData;

/**
 * @author Cu Pham
 *
 */
public class TemperatureDeviceObject extends EchonetDataObject {

	/**
	 * EPC: 0xE0 Measured temperature value in units of 0.1 Celcius Value
	 * between: 0xF554–0x7FFE (-2732–32766)~(-273.2–3276.6 Celcius)
	 */
	protected int temperature;

	/**
	 * @return the temperature
	 */
	public int getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature
	 * the temperature to set
	 */
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	/**
	 * Constructor
	 */
	public TemperatureDeviceObject() {
		super();
		this.groupCode = (byte) 0x00;
		this.classCode = (byte) 0x11;
	}
	
	/**
	 * Constructor
	 */
	public TemperatureDeviceObject(byte groupCode, byte classCode) {
		super();
		this.groupCode = groupCode;
		this.classCode = classCode;
	}

	/**
	 * Constructor
	 * @param operationStatus
	 * @param temperature
	 */
	public TemperatureDeviceObject(boolean operationStatus, int temperature) {
		super(operationStatus);
		this.groupCode = (byte) 0x00;
		this.classCode = (byte) 0x11;
		this.temperature = temperature;
	}
	
	/**
	 * Constructor
	 * @param operationStatus
	 * @param temperature
	 * @param groupCode
	 * @param classCode
	 */
	public TemperatureDeviceObject(boolean operationStatus, int temperature, byte groupCode, byte classCode) {
		super(operationStatus);
		this.groupCode = groupCode;
		this.classCode = classCode;
		this.temperature = temperature;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see echowand.objects.EchonetDataObject#ParseDataFromRemoteObject(pihome.
	 * object.RemoteObject)
	 */
	@Override
	public void ParseDataFromRemoteObject(RemoteObject rObj) throws EchonetObjectException {
		boolean temperatureStatus = true;
		int temperatureValue = 0;
		this.instanceCode = rObj.getEOJ().getInstanceCode();
		if (rObj.contains(EPC.x80)) { // operation status
			temperatureStatus = (ConvertData.dataToInteger(rObj.getData(EPC.x80)) == 48) ? true : false;
		}

		if (rObj.contains(EPC.xE0)) { // temperature
			temperatureValue = ConvertData.dataToInteger(rObj.getData(EPC.xE0));
		}
		this.operationStatus = temperatureStatus;
		this.temperature = temperatureValue;
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see echowand.objects.EchonetDataObject#ToString()
	 */
	@Override
	public String ToString() {
		StringBuilder rs = new StringBuilder();
		rs.append("EOJ: "+String.format("%02x", this.getGroupCode())+
				String.format("%02x", this.getClassCode())+
				String.format("%02x", this.getInstanceCode())+"\r\n");
		rs.append("\tStatus: "+((this.operationStatus)?"ON":"OFF")+"\r\n");
		rs.append("\tTemperature: "+this.temperature/10+"*C");
		return rs.toString();
	}
}
