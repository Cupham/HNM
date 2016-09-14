/**
 * 
 */
package CoAP.Client.objects;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import CoAP.Client.Util.SaxHandler;
import CoAP.Client.services.ServerSubcribeService;
import echowand.objects.EchonetProfileObject;
import echowand.objects.EchonetDevice.DeviceType;
import echowand.services.SenderRequest;

/**
 * @author Cu Pham
 *
 */
public class DirectDevice {

	private final static Logger logger = Logger.getLogger(DirectDevice.class);

	public static LinkedList<DirectDataObject> listDataObject;
	public static EchonetProfileObject profileObject;
	public static Queue<String> updatedRequestQueue;
	private NetworkInterface networkInterface;
	private String ip;
	private String serverUrl;
	
	public DirectDevice(){
		this.profileObject = null;
		this.listDataObject = new LinkedList<DirectDataObject>();
		this.updatedRequestQueue = new LinkedList<String>();
		this.networkInterface = null;
		this.ip = null;
		this.serverUrl = null;
	}

	public void loadFromXML(String profileXml, String dataXml, String netCard, String server) {
		try {
			profileObject = loadXMLProfileFile(profileXml);
			listDataObject = loadXMLDataObjectFile(dataXml);
			this.networkInterface = NetworkInterface.getByName(netCard);
			this.ip = loadIP(networkInterface);
			this.serverUrl = server;

		} catch (NumberFormatException | UnsupportedBusNumberException | IOException | SAXException e) {

		} catch (ParserConfigurationException e) {

		}

	};

	public void run() {

		// Bootstrapping
		bootstrap(this.ip);
		String serverInteractUrl = this.serverUrl + "/CoAPObserve";
		
		/****************************************************
		 * Create a thread to run listening server
		 ****************************************************/
		ServerSubcribeService serverSubcribe = new ServerSubcribeService(serverInteractUrl);
		Thread serverInteract = new Thread() {
			public void run() {
				logger.info("==Start subcribing to the management platform (SMP)");
				serverSubcribe.doObserve(serverInteractUrl);
			}
		};

		/****************************************************
		 * Create a thread to run updated queue from listening action to request
		 * as response for server after update
		 ****************************************************/
		SenderRequest sendUpdate = new SenderRequest();
		Thread checkUpdatedQueue = new Thread() {
			public void run() {
				logger.info("==Start send updated listening to the management platform (SMP)");
				while (true) {
					try {
						sleep(1000);
						if (updatedRequestQueue.isEmpty())
							continue;
						logger.debug("===Send: " + updatedRequestQueue.peek());
						long startTime = System.currentTimeMillis();
						for (int i = 0; i < updatedRequestQueue.size(); i++) {

							boolean rs = sendUpdate.PostRequest(updatedRequestQueue.peek(), serverUrl, "CoAPUpdate");
							if (rs) {
								updatedRequestQueue.poll();
								logger.info("  [SMP] Home network resources are synced to the management platform");
							} else {
								logger.info(
										" [SMP] there is a problem while trying connect to the managemenr platform");
							}
						}
						long executationTime = System.currentTimeMillis() - startTime;
						logger.info("Total sending update request time: " + executationTime + " ms");
					} catch (InterruptedException e) {
						logger.error("Send updated listening failed, detail: " + e.getMessage());
						continue;
					}
				}
			}
		};

		/****************************************************
		 * Create a thread to run device observe Action name to post device
		 * change information to server: "register", "update"
		 ****************************************************/
		Thread resourceManagement = new Thread() {
			public void run() {
				logger.info("Self management processes have been started");
				while (true) {
					try {
						sleep(5 * 60 * 1000); // sleep 5 minutes
						long startTime = System.currentTimeMillis();
						SenderRequest sender = new SenderRequest();
						String jsonToSend = toJson();
						// send
						boolean rs = sender.PutRequest(jsonToSend, serverUrl, "CoAPRegister");
						if (rs) {
							long executationTime = System.currentTimeMillis() - startTime;
							logger.info("Total Loading device resouces time: " + executationTime);
							break;
						} else {
							continue;
						}
					} catch (Exception ex) {
						logger.error("Can not connect to the home network. Detailed error: " + ex.getMessage());
						continue;
					}
				}
			}
		};

		/****************************************************
		 * Create a thread to run device observe get change change information
		 * to server: "register", "update"
		 ****************************************************/
		Thread monitorManagement = new Thread() {
			public void run() {

				logger.info("Monitor management processes have been started");
				while (true) {
					try {
						sleep(2 * 1000);
						for (int i = 0; i < listDataObject.size(); i++) {
							boolean rs = listDataObject.get(i).delegateNotify();
							if (rs) {
								if (listDataObject.get(i).getGroupCode() == (byte) 0x00
										&& listDataObject.get(i).getClassCode() == (byte) 0x00) {

									RequestObject robj = new RequestObject();
									robj.setGroupCode(listDataObject.get(i).getGroupCode());
									robj.setClassCode(listDataObject.get(i).getClassCode());
									robj.setInstanceCode(listDataObject.get(i).getInstanceCode());
									robj.setEpc((byte)0x80);
									robj.setIp(ip);
									robj.setValue(((((LED_Sensor)listDataObject.get(i)).isLedON())?"true":"false"));
									addUpdatedRequestQueue(robj.toJson());
								}else if (listDataObject.get(i).getGroupCode() == (byte) 0x00
										&& listDataObject.get(i).getClassCode() == (byte) 0x11) {
									RequestObject robj = new RequestObject();
									robj.setGroupCode(listDataObject.get(i).getGroupCode());
									robj.setClassCode(listDataObject.get(i).getClassCode());
									robj.setInstanceCode(listDataObject.get(i).getInstanceCode());
									robj.setEpc((byte)0xE0);
									robj.setIp(ip);
									robj.setValue(((((TCN75_Temperature)listDataObject.get(i)).getTemperature() + "")));
									addUpdatedRequestQueue(robj.toJson());
								}

							}

						}

					} catch (Exception ex) {
						logger.error("Can not connect to the home network. Detailed error: " + ex.getMessage());
						continue;
					}
				}
			}
		};


		resourceManagement.start();
		serverInteract.start();
		checkUpdatedQueue.start();
		monitorManagement.start();

		try {
			serverInteract.join();
			resourceManagement.join();
			checkUpdatedQueue.join();
			monitorManagement.join();
		} catch (InterruptedException e) {
			logger.error("Cannot join thread, detail: "+e.getMessage());
		}
	}

