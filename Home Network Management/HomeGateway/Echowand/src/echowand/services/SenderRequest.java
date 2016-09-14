/**
 * Object to send request to server
 */
package echowand.services;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

/**
 * @author Cu Pham
 *
 */
public class SenderRequest {

	final static Logger logger = Logger.getLogger(SenderRequest.class);

	public SenderRequest() {
		super();
	}

	public void Initilizes() {

	}

	/**
	 * Send request to server with data is string content as PUT method
	 * 
	 * @param content
	 * @param uris
	 * @param actionName
	 * @throws Exception
	 */
	public boolean PutRequest(String content, String uris, String actionName) throws Exception {
		URI uri = null; // URI parameter of the request
		if (uris.length() > 0) {
			try {
				uri = new URI(uris);
			} catch (URISyntaxException e) {
				logger.debug("Invalid URI: " + e.getMessage());
				System.exit(-1);
			}
			logger.info("[PUT] Request URL: " + uri + "/" + actionName);
			CoapClient client = new CoapClient(uri + "/" + actionName);
			CoapResponse response = client.put(content, 1);
			if (response != null && response.isSuccess()) {
				logger.info("[PUT] '" + actionName + "' Success!");
				return true;
			} else {
				logger.error("[PUT] '" + actionName + "' Failed!" + ((response != null)
						? (response.getCode().value + " - " + response.getResponseText()) : "not received response"));
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Send request to server with data is string content as POST method
	 * 
	 * @param content
	 * @param uris
	 * @param actionName
	 */
	public boolean PostRequest(String content, String uris, String actionName) {
		URI uri = null; // URI parameter of the request
		if (uris.length() > 0) {
			try {
				uri = new URI(uris);
			} catch (URISyntaxException e) {
				logger.debug("Invalid URI: " + e.getMessage());
				System.exit(-1);
			}
			logger.info("[POST] Request URL: " + uri + "/" + actionName);
			CoapClient client = new CoapClient(uri + "/" + actionName);
			CoapResponse response = client.post(content, 1);
			if (response != null && response.isSuccess()) {
				logger.info("[POST] '" + actionName + "' Success!");
				return true;
			} else {
				logger.error("[POST] '" + actionName + "' Failed!" + ((response != null)
						? (response.getCode().value + " - " + response.getResponseText()) : "not received response"));
				return false;
			}
		} else {
			return false;
		}
	}
}
