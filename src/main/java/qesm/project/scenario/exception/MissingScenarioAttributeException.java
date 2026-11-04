package qesm.project.scenario.exception;

public class MissingScenarioAttributeException extends RuntimeException {

	private static final long serialVersionUID = 4416710116347400204L;

	private final String attributeName;

	public MissingScenarioAttributeException(String attributeName) {
		super("Missing " + attributeName);
		this.attributeName = attributeName;
	}

	public String getAttributeName() { return attributeName; }
}
