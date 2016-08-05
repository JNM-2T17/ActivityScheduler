package security;

public class MissingTokenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 152061344301570510L;
	public MissingTokenException() {
		super("Missing token");
	}
}
