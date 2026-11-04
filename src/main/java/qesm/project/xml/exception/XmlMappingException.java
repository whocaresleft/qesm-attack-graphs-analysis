package qesm.project.xml.exception;

public class XmlMappingException extends RuntimeException {
	
	private static final long serialVersionUID = -2720482178900909622L;
	
	private final Throwable cause;

	public XmlMappingException(String message) {
		this(message, null);
	}
	
	public XmlMappingException(String message, Throwable cause) {
		super("Error while mapping: " + message);
		this.cause = cause;
	}
	
	@Override
	public Throwable getCause() { return cause; }
}
