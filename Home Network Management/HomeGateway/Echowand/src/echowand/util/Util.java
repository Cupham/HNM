package echowand.util;

import java.util.LinkedList;
import java.util.List;

import echowand.common.EOJ;
import echowand.common.EPC;
import echowand.object.EchonetObjectException;
import echowand.object.RemoteObject;
import echowand.objects.EchonetDevice;
import echowand.objects.EchonetProfileObject;
import echowand.service.result.GetResult;

/**
 * @author Cu Pham
 *
 */
public class Util extends EchonetProfileObject {
	public static List<EchonetProfileObject> objectListParser(GetResult r) {

		EchonetProfileObject eobj = new EchonetProfileObject();
		List<EchonetProfileObject> eobjList = new LinkedList<EchonetProfileObject>();

		for (int i = 0; i < r.countData(); i++) {

			if (r.getData(i).epc == EPC.x80) {
				if (r.getData(i).data.toString().equals("30")) {
					eobj.setOperationStatus(true);
				} else {
					eobj.setOperationStatus(false);
				}

			}
			eobjList.add(eobj);
		}

		return eobjList;
	}

	public static EchonetProfileObject objectParser(GetResult r) {

		EchonetProfileObject eobj = new EchonetProfileObject();

		for (int i = 0; i < r.countData(); i++) {

			if (r.getData(i).epc == EPC.x80) {
				if (r.getData(i).data.toString().equals("30")) {
					eobj.setOperationStatus(true);
				} else {
					eobj.setOperationStatus(false);
				}

			}
		}
		return eobj;
	}
}
