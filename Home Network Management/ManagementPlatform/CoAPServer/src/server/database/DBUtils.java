package server.database;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import echowand.objects.EchonetDataObject;
import echowand.objects.EchonetDevice;
import echowand.objects.HumidityDeviceObject;
import echowand.objects.TemperatureDeviceObject;
import echowand.objects.EchonetDevice.DeviceType;
import server.config.CONSTANT;
import server.config.TAG_NAME;
import server.objects.AttributeObject;
import server.objects.DirectAttributeObject;
import server.objects.DirectDevice;
import server.objects.EchonetDeviceEx;
import server.objects.EchonetDeviceStatus;
import server.objects.LED_Sensor;
import server.utils.Utils;

/**
 * @author Cu Pham
 *
 */
public class DBUtils {
	// logger
	final static Logger logger = Logger.getLogger(DBUtils.class);

	/**
	 * Remove all devices data
	 * 
	 * @param db
	 *            mongodb
	 * @return true if remove success, else return false
	 */
	public static boolean removeAllDevices(DB db) {
		try {
			// get collection Device
			DBCollection collection = db.getCollection("Device");
			if (collection == null) {
				// can't access to db, return false
				return false;
			} else {
				// get DBCursor
				DBCursor cursor = collection.find();
				// if cursor is null then return false
				if (cursor == null)
					return false;
				// remove all documents
				while (cursor.hasNext()) {
					// remove each document to the end
					collection.remove(cursor.next());
				}

				// successfully remove all documents
				return true;
			}
		} catch (Exception ex) {
			logger.error(ex);
			return false;
		}
	}

