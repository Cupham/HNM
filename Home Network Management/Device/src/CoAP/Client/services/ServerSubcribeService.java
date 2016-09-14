package CoAP.Client.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import com.google.gson.Gson;

import CoAP.Client.Util.TAG_NAME;
import CoAP.Client.objects.DirectDevice;
import CoAP.Client.objects.RequestObject;
import echowand.common.EPC;

/**
 * @author Cu Pham
 *
 */
public class ServerSubcribeService {

	final static Logger logger = Logger.getLogger(ServerSubcribeService.class);
	private CoapClient client;

	public ServerSubcribeService(String coapUrl) {
		logger.info("Initial subcribing to the management platform at:" + coapUrl);
		this.client = new CoapClient(coapUrl);
	}

	public void doObserve(String serverUrl) {
		try {
			CoapObserveRelation relation = client.observe(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					String content = response.getResponseText();
					content = content.trim();
					if (content.length() < 1) {
						return;
					}
					/****************************************************
					 * Parse object to change from response Parse to
					 * RequestObject
					 ****************************************************/
					logger.info("   [SMP] Received command from server");
					logger.debug("  [SMP] Received command: " + content);
					Gson gson = new Gson();
					RequestObject rObj = gson.fromJson(content, RequestObject.class);
					System.out.println(rObj.toJson());
					try {
						if (doUpdate(rObj)) {
							DirectDevice.addUpdatedRequestQueue(rObj.toJson());
							return;
						}
						logger.info("[SMP] Can not update device resources");
					} catch (Exception e) {
						logger.error(
								"[SMP] Can not connect to the management platform. Detailed error: " + e.getMessage());
						logger.error(e.toString());
					}
				}

				@Override
				public void onError() {
					logger.error("[SMP] Can not connect to the management platform.");
				}
			});

		} catch (Exception ex) {
			logger.error("[SMP] The connection to the mangement platform was corrupted");
		}
	}

	public static boolean doUpdate(RequestObject rObj) {
		long startTime = System.currentTimeMillis();
		boolean rs = false;
		try {
			if (rObj == null || DirectDevice.profileObject == null || DirectDevice.listDataObject == null) {
				return false;
			}
			// Profile
			if (rObj.getGroupCode() == DirectDevice.profileObject.getGroupCode()
					&& rObj.getClassCode() == DirectDevice.profileObject.getClassCode()
					&& rObj.getInstanceCode() == DirectDevice.profileObject.getInstanceCode()) {

				switch (rObj.getAttributeName()) {
				case TAG_NAME.OPERATION_STATUS:
					DirectDevice.profileObject.setOperationStatus(boolParser(rObj.getValue()));
					rs = true;
					break;
				case TAG_NAME.INSTALLATION_LOCATION:
					DirectDevice.profileObject.setInstallLocation(rObj.getValue());
					rs = true;
					break;
				case TAG_NAME.CURRENT_LIMIT_SETTING:
					DirectDevice.profileObject.setCurrentLimitSetting(Integer.parseInt(rObj.getValue()));
					rs = true;
					break;
				case TAG_NAME.POWER_SAVING_OPERATION_SETTING:
					DirectDevice.profileObject.setPowerSaving(boolParser(rObj.getValue()));
					rs = true;
					break;
				case TAG_NAME.REMOTE_CONTROL_SETTING:
					DirectDevice.profileObject.setThroughPublicNetwork(boolParser(rObj.getValue()));
					rs = true;
					break;
				case TAG_NAME.CURRENT_DATE_SETTING:
					DirectDevice.profileObject.setCurrentDateSetting(dateFormat(rObj.getValue()));
					rs = true;
					break;
				case TAG_NAME.CURRENT_TIME_SETTING:
					DirectDevice.profileObject.setCurrentTimeSetting(rObj.getValue());
					rs = true;
					break;
				case TAG_NAME.POWER_LIMIT_SETTING:
					DirectDevice.profileObject.setPowerLimit(Short.parseShort(rObj.getValue()));
					rs = true;
					break;
				default:
					rs = false;
					logger.error("Attribute does not exist");
					break;
				}
			} else {
				for (int i = 0; i < DirectDevice.listDataObject.size(); i++) {
					if (rObj.getGroupCode() == DirectDevice.listDataObject.get(i).getGroupCode()
							&& rObj.getClassCode() == DirectDevice.listDataObject.get(i).getClassCode()
							&& rObj.getInstanceCode() == DirectDevice.listDataObject.get(i).getInstanceCode()) {
						if (DirectDevice.listDataObject.get(i).getGroupCode() == (byte) 0x00
								&& DirectDevice.listDataObject.get(i).getGroupCode() == (byte) 0x00) { // led
							logger.info("Update led sensor");
							if (rObj.getValue().toLowerCase().equals("on")) {
								byte[] data = new byte[] { (byte) 0x30 };
								DirectDevice.listDataObject.get(i).delegateSetValue(data);
								rs = true;
								break;
							} else if (rObj.getValue().toLowerCase().equals("off")) {
								byte[] data = new byte[] { (byte) 0x31 };
								DirectDevice.listDataObject.get(i).delegateSetValue(data);
								rs = true;
								break;
							}
							rs = false;
							break;
						}
						if (DirectDevice.listDataObject.get(i).getGroupCode() == (byte) 0x00
								&& DirectDevice.listDataObject.get(i).getGroupCode() == (byte) 0x12) { // temperature
							logger.info("Update temperature sensor");
							if (rObj.getEpc() == EPC.x80.toByte() && rObj.getValue().toLowerCase().equals("on")) {
								byte[] data = new byte[] { (byte) 0x30 };
								DirectDevice.listDataObject.get(i).delegateSetValue(data);
								rs = true;
								break;
							} else if (rObj.getEpc() == EPC.x80.toByte()
									&& rObj.getValue().toLowerCase().equals("off")) {
								byte[] data = new byte[] { (byte) 0x31 };
								DirectDevice.listDataObject.get(i).delegateSetValue(data);
								rs = true;
								break;
							}
							rs = false;
							break;
						}
					}
				}
			}
			return rs;

		} catch (ParseException ex) {
			logger.error("Data is in wrong format!");
			rs = false;
		}
		long executationTime = System.currentTimeMillis() - startTime;
		logger.info("Total updating device resouces time: " + executationTime + " ms");
		return rs;

	}

	public static Date dateFormat(String date) throws ParseException {
		DateFormat df = new SimpleDateFormat("YYYY:MM:dd");
		return df.parse(date);
	}

	public static Boolean boolParser(String value) {
		if (value.equalsIgnoreCase("YES") || value.equalsIgnoreCase("ON")
				|| value.equalsIgnoreCase("Through a public network") || value.equalsIgnoreCase("Has Fault")) {
			return true;
		} else {
			return false;
		}
	}

}
