package qesm.project.common;

public final class NoneDistribution implements Distribution {
	
	private static final NoneDistribution INSTANCE = new NoneDistribution();
	
	private NoneDistribution() {}
	
	public static NoneDistribution get() {
		return INSTANCE;
	}
	
	@Override
	public double mean() { return Double.POSITIVE_INFINITY; }
	
	@Override
	public double cdf(double t) { return 0.0; }
	
	@Override
	public int hashCode() {
		return NoneDistribution.class.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof NoneDistribution;
	}
}
