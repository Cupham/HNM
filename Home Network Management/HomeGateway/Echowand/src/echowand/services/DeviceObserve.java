/**
 * Catch fault from device when change status
 */
package echowand.services;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.main.HomeGateway;
import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.EchonetObjectException;
import echowand.object.RemoteObject;
import echowand.object.RemoteObjectManager;
import echowand.objects.EchonetDevice;
import echowand.objects.announcement.RequestObject;
import echowand.service.Service;
import echowand.service.result.ObserveResult;
import echowand.service.result.UpdateRemoteInfoResult;
import echowand.util.ConvertData;

/**
 * @author Cu Pham
 *
 */
public class DeviceObserve {

	final static Logger logger = Logger.getLogger(DeviceObserve.class);

	public enum FaultType {
		Recoverable_Faults, Require_Repair_Faults, Undefined
	};

	private Service service;

	public DeviceObserve(Service service) {
		this.service = service;
	}

	/**
	 * Observe to get fault status change and get fault description if exist
	 * 
	 * @param service
	 * @return list of request object
	 * @throws SubnetException
	 * @throws EchonetObjectException
	 * @throws SocketException
	 * @throws TooManyObjectsException
	 * @throws InterruptedException
	 */
	public void doObserverCatchFault(String gatewayIpAddress, String serverUri, String registerAction,
			String updateAction, EPC[] announceEPCLst) throws SubnetException, EchonetObjectException, SocketException,
			TooManyObjectsException, InterruptedException {

		List<EPC> listEPCObserve = Arrays.asList(announceEPCLst);

		/****************************************************
		 * Check all property changed by announce status
		 ****************************************************/

		ObserveResult resultObserver = this.service.doObserve(listEPCObserve);
		Thread.sleep(5000);
		/****************************************************
		 * Loop all property changed, get this value change
		 ****************************************************/
		if (resultObserver.countData() > 0) {
			logger.debug("===CHANGE: " + resultObserver.countData() + " changes");
			Thread newThreadObserve = createSendThread(resultObserver, this.service, gatewayIpAddress, serverUri,
					registerAction, updateAction, announceEPCLst);
			if (newThreadObserve != null)
				newThreadObserve.start();
		}
		resultObserver.stopObserve();
		return;
	}

