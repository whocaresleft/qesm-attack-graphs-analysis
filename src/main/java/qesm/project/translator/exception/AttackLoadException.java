package qesm.project.translator.exception;

public class AttackLoadException extends RuntimeException {

	private static final long serialVersionUID = -2720482178900909621L;
	
	private final Throwable cause;

	public AttackLoadException(String message) {
		super(message);
		this.cause = null;
	}
	
	public AttackLoadException(String message, Throwable cause) {
		super(message);
		this.cause = cause;
	}
	
	public Throwable getCause() { return cause; }
}
