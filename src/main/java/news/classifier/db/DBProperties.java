package news.classifier.db;

import java.io.*;
import java.util.Properties;

/**
 * This class immediately loads the DB properties file 'database.properties' once in memory and provides
 * a constructor which takes the specific key which is to be used as property key prefix of the DB
 * properties file. There is a property getter which only returns the property prefixed with
 * 'specificKey.' and provides the option to indicate whether the property is mandatory or not.
 * 
 * Since WEKA has its own DatabaseLoader class, only the properties file is used
 *
 * @author BalusC, Alvin Natawiguna
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 * @link https://github.com/alvin-nt/News-Classifier
 */

public class DBProperties {
	// Constants
	private static final String PROPERTIES_FILE = "database.properties";
	private static final Properties PROPERTIES = new Properties();
	
	static {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream propertiesFile = classLoader.getResourceAsStream(PROPERTIES_FILE);
		
		if(propertiesFile == null) {
			throw new DBConfigurationException("Properties file " + PROPERTIES_FILE + " is missing in classpath");
		}
		
		try {
			PROPERTIES.load(propertiesFile);
		} catch (IOException e) {
			throw new DBConfigurationException("Cannot load properties file " + PROPERTIES_FILE, e);
		}
	}
	
	// vars
	private String specificKey;
	
	public DBProperties(String specificKey) {
		this.specificKey = specificKey;
	}
	
	public String getProperty(String key, boolean mandatory) throws DBConfigurationException {
		String fullKey = specificKey + "." + key;
        String property = PROPERTIES.getProperty(fullKey);

        if (property == null || property.trim().length() == 0) {
            if (mandatory) {
                throw new DBConfigurationException("Required property '" + fullKey + "'"
                    + " is missing in properties file '" + PROPERTIES_FILE + "'.");
            } else {
                // Make empty value null. Empty Strings are evil.
                property = null;
            }
        }

        return property;
	}
}