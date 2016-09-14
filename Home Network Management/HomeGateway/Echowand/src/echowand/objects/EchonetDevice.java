/**
 * Echonet device alias device
 */
package echowand.objects;

import java.util.ArrayList;

import com.google.gson.Gson;

import echowand.common.EOJ;
import echowand.object.EchonetObjectException;
import echowand.object.RemoteObject;

/**
 * @author Cu Pham
 *
 */
public class EchonetDevice {
	
	
	public static enum DeviceType{
		EchonetLiteDevice,
		HomeGateway,
		DirectDevice
	};

	private EchonetProfileObject profile;
	private ArrayList<EchonetDataObject> eObjList;
	private DeviceType deviceType;
	
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

	public EchonetProfileObject getProfile() {
		return profile;
	}

	public void setProfile(EchonetProfileObject profile) {
		this.profile = profile;
	}

	public void addObject(EchonetDataObject dataObject) throws EchonetObjectException {
		if(this.eObjList == null){
			this.eObjList =new ArrayList<EchonetDataObject>();
		}
		this.eObjList.add(dataObject);
	}
	
	public void addObject(EOJ eoj, RemoteObject rObj) throws EchonetObjectException {
		byte classGroupCode = eoj.getClassGroupCode();
		byte classCode = eoj.getClassCode();
		EchonetDataObject insertObject = null;
		
		switch (classGroupCode) {
		case (byte) (0x00): // Sensor-related Device Class Group
			switch (classCode) {
			case (byte) (0x11): // temperature sensor
				insertObject = new TemperatureDeviceObject();
				break;
			case (byte) (0x12): // Humidity sensor
				insertObject = new HumidityDeviceObject();
				break;
			default:
				return;
			}
			break;
		default:
			return;
		}
		insertObject.ParseDataFromRemoteObject(rObj);
		this.eObjList.add(insertObject);
	}

	public EchonetDevice() {
		this.eObjList = new ArrayList<EchonetDataObject>();
		this.deviceType = DeviceType.EchonetLiteDevice;
		this.profile = null;
	}
	public EchonetDevice(DeviceType deviceType) {
		this.eObjList = new ArrayList<EchonetDataObject>();
		this.deviceType = deviceType;
		this.profile = null;
	}
	public EchonetDevice(EchonetProfileObject profile) {
		this.profile = profile;
		this.eObjList = new ArrayList<EchonetDataObject>();
		this.deviceType = DeviceType.EchonetLiteDevice;
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	@Override
	public String toString() {
		StringBuilder rs = new StringBuilder();
		rs.append("\r\n*********************************************");
		rs.append("\r\n>Profile Object: \r\n");
		rs.append(this.profile.toString());
		rs.append("\r\n>Data Object: "+eObjList.size()+" devices\r\n");

		for (EchonetDataObject deviceObject : eObjList) {
			rs.append("\r\n\t####################\r\n");
			rs.append("\t"+ deviceObject.ToString()+"\r\n");
			rs.append("\t####################\r\n");
		}
		rs.append("*********************************************\r\n");
		return rs.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if (!(obj instanceof EchonetDevice))
            return false;
        if (obj == this)
            return true;

        EchonetDevice checkDevice = (EchonetDevice) obj;
        return this.profile.equals(checkDevice.profile);
	}
}
