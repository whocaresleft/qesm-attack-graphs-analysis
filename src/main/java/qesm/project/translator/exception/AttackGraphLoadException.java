package qesm.project.translator.exception;

public class AttackGraphLoadException extends RuntimeException {

	private static final long serialVersionUID = -2720482178900909621L;
	
	private Exception captured;
	
	public AttackGraphLoadException(String message, Exception e) {
		super(message);
		this.captured = e;
	}
	
	public AttackGraphLoadException(String message) {
		super(message);
		this.captured = null;
	}
	
	@Override
	public String getMessage() {
		return getMessage() + captured != null ? (": " + captured.getMessage()) : ("");
	}
}
