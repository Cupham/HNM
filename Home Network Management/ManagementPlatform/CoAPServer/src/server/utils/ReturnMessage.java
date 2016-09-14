package server.utils;

/**
 * @author Cu Pham
 *
 */
public class ReturnMessage {
	private boolean result;
	private String message;
	
	public ReturnMessage(){
	}
	
	public ReturnMessage(boolean result, String message){
		this.result = result;
		this.message = message;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
