package server.objects;

import java.util.ArrayList;

import CoAP.Client.objects.DirectDataObject;
import echowand.objects.EchonetProfileObject;
import echowand.objects.EchonetDevice.DeviceType;

/**
 * @author Cu Pham
 *
 */
public class DirectDevice {

	private EchonetProfileObject profile;
	private ArrayList<DirectDataObject> eObjList;
	private DeviceType deviceType;
	
	/**
	 * @return the deviceType
	 */
	public DeviceType getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * @return the profile
	 */
	public EchonetProfileObject getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(EchonetProfileObject profile) {
		this.profile = profile;
	}

	/**
	 * @return the eObjList
	 */
	public ArrayList<DirectDataObject> geteObjList() {
		return eObjList;
	}

	/**
	 * @param eObjList the eObjList to set
	 */
	public void seteObjList(ArrayList<DirectDataObject> eObjList) {
		this.eObjList = eObjList;
	}
	
	
}
