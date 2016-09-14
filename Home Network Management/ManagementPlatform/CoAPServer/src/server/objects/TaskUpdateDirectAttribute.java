package server.objects;

import java.util.Date;

/**
 * @author Cu Pham
 *
 */
public class TaskUpdateDirectAttribute {
	// direct attribute object
	private DirectAttributeObject directAttribute;
	// time
	private Date time;
	
	public TaskUpdateDirectAttribute(){
	}

	public DirectAttributeObject getDirectAttribute() {
		return directAttribute;
	}

	public void setDirectAttribute(DirectAttributeObject directAttribute) {
		this.directAttribute = directAttribute;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
