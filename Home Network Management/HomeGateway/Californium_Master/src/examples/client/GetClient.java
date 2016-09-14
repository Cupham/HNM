/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 ******************************************************************************/
package examples.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;


public class GetClient {

	/**
	 * Application entry point.
	 * 
	 */	
	public static void main(String args[]) {
		
		URI uri = null; // URI parameter of the request
		//URI uri = new URI("coap://192.168.0.102/helloWorld");
		
		if (args.length > 0) {
			
			// input URI from command line arguments
			try {
				uri = new URI(args[0]);
			} catch (URISyntaxException e) {
				System.err.println("Invalid URI: " + e.getMessage());
				System.exit(-1);
			}
			
			CoapClient client = new CoapClient(uri+"/helloWorld"); 
            String outValue = "ABCDEF";                           
			CoapResponse response = client.post(outValue, 1);
                        if (response!=null){ 
				System.out.println("Result: " +response.getResponseText());
			} else {
				System.out.println("No response received.");
			}
		}
    }
}