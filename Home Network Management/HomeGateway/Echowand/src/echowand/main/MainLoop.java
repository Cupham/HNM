/**
 * Main
 */
package echowand.main;

import java.net.SocketException;

import org.apache.log4j.Logger;

import echowand.common.EPC;
import echowand.logic.TooManyObjectsException;
import echowand.net.SubnetException;
import echowand.object.EchonetObjectException;

/**
 * @author Cu Pham
 *
 */
public class MainLoop {

	final static Logger logger = Logger.getLogger(MainLoop.class);

	public static String usage() {
		return "Usage: Help [-h] | Ethernet [ethx] | ServerUri [coap://server.uri]";
	}

	/**
	 * @param args
	 * @throws SocketException
	 * @throws SubnetException
	 * @throws TooManyObjectsException
	 * @throws InterruptedException
	 * @throws EchonetObjectException
	 */
	public static void main(String[] args) throws SocketException, SubnetException, TooManyObjectsException,
			InterruptedException, EchonetObjectException {

		logger.info("Program is starting...");
		if (args.length > 0 && args[0].equals("-h")) {
			System.out.println(usage());
			logger.error("Input paramater invalid");
			System.exit(0);
		}
		if (args.length == 0 || args.length % 2 == 1) {
			System.out.println(usage());
			logger.error("Input paramater invalid");
			System.exit(-1);
		}

		if (args.length == 2) {

			String cardNetwork = args[0];
			String serverUri = args[1];
			String registerAction = "register";
			String updateAction = "update";
			String observeAction = "observe";
			EPC[] announceEPCLst = new EPC[]{EPC.x80, EPC.x81,EPC.x88};
			
			HomeGateway hgw = new HomeGateway(cardNetwork, serverUri, registerAction, updateAction,
					observeAction, announceEPCLst);
			if(!hgw.inital()){
				logger.error("Home gateway inital failed, program will exit!");
				logger.info("Program ended");
				return;
			}
			hgw.run();
			
		} else {
			System.out.println(usage());
			logger.info("Program ended");
		}
		logger.trace("Program ended");
	}

}
