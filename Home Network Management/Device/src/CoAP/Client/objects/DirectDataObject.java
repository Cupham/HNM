/**
 * 
 */
package CoAP.Client.objects;

import com.google.gson.Gson;

/**
 * @author Cu Pham
 *
 */
public abstract class DirectDataObject {
	protected byte groupCode;
	protected byte classCode;
	protected byte instanceCode;
	protected boolean operationStatus;

	public boolean isOperationStatus() {
		return operationStatus;
	}
	
	public void setOperationStatus(boolean operationStatus) {
		this.operationStatus = operationStatus;
	}

	public byte getGroupCode() {
		return groupCode;
	}

	public byte getClassCode() {
		return classCode;
	}

	public byte getInstanceCode() {
		return instanceCode;
	}

	public DirectDataObject() {
		this.operationStatus = false;
	}
	
	public DirectDataObject(boolean operationStatus) {
		this.operationStatus = operationStatus;
	}

	public abstract String ToString();
	
	public abstract String toJson();
	
	public abstract int delegateGetValue();
	
	public abstract void delegateSetValue(byte[] value);
	
	public abstract boolean delegateNotify();
	
}
