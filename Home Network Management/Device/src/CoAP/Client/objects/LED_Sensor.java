/**
 * 
 */
package CoAP.Client.objects;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

/**
 * @author Cu Pham
 *
 */
public class LED_Sensor extends DirectDataObject {

	private boolean ledON = false;

	public boolean isLedON() {
		return ledON;
	}

	private int pinNumber;
	private GpioPinDigitalOutput digitalOutput;

	public void setPinNumber(int pinNumber) {
		this.pinNumber = pinNumber;
		this.digitalOutput = GpioFactory.getInstance()
				.provisionDigitalOutputPin(RaspiPin.getPinByAddress(this.pinNumber));
	}

	public LED_Sensor() {
		super();
		this.groupCode = (byte) 0x00;
		this.classCode = (byte) 0x00;
	}

	public LED_Sensor(byte groupCode, byte classCode) {
		super();
		this.groupCode = groupCode;
		this.classCode = classCode;
	}

	@Override
	public String ToString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJson() {
		String jsonStr = "{";
		
		jsonStr += "\"groupCode\":"+this.groupCode;
		jsonStr += ",\"classCode\":"+this.classCode;
		jsonStr += ",\"instanceCode\":"+this.instanceCode;
		jsonStr += ",\"operationStatus\":"+((this.operationStatus)?"true":"false");
		jsonStr += ",\"ledON\":"+((this.ledON)?"true":"false");
		
		jsonStr += "}";
		return jsonStr;
	}

	@Override
	public int delegateGetValue() {
		return (this.ledON) ? 1 : 0;
	}

	@Override
	public void delegateSetValue(byte[] value) {
		if(value[0] == (byte)0x30)
			this.digitalOutput.high();
		else
			this.digitalOutput.low();
	}

	@Override
	public boolean delegateNotify() {
		boolean currentState = this.digitalOutput.isHigh();
		if(this.ledON != currentState){
			this.ledON = currentState;
			return true;
		}
		return false;
	}
}
