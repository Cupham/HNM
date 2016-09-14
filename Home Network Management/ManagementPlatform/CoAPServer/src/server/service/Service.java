package server.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import echowand.objects.HumidityDeviceObject;
import echowand.objects.TemperatureDeviceObject;
import server.config.CONSTANT;
import server.config.TAG_NAME;
import server.objects.AttributeObject;
import server.objects.DirectAttributeObject;
import server.objects.EchonetDeviceStatus;
import server.utils.ReturnMessage;
import server.utils.Utils;

/**
 * @author Cu Pham
 *
 */
public class Service {
	/**
	 * Check valid of AttributeObject
	 * 
	 * @param attribute
	 *            AttributeObject
	 * @param returnMessage
	 * @return ReturnMessage
	 * @see ReturnMessage
	 */
	public static ReturnMessage checkAttribute(AttributeObject attribute) {
		String returnMessage = null;
		try {
			// ip must be not null
			if (attribute.getIp() == null || "".equals(attribute.getIp().trim())) {
				returnMessage = "IP is null or empty.";
				return new ReturnMessage(false, returnMessage);
			}

			// ip must match pattern X.X.X.X
			String ipRegex = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
			if (!attribute.getIp().matches(ipRegex)) {
				returnMessage = "IP doesn't match X.X.X.X";
				return new ReturnMessage(false, returnMessage);
			}

			// attribute value must be not null
			if (attribute.getValue() == null || "".equals(attribute.getValue().trim())) {
				returnMessage = "Attribute value is null or empty.";
				return new ReturnMessage(false, returnMessage);
			}

			// define which groupCode
			switch (attribute.getGroupCode()) {
			// sensor
			case 0x00:
				// define which classCode
				switch (attribute.getClassCode()) {
				// led sensor
				case 0x00:
					// define which property
					switch (attribute.getEpc()) {
					case (byte) 0x80:
						return new ReturnMessage(true, returnMessage);
					default:
						returnMessage = "Attribute is not supported right now.";
						return new ReturnMessage(false, returnMessage);
					}
				// temperature sensor
				case 0x11:
					// define which property
					switch (attribute.getEpc()) {
					case (byte) 0x80:
						return new ReturnMessage(true, returnMessage);
					default:
						returnMessage = "Attribute is not supported right now.";
						return new ReturnMessage(false, returnMessage);
					}
				// humidity sensor
				case 0x12:
					// define which property
					switch (attribute.getEpc()) {
					case (byte) 0x80:
						return new ReturnMessage(true, returnMessage);
					default:
						returnMessage = "Attribute is not supported right now.";
						return new ReturnMessage(false, returnMessage);
					}
				default:
					returnMessage = "Attribute is not supported right now.";
					return new ReturnMessage(false, returnMessage);
				}
				// profile object
			case 0x0e:
				// define which classCode
				switch (attribute.getClassCode()) {
				// node profile
				case (byte) 0xf0:
					// define which attribute
					switch (attribute.getEpc()) {
					// status
					case (byte) 0x80:
						return new ReturnMessage(true, returnMessage);
					// installation location
					case (byte) 0x81:
						// define which INSTALLATION_LOCATION
						switch (attribute.getValue().toLowerCase()) {
						case CONSTANT.Living_Room:
						case CONSTANT.Dining_Room:
						case CONSTANT.Kitchen:
						case CONSTANT.Bathroom:
						case CONSTANT.WashRoom_ChangingRoom:
							return new ReturnMessage(true, returnMessage);
						default:
							returnMessage = "Attribute value is not supported right now.";
							return new ReturnMessage(false, returnMessage);
						}
						// current limit setting
					case (byte) 0x87:
						int currentLimit = Integer.parseInt(attribute.getValue());
						if (currentLimit < 0 || currentLimit > 100) {
							returnMessage = "Current limit setting must be in range 0 - 100%.";
							return new ReturnMessage(false, returnMessage);
						}
						return new ReturnMessage(true, returnMessage);
					// power saving
					case (byte) 0x8f:
						return new ReturnMessage(true, returnMessage);
					// remote control/through public network
					case (byte) 0x93:
						return new ReturnMessage(true, returnMessage);
					// current time setting
					case (byte) 0x97:
						if (!attribute.getValue().matches("\\d{2}:\\d{2}")) {
							returnMessage = "Current time setting is not in format HH:MM.";
							return new ReturnMessage(false, returnMessage);
						}
						String timeValues[] = attribute.getValue().split(":");
						// parse time value
						int h = Integer.parseInt(timeValues[0]);
						int m = Integer.parseInt(timeValues[1]);
						// check hour range
						// hour range is 0-23
						if (h < 0 || h > 23) {
							returnMessage = "Hour in Current time setting must be in range 0-23.";
							return new ReturnMessage(false, returnMessage);
						}
						// check minute range
						// minute range is 0-59
						if (m < 0 || m > 59) {
							returnMessage = "Minute in Current time setting must be in range 0-59.";
							return new ReturnMessage(false, returnMessage);
						}

						return new ReturnMessage(true, returnMessage);
					// current date setting
					case (byte) 0x98:
						if (!attribute.getValue().matches("\\d{4}:\\d{2}:\\d{2}")) {
							returnMessage = "Current date setting is not in format YYYY:MM:DD.";
							return new ReturnMessage(false, returnMessage);
						}
						String dateValues[] = attribute.getValue().split(":");
						// parse date value
						int year = Integer.parseInt(dateValues[0]);
						int month = Integer.parseInt(dateValues[1]);
						int day = Integer.parseInt(dateValues[2]);
						// check date valid
						if (!Utils.isValidDate(year, month, day)) {
							returnMessage = "Current date setting is not valid.";
							return new ReturnMessage(false, returnMessage);
						}
						return new ReturnMessage(true, returnMessage);
					// power limit setting
					case (byte) 0x99:
						// check value
						int powerLimit = Integer.parseInt(attribute.getValue());
						if (powerLimit < 0 || powerLimit > 65535) {
							returnMessage = "Power limit setting must be in range 0-65535.";
							return new ReturnMessage(false, returnMessage);
						}
						return new ReturnMessage(true, returnMessage);
					default:
						returnMessage = "Attribute is not supported right now.";
						return new ReturnMessage(false, returnMessage);
					}
				default:
					returnMessage = "Attribute is not supported right now.";
					return new ReturnMessage(false, returnMessage);
				}
			default:
				returnMessage = "Attribute is not supported right now.";
				return new ReturnMessage(false, returnMessage);
			}
		} catch (Exception ex) {
			returnMessage = ex.getMessage();
			return new ReturnMessage(false, returnMessage);
		}
	}

