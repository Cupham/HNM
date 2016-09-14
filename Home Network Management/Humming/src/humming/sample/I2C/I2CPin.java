/**
 * 
 */
package humming.sample.I2C;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * @author Cu Pham
 *
 */
public abstract class I2CPin {
	
	private static final Logger LOGGER = Logger.getLogger(I2CPin.class.getName());
    private static final String CLASS_NAME = I2CPin.class.getName();
    
    private String exportFile = "/sys/class/i2c/export";
    private String unexportFile = "/sys/class/i2c/unexport";
    private String baseDirectoryTemplate = "/sys/class/i2c/i2c%d";
    private String valueFileTemplate = "/sys/class/i2c/i2c%d/value";
    private String directionFileTemplate = "/sys/class/i2c/i2c%d/direction";
    
    private int address;
    protected I2CDevice device;
    private boolean negative = false;
    
    private String groupName = null;
    private boolean groupReadable = false;
    private boolean groupWritable = false;
    private boolean otherReadable = false;
    private boolean otherWritable = false;
    
    private boolean useSudo = false;
    //Pin nPin = RaspiPin.getPinByAddress(01);
	//GpioController gpioController = GpioFactory.getInstance();
	//GpioPinDigitalOutput digitalOutput = gpioController.provisionDigitalOutputPin(nPin, "My Output", PinState.HIGH);
    
    private String getBaseDirectory() {
        LOGGER.entering(CLASS_NAME, "getBaseDirectory");
        
        String result = String.format(baseDirectoryTemplate, address);
        
        LOGGER.exiting(CLASS_NAME, "getBaseDirectory", result);
        return result;
        
    }
    
    public String getValueFile() {
        LOGGER.entering(CLASS_NAME, "getValueFile");
        
        String result = String.format(valueFileTemplate, address);
        
        LOGGER.exiting(CLASS_NAME, "getValueFile", result);
        return result;
    }
    
    public String getDirectionFile() {
        LOGGER.entering(CLASS_NAME, "getDirectionFile");
        
        String result = String.format(directionFileTemplate, address);
        
        LOGGER.exiting(CLASS_NAME, "getDirectionFile", result);
        return result;
    }
    
    private boolean write(String filename, int num) {
        LOGGER.entering(CLASS_NAME, "write", new Object[]{filename, num});
        
        boolean result = write(filename, String.format("%d", num));
        
        LOGGER.exiting(CLASS_NAME, "write", result);
        return result;
    }
    
    private boolean write(String filename, String value) {
        LOGGER.entering(CLASS_NAME, "write", new Object[]{filename, value});
        
        try {
            FileWriter writer = new FileWriter(filename);
            writer.append(value);
            writer.close();
            LOGGER.exiting(CLASS_NAME, "write", true);
            return true;
        } catch (IOException ex) {
            LOGGER.exiting(CLASS_NAME, "write", false);
            return false;
        }
    }
    
    public void setNegative(boolean negative) {
        LOGGER.entering(CLASS_NAME, "setNegative", negative);
        
        this.negative = negative;
        
        LOGGER.exiting(CLASS_NAME, "setNegative");
    }
    
    public boolean isNegative() {
        LOGGER.entering(CLASS_NAME, "isNegative");
        LOGGER.exiting(CLASS_NAME, "isNegative", negative);
        return negative;
    }
    
    private int convertLogic(int value) {
        LOGGER.entering(CLASS_NAME, "convertLogic", value);
        
        int newValue = value;
        
        if (negative) {
            switch (value) {
                case 0:
                    newValue = 1;
                    break;
                case 1:
                    newValue = 0;
                    break;
            }
        }
        
        LOGGER.exiting(CLASS_NAME, "convertLogic", newValue);
        return newValue;
    }
    
    public boolean isExported() {
        LOGGER.entering(CLASS_NAME, "isExported");
        
        boolean result = Files.exists(new File(getBaseDirectory()).toPath());
        
        LOGGER.exiting(CLASS_NAME, "isExported", result);
        return result;
    }
    
    private String joinStrings(String delimiter, String... strings) {
        if (strings.length == 0) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder(strings[0]);
        
        for (int i=1; i<strings.length; i++) {
            builder.append(delimiter).append(strings[i]);
        }
        
        return builder.toString();
    }
    
    private int chgrp(String filename, String groupName) {
        int result = -1;
        
        String[] com;
        
        if (useSudo) {
            com = new String[]{"sudo", "chgrp", groupName, filename};
        } else {
            com = new String[]{"chgrp", groupName, filename};
        }
        
        String comStr = joinStrings(" ", com);
            
        try {
            Process proc = Runtime.getRuntime().exec(com);
            result = proc.waitFor();
            
            if (result != 0) {
                LOGGER.logp(Level.WARNING, CLASS_NAME, "chgrp", "Failed: " + comStr + ": " + result);
            }
        } catch (IOException ex) {
            LOGGER.logp(Level.WARNING, CLASS_NAME, "chgrp", "Failed: " + comStr + ": " + filename, ex);
        } catch (InterruptedException ex) {
            LOGGER.logp(Level.WARNING, CLASS_NAME, "chgrp", "Failed: " + comStr + ": " + filename, ex);
        }
        
        return result;
    }
    
