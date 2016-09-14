package controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.google.gson.reflect.TypeToken;

import config.ServerConfig;
import dto.AttributeDataObject;
import dto.EchonetDataObjectEx;

/**
 * @author Cu Pham
 *
 */
@WebServlet(name = "DataObject", urlPatterns = "/sensor")
public class SensorServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * override method doPost, get EchonetDataObject and response to client
	 * input: deviceIP, groupCode, classCode, instanceCode
	 * output: EchonetDeviceEx in json format
	 * @see EchonetDeviceEx
	 */
	@SuppressWarnings("unused")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String strDeviceIP = (String) req.getParameter("deviceIP");
			String strGroupCode = (String) req.getParameter("groupCode");
			String strClassCode = (String) req.getParameter("classCode");
			String strInstanceCode = (String) req.getParameter("instanceCode");
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
			} else {
				// get serverip, serverport
				ServerConfig constanst = ServerConfig.getInstance();
				String serverIP = constanst.getSvIP();
				String serverPort = constanst.getSvPort();

				// access to coap server
				CoapClient client = new CoapClient("coap://" + serverIP + ":" + serverPort + "/getsensor");
				if (client == null) {
					resp.setContentType("application/json");
					resp.getWriter().write("{\"success\": false, \"message\": \"Can't access to Coap server.\"}");
				} else {
					// set data for EchonetDataObjectEx
					EchonetDataObjectEx dataObj = new EchonetDataObjectEx();
					dataObj.setDeviceIP(strDeviceIP);
					dataObj.setGroupCode(Byte.parseByte(strGroupCode));
					dataObj.setClassCode(Byte.parseByte(strClassCode));
					dataObj.setInstanceCode(Byte.parseByte(strInstanceCode));
					// create new gson
					Gson gson = new Gson();
					// parse EchonetDataObjectEx to json
					String jsonRequest = gson.toJson(dataObj);
					// request to coap server
					CoapResponse coapResponse = client.post(jsonRequest, MediaTypeRegistry.APPLICATION_JSON);
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
							String jsonResponse = coapResponse.getResponseText();
							if (jsonResponse == null || "".equals(jsonResponse)) {
								resp.setContentType("application/json");
								resp.getWriter()
										.write("{\"success\": false, \"message\": \"Coapserver response empty failed.\"}");
							} else {
								// parse json to a Map
								Type type = new TypeToken<Map<String,String>>(){}.getType();
								Map<String,String> dataObjMapper = gson.fromJson(jsonResponse, type);
								// if parse failed, response failed
								if (dataObjMapper == null){
									resp.setContentType("application/json");
									resp.getWriter()
											.write("{\"success\": false, \"message\": \"Cannot parse data received from Coap server.\"}");
								} // parse success, analyze data before response to client 
								else {
									// parse operation status
									if (dataObjMapper.containsKey("operationStatus"))
										dataObj.setOperationStatus(Boolean.parseBoolean(dataObjMapper.get("operationStatus")));
									// parse extended attributes
									List<AttributeDataObject> listAttributes = new ArrayList<AttributeDataObject>();
									// switch group code
									switch(dataObj.getGroupCode()){
									case 0x00:
									// switch class code
										switch(dataObj.getClassCode()){
										// led sensor
										case 0x00:
											if (dataObjMapper.containsKey("ledON")){
												AttributeDataObject att = new AttributeDataObject();
												att.setAttributeName("LED ON");
												att.setChangeable(false);
												att.setEpc((byte)0x80);
												att.setValue(dataObjMapper.get("ledON"));
												listAttributes.add(att);
											}
											break;
										// temperature sensor
										case 0x11:
											if (dataObjMapper.containsKey("temperature")){
												AttributeDataObject att = new AttributeDataObject();
												att.setAttributeName("Temperature");
												att.setChangeable(false);
												att.setEpc((byte)0xe0);
												att.setValue(dataObjMapper.get("temperature"));
												listAttributes.add(att);
											}											
											break;
										// humidity sensor
										case 0x12:
											if (dataObjMapper.containsKey("humidity")){
												AttributeDataObject att = new AttributeDataObject();
												att.setAttributeName("Humidity");
												att.setChangeable(false);
												att.setEpc((byte)0xe0);
												att.setValue(dataObjMapper.get("humidity"));
												listAttributes.add(att);
											}			
											break;
										}
										break;
									}
									// set list extended attributes for EchonetDataObjectEx
									dataObj.setListAttributes(listAttributes);
									// parse EchonetDataObjectEx to json
									String jsonDataObj = gson.toJson(dataObj);
									// response
									resp.setContentType("application/json");
									resp.getWriter().write(jsonDataObj);
								}
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