	public String toJson() {

		Gson gson = new Gson();
		String type = DeviceType.DirectDevice.toString();
		String profileJs = gson.toJson(this.profileObject);
		String datalist = "[";
		int j=0;
		for(int i=0;i<this.listDataObject.size();i++){
			if(this.listDataObject.get(i) == null)
				continue;
			if(j++ == 0){
				datalist += this.listDataObject.get(i).toJson();
			}
			else{
				datalist += ","+this.listDataObject.get(i).toJson();
			}
		}
		datalist += "]";

		String js = String.format("{\"deviceType\":\"%s\", \"profile\":%s, \"eObjList\":%s}", type, profileJs,
				datalist);
		return js;
	}

	public static void addUpdatedRequestQueue(String jsonToSend) {
		if (updatedRequestQueue == null) {
			updatedRequestQueue = new LinkedList<String>();
		}
		updatedRequestQueue.add(jsonToSend);
	}

	// Bootstrap interface
	public void bootstrap(String deviceIP) {
		logger.info("===Start bootstrapping...");
		while (true) {
			try {
				long startTime = System.currentTimeMillis();
				profileObject.setDeviceIP(deviceIP);

				// create object to send
				SenderRequest sender = new SenderRequest();
				String jsonToSend = this.toJson();
				// send
				boolean rs = sender.PutRequest(jsonToSend, this.serverUrl, "CoAPRegister");
				if (rs) {
					long executationTime = System.currentTimeMillis() - startTime;
					logger.info("Total Loading device resouces time: " + executationTime);
					break;
				} else {
					continue;
				}
			} catch (Exception ex) {
				logger.error("Sending to server occured an error, try again...");
				continue;
			}
		}
	}

	public static EchonetProfileObject loadXMLProfileFile(String filename)
			throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		SaxHandler handler = new SaxHandler();
		saxParser.parse(filename, handler);

		return handler.getProfileObject();
	}

	public static String loadIP(NetworkInterface nif) {
		String ip = nif.getInetAddresses().nextElement().getHostAddress();
		Enumeration<InetAddress> inetAddress = nif.getInetAddresses();
		InetAddress currentAddress;

		currentAddress = inetAddress.nextElement();
		while (inetAddress.hasMoreElements()) {
			currentAddress = inetAddress.nextElement();
			if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
				ip = currentAddress.getHostAddress();
				break;
			}
		}
		return ip.trim();
	}

	public static LinkedList<DirectDataObject> loadXMLDataObjectFile(String fileName)
			throws NumberFormatException, UnsupportedBusNumberException, IOException, SAXException {
		try {
			File file = new File(fileName);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(file);
			NodeList nList = doc.getElementsByTagName("object");

			LinkedList<DirectDataObject> allResult = new LinkedList<DirectDataObject>();

			for (int i = 0; i < nList.getLength(); i++) { // list objects
				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					
					Element eElement = (Element) nNode;
					String eoj = eElement.getAttribute("ceoj").trim();
					String eojObject = eoj.substring(0, 4);
					if (eojObject.equals("0000")) { // led sensor
						LED_Sensor item = new LED_Sensor();
						String pin = eElement.getAttribute("pin").trim();

						NodeList nNodeEpc = eElement.getElementsByTagName("property");

						for (int j = 0; j < nNodeEpc.getLength(); j++) { // list
																			// properties
							Node nNode1 = nNodeEpc.item(j);
							if (nNode1 != null && nNode1.getNodeType() == Node.ELEMENT_NODE) {

								Element eElement1 = (Element) nNode1;
								String epc = eElement1.getAttribute("epc").trim();

								if (epc.equals("80")) {
									boolean isOn = ((eElement1.getElementsByTagName("data").item(0).getTextContent()
											.trim().equals("30"))) ? true : false;
									item.setOperationStatus(isOn);
									item.setPinNumber(Integer.parseInt(pin));
									allResult.add(item);
									break;
								}
							}
						}
					}
					else if (eojObject.equals("0011")) { // temperature
						TCN75_Temperature item = new TCN75_Temperature();
						String pin = eElement.getAttribute("pin").trim();
						String bus = eElement.getAttribute("bus").trim();
						item.setOperationStatus(true);
						item.setAddress(Integer.parseInt(pin), Integer.parseInt(bus));
						allResult.add(item);
					}
				}

			}
			return allResult;
		} catch (ParserConfigurationException ex) {
			return null;
		}
	}

}
