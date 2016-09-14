package server.resources.others;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import echowand.objects.EchonetDevice;
import server.database.DBUtils;
import server.database.MongoUtils;

/**
 * @author Cu Pham
 *
 */
public class GetDeviceResource extends CoapResource {
	// logger
	final static Logger logger = Logger.getLogger(GetDeviceResource.class);
	
	/**
	 * Constructor
	 * 
	 * @param name
	 *            identification name
	 */
	public GetDeviceResource(String name) {
		super(name);
	}

	/**
	 * Handle get request, response all devices in json format
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		try {
			// print request info
			logger.info("[GET] Client gets devices.");
			// accept request
			exchange.accept();
			// connect to mongodb
			MongoClient mongoClient = MongoUtils.getMongoClient();
			// get mongo database
			DB db = MongoUtils.getDB(mongoClient);
			// get all devices in json format
			String json = DBUtils.getAllDevicesInJson(db); 
			// response request
			logger.info("[GET] Successfully gets devices.");
			exchange.respond(ResponseCode.CONTENT, json, MediaTypeRegistry.APPLICATION_JSON);
		} catch (Exception ex) {
			logger.info("[GET] An error has been occurred. Detailed: "+ ex.getMessage());
			// if exception occurs, server send bad request to client
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}
	
	/**
	 * Handle post resquest, response EchonetDevice in json format
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		try {
			// print request info
			logger.info("[GET] Client gets device");
			// accept request
			exchange.accept();
			// get deviceIP
			String deviceIP = exchange.getRequestText();

			// check deviceIP
			// if deviceIP is null or empty, response BAD_REQUEST
			if (deviceIP == null || "".equals(deviceIP)){
				logger.info("[GET] Device's IP is null.");
				exchange.respond(ResponseCode.BAD_REQUEST);
			}
			// else, get device by deviceIP and response to client
			else {
				// get mongo client
				MongoClient mongoClient = MongoUtils.getMongoClient();
				// get mongo database
				DB db = MongoUtils.getDB(mongoClient);
				// get device
				EchonetDevice device = DBUtils.getDevice(db, deviceIP);
				if (device == null){
					logger.info("[GET] Device is not found.");
					exchange.respond(ResponseCode.BAD_REQUEST);
				}
				// return device in json format
				Gson gson = new Gson();
				String json = gson.toJson(device);
				// response
				logger.info("[GET] Server responses: DeviceIP " + device.getProfile().getDeviceIP()
						+ ", device status: " 
						+ (device.getProfile().isOperationStatus() ? "ON" : "OFF"));
				exchange.respond(ResponseCode.CONTENT, json, MediaTypeRegistry.APPLICATION_JSON);
			}
		} catch (Exception ex) {
			logger.info("[GET] An error has occurred. Detailed: " + ex.getMessage());
			// if exception occurs, server send bad request to client
			exchange.respond(ResponseCode.BAD_REQUEST, ex.getMessage(), MediaTypeRegistry.TEXT_PLAIN);
		}
	}
}
