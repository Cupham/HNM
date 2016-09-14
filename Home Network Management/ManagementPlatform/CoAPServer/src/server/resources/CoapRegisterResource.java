package server.resources;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import server.database.DBUtils;
import server.database.MongoUtils;
import server.objects.DirectDevice;
import server.objects.DirectDeviceEx;

/**
 * @author Cu Pham
 *
 */
public class CoapRegisterResource extends CoapResource {
	// logger
	final static Logger logger = Logger.getLogger(CoapRegisterResource.class);
	
	/**
	 * Constructor
	 * 
	 * @param name
	 *            identification name
	 */
	public CoapRegisterResource(String name) {
		super(name);
	}
	
	@Override
	public void handlePUT(CoapExchange exchange) {
		try {
			// print request info
			logger.info("[REGISTER] CoAP device resources registration request has been received");
			// accept request
			exchange.accept();
			// get request text
			String request = exchange.getRequestText();
			// for debug
			//logger.debug("Data: " + request);
			// parse json to DirectDevice object and store in database
			Gson gson = new Gson();
			// parse json to DirectDevice object
			DirectDeviceEx deviceEx = gson.fromJson(request, DirectDeviceEx.class);
			if (deviceEx != null) {
				DirectDevice device = deviceEx.toDirectDevice();
				String devIP = device.getProfile().getDeviceIP();
				logger.info("[REGISTER] 1 CoAP device is registered IP: " + devIP);

				// put DirectDevice object to database
				// get connection
				MongoClient mongoClient = MongoUtils.getMongoClient();
				// get database
				DB db = MongoUtils.getDB(mongoClient);
				// put DirectDevice
				boolean success = DBUtils.storeDirectDevice(db, device);
				// close connection
				MongoUtils.closeMongoClient(mongoClient);
				if (!success) // failed, send bad request
				{
					logger.error("[REGISTER] Can not register CoAP device with IP "+devIP+" to the database");
					exchange.respond(ResponseCode.BAD_REQUEST);
				}
				else // success, send CHANGED response
				{
					logger.info("[REGISTER] CoAP device with IP: "+devIP+" was registered successfully");
					exchange.respond(ResponseCode.CHANGED);
				}
			} else // device data is null, send bad request
			{
				logger.error("[REGISTER] Data is in wrong format");
				exchange.respond(ResponseCode.BAD_REQUEST);
			}
		} catch (Exception ex) {
			logger.info("[REGISTER] An error has occurred. Detailed: " + ex.getMessage());
			// if exception occurs, server send bad request to client
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}
}
