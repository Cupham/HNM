package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

import config.ServerConfig;
import util.Utils;

/**
 * @author Cu Pham
 *
 */
@WebServlet(name = "DownloadDeviceStatus", urlPatterns = "/download")
public class DownloadDeviceStatusServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// get serverip, serverport
			ServerConfig constanst = ServerConfig.getInstance();
			String serverIP = constanst.getSvIP();
			String serverPort = constanst.getSvPort();
			
			// access to coap server
			CoapClient client = new CoapClient("coap://" + serverIP + ":" + serverPort + "/getdevicestatus");
			CoapResponse coapResponse = client.get();
			if (coapResponse == null || !coapResponse.isSuccess()) {
				// if error occurs, forward to page error
				RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/views/error.jsp");
				request.setAttribute("error", "Connect to Coapserver failed.");
				dispatcher.forward(request, response);
			} else {
				// reads input file from an absolute path
				String fileName = "device-status.xml";
				File downloadFile = new File(fileName);
				Utils.writeFile(downloadFile, coapResponse.getResponseText());
				
				FileInputStream inStream = new FileInputStream(downloadFile);
				// if you want to use a relative path to context root:
				String relativePath = getServletContext().getRealPath("");
				System.out.println("relativePath = " + relativePath);

				// obtains ServletContext
				ServletContext context = getServletContext();

				// gets MIME type of the file
				String mimeType = context.getMimeType(fileName);
				if (mimeType == null) {
					// set to binary type if MIME mapping not found
					mimeType = "application/octet-stream";
				}
				System.out.println("MIME type: " + mimeType);

				// modifies response
				response.setContentType(mimeType);
				response.setContentLength((int) downloadFile.length());

				// forces download
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
				response.setHeader(headerKey, headerValue);

				// obtains response's output stream
				OutputStream outStream = response.getOutputStream();

				byte[] buffer = new byte[4096];
				int bytesRead = -1;

				while ((bytesRead = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}

				inStream.close();
				outStream.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			// if error occurs, forward to page error
			RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/WEB-INF/views/error.jsp");
			request.setAttribute("error", ex.getMessage());
			dispatcher.forward(request, response);
		}
	}

}
