package news.classifier.db;

import java.io.IOException;
import java.io.Serializable;

import weka.core.Instances;
import weka.core.converters.DatabaseLoader;

public class DBLoader implements Serializable {

	private static final String SQL_QUERY = "SELECT * FROM classify";
	
	private DatabaseLoader loader;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9112177108888654432L;
	
	public DBLoader() {
		DBProperties properties = new DBProperties("news.classifier");
		
		String dbUrl = properties.getProperty("url", true);
		String dbUsername = properties.getProperty("username", true);
		String dbPassword = properties.getProperty("password", false);
		
		try {
			loader = new DatabaseLoader();
			loader.setSource(dbUrl, dbUsername, dbPassword);
			loader.setQuery(SQL_QUERY);
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
	
	public Instances getDataSet() throws IOException {
		return loader.getDataSet();
	}
}
