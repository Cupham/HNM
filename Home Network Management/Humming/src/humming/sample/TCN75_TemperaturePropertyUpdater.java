/**
 * 
 */
package humming.sample;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import echowand.common.EPC;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.PropertyUpdater;

/**
 * @author Cu Pham
 *
 */
public class TCN75_TemperaturePropertyUpdater extends PropertyUpdater {

	private static final Logger LOGGER = Logger.getLogger(TCN75_TemperaturePropertyUpdater.class.getName());
	private static final int busNumber = I2CBus.BUS_1;
	private static final byte localAddress = (byte) 0x48;

	private EPC epc;
	private I2CBus bus;
	private I2CDevice device;

	public TCN75_TemperaturePropertyUpdater() {
		epc = EPC.xE0;
		try {
			bus = I2CFactory.getInstance(busNumber);
			device = bus.getDevice(localAddress);
			device.write(0x01, (byte) 0x60);
		} catch (UnsupportedBusNumberException e) {
			LOGGER.logp(Level.INFO, this.getClass().getName(), "main", "Create BUS failed. Detail: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.logp(Level.INFO, this.getClass().getName(), "main", "Read data failed. Detail: " + e.getMessage());
		}
	}

	public void setEPC(EPC epc) {
		this.epc = epc;
	}

	public EPC getEPC() {
		return epc;
	}

	@Override
	public void loop(LocalObject localObject) {
		byte[] data = new byte[2];
		try {
			// 0x00 is register type
			device.read(0x00, data, 0, 2);
			int temperature = (int) (getTempData(data[0], data[1]) * 10);
			byte b1 = (byte) (temperature & 0xFF);
			byte b2 = (byte) ((temperature >> 8) & 0xFF);
			localObject.forceSetData(epc, new ObjectData(b2, b1));
		} catch (IOException e) {
			LOGGER.logp(Level.INFO, this.getClass().getName(), "main",
					"Get temperature data failed. Detail: " + e.getMessage());
		}
	}

	private static double getTempData(byte data0, byte data1) {
		int data = ((data0 << 1) + ((data1 & 0x80) >> 7));
		int sign = ((data0 >> 7) & 0x80);
		if (sign == 1) {
			data = ~data - 1;
		}
		return ((double) data / 2);
	}
}
