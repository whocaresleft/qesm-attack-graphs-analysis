package qesm.project.utility;

public sealed interface Distribution permits Exponential, NoneDistribution {
	public double mean();
	public double cfd(double t);
}