	public static ReturnMessage checkDirectAttribute(DirectAttributeObject attribute) {
		String returnMessage = null;
		try {
			// ip must be not null
			if (attribute.getIp() == null || "".equals(attribute.getIp().trim())) {
				returnMessage = "IP is null or empty.";
				return new ReturnMessage(false, returnMessage);
			}
			// ip must match pattern X.X.X.X
			String ipRegex = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$";
			if (!attribute.getIp().matches(ipRegex)) {
				returnMessage = "IP doesn't match X.X.X.X";
				return new ReturnMessage(false, returnMessage);
			}
			// attribute value must not be null
			if (attribute.getValue() == null || "".equals(attribute.getValue().trim())) {
				returnMessage = "Attribute value is null or empty.";
				return new ReturnMessage(false, returnMessage);
			}
			// attribute name must not be null
			if (attribute.getAttributeName() == null || "".equals(attribute.getAttributeName().trim())) {
				returnMessage = "Attribute name is null or empty.";
				return new ReturnMessage(false, returnMessage);
			}

			// check valid of attribute name
			switch (attribute.getAttributeName()) {
			case TAG_NAME.OPERATION_STATUS:
				return new ReturnMessage(true, returnMessage);
			case TAG_NAME.INSTALLATION_LOCATION:
				// define which INSTALLATION_LOCATION
				switch (attribute.getValue().toLowerCase()) {
				case CONSTANT.Living_Room:
				case CONSTANT.Dining_Room:
				case CONSTANT.Kitchen:
				case CONSTANT.Bathroom:
				case CONSTANT.WashRoom_ChangingRoom:
					return new ReturnMessage(true, returnMessage);
				default:
					returnMessage = "Attribute value is not supported right now.";
					return new ReturnMessage(false, returnMessage);
				}
			case TAG_NAME.REMOTE_CONTROL_SETTING:
				return new ReturnMessage(true, returnMessage);
			case TAG_NAME.CURRENT_TIME_SETTING:
				if (!attribute.getValue().matches("\\d{2}:\\d{2}")) {
					returnMessage = "Current time setting is not in format HH:MM.";
					return new ReturnMessage(false, returnMessage);
				}
				String timeValues[] = attribute.getValue().split(":");
				// parse time value
				int h = Integer.parseInt(timeValues[0]);
				int m = Integer.parseInt(timeValues[1]);
				// check hour range
				// hour range is 0-23
				if (h < 0 || h > 23) {
					returnMessage = "Hour in Current time setting must be in range 0-23.";
					return new ReturnMessage(false, returnMessage);
				}
				// check minute range
				// minute range is 0-59
				if (m < 0 || m > 59) {
					returnMessage = "Minute in Current time setting must be in range 0-59.";
					return new ReturnMessage(false, returnMessage);
				}

				return new ReturnMessage(true, returnMessage);
			case TAG_NAME.CURRENT_DATE_SETTING:
				if (!attribute.getValue().matches("\\d{4}:\\d{2}:\\d{2}")) {
					returnMessage = "Current date setting is not in format YYYY:MM:DD.";
					return new ReturnMessage(false, returnMessage);
				}
				String dateValues[] = attribute.getValue().split(":");
				// parse date value
				int year = Integer.parseInt(dateValues[0]);
				int month = Integer.parseInt(dateValues[1]);
				int day = Integer.parseInt(dateValues[2]);
				// check date valid
				if (!Utils.isValidDate(year, month, day)) {
					returnMessage = "Current date setting is not valid.";
					return new ReturnMessage(false, returnMessage);
				}
				return new ReturnMessage(true, returnMessage);
			case TAG_NAME.POWER_LIMIT_SETTING:
				// check value
				int powerLimit = Integer.parseInt(attribute.getValue());
				if (powerLimit < 0 || powerLimit > 65535) {
					returnMessage = "Power limit setting must be in range 0-65535.";
					return new ReturnMessage(false, returnMessage);
				}
				return new ReturnMessage(true, returnMessage);
			case TAG_NAME.CURRENT_LIMIT_SETTING:
				int currentLimit = Integer.parseInt(attribute.getValue());
				if (currentLimit < 0 || currentLimit > 100) {
					returnMessage = "Current limit setting must be in range 0 - 100%.";
					return new ReturnMessage(false, returnMessage);
				}
				return new ReturnMessage(true, returnMessage);
			case TAG_NAME.POWER_SAVING_OPERATION_SETTING:
				return new ReturnMessage(true, returnMessage);
			default:
				returnMessage = "Attribute is not supported right now.";
				return new ReturnMessage(false, returnMessage);
			}
		} catch (Exception ex) {
			return new ReturnMessage(false, ex.getMessage());
		}
	}

