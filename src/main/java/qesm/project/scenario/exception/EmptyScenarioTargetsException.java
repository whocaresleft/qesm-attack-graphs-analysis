package qesm.project.scenario.exception;

public class EmptyScenarioTargetsException extends RuntimeException {

	private static final long serialVersionUID = 4416710116347400202L;

	public EmptyScenarioTargetsException() {
		super("Must contain at least 1 target");
	}
}
