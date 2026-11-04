package qesm.project.xml.exception;

public class XmlLoadingException extends RuntimeException {

	private static final long serialVersionUID = -2720482178900909621L;
	
	private final Throwable cause;

	public XmlLoadingException(String message) {
		this(message, null);
	}
	
	public XmlLoadingException(String message, Throwable cause) {
		super("Error while loading: " + message);
		this.cause = cause;
	}
	
	@Override
	public Throwable getCause() { return cause; }
}
