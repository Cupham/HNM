/**
 * 
 */
package echowand.objects.announcement;

/**
 * @author Cu Pham
 *
 */
public class RequestUpdate {
	
	private String requestContent;
	private long startTime;
	/**
	 * @return the requestContent
	 */
	public String getRequestContent() {
		return requestContent;
	}
	/**
	 * @param requestContent the requestContent to set
	 */
	public void setRequestContent(String requestContent) {
		this.requestContent = requestContent;
	}
	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public RequestUpdate(String requestContent, long startTime){
		this.requestContent = requestContent;
		this.startTime = startTime;
	}

}
