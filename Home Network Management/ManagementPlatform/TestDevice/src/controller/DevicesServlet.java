package controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import config.ServerConfig;
import dto.EchonetDeviceEx;

/**
 * @author Cu Pham
 *
 */
@WebServlet(name = "Devices", urlPatterns = "/devices")
public class DevicesServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * override method doGet, response List EchonetDevice in json format
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			RequestDispatcher dispatcher = req.getServletContext()
					.getRequestDispatcher("/WEB-INF/views/deviceManagement.jsp");		
			
			// get serverip, serverport
			ServerConfig constanst = ServerConfig.getInstance();
			String serverIP = constanst.getSvIP();
			String serverPort = constanst.getSvPort();
			
			// access to coap server
			CoapClient client = new CoapClient("coap://" + serverIP + ":" + serverPort + "/getdevice");
			
			// send a get request to coapserver and get response
			CoapResponse coapResponse = client.get();
			// if coapResponse != null then parse json to List EchonetDevice
			if (coapResponse != null) {
				// if response code is not CONTENT, then print response Text
				if (coapResponse.getCode() != ResponseCode.CONTENT){
					String responseText = coapResponse.getResponseText();
					System.out.println(responseText);
					// set null for list device object
					req.setAttribute("listDevices", null);
				} 
				// if response code is CONTENT, then parse it to List EchonetDeviceEx
				else {
					// get response text
					String json = coapResponse.getResponseText();
					// parse json to List EchonetDevice
					Gson gson = new Gson();
					// parse json to List EchonetDevice object
					Type type = new TypeToken<ArrayList<EchonetDeviceEx>>(){}.getType();
					ArrayList<EchonetDeviceEx> listDevices = new ArrayList<EchonetDeviceEx>();
					// parse json to ArrayList<EchonetDeviceEx>
					 listDevices = gson.fromJson(json, type);
					
					// add name and eoj to EchonetDataObject so that user can see them in web UI
					for (EchonetDeviceEx device:listDevices){
						if (device.geteObjList() != null){
							for (Map<String, String> dataObj:device.geteObjList()){
								if (dataObj.containsKey("groupCode") && dataObj.containsKey("classCode")
										&& dataObj.containsKey("instanceCode")){
									// parse groupCode, classCode, instance to byte
									byte groupCode = Byte.parseByte(dataObj.get("groupCode"));
									byte classCode = Byte.parseByte(dataObj.get("classCode"));
									switch (groupCode) {
									// List of Objects of Sensor-related Device Class Group
									case 0x00:
										switch (classCode) {
										// LED sensor
										case 0x00:
											dataObj.put("name", "LED sensor");
											break;
										// Temperature sensor
										case 0x11:
											dataObj.put("name", "Temperature sensor");
											break;
										// Humidity sensor
										case 0x12:
											dataObj.put("name", "Humidity sensor");
											break;
										}
										break;
									}
								}
							}
						}
					}
					
					// set list device object
					req.setAttribute("listDevices", listDevices);
				}
				
			} else {
				// set null for list device object
				req.setAttribute("listDevices", null);
			}
			
			// Set refresh, auto load time as 5 seconds
			//resp.setIntHeader("Refresh", 5);
			dispatcher.forward(req, resp);
		} catch (Exception ex) {
			ex.printStackTrace();
			// if error occurs, forward to page error
			req.setAttribute("error", ex.getMessage());
			RequestDispatcher dispatcher = req.getServletContext().getRequestDispatcher("/WEB-INF/views/error.jsp");
			dispatcher.forward(req, resp);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			// get serverip, serverport
			ServerConfig constanst = ServerConfig.getInstance();
			String serverIP = constanst.getSvIP();
			String serverPort = constanst.getSvPort();
			// access to coap server
			CoapClient client = new CoapClient("coap://" + serverIP + ":" + serverPort + "/updatedevice");
			// request to coap server
			CoapResponse coapResponse = client.delete();
			// check response
			if (coapResponse == null) {
				resp.setContentType("application/json");
				resp.getWriter().write("{\"success\": false, \"message\": \"Request to Coapserver failed.\"}");
			} else {
				if (coapResponse.getCode() != ResponseCode.CHANGED){
					resp.setContentType("application/json");
					resp.getWriter().write("{\"success\": false, \"message\": \"" + coapResponse.getResponseText() + "\"}");
				} else {
					resp.setContentType("text/plain");
					resp.getWriter().write(coapResponse.getResponseText());
				}
			}
		} catch(Exception ex){
			ex.printStackTrace();
			// if error occurs, forward to page error
			RequestDispatcher dispatcher = req.getServletContext().getRequestDispatcher("/WEB-INF/views/error.jsp");
			dispatcher.forward(req, resp);
		}
	}
}
