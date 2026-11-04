package qesm.project.graph.exception;

public class MissingStepDurationException extends RuntimeException {

	private static final long serialVersionUID = 4416710116347400112L;

	public MissingStepDurationException() {
		super("Missing duration");
	}
}
