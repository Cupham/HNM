package server.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import echowand.objects.EchonetDevice;
import server.database.DBUtils;
import server.database.MongoUtils;
import server.objects.EchonetDeviceEx;

/**
 * @author Cu Pham
 *
 */
public class RegisterResource extends CoapResource {
	// logger
	final static Logger logger = Logger.getLogger(RegisterResource.class);
	
	/**
	 * Constructor
	 * 
	 * @param name
	 *            identification name
	 */
	public RegisterResource(String name) {
		// set resource identifier
		super(name);
	}

	/**
	 * Handle put request, receive device data in json format
	 *  and store to database
	 */
	@Override
	public void handlePUT(CoapExchange exchange) {
		try {
			// print request info
			logger.info("[REGISTER] ECHONET Lite device resources registration request has been received");
			// accept request
			exchange.accept();
			// get request text
			String requestText = exchange.getRequestText();
			// for debug
			//logger.debug("Data: " + requestText);
			// parse json to EchonetDevice object and store in database
			Gson gson = new Gson();
			// parse json to List EchonetDevice object
			Type type = new TypeToken<ArrayList<EchonetDeviceEx>>() {}.getType();
			List<EchonetDeviceEx> listDeviceExs = new ArrayList<EchonetDeviceEx>();

			listDeviceExs = gson.fromJson(requestText, type);
			if (listDeviceExs != null) {
				// parse EchonetDeviceEx to EchonetDevice indirectly
				List<EchonetDevice> listDevices = new ArrayList<EchonetDevice>();
				for (EchonetDeviceEx eDeviceEx : listDeviceExs) {
					EchonetDevice eDevice = eDeviceEx.toEchonetDevice();
					if (eDevice != null)
						listDevices.add(eDevice);
				}
				logger.info("[REGISTER] "+ listDevices.size() + " ECHONET Lite device(s) are registered");

				// put EchonetDevice object to database
				// get connection
				MongoClient mongoClient = MongoUtils.getMongoClient();
				// get database
				DB db = MongoUtils.getDB(mongoClient);
				// put EchonetDevice
				boolean success = DBUtils.storeListDevices(db, listDevices);
				// close connection
				MongoUtils.closeMongoClient(mongoClient);
				if (!success) // failed, send bad request
				{
					logger.error("[REGISTER] Can not register "+listDevices.size()+" ECHONET Lite devices to the database");
					exchange.respond(ResponseCode.BAD_REQUEST);
				}
				else // success, send CHANGED response
				{
					logger.info("[REGISTER] " + listDevices.size() +" ECHONET Lite device(s) were registered successfully");
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
