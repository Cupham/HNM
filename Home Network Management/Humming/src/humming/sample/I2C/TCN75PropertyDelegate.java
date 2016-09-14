/**
 * 
 */
package humming.sample.I2C;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import echowand.common.EPC;
import echowand.object.LocalObject;
import echowand.object.ObjectData;
import echowand.service.Core;
import echowand.service.PropertyDelegate;

/**
 * @author Cu Pham
 *
 */
public class TCN75PropertyDelegate extends PropertyDelegate {
	private static final Logger LOGGER = Logger.getLogger(TCN75PropertyDelegate.class.getName());
	private static final String CLASS_NAME = TCN75PropertyDelegate.class.getName();

	private TCN75_I2CPin pin = null;
	private Timer timer = null;
	private TimerTask updateTask = null;
	private int delay = 1000;
	private int interval = 1000;

	private int lastValue;


	public TCN75PropertyDelegate(EPC epc, boolean getEnabled, boolean setEnabled, boolean notifyEnabled) {
		super(epc, getEnabled, setEnabled, notifyEnabled);
		LOGGER.entering(CLASS_NAME, "GPIOPinPropertyDelegate",
				new Object[] { epc, getEnabled, setEnabled, notifyEnabled });

		LOGGER.exiting(CLASS_NAME, "GPIOPinPropertyDelegate");
	}

	public void setPinNumber(int address, int bus) throws UnsupportedBusNumberException, IOException {
		LOGGER.entering(CLASS_NAME, "setPinNumber", address);

		pin = new TCN75_I2CPin(address, bus);

		LOGGER.exiting(CLASS_NAME, "setPinNumber");
	}

	private void exportPin() {
		LOGGER.entering(CLASS_NAME, "exportPin");

		if (!pin.isExported()) {
			if (!pin.export()) {
				LOGGER.logp(Level.WARNING, CLASS_NAME, "exportPin", "Cannot export pin: " + pin.getAddressNumber());
				LOGGER.exiting(CLASS_NAME, "setPinNumber");
				return;
			}
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				LOGGER.logp(Level.INFO, CLASS_NAME, "exportPin", "Set pin \"in\": " + pin.getAddressNumber());
				if (!pin.setInput()) {
					LOGGER.logp(Level.WARNING, CLASS_NAME, "exportPin",
							"Cannot set pin \"in\": " + pin.getAddressNumber());
				}

				LOGGER.logp(Level.INFO, CLASS_NAME, "exportPin", "Unexport pin: " + pin.getAddressNumber());
				if (!pin.unexport()) {
					LOGGER.logp(Level.WARNING, CLASS_NAME, "exportPin",
							"Cannot unexport pin: " + pin.getAddressNumber());
				}
			}
		});

		if (isSetEnabled()) {
			if (!pin.setOutput()) {
				LOGGER.logp(Level.WARNING, CLASS_NAME, "exportPin",
						"Cannot set pin \"out\": " + pin.getAddressNumber());
			}
		} else {
			if (!pin.setInput()) {
				LOGGER.logp(Level.WARNING, CLASS_NAME, "exportPin", "Cannot set pin \"in\": " + pin.getAddressNumber());
			}
		}

		LOGGER.exiting(CLASS_NAME, "exportPin");
	}

	public void setDelay(int delay) {
		LOGGER.entering(CLASS_NAME, "setDelay", delay);

		this.delay = delay;

		LOGGER.exiting(CLASS_NAME, "setDelay");
	}

	public void setInterval(int interval) {
		LOGGER.entering(CLASS_NAME, "setInterval", interval);

		this.interval = interval;

		LOGGER.exiting(CLASS_NAME, "setInterval");
	}

	public void setNegative(boolean negative) {
		LOGGER.entering(CLASS_NAME, "setNegative", negative);

		pin.setNegative(negative);

		LOGGER.exiting(CLASS_NAME, "setNegative");
	}

	public void setGroupName(String name) {
		pin.setGroupName(name);
	}

	public void clearGroupName(String name) {
		pin.setGroupName(null);
	}

	public void setGroupReadable(boolean readable) {
		pin.setGroupReadable(readable);
	}

	public void setGroupWritable(boolean writable) {
		pin.setGroupWritable(writable);
	}

	public void setGroup(boolean readable, boolean writable) {
		pin.setGroup(readable, writable);
	}

	public void setOtherReadable(boolean readable) {
		pin.setOtherReadable(readable);
	}

	public void setOtherWritable(boolean writable) {
		pin.setOtherWritable(writable);
	}

	public void setOther(boolean readable, boolean writable) {
		pin.setOther(readable, writable);
	}

	public void setUseSudo(boolean useSudo) {
		pin.setUseSudo(useSudo);
	}

	public synchronized void notifyPinStatus(LocalObject object) {
		LOGGER.entering(CLASS_NAME, "notifyPinStatus", object);

		int value = pin.getValue();
		for (;;) {

			if (lastValue == value) {
				break;
			}
			System.out.println("PIN DATA CHANGE! NEW VALUE = " + value);
			LOGGER.logp(Level.INFO, CLASS_NAME, "notifyPinStatus",
					"notify LocalObject: " + object + ", I2C: " + pin.getAddressNumber() + ", value: " + value);

			EPC epc = TCN75PropertyDelegate.this.getEPC();
			ObjectData data = object.getData(epc);
			object.notifyDataChanged(epc, data, null);
			lastValue = value;

			value = pin.getValue();
		}

		LOGGER.exiting(CLASS_NAME, "notifyPinStatus");
	}

	@Override
	public void notifyCreation(final LocalObject object, Core core) {
		LOGGER.entering(CLASS_NAME, "notifyCreation", object);

		LOGGER.logp(Level.INFO, CLASS_NAME, "notifyCreation",
				"created LocalObject: " + object + ", I2C: " + pin.getAddressNumber());

		exportPin();

		if (isNotifyEnabled()) {
			timer = new Timer();
			lastValue = pin.getValue();

			updateTask = new TimerTask() {
				@Override
				public void run() {
					notifyPinStatus(object);
				}
			};

			LOGGER.logp(Level.INFO, CLASS_NAME, "notifyCreation",
					"start notification task: LocalObject: " + object + ", EPC:" + this.getEPC() + ", I2C: "
							+ pin.getAddressNumber() + ", delay: " + delay + ", interval: " + interval);

			timer.schedule(updateTask, delay, interval);
		}

		LOGGER.exiting(CLASS_NAME, "notifyCreation");
	}

	@Override
	public synchronized ObjectData getUserData(LocalObject object, EPC epc) {
		LOGGER.entering(CLASS_NAME, "getUserData", new Object[] { object, epc });

		int intValue = pin.getValue();
		byte[] dataArr = new byte[2];
		dataArr[1] = (byte) (intValue & 0xFF);
		dataArr[0] = (byte) ((intValue >> 8) & 0xFF);
		
		ObjectData data = new ObjectData(dataArr);

		LOGGER.exiting(CLASS_NAME, "getUserData", data);

		return data;
	}

	@Override
	public synchronized boolean setUserData(LocalObject object, EPC epc, ObjectData data) {
		/*LOGGER.entering(CLASS_NAME, "setUserData", new Object[] { object, epc, data });
		boolean result;
		if (isSetEnabled()) {
			result = pin.setValue(data.toBytes());
		}
		result = false;
		lastValue = pin.getValue();

		LOGGER.exiting(CLASS_NAME, "setUserData", result);
		return result;*/
		return false;	// temperature not set
	}
}