	/**
	 * get current time in time zone UTC+07
	 * 
	 * @return date
	 */
	public static Date getCurrentTime() {
		// get calendar
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(CONSTANT.VN_TIME_ZONE));
		return calendar.getTime();
	}

	/**
	 * convert list EchonetDevicesStatus to XML
	 * 
	 * @param devicesStatus
	 *            list EchonetDeviceStatus
	 * @return list EchonetDeviceStatus in xml format
	 */
	public static String convertListEchonetDevicesStatusToXML(List<EchonetDeviceStatus> devicesStatus) {
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		xml.append("\n<HomeNetwork>");
		for (EchonetDeviceStatus deviceStatus : devicesStatus)
			xml.append(deviceStatus.toXML());
		xml.append("\n</HomeNetwork>");
		return xml.toString();
	}

	/**
	 * Convert TemperatureDeviceObject to UPnP XML standard
	 * 
	 * @param te
	 *            TemperatureDeviceObject
	 * @return string
	 */
	public static String convertTemperatureSensorToUPnPXML(TemperatureDeviceObject te) {
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("\n<DataRecords xmlns=\"urn:schemas-upnp-org:ds:drecs\""
				+ "\n\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ "\n\txsi:schemaLocation=\"urn:schemas-upnp-org:ds:drecs"
				+ "\n\thttp://www.upnp.org/schemas/ds/drecs-v1.xsd\">");
		xml.append("\n\t<datarecord>");

		xml.append("\n\t\t<field name=\"GroupCode\">" + te.getGroupCode() + "</field>");
		xml.append("\n\t\t<field name=\"ClassCode\">" + te.getClassCode() + "</field>");
		xml.append("\n\t\t<field name=\"InstanceCode\">" + te.getInstanceCode() + "</field>");
		xml.append("\n\t\t<field name=\"OperationStatus\">" + te.isOperationStatus() + "</field>");
		xml.append("\n\t\t<field name=\"Temperature\">" + te.getTemperature() + "</field>");

		xml.append("\n\t</datarecord>");
		xml.append("\n</Datarecords>");
		return xml.toString();
	}

	/**
	 * Convert HumidityDeviceObject to UPnP XML standard
	 * 
	 * @param hu
	 *            HumidityDeviceObject
	 * @return string
	 */
	public static String convertHmiditySensorToUPnPXML(HumidityDeviceObject hu) {
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("\n<DataRecords xmlns=\"urn:schemas-upnp-org:ds:drecs\""
				+ "\n\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ "\n\txsi:schemaLocation=\"urn:schemas-upnp-org:ds:drecs"
				+ "\n\thttp://www.upnp.org/schemas/ds/drecs-v1.xsd\">");
		xml.append("\n\t<datarecord>");

		xml.append("\n\t\t<field name=\"GroupCode\">" + hu.getGroupCode() + "</field>");
		xml.append("\n\t\t<field name=\"ClassCode\">" + hu.getClassCode() + "</field>");
		xml.append("\n\t\t<field name=\"InstanceCode\">" + hu.getInstanceCode() + "</field>");
		xml.append("\n\t\t<field name=\"OperationStatus\">" + hu.isOperationStatus() + "</field>");
		xml.append("\n\t\t<field name=\"Humidity\">" + hu.getHumidity() + "</field>");

		xml.append("\n\t</datarecord>");
		xml.append("\n</Datarecords>");
		return xml.toString();
	}

}
