package qesm.project.translator.exception;

public class AttackLoadException extends RuntimeException {

	private static final long serialVersionUID = -2720482178900909621L;
	
	private final Throwable cause;

	public AttackLoadException(String message) {
		this(message, null);
	}
	
	public AttackLoadException(String message, Throwable cause) {
		super("Error while loading: " + message);
		this.cause = cause;
	}
	
	public Throwable getCause() { return cause; }
}
