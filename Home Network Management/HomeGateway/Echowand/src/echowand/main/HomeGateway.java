/**
 * Home gateway: provide APIs to control gateway
 */
package echowand.main;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Inet4Subnet;
import echowand.net.SubnetException;
import echowand.objects.EchonetDevice;
import echowand.objects.announcement.RequestUpdate;
import echowand.service.Core;
import echowand.service.Service;
import echowand.services.DeviceObserve;
import echowand.services.DeviceScanner;
import echowand.services.ListeningServer;
import echowand.services.SenderRequest;

/**
 * @author Cu Pham
 *
 */
public class HomeGateway {

	final static Logger logger = Logger.getLogger(HomeGateway.class); // define
																		// logger

	private static ArrayList<EchonetDevice> networkDevices; // list echonet
															// device in current
															// network
	private static Queue<RequestUpdate> updatedRequestQueue; // list request
	// need send update to
	// server

	private String netCard; // network card interface to work?
	private String serverUri; // path to connect server
	private String registerAction; // action name to register device to server
	private String updateAction; // action name to update device to server
	private String observeAction; // action name to register observe with server
	private EPC[] announceEPCLst; // list epc, that have announce status when
									// this change

	private Thread DRMThread; // device observe
	private Thread SMPThread; // listening server
	private Thread SYNThread; // scanner device
	private Thread requestQueueThread; // checking request queue to send update
										// to server

	private String ipAddress; // ip address
	private Service service; // service
	private ListeningServer serverNotify; // listening notify from server
	private DeviceScanner deviceScanner; // scanner device list
	private SenderRequest sendRequestUpdate; // send request update to server
	private DeviceObserve deviceNotify; // listening notify from device then
										// send change data to server

	public HomeGateway() {
		networkDevices = new ArrayList<EchonetDevice>();
		updatedRequestQueue = new LinkedList<RequestUpdate>();

		this.netCard = null;
		this.serverUri = null;
		this.registerAction = null;
		this.updateAction = null;
		this.observeAction = null;
		this.announceEPCLst = null;

		this.ipAddress = null;
		this.service = null;
		this.serverNotify = null;
		this.deviceScanner = null;
		this.sendRequestUpdate = null;
		this.deviceNotify = null;

		this.DRMThread = null;
		this.SMPThread = null;
		this.SYNThread = null;
		this.requestQueueThread = null;
	}

	public HomeGateway(String netCard, String serverUri, String registerAction, String updateAction,
			String observeAction, EPC[] announceEPCLst) {
		networkDevices = new ArrayList<EchonetDevice>();
		updatedRequestQueue = new LinkedList<RequestUpdate>();

		this.netCard = netCard;
		this.serverUri = serverUri;
		this.registerAction = registerAction;
		this.updateAction = updateAction;
		this.observeAction = observeAction;
		this.announceEPCLst = announceEPCLst;

		this.ipAddress = null;
		this.service = null;
		this.serverNotify = null;
		this.deviceScanner = null;
		this.sendRequestUpdate = null;
		this.deviceNotify = null;

		this.DRMThread = null;
		this.SMPThread = null;
		this.SYNThread = null;
		this.requestQueueThread = null;
	}

