package qesm.project.graph.exception;

public class StepProbabilitySumUnderOneException extends RuntimeException {
	
	private final double overall;
	
	private static final long serialVersionUID = 4416710116347400110L;
	
	public StepProbabilitySumUnderOneException(double overall) {
		super(String.format("The three probabilities are under 1.000000 (%f)", overall));
		this.overall = overall;
	}
	
	public double getOverallProbabilities() { return overall; }
}
