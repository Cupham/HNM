/**
 * Scan all device in network
 */
package echowand.services;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.EchonetObjectException;
import echowand.object.RemoteObject;
import echowand.objects.EchonetDevice;
import echowand.objects.EchonetDevice.DeviceType;
import echowand.objects.EchonetProfileObject;
import echowand.service.Service;
import echowand.service.result.UpdateRemoteInfoResult;

/**
 * @author Cu Pham
 *
 */
public class DeviceScanner {

	final static Logger logger = Logger.getLogger(DeviceScanner.class);

	private Service service;

	public DeviceScanner(Service service) {
		logger.info("Initial Resource Information Collector (RIC)");
		this.service = service;
	}

	public static EchonetDevice getDeviceInformation(Service service, Node node, String gatewayIP) throws EchonetObjectException {
		DeviceType deviceType = DeviceType.EchonetLiteDevice;
		if (node.toString().trim().equals(gatewayIP.trim())) {
			deviceType = deviceType.HomeGateway;
		}
		EchonetDevice device = new EchonetDevice(deviceType);
		List<EOJ> eojs = service.getRemoteEOJs(node);
		logger.debug("===EOJ: "+eojs.size()+" eojs");
		
		/********************************************
		 * Parse object in device (node)
		 *******************************************/
		for (EOJ eoj : eojs) {
			RemoteObject remoteobject = service.getRemoteObject(node, eoj);
			logger.debug("===Remote object: "+((remoteobject == null)?"NULL":remoteobject.toString()));
			if (eoj.isNodeProfileObject()) {
				EchonetProfileObject profileObject = new EchonetProfileObject(node.toString(),
						node.getNodeInfo().toString());
				
				for (int i = 0x80; i < 0xff; i++) {
					EPC epc = EPC.fromByte((byte) i);
					if (remoteobject.isGettable(epc)) {
						profileObject.ParseProfileObjectFromEPC(remoteobject);
					}
				}
				device.setProfile(profileObject);
			} else if (eoj.isDeviceObject()) {
				device.addObject(eoj, remoteobject);
			} else {
				logger.error("[Resource Information Collector] Unknown Object: " + eoj.toString());
			}
		}
		return device;
	}

	public ArrayList<EchonetDevice> scanDevice(String gatewayIP) throws SocketException, SubnetException,
			TooManyObjectsException, InterruptedException, EchonetObjectException {

		ArrayList<EchonetDevice> allDevice = new ArrayList<EchonetDevice>();

		UpdateRemoteInfoResult remoteResult = service.doUpdateRemoteInfo(3000);
		remoteResult.join();

		List<Node> nodes = service.getRemoteNodes(); // list device object
		logger.info(
				"[RIC] Get all ECHONET Lite device resources in the home network. (" + nodes.size() + "device(s).)");
		/********************************************
		 * Parse device (node)
		 ********************************************/
		for (Node node : nodes) {
			allDevice.add(getDeviceInformation(this.service,node, gatewayIP));
		}
		logger.info("==Finish Resource Information Collector " + allDevice.size() + " device resources collected.");
		return allDevice;
	}
}
