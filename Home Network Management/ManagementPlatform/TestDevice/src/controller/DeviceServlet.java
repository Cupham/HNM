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
import dto.EchonetDeviceEx;
import echowand.objects.EchonetDevice;

/**
 * @author Cu Pham
 *
 */
@WebServlet(name = "Device", urlPatterns = "/device")
public class DeviceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Override doPost method receive deviceIP from client and response
	 * EchonetDevice in json format
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String deviceIP = (String) req.getParameter("deviceIP");
			if (deviceIP == null || "".equals(deviceIP)) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"Device's IP is null.\"}");
			} else {
				// get serverip, serverport
				ServerConfig constanst = ServerConfig.getInstance();
				String serverIP = constanst.getSvIP();
				String serverPort = constanst.getSvPort();
				
				// access to coap server
				CoapClient client = new CoapClient("coap://" + serverIP + ":" + serverPort + "/getdevice");
				// request to coap server
				CoapResponse coapResponse = client.post(deviceIP, MediaTypeRegistry.TEXT_PLAIN);
				// check response
				if (coapResponse == null) {
					resp.setContentType("application/json");
					resp.getWriter().write("{\"success\": false, \"message\": \"Request to Coapserver failed.\"}");
				} else {
					if (coapResponse.getCode() != ResponseCode.CONTENT){
						resp.setContentType("application/json");
						resp.getWriter().write("{\"success\": false, \"message\": \"" + coapResponse.getResponseText() + "\"}");
					} else {
						// get response
						String json = coapResponse.getResponseText();
						if (json == null || "".equals(json)){
							resp.setContentType("application/json");
							resp.getWriter().write("{\"success\": false, \"message\": \"Coapserver response empty failed.\"}");
						} else {
							// parse json to EchonetDevice
							Gson gson = new Gson();
							EchonetDeviceEx deviceEx = gson.fromJson(json, EchonetDeviceEx.class);
							if (deviceEx == null){
								resp.setContentType("application/json");
								resp.getWriter().write("{\"success\": false, \"message\": \"Parse data from CoapServer failed.\"}");
							} else {
								// convert EchonetDeviceEx to EchonetDevice
								EchonetDevice device = deviceEx.toEchonetDevice();
								String deviceData = gson.toJson(device);
								resp.setContentType("application/json");
								resp.getWriter().write(deviceData);
							}
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
