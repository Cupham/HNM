package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import com.google.gson.Gson;

import config.ServerConfig;
import dto.AttributeObject;

/**
 * @author Cu Pham
 *
 */
@WebServlet(name = "Attribute", urlPatterns = "/attribute")
public class AttributeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * time out for request
	 */
	private static final long TIME_OUT = 10000;

	/**
	 * Override doPost method, set attribute for device input: deviceIP,
	 * groupCode, classCode, instanceCode, epc, value ouput: response a message
	 * to user, message shows that setting device attribute success or faild
	 */
	@SuppressWarnings("unused")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			// get parameters
			String strDeviceIP = (String) req.getParameter("deviceIP");
			String strGroupCode = (String) req.getParameter("groupCode");
			String strClassCode = (String) req.getParameter("classCode");
			String strInstanceCode = (String) req.getParameter("instanceCode");
			String strEPC = (String) req.getParameter("epc");
			String strValue = (String) req.getParameter("value");

			if (strDeviceIP == null || "".equals(strDeviceIP)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"deviceIP is null.\"}");
			} else if (strGroupCode == null || "".equals(strGroupCode)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"groupCode is null.\"}");
			} else if (strClassCode == null || "".equals(strClassCode)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"classCode is null.\"}");
			} else if (strInstanceCode == null || "".equals(strInstanceCode)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"instanceCode is null.\"}");
			} else if (strEPC == null || "".equals(strEPC)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"EPC is null.\"}");
			} else if (strValue == null || "".equals(strValue)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"Value is null.\"}");
			} else {
				// get serverip, serverport
				ServerConfig constanst = ServerConfig.getInstance();
				String serverIP = constanst.getSvIP();
				String serverPort = constanst.getSvPort();

				// access to coap server
				CoapClient client = new CoapClient("coap://" + serverIP + ":" + serverPort + "/observe");
				if (client == null) {
					resp.setContentType("application/json");
					resp.getWriter().write("{\"success\": false, \"message\": \"Can't access to Coap server.\"}");
				} else {
					// set property for AttributeObject
					AttributeObject attribute = new AttributeObject();
					attribute.setIp(strDeviceIP);
					attribute.setGroupCode(Byte.parseByte(strGroupCode));
					attribute.setClassCode(Byte.parseByte(strClassCode));
					attribute.setInstanceCode(Byte.parseByte(strInstanceCode));
					byte epc = Byte.parseByte(strEPC);
					attribute.setEpc(epc);
					attribute.setValue(strValue);

					// create new gson
					Gson gson = new Gson();
					// parse EchonetDataObjectEx to json
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
						if (coapResponse.getCode() != ResponseCode.CHANGED) {
							resp.setContentType("application/json");
							resp.getWriter().write(
									"{\"success\": false, \"message\": \"" + coapResponse.getResponseText() + "\"}");
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
