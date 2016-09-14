package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import com.google.gson.Gson;

import config.ServerConfig;
import dto.DirectAttributeObject;

/**
 * @author Cu Pham
 *
 */
@WebServlet(name = "DirectAttribute", urlPatterns = "/directattribute")
public class DirectAttributeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * time out for request
	 */
	private static final long TIME_OUT = 10000;
	
	/**
	 * Override doPost method, set attribute for device
	 * input: ip, attributeName, value
	 * ouput: response a message to user, message shows that setting device attribute success or faild
	 */
	@SuppressWarnings("unused")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			// get parameters
			String ip = (String) req.getParameter("ip");
			String attributeName = (String) req.getParameter("attributeName");
			String value = (String) req.getParameter("value");
			
			if (ip == null || "".equals(ip)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"IP is null.\"}");
			} else if (attributeName == null || "".equals(attributeName)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"attributeName is null.\"}");
			} else if (value == null || "".equals(value)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"value is null.\"}");
			} else {
				// get serverip, serverport
				ServerConfig constanst = ServerConfig.getInstance();
				String serverIP = constanst.getSvIP();
				String serverPort = constanst.getSvPort();
				
				// access to coap server
				CoapClient client = new CoapClient("coap://" + serverIP + ":" + serverPort + "/CoAPObserve");
				if (client == null) {
					resp.setContentType("application/json");
					resp.getWriter().write("{\"success\": false, \"message\": \"Can't access to Coap server.\"}");
				} else {
					DirectAttributeObject attribute = new DirectAttributeObject();
					attribute.setIp(ip);
					attribute.setAttributeName(attributeName);
					attribute.setValue(value);
					
					// create new gson
					Gson gson = new Gson();
					// parse DirectAttributeObject to json
					String jsonRequest = gson.toJson(attribute);
					// set time out
					client.setTimeout(TIME_OUT);
					// request to coap server
					CoapResponse coapResponse = client.post(jsonRequest, MediaTypeRegistry.APPLICATION_JSON);
					
					// will update here
					// check response
					if (coapResponse == null) {
						resp.setContentType("application/json");
						resp.getWriter().write("{\"success\": false, \"message\": \"Request to Coapserver failed.\"}");
					} else {
						if (coapResponse.getCode() != ResponseCode.CHANGED){
							resp.setContentType("application/json");
							resp.getWriter().write("{\"success\": false, \"message\": \"" + coapResponse.getResponseText() + "\"}");
						} else {
							resp.getWriter().write(coapResponse.getResponseText());
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			resp.setContentType("application/json");
			resp.getWriter().write("{\"success\": false, \"message\": \"" + ex.getMessage() + "\"}");
		}
	}
}