	/**
	 * Store device to database
	 * 
	 * @param db
	 *            mongo database
	 * @param device
	 *            EchonetDevice
	 * @return true if put data to db success
	 */
	public static boolean storeDevice(DB db, EchonetDevice device) {
		// get collection Device
		DBCollection collection = db.getCollection("Device");
		if (collection == null) {
			// can't access to db, return false
			return false;
		}
		Gson gson = new Gson();
		try {
			// only focus on devices which have IP
			if (device != null && device.getProfile() != null && device.getProfile().getDeviceIP() != null
					&& !device.getProfile().getDeviceIP().trim().equals("")) {
				// parse EchonetDevice to json
				String json = gson.toJson(device);
				// parse json to DBObject
				DBObject dbObject = (DBObject) JSON.parse(json);
				// put primary key to dbObject
				dbObject.put("_id", device.getProfile().getDeviceIP());
				// check if dbObj exist in db
				DBObject existedObject = collection.findOne(dbObject.get("_id"));
				if (existedObject != null) {
					// dbObj exist in db, update it
					collection.update(existedObject, dbObject);
				} else {
					// dbObj isn't exist in db, insert it
					collection.insert(dbObject);
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			logger.error(ex);
			return false;
		}
	}

	/**
	 * Store device to database
	 * 
	 * @param db
	 *            mongo database
	 * @param device
	 *            DirectDevice
	 * @return true if put data to db success
	 */
	public static boolean storeDirectDevice(DB db, DirectDevice device) {
		// get collection Device
		DBCollection collection = db.getCollection("Device");
		if (collection == null) {
			// can't access to db, return false
			return false;
		}
		Gson gson = new Gson();
		try {
			// only focus on devices which have IP
			if (device != null && device.getProfile() != null && device.getProfile().getDeviceIP() != null
					&& !device.getProfile().getDeviceIP().trim().equals("")) {
				// parse DirectDevice to json
				String json = gson.toJson(device);
				// parse json to DBObject
				DBObject dbObject = (DBObject) JSON.parse(json);
				// put primary key to dbObject
				dbObject.put("_id", device.getProfile().getDeviceIP());
				// check if dbObj exist in db
				DBObject existedObject = collection.findOne(dbObject.get("_id"));
				if (existedObject != null) {
					// dbObj exist in db, update it
					collection.update(existedObject, dbObject);
				} else {
					// dbObj isn't exist in db, insert it
					collection.insert(dbObject);
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			logger.error(ex);
			return false;
		}
	}

	/**
	 * Store List EchonetDevice objects to database
	 * 
	 * @param db
	 *            mongo database
	 * @param listDevices
	 *            List EchonetDevice
	 * @return true if put data to db success
	 */
	public static boolean storeListDevices(DB db, List<EchonetDevice> listDevices) {
		// get collection Device
		DBCollection collection = db.getCollection("Device");
		if (collection == null) {
			// can't access to db, return false
			return false;
		}

		Gson gson = new Gson();
		// browse listDevices
		for (EchonetDevice eDevice : listDevices) {
			try {
				// only focus on devices which have IP
				if (eDevice != null && eDevice.getProfile() != null && eDevice.getProfile().getDeviceIP() != null
						&& !eDevice.getProfile().getDeviceIP().trim().equals("")) {
					// parse EchonetDevice to json
					String json = gson.toJson(eDevice);
					// parse json to DBObject
					DBObject dbObject = (DBObject) JSON.parse(json);
					// put primary key to dbObject
					dbObject.put("_id", eDevice.getProfile().getDeviceIP());
					// check if dbObj exist in db
					DBObject existedObject = collection.findOne(dbObject.get("_id"));
					if (existedObject != null) {
						// if eDevice is a gateway, update it to database
						if (eDevice.getDeviceType() == DeviceType.HomeGateway)
							collection.update(existedObject, dbObject);
						// else eDevice is not a gateway
						else {
							// if existedObject is not a gateway, update it
							if (!"HomeGateway".equals((String) existedObject.get("deviceType")))
								collection.update(existedObject, dbObject);
						}

					} else
						// dbObj isn't exist in db, insert it
						collection.insert(dbObject);
				}
			} catch (Exception ex) {
				logger.error(ex);
				return false;
			}
		}

		return true;
	}

	/**
	 * Get all devices from database
	 * 
	 * @param db
	 *            mongo database
	 * @return List EchonetDevice
	 * @see echowand.objects.EchonetDevice
	 */
	public static List<EchonetDevice> getAllDevices(DB db) {
		// declare a list devices
		List<EchonetDevice> listDevices = null;
		// connect to table EchonetDevice in mongodb
		DBCollection collection = db.getCollection("Device");
		// get DBCursor
		DBCursor cursor = collection.find();
		List<DBObject> listDBObjects = new ArrayList<DBObject>();
		while (cursor.hasNext()) {
			// add each DBObject to listDBObjects
			DBObject dbObject = cursor.next();
			listDBObjects.add(dbObject);
		}
		cursor.close();
		// Serialise listDBObjects to JSON
		String json = JSON.serialize(listDBObjects);
		// create a type to parse json to EchonetDeviceEx
		Type type = new TypeToken<ArrayList<EchonetDeviceEx>>() {
		}.getType();
		// create a gson
		Gson gson = new Gson();
		// parse json to ArrayList<EchonetDeviceEx>
		ArrayList<EchonetDeviceEx> listDeviceExs = gson.fromJson(json, type);
		// check if listDeviceExs is not null
		if (listDeviceExs != null) {
			// initialise listDevices
			listDevices = new ArrayList<EchonetDevice>();
			// browse listDeviceExs, convert to EchonetDevice
			for (EchonetDeviceEx deviceEx : listDeviceExs) {
				// convert EchonetDeviceEx to EchonetDevice
				EchonetDevice device = deviceEx.toEchonetDevice();
				// if convert success, device is not null then add device to //
				// listDevices
				if (device != null)
					listDevices.add(device);
			}
		}
		return listDevices;
	}

	/**
	 * Get all devices from database
	 * 
	 * @param db
	 *            mongo database
	 * @return List EchonetDevice in json format
	 */
	public static String getAllDevicesInJson(DB db) {
		// connect to table EchonetDevice in mongodb
		DBCollection collection = db.getCollection("Device");
		// get DBCursor
		DBCursor cursor = collection.find();
		List<DBObject> listDBObjects = new ArrayList<DBObject>();
		while (cursor.hasNext()) {
			// add each DBObject to listDBObjects
			DBObject dbObject = cursor.next();
			listDBObjects.add(dbObject);
		}
		cursor.close();
		// serialise list dbobject to json format
		return JSON.serialize(listDBObjects);
	}

	/**
	 * 
	 * @param db
	 *            mongo database
	 * @param deviceIP
	 *            device's IP
	 * @return EchonetDevice
	 * @see EchonetDevice
	 */
	public static EchonetDevice getDevice(DB db, String deviceIP) {
		// connect to table EchonetDevice in mongodb
		DBCollection collection = db.getCollection("Device");
		// find EchonetDevice
		DBObject dbObject = collection.findOne(deviceIP);
		if (dbObject == null)
			return null;

		// serialize dbObject to JSON
		String json = JSON.serialize(dbObject);
		if (json == null)
			return null;

		// create a gson
		Gson gson = new Gson();
		// parst json to EchonetDeviceEx
		EchonetDeviceEx deviceEx = gson.fromJson(json, EchonetDeviceEx.class);
		if (deviceEx == null)
			return null;

		// return EchonetDevice
		return deviceEx.toEchonetDevice();
	}

	/**
	 * Get EchonetDataObject based on deviceIP, groupCode, classCode,
	 * instanceCode
	 * 
	 * @param db
	 *            mongodb
	 * @param deviceIP
	 *            device's IP
	 * @param groupCode
	 *            data object's group code
	 * @param classCode
	 *            data object's class code
	 * @param instanceCode
	 *            data object's instance code
	 * @return EchonetDataObject
	 * @see EchonetDataObject
	 */
	public static EchonetDataObject getEchonetDataObject(DB db, String deviceIP, byte groupCode, byte classCode,
			byte instanceCode) {
		// connect to table EchonetDevice in mongodb
		DBCollection collection = db.getCollection("Device");
		// find EchonetDevice
		DBObject dbObject = collection.findOne(deviceIP);
		if (dbObject == null)
			return null;

		// get BasicDBList
		BasicDBList dbList = (BasicDBList) dbObject.get("eObjList");
		for (int i = 0; i < dbList.size(); i++) {
			BasicDBObject dbObj = (BasicDBObject) dbList.get(i);
			// check groupCode, classCode, instanceCode
			if (dbObj.getString("groupCode") != null && dbObj.getString("classCode") != null
					&& dbObj.getString("instanceCode") != null) {
				byte gCode = Byte.parseByte(dbObj.getString("groupCode"));
				byte cCode = Byte.parseByte(dbObj.getString("classCode"));
				byte iCode = Byte.parseByte(dbObj.getString("instanceCode"));
				if (groupCode == gCode && classCode == cCode && instanceCode == iCode) {
					switch (groupCode) {
					// List of Objects of Sensor-related Device Class Group
					case 0x00:
						switch (classCode) {
						// led sensor
						case 0x00:
							LED_Sensor ledSensor = new LED_Sensor();
							ledSensor.setGroupCode(groupCode);
							ledSensor.setClassCode(classCode);
							ledSensor.setInstanceCode(instanceCode);
							if (dbObj.getString("operationStatus") != null) {
								boolean operationStatus = dbObj.getBoolean("operationStatus");
								ledSensor.setOperationStatus(operationStatus);
							}
							if (dbObj.getString("ledON") != null) {
								boolean ledON = dbObj.getBoolean("ledON");
								ledSensor.setLedON(ledON);
							}
							return ledSensor;
						// Temperature sensor
						case 0x11:
							TemperatureDeviceObject temperatureObj = new TemperatureDeviceObject();
							temperatureObj.setGroupCode(groupCode);
							temperatureObj.setClassCode(classCode);
							temperatureObj.setInstanceCode(instanceCode);

							if (dbObj.getString("operationStatus") != null) {
								boolean operationStatus = dbObj.getBoolean("operationStatus");
								temperatureObj.setOperationStatus(operationStatus);
							}
							if (dbObj.getString("temperature") != null) {
								try {
									int temperature = dbObj.getInt("temperature");
									temperatureObj.setTemperature(temperature);
								} catch (NumberFormatException ex) {
									logger.error(ex);
								}
							}
							return temperatureObj;
						// Humidity sensor
						case 0x12:
							HumidityDeviceObject huminityObj = new HumidityDeviceObject();
							huminityObj.setGroupCode(groupCode);
							huminityObj.setClassCode(classCode);
							huminityObj.setInstanceCode(instanceCode);

							if (dbObj.getString("operationStatus") != null) {
								boolean operationStatus = dbObj.getBoolean("operationStatus");
								huminityObj.setOperationStatus(operationStatus);
							}
							if (dbObj.getString("humidity") != null) {
								try {
									double humidity = dbObj.getDouble("humidity");
									huminityObj.setHumidity(humidity);
								} catch (NumberFormatException ex) {
									logger.error(ex);
								}
							}
							return huminityObj;
						}
						break;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Check if AttributeObject is changed or not
	 * 
	 * @param db
	 *            mongodb
	 * @param attributeObj
	 *            AttributeObject
	 * @return -1: if attribute is not exist or error occur at runtime, 0: if
	 *         attribute is not changed, 1: if attribute is changed
	 */
	public static int checkAttribute(DB db, AttributeObject attributeObj) {
		// connect to table EchonetDevice in mongodb
		DBCollection collection = db.getCollection("Device");
		// find EchonetDevice
		BasicDBObject dbObject = (BasicDBObject) collection.findOne(attributeObj.getIp());
		// if dbObject is null, response
		if (dbObject == null)
			return -1;

		// define which groupCode
		switch (attributeObj.getGroupCode()) {
		// sensor
		case 0x00:
			// get BasicDBList
			BasicDBList dbList = (BasicDBList) dbObject.get("eObjList");
			for (int i = 0; i < dbList.size(); i++) {
				BasicDBObject dbObj = (BasicDBObject) dbList.get(i);
				// get group code, class code, instance code of
				// EchonetDataObject in database
				byte groupCode = Byte.parseByte(dbObj.getString("groupCode"));
				byte classCode = Byte.parseByte(dbObj.getString("classCode"));
				byte instanceCode = Byte.parseByte(dbObj.getString("instanceCode"));
				// define exactly data object in database
				if (attributeObj.getGroupCode() == groupCode && attributeObj.getClassCode() == classCode
						&& attributeObj.getInstanceCode() == instanceCode) {
					// define which classCode
					switch (attributeObj.getClassCode()) {
					// led sensor
					case 0x00:
						// define which attribute by using epc
						switch (attributeObj.getEpc()) {
						// status
						case (byte) 0x80:
							// check if EchonetDataObject in database contains
							// attribute status or not
							// operationStatus is not exist, return -1
							if (dbObj.get("operationStatus") == null)
								return -1;
							else {
								// operation status in database
								boolean statusInDb = dbObj.getBoolean("operationStatus");
								boolean statusAtClient = Boolean.parseBoolean(attributeObj.getValue());
								// if status in db and status at client are
								// equal, return 0
								if (statusInDb == statusAtClient)
									return 0;
								else // if status in db and status at client are
										// not equal, return 1
									return 1;
							}
						}
						break;
					// temperature sensor
					case 0x11:
						// define which attribute by using epc
						switch (attributeObj.getEpc()) {
						// status
						case (byte) 0x80:
							// check if EchonetDataObject in database contains
							// attribute status or not
							// operationStatus is not exist, return -1
							if (dbObj.get("operationStatus") == null)
								return -1;
							else {
								// operation status in database
								boolean statusInDb = dbObj.getBoolean("operationStatus");
								boolean statusAtClient = Boolean.parseBoolean(attributeObj.getValue());
								// if status in db and status at client are
								// equal, return 0
								if (statusInDb == statusAtClient)
									return 0;
								else // if status in db and status at client are
										// not equal, return 1
									return 1;
							}
						}
						break;
					// humidity sensor
					case 0x12:
						// define which attribute by using epc
						switch (attributeObj.getEpc()) {
						// status
						case (byte) 0x80:
							// check if EchonetDataObject in database contains
							// attribute status or not
							// operationStatus is not exist, return -1
							if (dbObj.get("operationStatus") == null)
								return -1;
							else {
								// operation status in database
								boolean statusInDb = dbObj.getBoolean("operationStatus");
								boolean statusAtClient = Boolean.parseBoolean(attributeObj.getValue());
								// if status in db and status at client are
								// equal, return 0
								if (statusInDb == statusAtClient)
									return 0;
								else // if status in db and status at client are
										// not equal, return 1
									return 1;
							}
						}
						break;
					} // end switch classCode

					// return -1 if attribute is not exist
					return -1;
				} // end define data object in database
			} // end for loop
				// return -1 cause EchonetDataObject is not exist
			return -1;

		// profile object
		case 0x0e:
			// get profile object
			BasicDBObject profileObj = (BasicDBObject) dbObject.get("profile");
			// if profile object is null then return -1
			if (profileObj == null)
				return -1;

			// get group code, class code, instance code of EchonetProfileObject
			// in database
			byte groupCode = Byte.parseByte(profileObj.getString("groupCode"));
			byte classCode = Byte.parseByte(profileObj.getString("classCode"));
			byte instanceCode = Byte.parseByte(profileObj.getString("instanceCode"));
			// define exactly data object in database
			if (attributeObj.getGroupCode() == groupCode && attributeObj.getClassCode() == classCode
					&& attributeObj.getInstanceCode() == instanceCode) {
				// define which classCode
				switch (attributeObj.getClassCode()) {
				// node profile
				case (byte) 0xf0:
					// define which attribute
					switch (attributeObj.getEpc()) {
					// status
					case (byte) 0x80:
						// check if EchonetProfileObject in database contains
						// attribute status or not
						// operationStatus is not exist, return -1
						if (profileObj.get("operationStatus") == null)
							return -1;
						else {
							// operation status in database
							boolean statusInDb = profileObj.getBoolean("operationStatus");
							boolean statusAtClient = Boolean.parseBoolean(attributeObj.getValue());
							// if status in db and status at client are
							// equal, return 0
							if (statusInDb == statusAtClient)
								return 0;
							else // if status in db and status at client are
									// not equal, return 1
								return 1;
						}
						// installation location
					case (byte) 0x81:
						// check if EchonetProfileObject in database contains
						// attribute installLocation or not
						// installLocation is not exist, return -1
						if (profileObj.get("installLocation") == null)
							return -1;
						else {
							// define which INSTALLATION_LOCATION
							boolean isSupported = false;
							switch (attributeObj.getValue().toLowerCase()) {
							case CONSTANT.Living_Room:
							case CONSTANT.Dining_Room:
							case CONSTANT.Kitchen:
							case CONSTANT.Bathroom:
							case CONSTANT.WashRoom_ChangingRoom:
								isSupported = true;
								break;
							}
							// installation location is not in correct format
							if (!isSupported)
								return -1;
							else {
								// compare two value
								String locationInDb = (String) profileObj.get("installLocation");
								// if two value is equal, return 0
								if (attributeObj.getValue().toLowerCase().equals(locationInDb.toLowerCase()))
									return 0;
								// otherwise, return 1
								else
									return 1;
							}
						}
						// current limit
					case (byte) 0x87:
						// currentLimitSetting is not exist, return -1
						if (profileObj.get("currentLimitSetting") == null)
							return -1;
						int currentLimitInDb = profileObj.getInt("currentLimitSetting");
						int currentLimitAtClient = Integer.parseInt(attributeObj.getValue());
						// compare two value
						if (currentLimitInDb == currentLimitAtClient)
							return 0;
						else
							return 1;
						// power saving
					case (byte) 0x8f:
						// check if EchonetProfileObject in database contains
						// attribute powerSaving or not
						// powerSaving is not exist, return -1
						if (profileObj.get("powerSaving") == null)
							return -1;
						else {
							;
							// powerSaving in database
							boolean powerSavingInDb = profileObj.getBoolean("powerSaving");
							boolean powerSavingAtClient = Boolean.parseBoolean(attributeObj.getValue());
							// if powerSaving in db and powerSaving at client
							// are
							// equal, return 0
							if (powerSavingInDb == powerSavingAtClient)
								return 0;
							else // if powerSaving in db and powerSaving at
									// client are not equal, return 1
								return 1;
						}
						// remote control/through public network
					case (byte) 0x93:
						// check if EchonetProfileObject in database contains
						// attribute throughPublicNetwork or not
						// throughPublicNetwork is not exist, return -1
						if (profileObj.get("throughPublicNetwork") == null)
							return -1;
						else {
							// throughPublicNetwork in database
							boolean throughPublicInDb = profileObj.getBoolean("throughPublicNetwork");
							;
							// throughPublicNetwork at client
							boolean throughPublicAtClient = Boolean.parseBoolean(attributeObj.getValue());
							// if throughPublicNetwork in db and
							// throughPublicNetwork at client are equal, return
							// 0
							if (throughPublicInDb == throughPublicAtClient)
								return 0;
							else // if throughPublicNetwork in db and
									// throughPublicNetwork at client are not
									// equal, return 1
								return 1;
						}
						// current time setting
					case (byte) 0x97:
						// if currentTimeSetting is not exist, return -1;
						if (profileObj.get("currentTimeSetting") == null)
							return -1;
						else {
							if (!attributeObj.getValue().matches("\\d{2}:\\d{2}"))
								return -1;
							String timeValues[] = attributeObj.getValue().split(":");
							// parse time value
							int h = Integer.parseInt(timeValues[0]);
							int m = Integer.parseInt(timeValues[1]);
							// check hour range
							// hour range is 0-23
							if (h < 0 || h > 23)
								return -1;
							// check minute range
							// minute range is 0-59
							if (m < 0 || m > 59)
								return -1;
							String currentTimeInDb = profileObj.getString("currentTimeSetting");
							// two value is the same, return 0
							if (currentTimeInDb.equals(attributeObj.getValue()))
								return 0;
							else
								return 1;
						}
						// current date setting
					case (byte) 0x98:
						// check if EchonetProfileObject in database contains
						// attribute currentDateSetting or not
						// currentDateSetting is not exist, return -1
						if (profileObj.get("currentDateSetting") == null)
							return -1;
						else {
							if (!attributeObj.getValue().matches("\\d{4}:\\d{2}:\\d{2}"))
								return -1;
							String dateValues[] = attributeObj.getValue().split(":");
							// parse date value
							int year = Integer.parseInt(dateValues[0]);
							int month = Integer.parseInt(dateValues[1]);
							int day = Integer.parseInt(dateValues[2]);
							// check date valid
							if (!Utils.isValidDate(year, month, day))
								return -1;
							String currentDateInDb = profileObj.getString("currentDateSetting");
							if (attributeObj.getValue().equals(currentDateInDb))
								return 0;
							else
								return 1;
						}
						// power limit setting
					case (byte) 0x99:
						// check if EchonetProfileObject in database contains
						// attribute powerLimit or not
						if (profileObj.get("powerLimit") == null) // powerLimit
																	// is not
																	// exist,
																	// return -1
							return -1;
						else {
							int powerLimitInDb = profileObj.getInt("powerLimit");
							int powerLimitAtClient = Integer.parseInt(attributeObj.getValue());
							// compare two value
							if (powerLimitInDb == powerLimitAtClient)
								return 0;
							else
								return 1;
						}
					}
					break;
				}
			}
			break;
		}

		// return -1 if Attribute is not exist
		return -1;
	}

	/**
	 * Check if AttributeObject is changed or not
	 * 
	 * @param db
	 *            mongodb
	 * @param attributeObj
	 *            AttributeObject
	 * @return -1: if attribute is not exist or error occur at runtime, 0: if
	 *         attribute is not changed, 1: if attribute is changed and
	 *         attribute is updated to database
	 */
	public static int storeAttribute(DB db, AttributeObject attributeObj) {
		// connect to table EchonetDevice in mongodb
		DBCollection collection = db.getCollection("Device");
		// find EchonetDevice
		BasicDBObject dbObject = (BasicDBObject) collection.findOne(attributeObj.getIp());
		// if dbObject is null, response
		if (dbObject == null)
			return -1;

		// define which groupCode
		switch (attributeObj.getGroupCode()) {
		// sensor
		case 0x00:
			// get BasicDBList
			BasicDBList dbList = (BasicDBList) dbObject.get("eObjList");
			for (int i = 0; i < dbList.size(); i++) {
				BasicDBObject dbObj = (BasicDBObject) dbList.get(i);
				// get group code, class code, instance code of
				// EchonetProfileObject in database
				byte groupCode = Byte.parseByte(dbObj.getString("groupCode"));
				byte classCode = Byte.parseByte(dbObj.getString("classCode"));
				byte instanceCode = Byte.parseByte(dbObj.getString("instanceCode"));
				// define exactly data object in database
				if (attributeObj.getGroupCode() == groupCode && attributeObj.getClassCode() == classCode
						&& attributeObj.getInstanceCode() == instanceCode) {
					// define which classCode
					switch (attributeObj.getClassCode()) {
					// temperature sensor
					case 0x11:
						// define which attribute by using epc
						switch (attributeObj.getEpc()) {
						// status
						case (byte) 0x80:
							// check if EchonetDataObject in database contains
							// attribute status or not
							if (dbObj.get("operationStatus") == null) // operationStatus
																		// is
																		// not
																		// exist,
																		// return
																		// -1
								return -1;
							else {
								// operation status in database
								boolean statusInDb = dbObj.getBoolean("operationStatus");
								boolean statusAtClient = Boolean.parseBoolean(attributeObj.getValue());
								// if status in db and status at client are
								// equal, return 0
								if (statusInDb == statusAtClient)
									return 0;
								else // if status in db and status at client are
										// not equal, return 1
								{
									// make a copy before edit
									BasicDBObject copy = (BasicDBObject) dbObject.copy();
									dbObj.replace("operationStatus", statusAtClient);
									collection.update(copy, dbObject);
									System.out.println("Device " + attributeObj.getIp()
											+ " update temperature operationStatus successfully!");
									return 1;
								}
							}
						}
						break;
					// humidity sensor
					case 0x12:
						// define which attribute by using epc
						switch (attributeObj.getEpc()) {
						// status
						case (byte) 0x80:
							// check if EchonetDataObject in database contains
							// attribute status or not
							if (dbObj.get("operationStatus") == null) // operationStatus
																		// is
																		// not
																		// exist,
																		// return
																		// -1
								return -1;
							else {
								// operation status in database
								boolean statusInDb = dbObj.getBoolean("operationStatus");
								boolean statusAtClient = Boolean.parseBoolean(attributeObj.getValue());
								// if status in db and status at client are
								// equal, return 0
								if (statusInDb == statusAtClient)
									return 0;
								else // if status in db and status at client are
										// not equal, return 1
								{
									// make a copy before edit
									BasicDBObject copy = (BasicDBObject) dbObject.copy();
									dbObj.replace("operationStatus", statusAtClient);
									collection.update(copy, dbObject);
									System.out.println("Device " + attributeObj.getIp()
											+ " update humidity operationStatus successfully!");
									return 1;
								}
							}
						}
						break;
					} // end switch classCode

					// return -1 if attribute is not exist
					return -1;
				} // end define data object in database
			} // end for loop
				// return -1 cause EchonetDataObject is not exist
			return -1;

		// profile object
		case 0x0e:
			// get profile object
			BasicDBObject profileObj = (BasicDBObject) dbObject.get("profile");
			// if profile object is null then return -1
			if (profileObj == null)
				return -1;

			// get group code, class code, instance code of EchonetProfileObject
			// in database
			byte groupCode = Byte.parseByte(profileObj.getString("groupCode"));
			byte classCode = Byte.parseByte(profileObj.getString("classCode"));
			byte instanceCode = Byte.parseByte(profileObj.getString("instanceCode"));
			// define exactly data object in database
			if (attributeObj.getGroupCode() == groupCode && attributeObj.getClassCode() == classCode
					&& attributeObj.getInstanceCode() == instanceCode) {
				// define which classCode
				switch (attributeObj.getClassCode()) {
				// node profile
				case (byte) 0xf0:
					// define which attribute
					switch (attributeObj.getEpc()) {
					// status
					case (byte) 0x80:
						// check if EchonetProfileObject in database contains
						// attribute status or not
						if (profileObj.get("operationStatus") == null) // operationStatus
																		// is
																		// not
																		// exist,
																		// return
																		// -1
							return -1;
						else {
							// operation status in database
							boolean statusInDb = profileObj.getBoolean("operationStatus");
							boolean statusAtClient = Boolean.parseBoolean(attributeObj.getValue());
							// if status in db and status at client are
							// equal, return 0
							if (statusInDb == statusAtClient)
								return 0;
							else // if status in db and status at client are not
									// equal, return 1
							{
								// make a copy before edit
								BasicDBObject copy = (BasicDBObject) dbObject.copy();
								profileObj.replace("operationStatus", statusAtClient);
								collection.update(copy, dbObject);
								System.out.println(
										"Device " + attributeObj.getIp() + " update operationStatus successfully!");
								return 1;
							}
						}
						// installation location
					case (byte) 0x81:
						// check if EchonetProfileObject in database contains
						// attribute installLocation or not
						// installLocation is not exist, return -1
						if (profileObj.get("installLocation") == null)
							return -1;
						else {
							// define which INSTALLATION_LOCATION
							boolean isSupported = false;
							switch (attributeObj.getValue().toLowerCase()) {
							case CONSTANT.Living_Room:
							case CONSTANT.Dining_Room:
							case CONSTANT.Kitchen:
							case CONSTANT.Bathroom:
							case CONSTANT.WashRoom_ChangingRoom:
								isSupported = true;
								break;
							}
							// installation location is not in correct format
							if (!isSupported)
								return -1;
							else {
								// compare two value
								String locationInDb = (String) profileObj.get("installLocation");
								// if two value is equal, return 0
								if (attributeObj.getValue().toLowerCase().equals(locationInDb.toLowerCase()))
									return 0;
								// otherwise, return 1
								else {
									// make a copy before edit
									BasicDBObject copy = (BasicDBObject) dbObject.copy();
									profileObj.replace("installLocation", attributeObj.getValue().toLowerCase());
									collection.update(copy, dbObject);
									System.out.println("Device " + attributeObj.getIp()
											+ " update installation location successfully!");
									return 1;
								}
							}
						}
						// current limit
					case (byte) 0x87:
						// currentLimitSetting is not exist, return -1
						if (profileObj.get("currentLimitSetting") == null)
							return -1;
						int currentLimitInDb = profileObj.getInt("currentLimitSetting");
						int currentLimitAtClient = Integer.parseInt(attributeObj.getValue());
						// compare two value
						if (currentLimitInDb == currentLimitAtClient)
							return 0;
						else {
							// make a copy before edit
							BasicDBObject copy = (BasicDBObject) dbObject.copy();
							profileObj.replace("currentLimitSetting", currentLimitAtClient);
							collection.update(copy, dbObject);
							System.out
									.println("Device " + attributeObj.getIp() + " update current limit successfully!");
							return 1;
						}
						// power saving
					case (byte) 0x8f:
						// check if EchonetProfileObject in database contains
						// attribute powerSaving or not
						if (profileObj.get("powerSaving") == null) // powerSaving
																	// is not
																	// exist,
																	// return -1
							return -1;
						else {
							// powerSaving in database
							boolean powerSavingInDb = profileObj.getBoolean("powerSaving");
							boolean powerSavingAtClient = Boolean.parseBoolean(attributeObj.getValue());
							// if powerSaving in db and powerSaving at client
							// are
							// equal, return 0
							if (powerSavingInDb == powerSavingAtClient)
								return 0;
							else // if powerSaving in db and powerSaving at
									// client are not equal, return 1
							{
								// make a copy before edit
								BasicDBObject copy = (BasicDBObject) dbObject.copy();
								profileObj.replace("powerSaving", powerSavingAtClient);
								collection.update(copy, dbObject);
								System.out.println(
										"Device " + attributeObj.getIp() + " update device power saving successfully!");
								return 1;
							}
						}
						// remote control/through public network
					case (byte) 0x93:
						// check if EchonetProfileObject in database contains
						// attribute throughPublicNetwork or not
						if (profileObj.get("throughPublicNetwork") == null) // throughPublicNetwork
																			// is
																			// not
																			// exist,
																			// return
																			// -1
							return -1;
						else {
							// throughPublicNetwork in database
							boolean throughPublicInDb = profileObj.getBoolean("throughPublicNetwork");
							;
							// throughPublicNetwork at client
							boolean throughPublicAtClient = Boolean.parseBoolean(attributeObj.getValue());
							// if throughPublicNetwork in db and
							// throughPublicNetwork at client are equal, return
							// 0
							if (throughPublicInDb == throughPublicAtClient)
								return 0;
							else // if throughPublicNetwork in db and
									// throughPublicNetwork at client are not
									// equal, return 1
							{
								// make a copy before edit
								BasicDBObject copy = (BasicDBObject) dbObject.copy();
								profileObj.replace("throughPublicNetwork", throughPublicAtClient);
								collection.update(copy, dbObject);
								System.out.println("Device " + attributeObj.getIp()
										+ " update through public network successfully!");
								return 1;
							}
						}
						// current time setting
					case (byte) 0x97:
						// if currentTimeSetting is not exist, return -1;
						if (profileObj.get("currentTimeSetting") == null)
							return -1;
						else {
							if (!attributeObj.getValue().matches("\\d{2}:\\d{2}"))
								return -1;
							String timeValues[] = attributeObj.getValue().split(":");
							// parse time value
							int h = Integer.parseInt(timeValues[0]);
							int m = Integer.parseInt(timeValues[1]);
							// check hour range
							// hour range is 0-23
							if (h < 0 || h > 23)
								return -1;
							// check minute range
							// minute range is 0-59
							if (m < 0 || m > 59)
								return -1;
							String currentTimeInDb = profileObj.getString("currentTimeSetting");
							// two value is the same, return 0
							if (currentTimeInDb.equals(attributeObj.getValue()))
								return 0;
							else {
								// make a copy before edit
								BasicDBObject copy = (BasicDBObject) dbObject.copy();
								profileObj.replace("currentTimeSetting", attributeObj.getValue());
								collection.update(copy, dbObject);
								System.out.println(
										"Device " + attributeObj.getIp() + " update current time successfully!");
								return 1;
							}
						}
						// current date setting
					case (byte) 0x98:
						// check if EchonetProfileObject in database contains
						// attribute currentDateSetting or not
						if (profileObj.get("currentDateSetting") == null) // currentDateSetting
																			// is
																			// not
																			// exist,
																			// return
																			// -1
							return -1;
						else {
							if (!attributeObj.getValue().matches("\\d{4}:\\d{2}:\\d{2}"))
								return -1;
							String dateValues[] = attributeObj.getValue().split(":");
							// parse date value
							int year = Integer.parseInt(dateValues[0]);
							int month = Integer.parseInt(dateValues[1]);
							int day = Integer.parseInt(dateValues[2]);
							// check date valid
							if (!Utils.isValidDate(year, month, day))
								return -1;
							String currentDateInDb = profileObj.getString("currentDateSetting");
							if (attributeObj.getValue().equals(currentDateInDb))
								return 0;
							else {
								// make a copy before edit
								BasicDBObject copy = (BasicDBObject) dbObject.copy();
								profileObj.replace("currentDateSetting", attributeObj.getValue());
								collection.update(copy, dbObject);
								System.out.println(
										"Device " + attributeObj.getIp() + " update current date successfully!");
								return 1;
							}
						}
						// power limit setting
					case (byte) 0x99:
						// check if EchonetProfileObject in database contains
						// attribute powerLimit or not
						if (profileObj.get("powerLimit") == null) // powerLimit
																	// is not
																	// exist,
																	// return -1
							return -1;
						else {
							int powerLimitInDb = profileObj.getInt("powerLimit");
							int powerLimitAtClient = Integer.parseInt(attributeObj.getValue());
							// compare two value
							if (powerLimitInDb == powerLimitAtClient)
								return 0;
							else {
								// make a copy before edit
								BasicDBObject copy = (BasicDBObject) dbObject.copy();
								profileObj.replace("powerLimit", powerLimitAtClient);
								collection.update(copy, dbObject);
								System.out.println(
										"Device " + attributeObj.getIp() + " update power limit successfully!");
								return 1;
							}
						}
					}
					break;
				}
			}
			break;
		}

		// return -1 if Attribute is not exist
		return -1;
	}

	/**
	 * Check attribute of direct device whether it is changed or not
	 * 
	 * @param db
	 *            mongodb
	 * @param attribute
	 *            attribute of direct device
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static int checkDirectAttribute(DB db, DirectAttributeObject attribute) {
		// get collection
		DBCollection collection = db.getCollection("Device");
		// find device
		DBObject deviceObj = collection.findOne(attribute.getIp());
		// if device is not exist, return -1
		if (deviceObj == null)
			return -1;
		// get profile object
		BasicDBObject profileObj = (BasicDBObject) deviceObj.get("profile");
		// if profile object is null, return -1
		if (profileObj == null)
			return -1;
		// get real name of attribute in database
		String attributeName = null;
		switch (attribute.getAttributeName()) {
		case TAG_NAME.OPERATION_STATUS:
			attributeName = "operationStatus";
			break;
		case TAG_NAME.INSTALLATION_LOCATION:
			attributeName = "installLocation";
			break;
		case TAG_NAME.REMOTE_CONTROL_SETTING:
			attributeName = "throughPublicNetwork";
			break;
		case TAG_NAME.CURRENT_TIME_SETTING:
			attributeName = "currentTimeSetting";
			break;
		case TAG_NAME.CURRENT_DATE_SETTING:
			attributeName = "currentDateSetting";
			break;
		case TAG_NAME.POWER_LIMIT_SETTING:
			attributeName = "powerLimit";
			break;
		case TAG_NAME.CURRENT_LIMIT_SETTING:
			attributeName = "currentLimitSetting";
			break;
		case TAG_NAME.POWER_SAVING_OPERATION_SETTING:
			attributeName = "powerSaving";
			break;
		}
		// attribute is not supported
		if (attributeName == null)
			return -1;

		// profile does not contain attribute, return -1
		if (!profileObj.containsKey(attributeName))
			return -1;
		// two attribute
		String attributeValueInDb = profileObj.getString(attributeName);
		// two attributes are equal, then return 0
		if (attribute.getValue().equalsIgnoreCase(attributeValueInDb))
			return 0;
		// two attributes are not equal, return 1
		else
			return 1;
	}

	/**
	 * Store direct attribute to database
	 * 
	 * @param db
	 *            mongodb
	 * @param attribute
	 *            attribute of direct device
	 * @return -1 if attribute is not exist, 0 if attribute value is the same, 1
	 *         if attribute is successfully updated
	 */
	@SuppressWarnings("deprecation")
	public static int storeDirectAttribute(DB db, DirectAttributeObject attribute) {
		// get collection
		DBCollection collection = db.getCollection("Device");
		// find device
		BasicDBObject deviceObj = (BasicDBObject) collection.findOne(attribute.getIp());
		// if device is not exist, return -1
		if (deviceObj == null)
			return -1;

		// define which groupCode
		switch (attribute.getGroupCode()) {
		// sensor
		case 0x00:
			// get BasicDBList
			BasicDBList dbList = (BasicDBList) deviceObj.get("eObjList");
			for (int i = 0; i < dbList.size(); i++) {
				BasicDBObject dbObj = (BasicDBObject) dbList.get(i);
				// get group code, class code, instance code of
				// DirectDataObject in database
				byte groupCode = Byte.parseByte(dbObj.getString("groupCode"));
				byte classCode = Byte.parseByte(dbObj.getString("classCode"));
				byte instanceCode = Byte.parseByte(dbObj.getString("instanceCode"));
				// define exactly data object in database
				if (attribute.getGroupCode() == groupCode && attribute.getClassCode() == classCode
						&& attribute.getInstanceCode() == instanceCode) {
					// define which classCode
					switch (attribute.getClassCode()) {
					// led sensor
					case 0x00:
						// define which attribute by using epc
						switch (attribute.getEpc()) {
						// status
						case (byte) 0x80:
							// check if DirectDataObject in database contains
							// attribute status or not
							if (dbObj.get("operationStatus") == null)
								return -1;
							else {
								// operation status in database
								boolean statusInDb = dbObj.getBoolean("operationStatus");
								boolean statusAtClient = Boolean.parseBoolean(attribute.getValue());
								// if status in db and status at client are
								// equal, return 0
								if (statusInDb == statusAtClient)
									return 0;
								else // if status in db and status at client are
										// not equal, return 1
								{
									// make a copy before edit
									BasicDBObject copy = (BasicDBObject) deviceObj.copy();
									dbObj.replace("operationStatus", statusAtClient);
									collection.update(copy, deviceObj);
									System.out.println("Device " + attribute.getIp()
											+ " update temperature operationStatus successfully!");
									return 1;
								}
							}
						}
						break;
					// tcn75 temperature sensor
					case 0x11:
						// define which attribute by using epc
						switch (attribute.getEpc()) {
						// status
						case (byte) 0x80:
							// check if DirectDataObject in database contains
							// attribute status or not
							if (dbObj.get("operationStatus") == null)
								return -1;
							else {
								// operation status in database
								boolean statusInDb = dbObj.getBoolean("operationStatus");
								boolean statusAtClient = Boolean.parseBoolean(attribute.getValue());
								// if status in db and status at client are
								// equal, return 0
								if (statusInDb == statusAtClient)
									return 0;
								else // if status in db and status at client are
										// not equal, return 1
								{
									// make a copy before edit
									BasicDBObject copy = (BasicDBObject) deviceObj.copy();
									dbObj.replace("operationStatus", statusAtClient);
									collection.update(copy, deviceObj);
									System.out.println("Device " + attribute.getIp()
											+ " update temperature operationStatus successfully!");
									return 1;
								}
							}
							// temperature
						case (byte) 0xe0:
							// check if DirectDataObject in database contains
							// attribute temperature or not
							if (dbObj.get("temperature") == null)
								return -1;
							else {
								// temperature in database
								double temperatureInDb = dbObj.getDouble("temperature");
								double temperatureAtClient = Double.parseDouble(attribute.getValue());
								// if temperature in db and temperature at
								// client are
								// equal, return 0
								if (temperatureInDb == temperatureAtClient)
									return 0;
								else // if temperature in db and temperature at
										// client are
										// not equal, return 1
								{
									// make a copy before edit
									BasicDBObject copy = (BasicDBObject) deviceObj.copy();
									dbObj.replace("temperature", temperatureAtClient);
									collection.update(copy, deviceObj);
									System.out.println(
											"Device " + attribute.getIp() + " update TCN75 temperature successfully!");
									return 1;
								}
							}
						}
						break;
					}
				}
			}
			break;
		// profile object
		case 0x0e:
			// get profile object
			BasicDBObject profileObj = (BasicDBObject) deviceObj.get("profile");
			// if profile object is null, return -1
			if (profileObj == null)
				return -1;
			// get real name of attribute in database
			String attributeName = null;
			switch (attribute.getAttributeName()) {
			// operation status
			case TAG_NAME.OPERATION_STATUS:
				attributeName = "operationStatus";
				// profile does not contain attribute, return -1
				if (!profileObj.containsKey(attributeName))
					return -1;
				String attributeValue = attribute.getValue();
				boolean operationStatus;
				// if attribute value is ON, operationStatus = true;
				if ("ON".equals(attributeValue))
					operationStatus = true;
				// if attribute value is OFF, operationStatus = true;
				else if ("OFF".equals(attributeValue))
					operationStatus = false;
				// otherwise, attribute value is not in correct format
				else
					return -1;
				// attribute value in database
				String attributeValueInDb = profileObj.getString(attributeName);
				// if attribute value in database and attribute value at client
				// are
				// the same, return 0
				if (Boolean.toString(operationStatus).equals(attributeValueInDb))
					return 0;
				else {
					// make a copy of device object before update
					BasicDBObject queryDeviceObj = (BasicDBObject) deviceObj.copy();
					// update attribute value in database
					profileObj.replace(attributeName, operationStatus);
					collection.update(queryDeviceObj, deviceObj);
					// update success, return 1
					return 1;
				}
				// power saving
			case TAG_NAME.POWER_SAVING_OPERATION_SETTING:
				attributeName = "powerSaving";
				// profile does not contain attribute, return -1
				if (!profileObj.containsKey(attributeName))
					return -1;
				attributeValue = attribute.getValue();
				boolean powerSaving;
				// if attribute value is ON, powerSaving = true
				if ("ON".equals(attributeValue))
					powerSaving = true;
				// if attribute value is OFF, powerSaving = false
				else if ("OFF".equals(attributeValue))
					powerSaving = false;
				// otherwise, attribute value is not in correct format
				else
					return -1;
				// attribute value in database
				attributeValueInDb = profileObj.getString(attributeName);
				// if attribute value in database and attribute value at client
				// are
				// the same, return 0
				if (Boolean.toString(powerSaving).equals(attributeValueInDb))
					return 0;
				else {
					// make a copy of device object before update
					BasicDBObject queryDeviceObj = (BasicDBObject) deviceObj.copy();
					// update attribute value in database
					profileObj.replace(attributeName, powerSaving);
					collection.update(queryDeviceObj, deviceObj);
					// update success, return 1
					return 1;
				}
				// through public network
			case TAG_NAME.REMOTE_CONTROL_SETTING:
				attributeName = "throughPublicNetwork";
				// profile does not contain attribute, return -1
				if (!profileObj.containsKey(attributeName))
					return -1;
				attributeValue = attribute.getValue();
				boolean throughPublicNetwork;
				// if attribute value is YES, throughPublicNetwork = true
				if ("YES".equals(attributeValue))
					throughPublicNetwork = true;
				// if attribute value is NO, throughPublicNetwork = false
				else if ("NO".equals(attributeValue))
					throughPublicNetwork = false;
				// otherwise, attribute value is not in correct format
				else
					return -1;
				// attribute value in database
				attributeValueInDb = profileObj.getString(attributeName);
				// if attribute value in database and attribute value at client
				// are
				// the same, return 0
				if (Boolean.toString(throughPublicNetwork).equals(attributeValueInDb))
					return 0;
				else {
					// make a copy of device object before update
					BasicDBObject queryDeviceObj = (BasicDBObject) deviceObj.copy();
					// update attribute value in database
					profileObj.replace(attributeName, throughPublicNetwork);
					collection.update(queryDeviceObj, deviceObj);
					// update success, return 1
					return 1;
				}
			case TAG_NAME.INSTALLATION_LOCATION:
				attributeName = "installLocation";
				break;
			case TAG_NAME.STARNDARD_VERSION_INFORMATION:
				attributeName = "standardVersionInfo";
				break;
			case TAG_NAME.CURRENT_TIME_SETTING:
				attributeName = "currentTimeSetting";
				break;
			case TAG_NAME.CURRENT_DATE_SETTING:
				attributeName = "currentDateSetting";
				break;
			case TAG_NAME.POWER_LIMIT_SETTING:
				attributeName = "powerLimit";
				break;
			case TAG_NAME.CURRENT_LIMIT_SETTING:
				attributeName = "currentLimitSetting";
				break;
			}

			// attribute is not supported
			if (attributeName == null)
				return -1;

			// profile does not contain attribute, return -1
			if (!profileObj.containsKey(attributeName))
				return -1;
			// two attribute
			String attributeValueInDb = profileObj.getString(attributeName);
			// two attributes are equal, then return 0
			if (attribute.getValue().equalsIgnoreCase(attributeValueInDb))
				return 0;
			// two attributes are not equal, update attribute
			else {
				// make a copy of device object before update
				BasicDBObject queryDeviceObj = (BasicDBObject) deviceObj.copy();
				profileObj.replace(attributeName, attribute.getValue());
				collection.update(queryDeviceObj, deviceObj);
				// update success, return 1
				return 1;
			}
		}

		// return -1 if Attribute is not exist
		return -1;
	}

	/**
	 * Get all device ip and device status
	 * 
	 * @param db
	 *            mongodb
	 * @return list EchonetDeviceStatus
	 * @see EchonetDeviceStatus
	 */
	public static List<EchonetDeviceStatus> getAllDevicesStatus(DB db) {
		// init list EchonetDeviceStatus
		List<EchonetDeviceStatus> devicesStatus = new ArrayList<EchonetDeviceStatus>();
		try {
			// connect to table EchonetDevice in mongodb
			DBCollection collection = db.getCollection("Device");
			DBCursor cursor = collection.find();
			if (cursor != null) {
				while (cursor.hasNext()) {
					DBObject dbObj = cursor.next();
					if (dbObj != null && dbObj.get("profile") != null) {
						BasicDBObject profileObj = (BasicDBObject) dbObj.get("profile");
						if (profileObj != null && profileObj.get("deviceIP") != null
								&& profileObj.get("operationStatus") != null) {
							// create deviceStatus
							EchonetDeviceStatus deviceStatus = new EchonetDeviceStatus();
							deviceStatus.setDeviceIP(profileObj.getString("deviceIP"));
							deviceStatus.setDeviceStatus(profileObj.getBoolean("operationStatus"));
							String deviceType = (String) dbObj.get("deviceType");
							switch (deviceType) {
							case "EchonetLiteDevice":
								deviceStatus.setDeviceType(DeviceType.EchonetLiteDevice);
								break;
							case "DirectDevice":
								deviceStatus.setDeviceType(DeviceType.DirectDevice);
								break;
							case "HomeGateway":
								deviceStatus.setDeviceType(DeviceType.HomeGateway);
								break;
							}
							// add to list
							devicesStatus.add(deviceStatus);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex);
		}

		// return list
		return devicesStatus;
	}

}
