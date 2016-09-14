package server.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import server.utils.Utils;

/**
 * @author Cu Pham
 *
 */
public class DBConfig {
	/**
	 * file database configuration
	 */
	final static String DB_FILE_NAME = "db.config";
	/**
	 * mongodb default host
	 */
	final static String DEFAULT_HOST = "localhost";
	/**
	 * mongodb default port
	 */
	final static String DEFAULT_PORT = "27017";
	/**
	 * default database name
	 */
	final static String DEFAULT_DB_NAME = "DeviceDb";

	/**
	 * host name
	 */
	private String host;
	/**
	 * port
	 */
	private int port;
	/**
	 * database name
	 */
	private String dbName;
	/**
	 * singleton
	 */
	private static DBConfig instance = null;
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * get instance of class, use singleton
	 */
	public static DBConfig getInstance() throws IOException {
		if (instance == null) {
			instance = new DBConfig();
			// read parameters from file db.config
			Map<String, String> map = DBConfig.getDBConfig();
			instance.setHost(map.get("host"));
			instance.setPort(Integer.parseInt(map.get("port")));
			instance.setDbName(map.get("dbname"));
		}
		return instance;
	}

	/**
	 * Read parameters from file db.config
	 * 
	 * @return constants in file db.config
	 * @see java.util.Map
	 */
	private static Map<String, String> getDBConfig() throws IOException {
		// if file is not exist, create a new file
		File file = new File(DB_FILE_NAME);
		if (file.exists()) {
			return readFile(file.getAbsolutePath());
		} else {
			if (file.createNewFile()) {
				// write file
				String content = "host: " + DEFAULT_HOST + "\r\nport: " + DEFAULT_PORT + "\r\ndbname: "
						+ DEFAULT_DB_NAME;
				Utils.writeFile(file, content);
			}

			// new a hash map
			// read default value and put to map
			Map<String, String> map = new HashMap<String, String>();
			System.out.println("default host: " + DEFAULT_HOST);
			map.put("host", DEFAULT_HOST);
			System.out.println("default port: " + DEFAULT_PORT);
			map.put("port", DEFAULT_PORT);
			System.out.println("default dbname: " + DEFAULT_DB_NAME);
			map.put("dbname", DEFAULT_DB_NAME);

			return map;
		}
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
		// read file
		BufferedReader dat = new BufferedReader(new FileReader(fileName));
		String line = dat.readLine();
		while (line != null) {
			// put each parameter to hashmap
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

	
}