	private static Thread createSendThread(ObserveResult resultObserver, Service service, String gatewayIpAddress,
			String serverUri, String registerAction, String updateAction, EPC[] announceEPCLst) {
		Thread sendThead = null;
		sendThead = new Thread() {
			public void run() {

				List<RequestObject> allChangeStatus = new ArrayList<RequestObject>();
				for (int i = 0; i < resultObserver.countData(); i++) {

					Node node = resultObserver.getData(i).node;
					EOJ eoj = resultObserver.getData(i).eoj;
					EPC epc = resultObserver.getData(i).epc;

					RemoteObjectManager remoteObjManager = service.getRemoteObjectManager();
					RemoteObject rmobj = remoteObjManager.get(node, eoj);

					if (rmobj == null) {
						continue;
					}

					RequestObject objChanged;
					try {
						objChanged = getRequestObjectChange(rmobj, epc, eoj, node);
						if (objChanged != null) {
							allChangeStatus.add(objChanged);
						}
					} catch (EchonetObjectException e) {
						logger.error("[DRM] cannot get object changed, detail: " + e.getMessage());
						continue;
					}

				}
				if (allChangeStatus.size() < 1) {
					return;
				}
				logger.info("[DRM] " + allChangeStatus.size() + "change(s) has been occurred");
				SenderRequest sender = new SenderRequest();
				for (RequestObject changeObj : allChangeStatus) {
					try {
						long startTime = System.currentTimeMillis();
						Node node = service.getRemoteNode(changeObj.getIp().trim());
						EchonetDevice device = DeviceScanner.getDeviceInformation(service, node, gatewayIpAddress);
						if (device == null) {
							logger.info("[DRM] Cannot get echonet device from change object");
							continue;
						}
						if (!HomeGateway.checkDeviceInNetwork(changeObj.getIp().trim())) {
							logger.info("[DRM] A new device has been installed to the home network");
							logger.debug("Device detailed: " + device.toJson());
							UpdateRemoteInfoResult remoteResult = service.doUpdateRemoteInfo(2000);
							remoteResult.join();
							if (HomeGateway.addDeviceToNetwork(device)) {
								List<EchonetDevice> listDeviceRegister = new ArrayList<EchonetDevice>();
								listDeviceRegister.add(device);
								Gson gson = new Gson();
								String jsonToSend = gson.toJson(listDeviceRegister);
								boolean rs = sender.PutRequest(jsonToSend, serverUri, registerAction);
								if (rs) {
									logger.info("Device with IP:" + device.getProfile().getDeviceIP()
											+ "has been joined to the home network successfully");
									long executeTime = System.currentTimeMillis() - startTime;
									logger.info("===[DRM] Executation time: " + executeTime + " ms");
								} else {
									logger.info("Management platform can not accept this device (IP:"
											+ device.getProfile().getDeviceIP() + ")");
								}

							} else {
								logger.info("Can not add new device to the home network");
							}
							continue;
						}
						HomeGateway.updateDeviceToNetwork(device);
						boolean rs = sender.PostRequest(changeObj.toJson(), serverUri, updateAction);
						if (rs) {
							logger.info("[DRM] Updated device resources to the management platform");
							long executeTime = System.currentTimeMillis() - startTime;
							logger.info("===[DRM] Executation time: " + executeTime + " ms");
						} else {
							logger.error("[DRM] The management platform rejected update request.");
						}

					} catch (SubnetException e) {
						logger.error("[DRM] Cannot get current device with IP, detail: " + e.getMessage());
						continue;
					} catch (EchonetObjectException e) {
						logger.error("[DRM] Cannot get echonet device from change object, detail: " + e.getMessage());
						continue;
					} catch (Exception e) {
						logger.error("[DRM] Send request has a error, detail: " + e.getMessage());
						continue;
					}
				}
				stop();
				return;
			}

		};
		return sendThead;
	}

	/**
	 * Get request object from change data to send server
	 * 
	 * @param rmobj
	 * @param epc
	 * @param eoj
	 * @param node
	 * @return
	 * @throws EchonetObjectException
	 */
	private static RequestObject getRequestObjectChange(RemoteObject rmobj, EPC epc, EOJ eoj, Node node)
			throws EchonetObjectException {
		RequestObject objChanged = null;
		if (epc.equals(EPC.x80)) { // operation status
			objChanged = new RequestObject(node.toString(), eoj.getClassGroupCode(), eoj.getClassCode(),
					eoj.getInstanceCode(), EPC.x80.toByte(),
					((ConvertData.dataToInteger(rmobj.getData(EPC.x80)) == 48) ? "true" : "false"));
		} else if (epc.equals(EPC.x81)) { // Install location
			objChanged = new RequestObject(node.toString(), eoj.getClassGroupCode(), eoj.getClassCode(),
					eoj.getInstanceCode(), EPC.x81.toByte(),
					(ConvertData.dataToInstallLocation(rmobj.getData(EPC.x81))));
		} else if (epc.equals(EPC.x88)) { // Fault
			if (ConvertData.dataToByte(rmobj.getData(EPC.x88)) != (byte) 0x41) {
				return null;
			}
			String faultDetail = getFaultDetail(rmobj);
			objChanged = new RequestObject(node.toString(), eoj.getClassGroupCode(), eoj.getClassCode(),
					eoj.getInstanceCode(), EPC.x89.toByte(), faultDetail);
		} else {

			return null;
		}
		return objChanged;
	}

	/**
	 * Get fault description from fault code (EPC 0x89) when occurred fault
	 * 
	 * @param remote-object
	 * @return fault description
	 * @throws EchonetObjectException
	 */
	private static String getFaultDetail(RemoteObject rmobj) throws EchonetObjectException {
		if (ConvertData.dataToByte(rmobj.getData(EPC.x88)) != (byte) 0x41) {
			return "No fault";
		}
		if (rmobj.contains(EPC.x89)) {
			return ConvertData.getFaultDetail(rmobj.getData(EPC.x89));
		}
		return "Fault detail not found";
	}
}
