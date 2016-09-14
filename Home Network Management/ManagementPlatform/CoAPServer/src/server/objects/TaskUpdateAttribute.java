package server.objects;

import java.util.Date;

/**
 * @author Cu Pham
 *
 */
public class TaskUpdateAttribute {
	// attribute object
	private AttributeObject attribute;
	// time
	private Date time;
	
	public TaskUpdateAttribute(){
	}
	
	public AttributeObject getAttribute(){
		return this.attribute;
	}
	
	public void setAttribute(AttributeObject attribute){
		this.attribute = attribute;
	}
	
	public Date getTime(){
		return this.time;
	}
	
	public void setTime(Date time){
		this.time = time;
	}
}
