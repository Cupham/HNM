package server.resources.others;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import server.database.DBUtils;
import server.database.MongoUtils;

/**
 * @author Cu Pham
 *
 */
public class UpdateDeviceResource extends CoapResource {
	// logger
	final static Logger logger = Logger.getLogger(UpdateDeviceResource.class);
	
	/**
	 * Constructor
	 * 
	 * @param name
	 *            identification name
	 */
	public UpdateDeviceResource(String name) {
		super(name);
	}

	/**
	 * Handle delete request, delete all devices in database
	 */
	@Override
	public void handleDELETE(CoapExchange exchange) {
		try{
			// print request info
			logger.info("[DELETE] Clears database");
			// accept request
			exchange.accept();
			// connect to mongodb
			MongoClient mongoClient = MongoUtils.getMongoClient();
			DB db = MongoUtils.getDB(mongoClient);
			// delete all devices
			boolean result = DBUtils.removeAllDevices(db);
			// close mongodb
			MongoUtils.closeMongoClient(mongoClient);
			// if result is false, response delete failed
			if (result == false){
				// if exception occurs, server send bad request to client
				logger.info("[DELETE] Clears database failed");
				exchange.respond(ResponseCode.BAD_REQUEST);
			} else {
				// if exception occurs, server send bad request to client
				logger.info("[DELETE] Successfully clears database.");
				exchange.respond(ResponseCode.CHANGED);
			}
		} catch (Exception ex){
			logger.info("[DELETE] An error has occurred. Detailed: " + ex.getMessage());
			// if exception occurs, server send bad request to client
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}
}
