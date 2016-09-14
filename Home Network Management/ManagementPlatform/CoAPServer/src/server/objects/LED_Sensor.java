package server.objects;

import echowand.object.EchonetObjectException;
import echowand.object.RemoteObject;
import echowand.objects.EchonetDataObject;

/**
 * @author Cu Pham
 *
 */
public class LED_Sensor extends EchonetDataObject {

	private boolean ledON = false;
	
	/**
	 * @param ledON the ledON to set
	 */
	public void setLedON(boolean ledON) {
		this.ledON = ledON;
	}

	public boolean isLedON() {
		return ledON;
	}
	
	public LED_Sensor() {
	}
	
	@Override
	public void ParseDataFromRemoteObject(RemoteObject arg0) throws EchonetObjectException {
		
	}

	@Override
	public String ToString() {
		return this.toString();
	}

}
