package config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Cu Pham
 *
 */
public class ServerConfig {
	// singleton
	private static ServerConfig instance = null;
	// file server.config
	private static String FILE_SERVER_CONFIG = "server.config";
	// coap server ip
	private String svIP;
	// coap server port
	private String svPort;

	public String getSvIP() {
		return svIP;
	}

	public void setSvIP(String svIP) {
		this.svIP = svIP;
	}

	public String getSvPort() {
		return svPort;
	}

	public void setSvPort(String svPort) {
		this.svPort = svPort;
	}

	/**
	 * get instance of class, use singleton
	 */
	public static ServerConfig getInstance() throws IOException {
		if (instance == null) {
			instance = new ServerConfig();
			
			// read parameters from file server.config
			Map<String, String> map = ServerConfig.getServerConfig();
			instance.setSvIP(map.get("svIP"));
			instance.setSvPort(map.get("svPort"));
		}
		return instance;
	}

	/**
	 * Read file config
	 * 
	 * @param fileName
	 *            name of file
	 * @return constants in file config
	 * @see java.util.Map
	 */
	private static Map<String, String> readFile(String fileName) throws IOException {
		// new a hash map
		Map<String, String> map = new HashMap<String, String>();
		// get file path
		String filePath = null;
		filePath = ServerConfig.class.getResource(fileName).getFile();
		// replace space with %20
		filePath = filePath.replaceAll("%20", " ");
		//read file
		BufferedReader dat = new BufferedReader(new FileReader(filePath));
		String line = dat.readLine();
		while (line != null) {
			//put each parameter to hashmap
			String[] params = line.split(":");
			if (params.length > 1) {
				map.put(params[0].trim(), params[1].trim());
			}
			System.out.println(line);
			line = dat.readLine();
		}
		dat.close();
		return map;
	}
	
	// read file server.config
		/**
		 * Read parameters from file server.config
		 * @return constants in file server.config
		 * @see java.util.Map
		 */
	public static Map<String, String> getServerConfig() throws IOException {
		return readFile(FILE_SERVER_CONFIG);
	}
}
