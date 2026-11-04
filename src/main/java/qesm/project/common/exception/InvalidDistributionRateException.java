package qesm.project.common.exception;

public class InvalidDistributionRateException extends RuntimeException {
	
	private final double rate;
	
	private static final long serialVersionUID = 4416710116347400111L;
	
	public InvalidDistributionRateException(double rate) {
		super(String.format("Rate is not valid (%f)", rate));
		this.rate = rate;
	}
	
	public double getRate() { return rate; }
}
