package server.run;

import java.util.Date;

import org.apache.log4j.Logger;

import server.config.CONSTANT;
import server.objects.TaskUpdateAttribute;
import server.objects.TaskUpdateDirectAttribute;
import server.resources.CoapObserveResource;
import server.resources.ObserveResource;
import server.service.Service;

/**
 * @author Cu Pham
 *
 */
public class CheckTaskUpdateThread extends Thread {
	// logger
	final static Logger logger = Logger.getLogger(CheckTaskUpdateThread.class);
	
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(CONSTANT.CHECK_QUEUE_TIME);
				// task update attribute
				TaskUpdateAttribute taskUpdateAttribute = ObserveResource.getTaskUpdateAttribute().peek();
				if (taskUpdateAttribute != null) {
					// print attribute info
					// used for debug
					/*logger.debug("queue TaskUpdateAttribute contains " + ObserveResource.getTaskUpdateAttribute().size() + " element(s)");
					logger.debug("current TaskUpdateAttribute: " + taskUpdateAttribute.getAttribute().getIp() 
							+ "_" + taskUpdateAttribute.getAttribute().getGroupCode() 
							+ "_" + taskUpdateAttribute.getAttribute().getClassCode()
							+ "_" + taskUpdateAttribute.getAttribute().getInstanceCode()
							+ "_" + taskUpdateAttribute.getAttribute().getEpc()
							+ "_" + taskUpdateAttribute.getAttribute().getValue()
							+ "_" + taskUpdateAttribute.getTime().toString());*/
					
					Date currentTime = Service.getCurrentTime();
					long diff = currentTime.getTime() - taskUpdateAttribute.getTime().getTime();
					if (diff >= CONSTANT.TASK_UPDATE_TIME_OUT)
						ObserveResource.getTaskUpdateAttribute().poll();
				}
				// task update direct attribute
				TaskUpdateDirectAttribute taskUpdateDirectAttribute = CoapObserveResource.getTaskUpdateDirectAttribute().peek();
				if (taskUpdateDirectAttribute != null) {
					// print attribute info
					// used for debug
					/*logger.debug("queue TaskUpdateDirectAttribute contains " + CoapObserveResource.getTaskUpdateDirectAttribute().size() + " element(s)");
					logger.debug("current TaskUpdateDirectAttribute: " + taskUpdateDirectAttribute.getDirectAttribute().getIp() 
							+ "_" + taskUpdateDirectAttribute.getDirectAttribute().getAttributeName()
							+ "_" + taskUpdateDirectAttribute.getDirectAttribute().getValue()
							+ "_" + taskUpdateDirectAttribute.getTime().toString());*/
					
					Date currentTime = Service.getCurrentTime();
					long diff = currentTime.getTime() - taskUpdateDirectAttribute.getTime().getTime();
					if (diff >= CONSTANT.TASK_UPDATE_TIME_OUT)
						CoapObserveResource.getTaskUpdateDirectAttribute().poll();
				}
			} catch (InterruptedException e) {
			}
		}
	}
}
