package qesm.project.graph.exception;

public class StepProbabilitySumOverOneException extends RuntimeException {
	
	private final double overall;
	
	private static final long serialVersionUID = 4416710116347400169L;
	
	public StepProbabilitySumOverOneException(double overall) {
		super(String.format("The three probabilities are over 1.000000 (%f)", overall));
		this.overall = overall;
	}
	
	public double getOverallProbabilities() { return overall; }
}
