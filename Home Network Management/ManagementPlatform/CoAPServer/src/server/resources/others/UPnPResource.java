package server.resources.others;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import server.utils.Utils;

/**
 * @author Cu Pham
 *
 */
public class UPnPResource extends CoapResource {

	// logger
	final static Logger logger = Logger.getLogger(UPnPResource.class);
	
	/**
	 * Resource file path
	 */
	private String resourceFilePath;
	
	/**
	 * constructor
	 * @param name resource name
	 */
	public UPnPResource(String name) {
		super(name);
	}

	/**
	 * @param resourceFilePath the resourceFilePath to set
	 */
	public void setResourceFilePath(String resourceFilePath) {
		this.resourceFilePath = resourceFilePath;
	}

	/**
	 * Return content of resource file
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		try {
			exchange.accept();
			logger.info("[UPnPResource] Client requests resource : " + this.resourceFilePath);
			
			if (resourceFilePath == null || "".equals(resourceFilePath)){
				logger.info("[UPnPResource] Resource is not found.");
				exchange.respond(ResponseCode.NOT_FOUND);
			}
			String content = Utils.readFile(resourceFilePath);
			if (content == null){
				logger.info("[UPnPResource] Resource is not found.");
				exchange.respond(ResponseCode.NOT_FOUND);
			} else {
				logger.info("[UPnPResource] Server successfully responses resource.");
				exchange.respond(ResponseCode.CONTENT, content, MediaTypeRegistry.APPLICATION_XML);
			}
		} catch(Exception ex){
			exchange.respond(ResponseCode.BAD_REQUEST);
		}
	}
}