	public boolean inital() {
		try {
			logger.info("[HGW] Home gateway INITAL");
			NetworkInterface nif = NetworkInterface.getByName(this.netCard);
			Core core = new Core(Inet4Subnet.startSubnet(nif));
			this.ipAddress = loadIPAddress(nif);
			logger.info("1. Start ECHONET Lite device management service at " + this.netCard
					+ " network interface, IP address:" + this.ipAddress);

			core.startService();
			this.service = new Service(core);
			logger.info("2. Start CoAP Client, CoAP Server address: " + serverUri);

			logger.info("3. Initial management interfaces");
			String obverseURL = this.serverUri + "/" + this.observeAction;
			this.serverNotify = new ListeningServer(service, obverseURL, this.announceEPCLst);
			this.deviceScanner = new DeviceScanner(service);
			this.sendRequestUpdate = new SenderRequest();
			this.deviceNotify = new DeviceObserve(service);

			/****************************************************
			 * Create a thread to run updated queue from listening action to
			 * request as response for server after update
			 ****************************************************/
			this.requestQueueThread = new Thread() {
				public void run() {
					logger.info("==Start send updated listening to the management platform (SMP)");
					while (true) {
						try {
							sleep(1000);
							if (updatedRequestQueue.isEmpty())
								continue;
							for (int i = 0; i < updatedRequestQueue.size(); i++) {

								RequestUpdate req = updatedRequestQueue.peek();
								boolean rs = sendRequestUpdate.PostRequest(req.getRequestContent(), serverUri,
										"update");
								if (rs) {
									updatedRequestQueue.poll();
									System.out.println(
											"  [SMP] Home network resources are synced to the management platform");
								} else {
									System.out.println(
											" [SMP] there is a problem while trying connect to the managemenr platform");
								}
								long uTime = System.currentTimeMillis() - req.getStartTime();
								logger.info("[UPDATE] Total time execute a update from server: " + uTime + " ms");
							}
						} catch (InterruptedException e) {
							logger.error("Send updated listening failed, detail: " + e.getMessage());
							continue;
						}
					}
				}
			};

			/****************************************************
			 * Create a thread to run gateway observe Action name to get observe
			 * from server: "observe"
			 ****************************************************/
			this.SMPThread = new Thread() {
				public void run() {
					logger.info("==Start subcribing to the management platform (SMP)");
					serverNotify.doClientObserve();
				}
			};

			/****************************************************
			 * Create a thread to run device observe Action name to post device
			 * change information to server: "register", "update"
			 ****************************************************/
			this.DRMThread = new Thread() {
				public void run() {
					logger.info("==Start ECHONET Lite device resources monitor (DRM)");
					while (true) {
						Thread drmChildThread = new Thread() {
							public void run() {
								try {
									deviceNotify.doObserverCatchFault(ipAddress, serverUri, registerAction,
											updateAction, announceEPCLst);
								} catch (Exception ex) {
									logger.error(
											"Can not connect to the home network. Detailed error: " + ex.getMessage());
									logger.info("[DRM] Restart observing");
								}
								return;
							}
						};
						drmChildThread.start();
						try {
							sleep(5000);
						} catch (InterruptedException e) {
							//
						}
					}
				}
			};

			/****************************************************
			 * Create a thread to run device scanner Action name to post scanner
			 * information to server: "register"
			 ****************************************************/
			this.SYNThread = new Thread() {
				public void run() {
					while (true) {
						try {
							sleep(5 * 60 * 1000); // sleep 5 minutes
							logger.info("[SYNC] Sync home network device resource to the management server");
							long startTime = System.currentTimeMillis();
							updatedRequestQueue.clear();
							List<EchonetDevice> allDeviceScan = deviceScanner.scanDevice(ipAddress);

							for (int i = 0; i < allDeviceScan.size(); i++) {
								if (!deviceChange(allDeviceScan.get(i))) {
									allDeviceScan.remove(i--);
								}
							}
							for (EchonetDevice echonetDevice : allDeviceScan) {
								logger.debug("[SYNC] Device: " + echonetDevice.getProfile().getDeviceIP() + "");
							}
							if (allDeviceScan.size() > 0) {
								// send update
								do {
									try {
										SenderRequest sender = new SenderRequest();
										Gson gson = new Gson();
										String jsonToSend = gson.toJson(allDeviceScan);
										boolean rs = sender.PutRequest(jsonToSend, serverUri, "register");
										if (rs) {
											logger.info(
													"[SYNC] home network resources were synced to the management server");
										} else {
											logger.error("Management Platform is not accepted the changes");
										}
										long executationTime = System.currentTimeMillis() - startTime;
										logger.info("[SYNC] Syncing time to the server: " + executationTime + " ms");
										break;
									} catch (Exception e) {
										logger.error(e.getCause());
										logger.error("[SYNC] A network's error occurred, detail: " + e.getMessage());
										logger.error("Try again to send server...");
										continue;
									}
								} while (true);
							} else {
								// do not thing
								logger.info("[SYNC] There is no change in the home network");
								long executationTime = System.currentTimeMillis() - startTime;
								logger.info("[SYNC] Resource checking time: " + executationTime + "ms");
							}
						} catch (Exception ex) {
							logger.error("Can not sync home network resources. Detailed error: " + ex.getMessage());
						}
					}
				}
			};

		} catch (SocketException e) {
			logger.error("Network card interface cannot create, detail: " + e.getMessage());
			return false;
		} catch (SubnetException e) {
			logger.error("Subnet cannot start, detail: " + e.getMessage());
			return false;
		} catch (TooManyObjectsException e) {
			logger.error("Core cannot start service, detail: " + e.getMessage());
			return false;
		}
		return true;
	}

