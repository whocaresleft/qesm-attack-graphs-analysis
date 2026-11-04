package qesm.project.graph.exception;

public class NegativeStepProbabilityException extends RuntimeException {
	
	private final double probability;
	
	private static final long serialVersionUID = 4416710116347400109L;
	
	public NegativeStepProbabilityException(double probability) {
		super(String.format("Probability is negative (%f)", probability));
		this.probability = probability;
	}
	
	public double getProbability() { return probability; }
}
