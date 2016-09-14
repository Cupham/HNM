package server.run;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import server.config.CONSTANT;
import server.database.DBUtils;
import server.database.MongoUtils;
import server.resources.*;
import server.resources.others.GetDeviceResource;
import server.resources.others.GetDeviceStatusResource;
import server.resources.others.GetSensorResource;
import server.resources.others.UPnPResource;
import server.resources.others.UpdateDeviceResource;

/**
 * @author Cu Pham
 *
 */
public class CoapServerEx extends CoapServer {
	// coap default port
	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
	// logger
	final static Logger logger = Logger.getLogger(CoapServerEx.class);
	
    /**
     * Application entry point.
     */
	public static void main(String[] args){
		try {
			java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
			logger.info("1. Initial management platform.");
			// new a server
			CoapServerEx server = new CoapServerEx();
			// add endpoints
			logger.info("2. Start server endpoints.");
			server.addEndpoints();
			// add resources
			logger.info("3. Add server resource.");
			server.addResources();
			logger.info("4. Initial mongo database.");
			// connect to mongodb
			MongoClient mongoClient = MongoUtils.getMongoClient();
			DB db = MongoUtils.getDB(mongoClient);
			// remove all device data
			DBUtils.removeAllDevices(db);
			//close mongodb
			MongoUtils.closeMongoClient(mongoClient);
			
			// thread check queue update task
			CheckTaskUpdateThread checkTaskUpdateThread = new CheckTaskUpdateThread();
			// start thread
			checkTaskUpdateThread.start();
			
			//start server
			server.start();
			logger.info("5. Management platform has been started.");
		} catch (Exception ex){
			// log error
			logger.error(ex);
		}
	}
	
	/**
	 * add resource to server
	 */
	private void addResources(){
		// add RegisterResource to server
    	// this resource is used for gateway to register devices
		logger.info("3.1. Device resources registration handler has been added");
        add(new RegisterResource("register"));
        
        // add RegisterResource to server
    	// this resource is used for gateway to register devices
        add(new CoapRegisterResource("CoAPRegister"));
        
        logger.info("3.2. Device resources update handler has been added");
        // add UpdateResource to server
        // this resource is used for gateway to update device's attributes
        add (new UpdateResource("update"));
        
        // add ObserverResource to server
        // this resource is used to implement feature observation between server and gateway
        logger.info("3.3. Device resources monitor handler has been added");
        add (new ObserveResource("observe"));
        
        // add CoapObserveResource to server
        // this resource is used to implement feature observation between server and direct device
        add (new CoapObserveResource("CoAPObserve"));
        
        // add CoAPUpdate to server
        add(new CoAPUpdateResource("CoAPUpdate"));
        
        /**
         * Others resources
         */
        // add GetDeviceResource to server
        // this resource is used for get device data in database
        logger.info("3.4. Device resources monitor handler has been added");
        add (new GetDeviceResource("getdevice"));
        
        // add UpdateDeviceResource to server
        // this resource is used for update device data in database
        logger.info("3.5. Database handler has been added");
        add (new UpdateDeviceResource("updatedevice"));
        
        // add GetSensorResource to server
        // this resource is used for get sensor data
        add(new GetSensorResource("getsensor"));
        
        // add GetDeviceStatusResource to server
        // this resource is used for get device status in database
        add (new GetDeviceStatusResource("getdevicestatus"));
        
        // add UPnPResource
        UPnPResource profileResource = new UPnPResource("echonetProfile.xml");
        profileResource.setResourceFilePath(CONSTANT.ECHONET_Lite_Profile_Description);
        add(profileResource);
        
        UPnPResource profileServiceResource = new UPnPResource("profileService.xml");
        profileServiceResource.setResourceFilePath(CONSTANT.ECHONET_Lite_Profile_Service_Description);
        add(profileServiceResource);
        
        UPnPResource temperatureSensorResource = new UPnPResource("temperatureSensor.xml");
        temperatureSensorResource.setResourceFilePath(CONSTANT.TemperatureSensor_Description);
        add(temperatureSensorResource);
        
        UPnPResource temperatureSensorServiceResource = new UPnPResource("temperatureSensorService.xml");
        temperatureSensorServiceResource.setResourceFilePath(CONSTANT.TemperatureSensor_Service_Description);
        add(temperatureSensorServiceResource);
        
        UPnPResource humiditySensorResource = new UPnPResource("humiditySensor.xml");
        humiditySensorResource.setResourceFilePath(CONSTANT.HumiditySensor_Description);
        add(humiditySensorResource);
        
        UPnPResource humiditySensorServiceResource = new UPnPResource("humiditySensorService.xml");
        humiditySensorServiceResource.setResourceFilePath(CONSTANT.HumiditySensor_Service_Description);
        add(humiditySensorServiceResource);
	}
	
	/**
     * Add individual endpoints listening on default CoAP port on all IPv4 addresses of all network interfaces.
     */
    private void addEndpoints() {
    	for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
    		// only binds to IPv4 addresses and localhost
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
    }
}
