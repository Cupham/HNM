/**
 * Profile object for device
 */
package echowand.objects;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

import echowand.common.EPC;
import echowand.object.EchonetObjectException;
import echowand.object.RemoteObject;
import echowand.util.ConvertData;

/**
 * @author Cu Pham
 *
 */
public class EchonetProfileObject {

	/**
	 * Device name
	 */
	private String deviceName;
	/**
	 * Device IP
	 */
	private String deviceIP;
	/**
	 * Class group code
	 */
	protected byte groupCode;
	/**
	 * Class code
	 */
	protected byte classCode;
	/**
	 * Instance code
	 */
	protected byte instanceCode;

	/**
	 * EPC: 0x80 ON: 0x30, OFF: 0x31
	 */
	private boolean operationStatus;
	/**
	 * EPC: 0x81
	 */
	private String installLocation;
	/**
	 * EPC: 0x82 the release number of the corresponding Appendix with fixed 4
	 * bytes
	 */
	private String standardVersionInfo;
	/**
	 * EPC: 0x83 Unique identification number fixed 9 bytes
	 */
	private String identificationNumber;
	/**
	 * EPC: 0x84 Instantaneous power consumption of the device in watts Value
	 * between: 0x0000–0xFFFD(0–65533W)
	 */
	private short instantaneousPower;
	/**
	 * EPC: 0x85 Cumulative power consumption of the device in increments of
	 * 0.001kWh Value between: 0x00000000–0x3B9AC9FF (0–999,999.999kWh)
	 */
	private long cumulativePower;
	/**
	 * EPC: 0x86 Manufacturer-defined fault code
	 */
	private String manufactureerFaultCode;
	/**
	 * EPC: 0x87 Current limit setting Value betwee: 0x00–0x64 (=0–100%)
	 */
	private int currentLimitSetting;
	/**
	 * EPC: 0x88 whether a fault (e.g. a sensor trouble) has occurred or not.
	 * Fault occurred: 0x41, No fault has occurred: 0x42
	 */
	private boolean faultStatus;
	/**
	 * EPC: 0x89 Describes the fault
	 */
	private String faultDescription;
	/**
	 * EPC:0x8A 3-byte manufacturer code unsigned (Defined by the ECHONET
	 * Consortium)
	 */
	private String manufacturerCode;
	/**
	 * EPC: 0x8B 3-byte business facility code (Defined by each manufacturer)
	 */
	private String businessFacilityCode;
	/**
	 * EPC: 0x8C Identifies the product using ASCII code (Defined by each
	 * manufacturer)
	 */
	private String productCode;
	/**
	 * EPC: 0x8D Production number using ASCII code (Defined by each
	 * manufacturer)
	 */
	private String productNumber;
	/**
	 * EPC: 0x8E 4-byte production date code, YYMD format
	 */
	private Date productDate;
	/**
	 * EPC: 0x8F Device is operating in power-saving mode Operating in
	 * power-saving mode: 0x41, Operating in normal operation mode: 0x42
	 */
	private boolean powerSaving;
	/**
	 * EPC: 0x93 Whether remote control is through a public network or not Not
	 * through a public network: 0x41 Through a public network: 0x42
	 */
	private boolean throughPublicNetwork;
	/**
	 * EPC: 0x97 Current time (HH: MM format), 0x00–0x17: 0x00–0x3B (=0–23):
	 * (=0–59)
	 */
	private String currentTimeSetting;
	/**
	 * EPC: 0x98 Current date (YYYY: MM: DD format),1–0x270F : 1–0x0C : 1–0x1F
	 * (=1–9999) : (=1–12) : (=1–31)
	 */
	private Date currentDateSetting;
	/**
	 * EPC: 0x99 Power limit setting in watts 0x0000–0xFFFF(0–65535W)
	 */
	private short powerLimit;
	/**
	 * EPC: 0x9A Cumulative operating time
	 */
	private String cumulativeTime;

	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @param deviceName
	 *            the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @return the deviceIP
	 */
	public String getDeviceIP() {
		return deviceIP;
	}

	/**
	 * @param deviceIP
	 *            the deviceIP to set
	 */
	public void setDeviceIP(String deviceIP) {
		this.deviceIP = deviceIP;
	}

