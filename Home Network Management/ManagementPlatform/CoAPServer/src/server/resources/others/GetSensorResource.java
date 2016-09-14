package server.resources.others;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import echowand.objects.EchonetDataObject;
import server.database.DBUtils;
import server.database.MongoUtils;
import server.objects.EchonetDataObjectEx;

/**
 * @author Cu Pham
 *
 */
public class GetSensorResource extends CoapResource {
	// logger
	final static Logger logger = Logger.getLogger(GetSensorResource.class);
	
	/**
	 * Constructor
	 * @param name identification name
	 */
	public GetSensorResource(String name) {
		super(name);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		try{
			// print request info
			logger.info("[GET] Client gets sensor.");
			// accept request
			exchange.accept();
			// get request text
			String requestText = exchange.getRequestText();
			// parse request text to EchonetDataObjectEx
			Gson gson = new Gson();
			EchonetDataObjectEx dataObjEx = gson.fromJson(requestText, EchonetDataObjectEx.class);
			if (dataObjEx == null){
				// server responses bad request
				logger.info("[GET] Data is in wrong format.");
				exchange.respond(ResponseCode.BAD_REQUEST);
			}
			else {
				// connect to mongodb
				MongoClient mongoClient = MongoUtils.getMongoClient();
				DB db = MongoUtils.getDB(mongoClient);
				// get EchonetDataObject
				EchonetDataObject dataObj = DBUtils.getEchonetDataObject(db, dataObjEx.getDeviceIP(), dataObjEx.getGroupCode()
						, dataObjEx.getClassCode(), dataObjEx.getInstanceCode());
				// close db
				MongoUtils.closeMongoClient(mongoClient);
				// if dataObj is null, server response NOT_FOUND
				if (dataObj == null){
					// server response NOT FOUND
					logger.info("[GET] Sensor is not exist in database.");
					exchange.respond(ResponseCode.NOT_FOUND);
				}
				// dataObj is not null, server response to client
				else {
					//convert EchonetDataObject to json
					String jsonData = gson.toJson(dataObj);
					// response
					logger.info("[GET] Successfully gets sensor.");
					exchange.respond(ResponseCode.CONTENT, jsonData,  MediaTypeRegistry.APPLICATION_JSON);
				}		
			}
		} catch(Exception ex){
			logger.info("[GET] An error has occurred. Detailed: " + ex.getMessage());
			// if exception occurs, server send bad request to client
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}
}
