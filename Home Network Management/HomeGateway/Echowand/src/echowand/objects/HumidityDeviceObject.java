/**
 * Device object for humidity sensor
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
public class HumidityDeviceObject extends EchonetDataObject {

	/**
	 * EPC: 0xE0 Measured value of relative humidity in %. between 0x00 - 0x64
	 * (0 - 100) If overflow: 0xFF, if underflow: 0xFE
	 */
	private double humidity;

	/**
	 * @return the humidity
	 */
	public double getHumidity() {
		return humidity;
	}

	/**
	 * @param humidity
	 *            the humidity to set
	 */
	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	/**
	 * Constructor
	 */
	public HumidityDeviceObject() {
		super();
		this.groupCode = (byte) 0x00;
		this.classCode = (byte) 0x12;
	}
	
	/**
	 * Constructor
	 */
	public HumidityDeviceObject(byte groupCode, byte classCode) {
		super();
		this.groupCode = groupCode;
		this.classCode = classCode;
	}

	/**
	 * Constructor
	 * @param operationStatus
	 * @param humidity
	 */
	public HumidityDeviceObject(boolean operationStatus, double humidity) {
		super(operationStatus);
		this.groupCode = (byte) 0x00;
		this.classCode = (byte) 0x12;
		this.humidity = humidity;
	}
	
	/**
	 * Constructor
	 * @param operationStatus
	 * @param humidity
	 * @param groupCode
	 * @param classCode
	 */
	public HumidityDeviceObject(boolean operationStatus, double humidity, byte groupCode, byte classCode) {
		super(operationStatus);
		this.groupCode = groupCode;
		this.classCode = classCode;
		this.humidity = humidity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see echowand.objects.EchonetDataObject#ParseDataFromRemoteObject(pihome.
	 * object.RemoteObject)
	 */
	@Override
	public void ParseDataFromRemoteObject(RemoteObject rObj) throws EchonetObjectException {
		boolean humidityStatus = true;
		double humidityValue = 0;
		this.instanceCode = rObj.getEOJ().getInstanceCode();
		if (rObj.contains(EPC.x80)) { // operation status
			humidityStatus = (ConvertData.dataToInteger(rObj.getData(EPC.x80)) == 48) ? true : false;
		}
		if (rObj.contains(EPC.xE0)) { // humidity
			humidityValue = ConvertData.dataToReal(rObj.getData(EPC.xE0));
		}
		this.operationStatus = humidityStatus;
		this.humidity = humidityValue;
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
		rs.append("\tHummidity: "+this.humidity+" %");
		return rs.toString();
	}
}