    private int chmod(String filename, String mode) {
        int result = -1;
        
        String[] com;
        
        if (useSudo) {
            com = new String[]{"sudo", "chmod", mode, filename};
        } else {
            com = new String[]{"chmod", mode, filename};
        }
        
        String comStr = joinStrings(" ", com);
        
        try {
            Process proc = Runtime.getRuntime().exec(com);
            result = proc.waitFor();
            
            if (result != 0) {
                LOGGER.logp(Level.WARNING, CLASS_NAME, "chmod", "Failed: " + comStr + ": " + result);
            }
        } catch (IOException ex) {
            LOGGER.logp(Level.WARNING, CLASS_NAME, "chmod", "Failed: " + comStr, ex);
        } catch (InterruptedException ex) {
            LOGGER.logp(Level.WARNING, CLASS_NAME, "chmod", "Failed: " + comStr, ex);
        }
        
        return -1;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroup(boolean readable, boolean writable) {
        groupReadable = readable;
        groupWritable = writable;
    }
    
    public void setGroupReadable(boolean readable) {
        groupReadable = readable;
    }
    
    public void setGroupWritable(boolean writable) {
        groupWritable = writable;
    }
    
    public boolean isGroupReadable() {
        return groupReadable;
    }
    
    public boolean isGroupWritable() {
        return groupWritable;
    }
    
    public void setOther(boolean readable, boolean writable) {
        otherReadable = readable;
        otherWritable = writable;
    }
    
    public void setOtherReadable(boolean readable) {
        otherReadable = readable;
    }
    
    public void setOtherWritable(boolean writable) {
        otherWritable = writable;
    }
    
    public boolean isOtherReadable() {
        return otherReadable;
    }
    
    public boolean isOtherWritable() {
        return otherWritable;
    }
    
    public void setUseSudo(boolean useSudo) {
        this.useSudo = useSudo;
    }
    
    public boolean isUseSudo() {
        return useSudo;
    }
    
    public boolean export() {
        LOGGER.entering(CLASS_NAME, "export");
        
        boolean result = false;
        
        if (write(exportFile, address)) {
            result = isExported();
        }
        
        if (groupName != null) {
            chgrp(getValueFile(), groupName);
            chgrp(getDirectionFile(), groupName);
        }
        
        if (groupReadable) {
            chmod(getValueFile(), "g+r");
            chmod(getDirectionFile(), "g+r");
        }
        
        if (groupWritable) {
            chmod(getValueFile(), "g+w");
            chmod(getDirectionFile(), "g+w");
        }
        
        if (otherReadable) {
            chmod(getValueFile(), "o+r");
            chmod(getDirectionFile(), "o+r");
        }
        
        if (otherWritable) {
            chmod(getValueFile(), "o+w");
            chmod(getDirectionFile(), "o+w");
        }
        
        LOGGER.exiting(CLASS_NAME, "export", result);
        return result;
    }
    
    public boolean unexport() {
        LOGGER.entering(CLASS_NAME, "unexport");
        
        boolean result = false;
        
        if (write(unexportFile, address)) {
            result = !isExported();
        }
        
        LOGGER.exiting(CLASS_NAME, "unexport", result);
        return result;
    }
    
    public I2CPin(int address, int bus) throws UnsupportedBusNumberException, IOException {
        LOGGER.entering(CLASS_NAME, "GPIOPin", address);
        
        this.address = address;
        I2CBus Bus = I2CFactory.getInstance(bus);
    	this.device = Bus.getDevice(this.address);
    	this.device.write(0x01, (byte) 0x60);
        LOGGER.exiting(CLASS_NAME, "GPIOPin");
    }
    
    public int getAddressNumber() {
        LOGGER.entering(CLASS_NAME, "getAddressNumber");
        
        int result = address;
        
        LOGGER.exiting(CLASS_NAME, "getPinNumber", result);
        return result;
    }
    
    public boolean setInput() {
        LOGGER.entering(CLASS_NAME, "setInput");
        
        boolean result = write(getDirectionFile(), "in");
        
        LOGGER.exiting(CLASS_NAME, "setInput", result);
        return result;
    }
    
    public boolean setOutput() {
        LOGGER.entering(CLASS_NAME, "setOutput");
        
        boolean result = write(getDirectionFile(), "out");
        
        LOGGER.exiting(CLASS_NAME, "setOutput", result);
        return result;
    }
    
    public abstract boolean setValue(byte[] data);
    public abstract int getValue(); 
    
}
