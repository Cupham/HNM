package CoAP.Client.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import echowand.objects.EchonetProfileObject;

/**
 * @author Cu Pham
 *
 */
public class SaxHandler extends DefaultHandler{
	private ArrayList<EchonetProfileObject> profileList = null;
	private EchonetProfileObject profile = null;
	
	public EchonetProfileObject getProfileObject() {
		return profile;
	}
	public ArrayList<EchonetProfileObject> getProfileList() {
		return profileList;
	}
	boolean operationStt;
	boolean install;
	boolean version;
	boolean identification;
	boolean instantaneousPower;
	boolean cumulativePower;
	boolean manufacturerCode;
	boolean currentLimitSetting;
	boolean faultStatus;
	boolean faultDescription;
	boolean manufacturerFaultCode;
	boolean businessCode;
	boolean productCode;
	boolean productionNumber;
	boolean productionDate;
	boolean power_saving;
	boolean remote_control;
	boolean currentTime;
	boolean currentDate;
	boolean powerLimit;
	boolean cumulativeOperating;
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//System.out.println(qName);
		if(qName.equalsIgnoreCase("object")) {
			profile = new EchonetProfileObject();
			profile.setGroupCode((byte)0x0e);
			profile.setClassCode((byte)0xf0);
			profile.setInstanceCode((byte)0x01);
			profile.setDeviceName(attributes.getValue("ObjectName"));
			if(profileList == null) {
				profileList = new ArrayList<>();
			}
			}else if (qName.equalsIgnoreCase(TAG_NAME.OPERATION_STATUS)) {
				operationStt = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.INSTALLATION_LOCATION)) {
				install = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.STARNDARD_VERSION_INFORMATION)) {
				version = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.IDENTIFICATION_NUMBER)) {
				identification =true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.MEASRURED_INSTANTANEOUS_POWER_CONSUMPTION)) {
				instantaneousPower = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.MEASRURED_CUMULATIVE_POWER_CONSUMPTION)) {
				cumulativePower = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.MANUFACTURER_FAULT_CODE)) {
				manufacturerFaultCode = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.CURRENT_LIMIT_SETTING)) {
				currentLimitSetting =true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.FAULT_STATUS)) {
				faultStatus =true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.FAULT_DESCRIPTION)) {
				faultDescription = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.MANUFACTURER_CODE)) {
				manufacturerCode = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.BUSINESS_FACILITY_CODE)) {
				businessCode = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.PRODUCT_CODE)) {
				productCode =true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.PRODUCTION_NUMBER)) {
				productionNumber = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.PRODUCT_DATE)) {
				productionDate = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.POWER_SAVING_OPERATION_SETTING)) {
				power_saving = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.REMOTE_CONTROL_SETTING)) {
				remote_control =true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.CURRENT_TIME_SETTING)) {
				currentTime = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.CURRENT_DATE_SETTING)) {
				currentDate =true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.POWER_LIMIT_SETTING)) {
				powerLimit = true;
			}else if (qName.equalsIgnoreCase(TAG_NAME.CUMULATIVE_OPERATING_TIME)) {
				cumulativeOperating = true;
			}
		}
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if(qName.equalsIgnoreCase("object")) {
			//System.out.println("Object");
			profileList.add(profile);
		}
	}
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(operationStt) {
			profile.setOperationStatus(boolParser(new String(ch, start, length)));
			operationStt = false;			
		} if (install){
			profile.setInstallLocation(new String(ch, start, length));
			install = false;
		}
		if (version){
			profile.setStandardVersionInfo(new String(ch, start, length));
			version = false;
		}if (identification){
			profile.setIdentificationNumber(new String(ch, start, length));
			identification = false;
		}if (instantaneousPower){
			Short iPower = Short.parseShort(new String(ch, start, length));
			profile.setInstantaneousPower(iPower);
			instantaneousPower = false;
		}
		if (cumulativePower){
			Long cPower = Long.parseLong(new String(ch, start, length));
			profile.setCumulativePower(cPower);
			cumulativePower = false;
		}if (manufacturerCode){
			profile.setManufacturerCode(new String(ch, start, length));
			manufacturerCode = false;
		}
		if (currentLimitSetting){
			
			int cLimitSetting = Integer.parseInt(new String(ch, start, length));
			profile.setCurrentLimitSetting(cLimitSetting);
			currentLimitSetting = false;
		}if (faultStatus){
			profile.setFaultStatus(boolParser((new String(ch, start, length))));
			faultStatus = false;
		}
		if (faultDescription){
			//Short faulDes = Short.parseShort((new String(ch, start, length)));
			profile.setFaultDescription(new String(ch, start, length));
			faultDescription = false;
		}if (manufacturerFaultCode){
			profile.setManufactureerFaultCode(new String(ch, start, length));
			manufacturerFaultCode = false;
		}
		if (businessCode){
			profile.setBusinessFacilityCode(new String(ch, start, length));
			businessCode = false;
		}if (productCode){
			profile.setProductCode(new String(ch, start, length));
			productCode = false;
		}
		if (productionNumber){
			profile.setProductNumber(new String(ch, start, length));
			productionNumber = false;
		}
		if (productionDate){
			try {
				Date pDate = dateFormat(new String(ch, start, length));
				profile.setProductDate(pDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}		
			productionDate = false;
		}
		if (power_saving){
			profile.setPowerSaving(boolParser(new String(ch, start, length)));
			power_saving = false;
		}if (remote_control){
			profile.setThroughPublicNetwork(boolParser(new String(ch, start, length)));
			remote_control = false;
		}
		if (currentTime){
			profile.setCurrentTimeSetting(new String(ch, start, length));
			currentTime = false;
		}if (currentDate){
			try {
				Date cdate = dateFormat(new String(ch, start, length));
				profile.setProductDate(cdate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			currentDate = false;
		}
		if (powerLimit){
			int currentLimitSetting = Integer.parseInt(new String(ch, start, length));
			profile.setCurrentLimitSetting(currentLimitSetting);
			powerLimit = false;
		}if (cumulativeOperating){
			profile.setCumulativeTime(new String(ch, start, length));
			cumulativeOperating = false;
		}
		
	}
	public static Date dateFormat(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("YYYY:MM:dd");
		return df.parse(date);	
	}
	public static Boolean boolParser(String value) {
		if(value.equalsIgnoreCase("YES") || value.equalsIgnoreCase("ON") 
		|| value.equalsIgnoreCase("Through a public network") 
		|| value.equalsIgnoreCase("Has Fault")) {
			
			return true;
		} else {
			return false;
		}
	}

}
