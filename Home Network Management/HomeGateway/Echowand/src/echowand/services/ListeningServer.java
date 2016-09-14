/**
 * Get observe from server then execute
 */
package echowand.services;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import com.google.gson.Gson;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.main.HomeGateway;
import echowand.net.Node;
import echowand.net.SubnetException;
import echowand.object.ObjectData;
import echowand.objects.announcement.RequestObject;
import echowand.service.Service;
import echowand.util.ConvertData;

/**
 * @author Cu Pham
 *
 */
public class ListeningServer {

	final static Logger logger = Logger.getLogger(ListeningServer.class);

	private CoapClient client;
	private Service service;
	private EPC[] announceEPC;

	public ListeningServer(Service service, String coapUrl, EPC[] announceEpc) {
		logger.info("Initial subcribing to the management platform (SMP)");
		this.client = new CoapClient(coapUrl);
		this.service = service;
		this.announceEPC = announceEpc;
	}

	/**
	 * Do get statement change device from server then update device value
	 */
	public void doClientObserve() {
		try {
			//logger.info("[LISTENING] [INITAL] Inital Coap observe relation");
			CoapObserveRelation relation = client.observe(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					
					String content = response.getResponseText();
					content = content.trim();
					if (content.length() < 1) {

						return;
					}
					long startTime = System.currentTimeMillis();
					logger.info("   [SMP] Received command from management platform");
					logger.debug("  [SMP] Received command: "+content);
					/****************************************************
					 * Parse object to change from response Parse to
					 * RequestObject
					 ****************************************************/
					//logger.info("[LISTENING] [RUN] Parse data from command");
					Gson gson = new Gson();
					RequestObject rpObj = gson.fromJson(content, RequestObject.class);
					DeviceUpdate update = new DeviceUpdate(service);
					String strEOJ = String.format(
							String.format("%02x", rpObj.getGroupCode()) + String.format("%02x", rpObj.getClassCode())
									+ String.format("%02x", rpObj.getInstanceCode()));
					logger.info("  [SMP] Received command detailed: " + strEOJ);

					/****************************************************
					 * Update value for device that valid server statement
					 ****************************************************/
					try {
						EOJ eoj = new EOJ(strEOJ);
						EPC epc = EPC.fromByte(rpObj.getEpc());
						Node node = service.getRemoteNode(rpObj.getIp());
						byte[] value = ConvertData.requestData(epc, rpObj.getValue());
						if(value == null)
							throw new ParseException("Cannot parse value to update!");
						ObjectData data = new ObjectData(value);
						boolean x  = update.updateDeviceAttribute( node, eoj, epc, data);
						
						if(x){
							logger.debug("[SMP] Update device success!");
							for (EPC epc1 : announceEPC) {
								if(epc.equals(epc1)){
									return;
								}
							}
							HomeGateway.addUpdatedRequestQueue(rpObj.toJson(),startTime);
						}else{
							logger.debug("[SMP] Update device failed!");
						}
					} catch (SubnetException  | ParseException e1) {
						logger.error("[SMP] Cannot update the home network. Detailed error: " + e1.getMessage());
					} catch (Exception e) {
						logger.error("[SMP] Cannot update the home network. Detailed error: " + e.getMessage());
					}
				}

				@Override
				public void onError() {
					logger.error(
							"[SMP] Can not subcribe to the management platform. Please check the internet connection");
				}
			});

		} catch (Exception ex) {
			logger.error("[SMP] There is an error in internet configuration.");
		}
	}
}