	public void run() {
		try {
			logger.info("[HGW] Start running home gateway");
			// run first scan
			while (true) {
				try {
					logger.info("[Bootstrapping] Start bootstrapping process");
					long startTime = System.currentTimeMillis();
					updatedRequestQueue.clear();
					networkDevices.clear();
					networkDevices = deviceScanner.scanDevice(ipAddress);

					for (EchonetDevice echonetDevice : networkDevices) {
						logger.info("[Bootstrapping] Devices with ip: " + echonetDevice.getProfile().getDeviceIP()
								+ " has been discovered");
					}
					SenderRequest sender = new SenderRequest();
					Gson gson = new Gson();
					String jsonToSend = gson.toJson(networkDevices);
					boolean rs = sender.PutRequest(jsonToSend, serverUri, "register");
					if (rs) {
						logger.info("Bootstrapping process has been executed sucessfully!");
						long executationTime = System.currentTimeMillis() - startTime;
						logger.info("Total bootstraping time: " + executationTime + " ms");
						break;
					} else {
						logger.error("There is an error occurred. Trying again...");
						continue;
					}
				} catch (Exception ex) {
					logger.error("Bootstrapping process can not executed properly. Detailed error: " + ex.getMessage());
					logger.info("Trying again...");
					continue;
				}
			}

			// start 4 thread is concurrency: DRM, SMP, SYN, Request update
			this.DRMThread.start();
			this.SMPThread.start();
			this.SYNThread.start();
			this.requestQueueThread.start();

			this.SMPThread.join();
			this.DRMThread.join();
			this.SYNThread.join();
			this.requestQueueThread.join();
			logger.info("[HGW] Stopped home gateway");
			return;

		} catch (Exception ex) {
			logger.error("[RUN] Cannot conitue running Home gateway. A error occurred, detail: " + ex.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	public boolean stop() {
		try {
			logger.info("[HGW] Stop home gateway");
			if (DRMThread.isAlive()) {
				DRMThread.stop();
			}
			if (SMPThread.isAlive()) {
				SMPThread.stop();
			}
			if (SYNThread.isAlive()) {
				SYNThread.stop();
			}
			if (requestQueueThread.isAlive()) {
				requestQueueThread.stop();
			}
			return true;
		} catch (Exception ex) {
			logger.error("[STOP] Cannot stopped Home gateway. A error occurred, detail: " + ex.getMessage());
			return false;
		}
	}

	public static void addUpdatedRequestQueue(String jsonToSend, long startTime) {
		if (updatedRequestQueue == null) {
			updatedRequestQueue = new LinkedList<RequestUpdate>();
		}
		updatedRequestQueue.add(new RequestUpdate(jsonToSend, startTime));
	}

	public static boolean addDeviceToNetwork(EchonetDevice device) {
		try {
			if (networkDevices == null)
				networkDevices = new ArrayList<EchonetDevice>();
			networkDevices.add(device);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean deviceChange(EchonetDevice checkDevice) {
		// logger.info("[RUN] Check device in network");
		for (int i = 0; i < networkDevices.size(); i++) {
			if (networkDevices.get(i).getProfile().getDeviceIP().equals(checkDevice.getProfile().getDeviceIP())) {
				if (networkDevices.get(i).equals(checkDevice)) {
					return false;
				} else {
					networkDevices.remove(i);
					networkDevices.add(checkDevice);
					return true;
				}
			}
		}
		networkDevices.add(checkDevice);
		return true;
	}

	public static boolean checkDeviceInNetwork(String ip) {
		// logger.info("[RUN] Check device in network");
		for (EchonetDevice device : networkDevices) {
			if (device.getProfile().getDeviceIP().equals(ip)) {
				return true;
			}
		}
		return false;
	}

	public static void updateDeviceToNetwork(EchonetDevice checkDevice) {
		for (int i = 0; i < networkDevices.size(); i++) {
			if (networkDevices.get(i).getProfile().getDeviceIP().equals(checkDevice.getProfile().getDeviceIP())) {
				networkDevices.remove(i--);
				networkDevices.add(checkDevice);
				logger.debug("Updated device to network: " + checkDevice.getProfile().getDeviceIP());
				return;
			}
		}
		return;
	}

	public static String loadIPAddress(NetworkInterface nif) {
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

}
