package qesm.project.xml.exception;

public class InvalidUuidFormatException extends IllegalArgumentException {

	private static final long serialVersionUID = -2720482178900909623L;

	private final String rawUuid;

	public InvalidUuidFormatException(String rawUuid, Throwable cause) {
		super("Invalid UUID: '" + rawUuid + "'", cause);
		this.rawUuid = rawUuid;
	}

	public String getRawUuid() { return rawUuid; }
}
