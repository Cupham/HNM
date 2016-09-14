package server.config;

/**
 * @author Cu Pham
 *
 */
public class CONSTANT {
	/**
	 * INSTALLATION LOCATION LIVING ROOM
	 */
	public final static String Living_Room = "living room";
	/**
	 * INSTALLATION LOCATION DINING ROOM
	 */
	public final static String Dining_Room = "dining room";
	/**
	 * INSTALLATION LOCATION KITCHEN
	 */
	public final static String Kitchen = "kitchen";
	/**
	 * INSTALLATION LOCATION BATHROOM
	 */
	public final static String Bathroom = "bathroom";
	/**
	 * INSTALLATION LOCATION LAVATORY
	 */
	public final static String Lavatory = "lavatory";
	/**
	 * INSTALLATION LOCATION WASHROOM/CHANGING ROOM
	 */
	public final static String WashRoom_ChangingRoom = "washroom/changing room";
	
	/**
	 * vietnam's time zone
	 */
	public final static String VN_TIME_ZONE = "UTC+7";
		
	/**
	 * timeout for task update
	 */
	public final static long TASK_UPDATE_TIME_OUT = 10000; 
	/**
	 * time for check queue
	 */
	public final static long CHECK_QUEUE_TIME = 5000;

	public final static String ECHONET_Lite_Profile_Description = "upnp/ECHONET Lite_Profile_Description.xml";
	
	public final static String ECHONET_Lite_Profile_Service_Description = "upnp/ECHONET Lite_ProfileService_Description.xml";
	
	public final static String TemperatureSensor_Description = "upnp/ECHONET Lite_TemperatureSensor_Description.xml";
	
	public final static String TemperatureSensor_Service_Description = "upnp/ECHONET Lite_TemperatureSensorService_Description.xml";
	
	public final static String HumiditySensor_Description = "upnp/ECHONET Lite_HumiditySensor_Description.xml";
	
	public final static String HumiditySensor_Service_Description = "upnp/ECHONET Lite_HumiditySensorService_Description.xml";
}
