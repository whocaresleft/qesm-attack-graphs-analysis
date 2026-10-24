package qesm.project.translator.exception;

public class AttackMapException extends RuntimeException {
	
	private static final long serialVersionUID = -2720482178900909622L;
	
	private final Throwable cause;

	public AttackMapException(String message) {
		this(message, null);
	}
	
	public AttackMapException(String message, Throwable cause) {
		super("Error while mapping: "+ message);
		this.cause = cause;
	}
	
	public Throwable getCause() { return cause; }
}
