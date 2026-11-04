package qesm.project.common;

import java.util.Objects;

import qesm.project.common.exception.InvalidDistributionRateException;

public final class Exponential implements Distribution {
	private final double rate;
	
	private Exponential(double rate) {
		this.rate = rate;
	}

	public static Exponential fromRate(double rate) throws InvalidDistributionRateException {
		if (rate <= 0 || Double.isInfinite(rate)) throw new InvalidDistributionRateException(rate);
		return new Exponential(rate);
	}
	
	public double getRate() { return rate; }

	@Override
	public double mean() { return 1.0 / rate; }
	
	@Override
	public double cdf(double t) { return t <= 0.0 ? 0.0 : 1.0 - Math.exp(-rate * t); }
	
	@Override
	public int hashCode() {
		return Objects.hash(rate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Exponential other = (Exponential) obj;
		return Double.doubleToLongBits(rate) == Double.doubleToLongBits(other.rate);
	}

	@Override
	public String toString() {
		return "Exponential [rate=" + rate + "]";
	}
}
