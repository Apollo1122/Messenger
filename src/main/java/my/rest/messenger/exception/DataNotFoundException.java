package my.rest.messenger.exception;

public class DataNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2223577315964398704L;

	public DataNotFoundException(String message) {
		super(message);
	}	
	
}