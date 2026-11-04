package qesm.project.scenario.exception;

public class InvalidScenarioTimeBoundException extends RuntimeException {

	private static final long serialVersionUID = 4416710116347400201L;

	private final double timeBound;

	public InvalidScenarioTimeBoundException(double timeBound) {
		super("Illegal duration, must be positive or infinite: " + timeBound);
		this.timeBound = timeBound;
	}

	public double getTimeBound() { return timeBound; }
}