	/**
	 * @return the groupCode
	 */
	public byte getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode
	 *            the groupCode to set
	 */
	public void setGroupCode(byte groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the classCode
	 */
	public byte getClassCode() {
		return classCode;
	}

	/**
	 * @param classCode
	 *            the classCode to set
	 */
	public void setClassCode(byte classCode) {
		this.classCode = classCode;
	}

	/**
	 * @return the instanceCode
	 */
	public byte getInstanceCode() {
		return instanceCode;
	}

	/**
	 * @param instanceCode
	 *            the instanceCode to set
	 */
	public void setInstanceCode(byte instanceCode) {
		this.instanceCode = instanceCode;
	}

	/**
	 * @return the operationStatus
	 */
	public boolean isOperationStatus() {
		return operationStatus;
	}

	/**
	 * @param operationStatus
	 *            the operationStatus to set
	 */
	public void setOperationStatus(boolean operationStatus) {
		this.operationStatus = operationStatus;
	}

	/**
	 * @return the installLocation
	 */
	public String getInstallLocation() {
		return installLocation;
	}

	/**
	 * @param installLocation
	 *            the installLocation to set
	 */
	public void setInstallLocation(String installLocation) {
		this.installLocation = installLocation;
	}

	/**
	 * @return the standardVersionInfo
	 */
	public String getStandardVersionInfo() {
		return standardVersionInfo;
	}

	/**
	 * @param standardVersionInfo
	 *            the standardVersionInfo to set
	 */
	public void setStandardVersionInfo(String standardVersionInfo) {
		this.standardVersionInfo = standardVersionInfo;
	}

	/**
	 * @return the identificationNumber
	 */
	public String getIdentificationNumber() {
		return identificationNumber;
	}

	/**
	 * @param identificationNumber
	 *            the identificationNumber to set
	 */
	public void setIdentificationNumber(String identificationNumber) {
		this.identificationNumber = identificationNumber;
	}

	/**
	 * @return the instantaneousPower
	 */
	public short getInstantaneousPower() {
		return instantaneousPower;
	}

	/**
	 * @param instantaneousPower
	 *            the instantaneousPower to set
	 */
	public void setInstantaneousPower(short instantaneousPower) {
		this.instantaneousPower = instantaneousPower;
	}

	/**
	 * @return the cumulativePower
	 */
	public long getCumulativePower() {
		return cumulativePower;
	}

	/**
	 * @param cumulativePower
	 *            the cumulativePower to set
	 */
	public void setCumulativePower(long cumulativePower) {
		this.cumulativePower = cumulativePower;
	}

	/**
	 * @return the manufactureerFaultCode
	 */
	public String getManufactureerFaultCode() {
		return manufactureerFaultCode;
	}

	/**
	 * @param manufactureerFaultCode
	 *            the manufactureerFaultCode to set
	 */
	public void setManufactureerFaultCode(String manufactureerFaultCode) {
		this.manufactureerFaultCode = manufactureerFaultCode;
	}

	/**
	 * @return the currentLimitSetting
	 */
	public int getCurrentLimitSetting() {
		return currentLimitSetting;
	}

	/**
	 * @param currentLimitSetting
	 *            the currentLimitSetting to set
	 */
	public void setCurrentLimitSetting(int currentLimitSetting) {
		this.currentLimitSetting = currentLimitSetting;
	}

	/**
	 * @return the faultStatus
	 */
	public boolean isFaultStatus() {
		return faultStatus;
	}

	/**
	 * @param faultStatus
	 *            the faultStatus to set
	 */
	public void setFaultStatus(boolean faultStatus) {
		this.faultStatus = faultStatus;
	}

	/**
	 * @return the faultDescription
	 */
	public String getFaultDescription() {
		return faultDescription;
	}

	/**
	 * @param faultDescription
	 *            the faultDescription to set
	 */
	public void setFaultDescription(String faultDescription) {
		this.faultDescription = faultDescription;
	}

	/**
	 * @return the manufacturerCode
	 */
	public String getManufacturerCode() {
		return manufacturerCode;
	}

	/**
	 * @param manufacturerCode
	 *            the manufacturerCode to set
	 */
	public void setManufacturerCode(String manufacturerCode) {
		this.manufacturerCode = manufacturerCode;
	}

	/**
	 * @return the businessFacilityCode
	 */
	public String getBusinessFacilityCode() {
		return businessFacilityCode;
	}

	/**
	 * @param businessFacilityCode
	 *            the businessFacilityCode to set
	 */
	public void setBusinessFacilityCode(String businessFacilityCode) {
		this.businessFacilityCode = businessFacilityCode;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode
	 *            the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the productNumber
	 */
	public String getProductNumber() {
		return productNumber;
	}

	/**
	 * @param productNumber
	 *            the productNumber to set
	 */
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	/**
	 * @return the productDate
	 */
	public Date getProductDate() {
		return productDate;
	}

	/**
	 * @param productDate
	 *            the productDate to set
	 */
	public void setProductDate(Date productDate) {
		this.productDate = productDate;
	}

	/**
	 * @return the powerSaving
	 */
	public boolean isPowerSaving() {
		return powerSaving;
	}

	/**
	 * @param powerSaving
	 *            the powerSaving to set
	 */
	public void setPowerSaving(boolean powerSaving) {
		this.powerSaving = powerSaving;
	}

	/**
	 * @return the throughPublicNetwork
	 */
	public boolean isThroughPublicNetwork() {
		return throughPublicNetwork;
	}

	/**
	 * @param throughPublicNetwork
	 *            the throughPublicNetwork to set
	 */
	public void setThroughPublicNetwork(boolean throughPublicNetwork) {
		this.throughPublicNetwork = throughPublicNetwork;
	}

	/**
	 * @return the currentTimeSetting
	 */
	public String getCurrentTimeSetting() {
		return currentTimeSetting;
	}

	/**
	 * @param currentTimeSetting
	 *            the currentTimeSetting to set
	 */
	public void setCurrentTimeSetting(String currentTimeSetting) {
		this.currentTimeSetting = currentTimeSetting;
	}

	/**
	 * @return the currentDateSetting
	 */
	public Date getCurrentDateSetting() {
		return currentDateSetting;
	}

	/**
	 * @param currentDateSetting
	 *            the currentDateSetting to set
	 */
	public void setCurrentDateSetting(Date currentDateSetting) {
		this.currentDateSetting = currentDateSetting;
	}

	/**
	 * @return the powerLimit
	 */
	public short getPowerLimit() {
		return powerLimit;
	}

	/**
	 * @param powerLimit
	 *            the powerLimit to set
	 */
	public void setPowerLimit(short powerLimit) {
		this.powerLimit = powerLimit;
	}

	/**
	 * @return the cumulativeTime
	 */
	public String getCumulativeTime() {
		return cumulativeTime;
	}

	/**
	 * @param cumulativeTime
	 *            the cumulativeTime to set
	 */
	public void setCumulativeTime(String cumulativeTime) {
		this.cumulativeTime = cumulativeTime;
	}

	/**
	 * Constructor
	 */
	public EchonetProfileObject() {

	}

	/**
	 * Constructor
	 * 
	 * @param ip
	 * @param name
	 */
	public EchonetProfileObject(String ip, String name) {
		this.deviceIP = ip;
		this.deviceName = name;
	}

	/**
	 * Parse data to this profile from remote object
	 * 
	 * @param rObj
	 * @throws EchonetObjectException
	 */
	@SuppressWarnings("deprecation")
	public void ParseProfileObjectFromEPC(RemoteObject rObj) throws EchonetObjectException {

		this.groupCode = rObj.getEOJ().getClassGroupCode();
		this.classCode = rObj.getEOJ().getClassCode();
		this.instanceCode = rObj.getEOJ().getInstanceCode();
		if (rObj.contains(EPC.x80)) { // operation status
			if (ConvertData.dataToInteger(rObj.getData(EPC.x80)) == 48) {
				this.operationStatus = true; // device status is ON
			} else {
				this.operationStatus = false; // device status is OFF
			}
		}
		if (rObj.contains(EPC.x81)) { // install location
			String rsLocation = ConvertData.dataToInstallLocation(rObj.getData(EPC.x81));
			if (rsLocation == null) {
				rsLocation = "Can not find install location!";
			}
			this.installLocation = rsLocation;
		}
		if (rObj.contains(EPC.x82)) { // standard version
			this.standardVersionInfo = ConvertData.dataToVersion(rObj.getData(EPC.x82));
		}

		if (rObj.contains(EPC.x83)) { // identification number
			this.identificationNumber = ConvertData.dataToIdentifiCationNumber(rObj.getData(EPC.x83)) + "";
		}

		if (rObj.contains(EPC.x84)) { // measured instantaneous power
										// consumption
			this.instantaneousPower = ConvertData.dataToShort(rObj.getData(EPC.x84));
		}

		if (rObj.contains(EPC.x85)) { // Measured cumulative power consumption
			this.cumulativePower = ConvertData.dataToLong(rObj.getData(EPC.x85));
		}

		if (rObj.contains(EPC.x86)) { // Manufacturer's fault code
			this.manufactureerFaultCode = ConvertData.dataToFaultCode(rObj.getData(EPC.x86)) + "";
		}

		if (rObj.contains(EPC.x87)) { // Current limit setting
			this.currentLimitSetting = ConvertData.dataToInteger(rObj.getData(EPC.x87));
		}

		if (rObj.contains(EPC.x88)) { // Fault status
			this.faultStatus = (ConvertData.dataToInteger(rObj.getData(EPC.x87)) == 65) ? true : false;
		}

		if (rObj.contains(EPC.x89)) { // Fault description
			if (this.faultStatus) {
				this.faultDescription = ConvertData.getFaultDetail(rObj.getData(EPC.x89));
			} else {
				this.faultDescription = "No Fault";
			}
		}
		if (rObj.contains(EPC.x8A)) { // Manufacture code
			this.manufacturerCode = ConvertData.dataToString(rObj.getData(EPC.x8A)) + "";
		}

		if (rObj.contains(EPC.x8B)) { // Business facility code
			this.manufacturerCode = ConvertData.dataToString(rObj.getData(EPC.x8B)) + "";
		}

		if (rObj.contains(EPC.x8C)) { // product code
			this.productCode = ConvertData.dataToString(rObj.getData(EPC.x8C)) + "";
		}

		if (rObj.contains(EPC.x8D)) { // producttion number
			this.productNumber = ConvertData.dataToString(rObj.getData(EPC.x8D)) + "";
		}

		if (rObj.contains(EPC.x8E)) { // production date default 4bytes with
										// format YYMD
			this.productDate = ConvertData.dataDateTime(rObj.getData(EPC.x8E));
		}

		if (rObj.contains(EPC.x8F)) { // Power saving mode
			this.powerSaving = (ConvertData.dataToInteger(rObj.getData(EPC.x8F)) == 65) ? true : false;
		}

		if (rObj.contains(EPC.x93)) { // Remote control
			this.throughPublicNetwork = (ConvertData.dataToInteger(rObj.getData(EPC.x93)) == 65) ? true : false;
		}

		if (rObj.contains(EPC.x97)) { // current time 2bytes with format HH:MM
			byte timeArray[];
			timeArray = rObj.getData(EPC.x97).toBytes();
			int h = timeArray[0];
			int m = timeArray[1];
			this.currentTimeSetting = "" + ((h < 10) ? ("0" + h) : (h + "")) + ":" + ((m < 10) ? ("0" + m) : ("" + m));
		}

		if (rObj.contains(EPC.x98)) { // current time 4bytes with format
										// YYYY:MM:DD
			this.currentDateSetting = ConvertData.dataDateTime(rObj.getData(EPC.x98));
		}

		if (rObj.contains(EPC.x99)) { // power limit
			this.powerLimit = ConvertData.dataToShort(rObj.getData(EPC.x99));
		}

		if (rObj.contains(EPC.x9A)) { // Cumulative operating time 5bytes with
										// first byte: Unit
										// 4 bytes next: format
										// Second:Minute:Hour:Day
			byte timeArray[];
			timeArray = rObj.getData(EPC.x9A).toBytes();
			String unit = "";
			switch (timeArray[0]) {
			case (byte) 0x41:
				unit = "seconds";
				break;
			case (byte) 0x42:
				unit = "months";
				break;
			case (byte) 0x43:
				unit = "hours";
				break;
			case (byte) 0x44:
				unit = "days";
				break;
			default:
				unit = "seconds";
				break;
			}
			byte valueArray[] = new byte[4];
			valueArray[0] = timeArray[1];
			valueArray[1] = timeArray[2];
			valueArray[2] = timeArray[3];
			valueArray[3] = timeArray[4];
			int timeSpan = ConvertData.dataToInteger(valueArray);
			this.cumulativeTime = timeSpan + " " + unit;
		}
		
		return;
	}

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder rs = new StringBuilder();
		rs.append(" Device IP: " + this.deviceIP + ",");
		rs.append(" Device Name: " + this.deviceName + ",");
		rs.append(" Operation status: " + ((this.operationStatus) ? "ON" : "OFF") + ",");
		rs.append(" Installation location: " + this.installLocation + ",");
		rs.append(" Standard version information: " + this.standardVersionInfo + ",");
		rs.append(" Identify number: " + this.identificationNumber + ",");
		rs.append(" Measure instantaneous power consumption: " + this.instantaneousPower + "W,");
		rs.append(" Measured cumulative power consumption: " + (this.cumulativePower / 1000) + "kWh,");
		rs.append(" Manufacturer’s fault code: " + this.manufactureerFaultCode + ",");
		rs.append(" Current limit setting: " + this.currentLimitSetting + "%,");
		rs.append(" Fault status: " + ((this.faultStatus) ? "Fault occurred" : "No fault has occurred") + ",");
		rs.append(" Fault description: " + this.faultDescription + ",");
		rs.append(" Manufacturer code: " + this.manufacturerCode + ",");
		rs.append(" Business facility code: " + this.businessFacilityCode + ",");
		rs.append(" Product code: " + this.productCode + ",");
		rs.append(" Production number: " + this.productNumber + ",");
		rs.append(" Production date: "
				+ ((this.productDate != null) ? (new SimpleDateFormat("yyyy-MM-dd").format(this.productDate)) : "NULL")
				+ ",");
		rs.append(" Power-saving operation setting: "
				+ ((this.powerSaving) ? "Power saving mode" : "Normal operation mode") + ",");
		rs.append(" Remote control setting: "
				+ ((this.throughPublicNetwork) ? "Not through a public network" : "Through a public network") + ",");
		rs.append(" Current time setting: " + this.currentTimeSetting + ",");
		rs.append(" Current date setting: " + ((this.currentDateSetting != null)
				? (new SimpleDateFormat("yyyy-MM-dd").format(this.currentDateSetting)) : "NULL") + ",");
		rs.append(" Power limit setting: " + this.powerLimit + "W,");
		rs.append(" Cumulative operating time: " + this.cumulativeTime);
		return rs.toString();
	}

	private <T> boolean compareObject(T obj1, T obj2) {

		if (obj1 == null && obj2 == null)
			return true;
		else if (obj1 == null && obj2 != null)
			return false;
		else if (obj1 != null && obj2 == null)
			return false;
		else {
			if (obj1.getClass() != obj2.getClass()) {
				return false;
			} else {
				if (obj1.getClass().isPrimitive()) {
					if (obj1 == obj2)
						return true;
					else {
						return false;
					}
				} else {
					if ((obj1 instanceof Date)) {
						Date date1 = (Date) obj1;
						Date date2 = (Date) obj2;

						return (date1.getDate() == date2.getDate() && date1.getMonth() == date2.getMonth()
								&& date1.getYear() == date2.getYear());
					} else {
						return obj1.equals(obj2);
					}
				}
			}
		}
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;
		if (!(obj instanceof EchonetProfileObject))
			return false;
		if (obj == this)
			return true;
		EchonetProfileObject checkObj = (EchonetProfileObject) obj;
		if (!compareObject(this.deviceIP, checkObj.deviceIP))
			return false;
		if (!compareObject(this.deviceName, checkObj.deviceName))
			return false;
		if (!compareObject(this.groupCode, checkObj.groupCode))
			return false;
		if (!compareObject(this.classCode, checkObj.classCode))
			return false;
		if (!compareObject(this.instanceCode, checkObj.instanceCode))
			return false;
		if (!compareObject(this.installLocation, checkObj.installLocation))
			return false;
		if (!compareObject(this.standardVersionInfo, checkObj.standardVersionInfo))
			return false;
		if (!compareObject(this.identificationNumber, checkObj.identificationNumber))
			return false;
		if (!compareObject(this.instantaneousPower, checkObj.instantaneousPower))
			return false;
		if (!compareObject(this.cumulativePower, checkObj.cumulativePower))
			return false;
		if (!compareObject(this.manufactureerFaultCode, checkObj.manufactureerFaultCode))
			return false;
		if (!compareObject(this.currentLimitSetting, checkObj.currentLimitSetting))
			return false;
		if (!compareObject(this.faultStatus, checkObj.faultStatus))
			return false;
		if (!compareObject(this.faultDescription, checkObj.faultDescription))
			return false;
		if (!compareObject(this.manufacturerCode, checkObj.manufacturerCode))
			return false;
		if (!compareObject(this.businessFacilityCode, checkObj.businessFacilityCode))
			return false;
		if (!compareObject(this.productCode, checkObj.productCode))
			return false;
		if (!compareObject(this.productNumber, checkObj.productNumber))
			return false;
		if (!compareObject(this.productDate, checkObj.productDate))
			return false;
		if (!compareObject(this.powerSaving, checkObj.powerSaving))
			return false;
		if (!compareObject(this.throughPublicNetwork, checkObj.throughPublicNetwork))
			return false;
		if (!compareObject(this.currentDateSetting, checkObj.currentDateSetting))
			return false;
		/*if (!compareObject(this.currentTimeSetting, checkObj.currentTimeSetting))
			return false;*/
		if (!compareObject(this.powerLimit, checkObj.powerLimit))
			return false;
		if (!compareObject(this.cumulativeTime, checkObj.cumulativeTime))
			return false;
		return true;
	}
}
