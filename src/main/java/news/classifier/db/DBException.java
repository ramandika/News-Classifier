package news.classifier.db;


public class DBException extends RuntimeException {

	/**
	 * serialVersionUID of the class 
	 */
	private static final long serialVersionUID = 1L;
	
	public DBException(String message) {
		super(message);
	}
	
	public DBException(Throwable cause) {
		super(cause);
	}
	
	public DBException(String message, Throwable cause) {
		super(message, cause);
	}
}
