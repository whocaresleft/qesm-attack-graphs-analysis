package qesm.project.attack_graph.exception;

public class OverallProbabilitiesOverOneException extends RuntimeException {
	
	private final double overall;
	
	private static final long serialVersionUID = 4416710116347400169L;
	
	public OverallProbabilitiesOverOneException(double overall) {
		super(String.format("The three probabilities are over 1.000000 (%f)", overall));
		this.overall = overall;
	}
	
	public double getOverallProbabilities() { return overall; }
}