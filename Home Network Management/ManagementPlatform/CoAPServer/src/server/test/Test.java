package server.test;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 * @author Cu Pham
 *
 */
public class Test {
	public static void main(String[] agrs) {
		try {
			 // test register device 
			/*CoapClient client = new CoapClient("coap://192.168.0.5:5683/register");
			String json = "[ {'profile' : { 'deviceName' : '192.168.0.103' , 'deviceIP' : '192.168.0.103' , 'groupCode' : 14 , 'classCode' : -16 , 'instanceCode' : 1 , 'operationStatus' : true , 'installLocation' : 'living room' , 'standardVersionInfo' : '0.1 (K) 16' , 'identificationNumber' : 'Ethernet 223' , 'instantaneousPower' : 570 , 'cumulativePower' : 33818120 , 'manufactureerFaultCode' : 'KIO - E01' , 'currentLimitSetting' : 70 , 'faultStatus' : false , 'faultDescription' : 'No Fault' , 'manufacturerCode' : 'KIO' , 'productCode' : 'Intel NUC' , 'productNumber' : 'NUC 1' , 'productDate' : 'Jan 25, 2001 10:31:00 AM' , 'powerSaving' : true , 'throughPublicNetwork' : false , 'currentTimeSetting' : '10:29' , 'currentDateSetting' : 'Aug 25, 2016 10:31:00 AM' , 'powerLimit' : 16912 , 'cumulativeTime' : '160 hours'} , 'eObjList' : [ { 'temperature' : 255 , 'groupCode' : 0 , 'classCode' : 17 , 'instanceCode' : 1 , 'operationStatus' : false} , { 'humidity' : 50.0 , 'groupCode' : 0 , 'classCode' : 18 , 'instanceCode' : 1 , 'operationStatus' : true} , { 'temperature' : 255 , 'groupCode' : 0 , 'classCode' : 17 , 'instanceCode' : 2 , 'operationStatus' : true}] , 'deviceType' : 'EchonetLiteDevice'} , { 'profile' : { 'deviceName' : '192.168.0.108' , 'deviceIP' : '192.168.0.108' , 'groupCode' : 14 , 'classCode' : -16 , 'instanceCode' : 1 , 'operationStatus' : true , 'standardVersionInfo' : '1.1 () 0' , 'identificationNumber' : 'Undefined' , 'instantaneousPower' : 0 , 'cumulativePower' : 0 , 'currentLimitSetting' : 0 , 'faultStatus' : false , 'powerSaving' : false , 'throughPublicNetwork' : false , 'currentTimeSetting' : '00:00' , 'currentDateSetting' : 'Sep 3, 2016 12:00:51 AM' , 'powerLimit' : 0} , 'eObjList' : [ ] , 'deviceType' : 'HomeGateway'}]";
			CoapResponse coapResponse = client.put(json, MediaTypeRegistry.APPLICATION_JSON);
			if (coapResponse != null)
				System.out.println(coapResponse.getResponseText());*/
			 

			// test register direct device
			/*CoapClient client = new CoapClient("coap://192.168.0.5:5683/CoAPRegister");
			String json = "{'deviceType':'DirectDevice', 'profile':{'deviceIP':'192.168.0.108','groupCode':14,'classCode':-16,'instanceCode':1,'operationStatus':true,'installLocation':'Living Room','standardVersionInfo':'1.1 K1','identificationNumber':'IEEE801.11/11b-19','instantaneousPower':5,'cumulativePower':200,'manufactureerFaultCode':'0000','currentLimitSetting':1000,'faultStatus':false,'faultDescription':'0000','manufacturerCode':'NEC','businessFacilityCode':'KIO','productCode':'LM35 Sensor','productNumber':'T01','productDate':'Jan 4, 2016 12:00:00 AM','powerSaving':true,'throughPublicNetwork':true,'currentTimeSetting':'16:45','powerLimit':0,'cumulativeTime':'11 hours'}, 'eObjList':[{'groupCode':0,'classCode':0,'instanceCode':0,'operationStatus':true,'ledON':false},{'groupCode':0,'classCode':17,'instanceCode':0,'operationStatus':true,'temperature':0}]}";
			CoapResponse coapResponse = client.put(json, MediaTypeRegistry.APPLICATION_JSON);
			if (coapResponse != null)
				System.out.println(coapResponse.getResponseText());*/
			 

			// test update attribute
			// create client
			/*CoapClient client = new CoapClient("coap://192.168.1.5:5683/update");
			String json = "{'ip':'192.168.0.102','groupCode':14,'classCode':-16,'instanceCode':1,'epc':-128,'value':[49]}";
			CoapResponse coapResponse = client.post(json, MediaTypeRegistry.APPLICATION_JSON);
			if (coapResponse != null)
				System.out.println(coapResponse.getResponseText());
			else
				System.out.println("coapResponse null.");*/

			// test update attribute of direct device
			// create client
			/*CoapClient client = new CoapClient("coap://192.168.0.101:5683/CoAPUpdate");
			String json = "{'ip':'192.168.0.108','groupCode':0,'classCode':17,'instanceCode':0,'epc':-32,'value':'290'}";
			CoapResponse coapResponse = client.post(json, MediaTypeRegistry.APPLICATION_JSON);
			if (coapResponse != null)
				System.out.println(coapResponse.getResponseText());
			else
				System.out.println("coapResponse null.");*/
			
			 // create client // get database
			/*CoapClient client = new CoapClient("coap://192.168.0.5:5683/getdevice");
			CoapResponse coapResponse = client.get();
			if (coapResponse != null)
				System.out.println(coapResponse.getResponseText());
			else
				System.out.println("coapResponse null.");*/
			 
			
			// create client 
			// get device status
			/*CoapClient client = new CoapClient("coap://192.168.0.5:5683/getdevice");
			CoapResponse coapResponse = client.get();
			if (coapResponse != null)
				System.out.println(coapResponse.getResponseText());
			else
				System.out.println("coapResponse null.");*/
			
			// test get device status
			/*CoapClient client = new CoapClient("coap://192.168.1.7:5683/getdevicestatus");
			CoapResponse coapResponse = client.get();
			if (coapResponse != null)
				System.out.println(coapResponse.getResponseText());*/
			
			// stress test
			/*String json1 = "{'ip':'192.168.0.103','groupCode':14,'classCode':-16,'instanceCode':1,'epc':-128,'value':'false'}";
			String json2 = "{'ip':'192.168.0.103','groupCode':14,'classCode':-16,'instanceCode':1,'epc':-128,'value':'true'}";
			int i = 0;
			while (i < 50){
				CoapClient client = new CoapClient("coap://192.168.0.101:5683/observe");
				CoapResponse response = client.post(json1, MediaTypeRegistry.APPLICATION_JSON);
				if (response != null){
					System.out.println("off " + (i*2));
				}
				Thread.sleep(1000);
				response = client.post(json2, MediaTypeRegistry.APPLICATION_JSON);
				if (response != null){
					System.out.println("on " + (i*2+1));
				}
				Thread.sleep(1000);
				i++;
			}*/
			
			// test observe
			/*CoapClient client = new CoapClient("coap://192.168.0.102:5683/observe");
			
			CoapObserveRelation relation = client.observe(new CoapHandler() {
				
				@Override
				public void onLoad(CoapResponse response) {
					System.out.println("Server responses: " + response.getResponseText());
				}
				
				@Override
				public void onError() {
					System.out.println("Error");
				}
			});
			
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				public void run() {
					System.out.println("Check relation");
					if (relation != null && !relation.isCanceled()){
						System.out.println("Cancel relation");
						relation.proactiveCancel();
					}
				}
			}));
			
			while (true){ }*/
			
			// test upnp resource 
			/*CoapClient client = new CoapClient("coap://192.168.0.101:5683/echonetProfile.xml");
			CoapResponse coapResponse = client.get();
			if (coapResponse.isSuccess())
				System.out.println(coapResponse.getResponseText());
			else
				System.out.println("FAILED");*/
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
