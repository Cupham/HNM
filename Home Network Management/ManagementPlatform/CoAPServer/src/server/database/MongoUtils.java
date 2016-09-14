package server.database;


import java.io.IOException;
import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * @author Cu Pham
 *
 */
public class MongoUtils {
	// connect to mongodb without authentication
	public static MongoClient getMongoClient() throws UnknownHostException, IOException {
		DBConfig dbConfig = DBConfig.getInstance();
		MongoClient mongoClient = new MongoClient(dbConfig.getHost(), dbConfig.getPort());
		return mongoClient;
	}

	// connect to db
	@SuppressWarnings({ "deprecation" })
	public static DB getDB(MongoClient mongoClient) throws IOException {
		DBConfig constant = DBConfig.getInstance();
		return mongoClient.getDB(constant.getDbName());
	}

	// close db
	public static void closeMongoClient(MongoClient mongoClient) {
		if (mongoClient != null)
			mongoClient.close();
	}
}
