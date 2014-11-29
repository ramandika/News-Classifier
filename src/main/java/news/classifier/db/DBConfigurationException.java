package news.classifier.db;

public class DBConfigurationException extends RuntimeException {

	/**
	 * serialVersionUID of the class 
	 */
	private static final long serialVersionUID = 1L;
	
	public DBConfigurationException(String message) {
		super(message);
	}
	
	public DBConfigurationException(Throwable cause) {
		super(cause);
	}
	
	public DBConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
