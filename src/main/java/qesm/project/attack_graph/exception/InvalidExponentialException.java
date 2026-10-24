package qesm.project.attack_graph.exception;

public class InvalidExponentialException extends RuntimeException {
	
	private final double rate;
	
	private static final long serialVersionUID = 4416710116347400111L;
	
	public InvalidExponentialException(double rate) {
		super(String.format("Rate is not valid (%f)", rate));
		this.rate = rate;
	}
	
	public double getRate() { return rate; }
}
