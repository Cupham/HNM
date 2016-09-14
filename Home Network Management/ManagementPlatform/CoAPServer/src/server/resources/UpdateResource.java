package server.resources;

import java.util.Date;
import java.util.Iterator;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import server.database.DBUtils;
import server.database.MongoUtils;
import server.objects.AttributeObject;
import server.objects.TaskUpdateAttribute;
import server.service.Service;
import server.utils.Utils;

/**
 * @author Cu Pham
 *
 */
public class UpdateResource extends CoapResource {
	// logger
	final static Logger logger = Logger.getLogger(UpdateResource.class);
	
	/**
	 * Constructor
	 * 
	 * @param name
	 *            identification name
	 */
	public UpdateResource(String name) {
		super(name);
	}

	/**
	 * Handle post request, receive attribute and update to database
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		try {
			// print request info
			logger.info("[UPDATE] ECHONET Lite device resources update request has been received");
			// accept request
			exchange.accept();
			// get request text
			String requestText = exchange.getRequestText();
			
			// create gson
			Gson gson = new Gson();
			// parse request to Attribute AttributeObject
			AttributeObject attributeObj = gson.fromJson(requestText, AttributeObject.class);
			// if attributeObj is null, server response bad request
			if (attributeObj == null){
				logger.error("[UPDATE] Data is in wrong format");
				exchange.respond(ResponseCode.BAD_REQUEST);
			}
			else{
				// connect to mongodb
				MongoClient mongoClient = MongoUtils.getMongoClient();
				DB db = MongoUtils.getDB(mongoClient);
				// store attribute
				int result = DBUtils.storeAttribute(db, attributeObj);
				// close mongodb
				MongoUtils.closeMongoClient(mongoClient);
				// check result
				switch (result){
				case -1:
					// response not found
					logger.error("[UPDATE] ECHONET Lite device resources do not exist in the database.");
					exchange.respond(ResponseCode.NOT_FOUND);
					break;
				case 0:
					// response not changed
					logger.info("[UPDATE] ECHONET Lite device resources are up to date.");
					exchange.respond(ResponseCode.BAD_REQUEST);
					break;
				case 1:
					// count time for update task here
					try{
						Queue<TaskUpdateAttribute> queue = ObserveResource.getTaskUpdateAttribute();
						if (queue != null && !queue.isEmpty()){
							TaskUpdateAttribute task = queue.element();
							// first element of queue is the object which needs to find out
							if (task.getAttribute().equals(attributeObj)){
								// count time for update task
								Date beginTime = task.getTime();
								Date endTime = Service.getCurrentTime();
								long diff = endTime.getTime() - beginTime.getTime();
								// print time
								logger.info("[UPDATE] Update task lasts " + Utils.printTime(diff));
								// remove this task form queue
								queue.poll();
							}
							// first element of queue is not the object which needs to find out,
							// browse queue to find correct object
							else {
								Iterator<TaskUpdateAttribute> i = queue.iterator();
								while (i.hasNext()){
									TaskUpdateAttribute t = i.next();
									if (t.getAttribute().equals(attributeObj)){
										// count time for update task
										Date beginTime = t.getTime();
										Date endTime = Service.getCurrentTime();
										long diff = endTime.getTime() - beginTime.getTime();
										logger.info("[UPDATE] Update task lasts " + Utils.printTime(diff));
										// remove this task form queue
										queue.remove(t);
										break;
									}
								}
							}
						}
					} catch(Exception ex){
					}
					// response changed
					logger.info("[UPDATE] ECHONET Lite device resources are updated successfully.");
					exchange.respond(ResponseCode.CHANGED);
					break;
				default:
					// response request
					logger.error("[UPDATE] Management Platform can not handle the request");
					exchange.respond(ResponseCode.NOT_IMPLEMENTED);
					break;
				}
			}
		} catch (Exception ex) {
			logger.error("[UPDATE] An error has been occurred. Detailed: " + ex.getMessage());
			// response bad request
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}
}
