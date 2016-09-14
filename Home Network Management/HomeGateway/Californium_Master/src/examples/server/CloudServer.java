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
 *    Kai Hudalla (Bosch Software Innovations GmbH) - add endpoints for all IP addresses
 ******************************************************************************/
package examples.server;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;


public class CloudServer extends CoapServer {

	private static final int COAP_PORT = NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT);
    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        
        try {
            // create server
        	CloudServer server = new CloudServer();
            // add endpoints on all IP addresses
            server.addEndpoints();
            server.start();
                
        } catch (SocketException e) {
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
    }

    /**
     * Add individual endpoints listening on default CoAP port on all IPv4 addresses of all network interfaces.
     */
    private void addEndpoints() {
    	for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
    		// only binds to IPv4 addresses and localhost
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
    }

    /**
     * Constructor for a new Cloud Server. Here, the resources
     * of the server are initialized.
     */
    public CloudServer() throws SocketException {
        
        // provide an instance of a Cloud Server resource
        add(new CloudServerResource());
    }

    /**
     * Definition of the Hello-World Resource
     */
    class CloudServerResource extends CoapResource {
        
        public CloudServerResource() {
            
            // set resource identifier
            super("cloudServer");	//se doi cho nay
            
            // set display name
            getAttributes().setTitle("Cloud Server Resource");
        }

        @Override
        public void handleGET(CoapExchange exchange) {
            
            // respond to the request
            exchange.respond("Company Name: KIOTEC");
        }
        @Override
        public void handlePOST(CoapExchange exchange){
            exchange.accept();
            String requestText = exchange.getRequestText();
            exchange.respond(requestText);
            System.out.println(requestText);
        }
    }
}

