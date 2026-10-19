package qesm.project.attack_graph.exception;

public class OverallProbabilitiesUnderOneException extends RuntimeException {
	
	private static final long serialVersionUID = 4416710116347400110L;
	
	public OverallProbabilitiesUnderOneException(double overall) {
		super(String.format("The three probabilities are under 1.000000: %f", overall));
	}
}