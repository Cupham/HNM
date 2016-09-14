/**
 * Control device by update data
 */
package echowand.services;

import java.net.SocketException;

import org.apache.log4j.Logger;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.EchonetObjectException;
import echowand.object.ObjectData;
import echowand.service.ObjectNotFoundException;
import echowand.service.Service;
import echowand.service.result.UpdateRemoteInfoResult;

/**
 * @author Cu Pham
 *
 */
public class DeviceUpdate {

	final static Logger logger = Logger.getLogger(DeviceUpdate.class);

	private Service service;

	public DeviceUpdate(Service service) {

		logger.info("[UPDATE] Inital device resources update interface");
		this.service = service;
	}

	public synchronized boolean updateDeviceAttribute(Node node, EOJ eoj, EPC epc, ObjectData value)
			throws SocketException, SubnetException, TooManyObjectsException, EchonetObjectException,
			ObjectNotFoundException, InterruptedException {

		logger.info(String.format("[UPDATE] Start updating device: IP:%s" + ", EOJ:%s, EPC:%s, Value: %s",
				node.toString(), eoj.toString(), epc.toString(), value.toString()));
		long startTime = System.currentTimeMillis();
		// update execute
		if (service.setRemoteData(node, eoj, epc, value)) {
			long updateTime = System.currentTimeMillis() - startTime;
			logger.info("[UPDATE] Time update device: " + updateTime + " ms");
			return true;
		}
		return false;
	}
}
