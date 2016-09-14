/**
 * 
 */
package CoAP.Client.objects;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * @author Cu Pham
 *
 */
public class TCN75_Temperature  extends DirectDataObject{
	
	protected int temperature;
	
	private int address;
	private I2CDevice device;

	public TCN75_Temperature() {
		super();
		this.groupCode = (byte) 0x00;
		this.classCode = (byte) 0x11;
	}
	
	public TCN75_Temperature(byte groupCode, byte classCode) {
		super();
		this.groupCode = groupCode;
		this.classCode = classCode;
	}

	public TCN75_Temperature(boolean operationStatus, int temperature) {
		super(operationStatus);
		this.groupCode = (byte) 0x00;
		this.classCode = (byte) 0x11;
		this.temperature = temperature;
	}
	
	public TCN75_Temperature(boolean operationStatus, int temperature, byte groupCode, byte classCode) {
		super(operationStatus);
		this.groupCode = groupCode;
		this.classCode = classCode;
		this.temperature = temperature;
	}
	
	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public void setAddress(int address, int bus) throws UnsupportedBusNumberException, IOException {
		this.address = address;
        I2CBus Bus = I2CFactory.getInstance(bus);
    	this.device = Bus.getDevice(this.address);
    	this.device.write(0x01, (byte) 0x60);
	}
	
	@Override
	public int delegateGetValue(){
		return this.temperature;
	}
	
	@Override
	public void delegateSetValue(byte[] value){
		// do nothing
	}
	
	@Override
	public boolean delegateNotify(){
		byte[] data = new byte[2];
		try {
			this.device.read(0x00, data, 0, 2);
			int temperature = (int) (getTemperatureData(data[0], data[1]) * 10);	// unit: 0.1*C
			if(this.temperature != temperature){
				this.temperature = temperature;
				return true;
			}
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	private static double getTemperatureData(byte data0, byte data1) {
		int data = ((data0 << 1) + ((data1 & 0x80) >> 7));
		int sign = ((data0 >> 7) & 0x80);
		if (sign == 1) {
			data = ~data - 1;
		}
		return ((double) data / 2);
	}

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

	
	@Override
	public String toJson() {
		String jsonStr = "{";
		
		jsonStr += "\"groupCode\":"+this.groupCode;
		jsonStr += ",\"classCode\":"+this.classCode;
		jsonStr += ",\"instanceCode\":"+this.instanceCode;
		jsonStr += ",\"operationStatus\":"+((this.operationStatus)?"true":"false");
		jsonStr += ",\"temperature\":"+this.temperature;
		
		jsonStr += "}";
		
		
		return jsonStr;
	}
}
