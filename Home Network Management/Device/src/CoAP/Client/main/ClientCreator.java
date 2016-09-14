package CoAP.Client.main;


import org.apache.log4j.Logger;
import CoAP.Client.objects.DirectDevice;

/**
 * @author Cu Pham
 *
 */
public class ClientCreator {

	private final static Logger logger = Logger.getLogger(ClientCreator.class);

	public static String usage() {
		return "Usage: Help [-h] |Ethernet interface | Configfile.XML | ServerUri [coap://server.uri]";
	}

	public static void main(String[] args) throws Exception {

		logger.info("CoAP client is starting...");
		if (args.length > 0 && args[0].equals("-h")) {
			System.out.println(usage());
			logger.error("Input paramater invalid");
			System.exit(0);
		}
		if (args.length == 0) {
			System.out.println(usage());
			logger.error("Input paramater invalid");
			System.exit(-1);
		}
		if (args.length == 4) {
			// get environment variable
			String netCard = args[0].trim();
			String profileFile = args[1].trim();
			String deviceFile = args[2].trim();
			String serverUrl = args[3].trim();
			
			DirectDevice device = new DirectDevice();
			device.loadFromXML(profileFile, deviceFile, netCard, serverUrl);
			device.run();
			logger.info("CoAP client is stopped");
		} else {
			logger.error("Input error");
		}
	}

}
