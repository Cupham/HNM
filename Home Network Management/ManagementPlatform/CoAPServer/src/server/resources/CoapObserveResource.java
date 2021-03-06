package server.resources;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import server.database.DBUtils;
import server.database.MongoUtils;
import server.objects.DirectAttributeObject;
import server.objects.TaskUpdateDirectAttribute;
import server.service.Service;
import server.utils.ReturnMessage;

/**
 * @author Cu Pham
 *
 */
public class CoapObserveResource extends CoapResource {
	// logger
	final static Logger logger = Logger.getLogger(CoapObserveResource.class);
	
	// queue for task update direct device
	private static Queue<TaskUpdateDirectAttribute> taskUpdateDirectAttribute = new LinkedList<TaskUpdateDirectAttribute>();
	// getter for taskUpdateDirectAttribute
	public static Queue<TaskUpdateDirectAttribute> getTaskUpdateDirectAttribute() {
		return taskUpdateDirectAttribute;
	}
	
	private boolean updated = false;
	private String data = null;
	private int index = 0;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            identification name
	 */
	public CoapObserveResource(String name) {
		super(name);
		setObservable(true); // enable observing
		setObserveType(Type.CON); // configure the notification type to CONs
		getAttributes().setObservable(); // mark observable in the Link-Format
	}

	/**
	 * Handle GET, send message to subscribers
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		//logger.debug("[OBSERVE] Number of observations: " + this.getObserverCount());
		
		// if there is a change, send data to subscribers
		if (updated) {
			// server notify to subscribers
			logger.info("[OBSERVER] Management Platform notifies changes to the direct device " 
					+ exchange.getSourceAddress().getHostName());
			//logger.debug("[OBSERVE] Observe in time: " + index);
			logger.debug("[OBSERVER] Data: " + data);
			exchange.respond(ResponseCode.CHANGED, data, MediaTypeRegistry.APPLICATION_JSON);
			
			if (++index >= this.getObserverCount()){
				// reset data
				data = "";
				updated = false;
				index  = 0;
			}
		} else
			// server auto send message in a defined time
			exchange.respond(ResponseCode.CONTENT, "", MediaTypeRegistry.TEXT_PLAIN);
	}

	/**
	 * Handle POST, receive changed message from client and notify to all
	 * subscribers
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		try {
			// print request info
			logger.info("[OBSERVER] Received a POST message from client");
			// accept request
			exchange.accept();
			// get request text
			String requestText = exchange.getRequestText();
			// use gson parse request to DirectAttributeObject
			Gson gson = new Gson();
			DirectAttributeObject attributeObj = gson.fromJson(requestText, DirectAttributeObject.class);
			// if DirectAttributeObject is null, server response BAD_REQUEST
			if (attributeObj == null) {
				// server responses bad request
				logger.error("[OBSERVER] Messages are in the wrong format!");
				exchange.respond(ResponseCode.BAD_REQUEST, "Messages are in the wrong format!", MediaTypeRegistry.TEXT_PLAIN);
			} else {
				// check valid attribute
				ReturnMessage returnMessage = Service.checkDirectAttribute(attributeObj);
				if (!returnMessage.isResult()) {
					// server responses bad request
					logger.error("[OBSERVER] Messages are in the wrong format.");
					exchange.respond(ResponseCode.BAD_REQUEST, "Messages are in the wrong format!", MediaTypeRegistry.TEXT_PLAIN);
				} else {
					// check whether the same request is in process or not
					boolean taskInProcess = false;
					Iterator<TaskUpdateDirectAttribute> i = CoapObserveResource.getTaskUpdateDirectAttribute().iterator();
					while (i.hasNext()){
						TaskUpdateDirectAttribute t = i.next();
						if (t.getDirectAttribute().equals(attributeObj)){
							taskInProcess = true;
							break;
						}
					}
					// if a same request is in process, sever response waiting
					if (taskInProcess){
						logger.info("[OBSERVER] Server received the same request and it has not finished yet.");
						exchange.respond(ResponseCode.GATEWAY_TIMEOUT, "Previous request is in process, please waiting.", MediaTypeRegistry.TEXT_PLAIN);
					} else {
						// connect to mongodb
						MongoClient mongoClient = MongoUtils.getMongoClient();
						DB db = MongoUtils.getDB(mongoClient);
						// compare AttributeObject to existed attribute in database
						int result = DBUtils.checkDirectAttribute(db, attributeObj);
						// close MongoDb
						MongoUtils.closeMongoClient(mongoClient);
						// check result
						switch (result) {
						// attribute is not exist in database
						case -1:
							// response not found
							logger.error("[OBSERVER] CoAP device resources do not exist in the database.");
							exchange.respond(ResponseCode.NOT_FOUND, "Attribute is not exist in database.",
									MediaTypeRegistry.TEXT_PLAIN);
							break;
						// attribute is not changed
						case 0:
							// response not changed
							logger.info("[OBSERVER] CoAP device resources are up to date.");
							exchange.respond(ResponseCode.BAD_REQUEST, "Attribute's value is up to date.",
									MediaTypeRegistry.TEXT_PLAIN);
							break;
						// attribute is changed
						case 1:
							data = attributeObj.toJson();
							updated = true;
							// notify change to all subscribers
							changed();
							// store the begin time of update task
							try {
								// create taskUpdate
								TaskUpdateDirectAttribute taskUpdate = new TaskUpdateDirectAttribute();
								// set value for taskUpdate
								taskUpdate.setDirectAttribute(attributeObj);
								// get current time
								Date currentTime = Service.getCurrentTime();
								taskUpdate.setTime(currentTime);
								// add taskUpdate to queue
								CoapObserveResource.getTaskUpdateDirectAttribute().add(taskUpdate);
							} catch(Exception ex){
							}
							// response that server notified to all subscribers
							logger.info("[OBSERVER] Server notified to all subscribers.");
							exchange.respond(ResponseCode.CHANGED, "Server notified to all subscribers.", MediaTypeRegistry.TEXT_PLAIN);
							break;
						default:
							// response request
							logger.info("[OBSERVER] Management Platform can not handle the request!");
							exchange.respond(ResponseCode.NOT_IMPLEMENTED, " Management Platform can not handle the request!", MediaTypeRegistry.TEXT_PLAIN);
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			// response bad request
			logger.info("[OBSERVER] An error has been occurred. Detailed: "+ ex.getMessage());
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}
}
