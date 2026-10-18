package qesm.project.analysis.attack_graph.exception;

public class OverallProbabilitiesOverOneException extends RuntimeException {
	
	private static final long serialVersionUID = 4416710116347400109L;
	
	public OverallProbabilitiesOverOneException(double overall) {
		super(String.format("The three probabilities are over 1.000000: %f", overall));
	}
}