/**
 * 
 */
package humming.sample.I2C;

import java.io.IOException;
import java.util.logging.Logger;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * @author Cu Pham
 *
 */
public class TCN75_I2CPin extends I2CPin {

	private static final Logger LOGGER = Logger.getLogger(TCN75_I2CPin.class.getName());
    private static final String CLASS_NAME = TCN75_I2CPin.class.getName();
	
	public TCN75_I2CPin(int address, int bus) throws UnsupportedBusNumberException, IOException {
		super(address, bus);
	}

	@Override
	public boolean setValue(byte[] data) {

		
		
		return false;
	}

	@Override
	public int getValue() {
		LOGGER.entering(CLASS_NAME, "getValue");
		byte[] data = new byte[2];
		try {
			this.device.read(0x00, data, 0, 2);
			int temperature = (int) (getTemperatureData(data[0], data[1]) * 10);	// unit: 0.1*C
			LOGGER.exiting(CLASS_NAME, "getValue",temperature);
			return temperature;
		} catch (IOException e) {
			LOGGER.exiting(CLASS_NAME, "getValue","Failed, detail "+e.getMessage());
			return -1;
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

}
