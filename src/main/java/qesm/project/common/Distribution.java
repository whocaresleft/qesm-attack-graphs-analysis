package qesm.project.common;

public sealed interface Distribution permits Exponential, NoneDistribution {
	public double mean();
	public double cdf(double t);
}
