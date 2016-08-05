package security;

public class AuthenticationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6109823748361545040L;

	public AuthenticationException() {
		super("Authentication failed");
	}
}
