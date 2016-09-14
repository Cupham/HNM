package server.resources.others;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.mongodb.DB;
import com.mongodb.MongoClient;

import server.database.DBUtils;
import server.database.MongoUtils;
import server.objects.EchonetDeviceStatus;
import server.service.Service;

/**
 * @author Cu Pham
 *
 */
public class GetDeviceStatusResource extends CoapResource {
	// logger
	final static Logger logger = Logger.getLogger(GetDeviceStatusResource.class);
	
	/**
	 * Constructor
	 * 
	 * @param name
	 *            identification name
	 */
	public GetDeviceStatusResource(String name) {
		super(name);
	}

	/**
	 * Handle get request, response all devices in json format
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		try{
			// print request info
			logger.info("SERVER REQUESTS GET TO RESOURCE getdevicestatus");
			// accept request
			exchange.accept();
			// connect to mongodb
			MongoClient mongoClient = MongoUtils.getMongoClient();
			// get mongo database
			DB db = MongoUtils.getDB(mongoClient);
			// get all devices status
			List<EchonetDeviceStatus> devicesStatus = DBUtils.getAllDevicesStatus(db);
			// close mongodb
			MongoUtils.closeMongoClient(mongoClient);
			// return list EchonetDeviceStatus in xml format
			String xml = Service.convertListEchonetDevicesStatusToXML(devicesStatus);
			// response
			logger.info("[GET] Server responses device status in xml format");
			exchange.respond(ResponseCode.CONTENT, xml, MediaTypeRegistry.APPLICATION_XML);
		} catch (Exception ex){
			logger.info("[GET] An error has occurred. Detailed: " + ex.getMessage());
			// if exception occurs, server send bad request to client
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}
}
