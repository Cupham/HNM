package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import config.ServerConfig;

/**
 * @author Cu Pham
 *
 */
@WebServlet(name = "UPnPResource", urlPatterns = "/UPnPResource")
public class UPnPResourceServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String resource = request.getParameter("resource");
			if (resource == null || "".equals(resource)){
				request.setAttribute("error", "Resource is null or empty.");
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/views/error.jsp");
				dispatcher.forward(request, response);
			} else {
				// get serverip, serverport
				ServerConfig constanst = ServerConfig.getInstance();
				String serverIP = constanst.getSvIP();
				String serverPort = constanst.getSvPort();
				
				CoapClient client = new CoapClient("coap://" + serverIP + ":" + serverPort + "/"+ resource);
				// send a get request to coapserver and get response
				CoapResponse coapResponse = client.get();
				if (!coapResponse.isSuccess()){
					request.setAttribute("error", "Resource is not found.");
					RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/views/error.jsp");
					dispatcher.forward(request, response);
				} else {
					String content = coapResponse.getResponseText();
					// Set response content type
					response.setContentType("text/xml");
					PrintWriter out = response.getWriter();
					out.print(content);
				}
			}	
		} catch(Exception ex){
			ex.printStackTrace();
			// if error occurs, forward to page error
			request.setAttribute("error", ex.getMessage());
			RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/views/error.jsp");
			dispatcher.forward(request, response);
		}	
	}
}
