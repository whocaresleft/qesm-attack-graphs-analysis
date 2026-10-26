package qesm.project.utility;

import java.util.Objects;

public final class Exponential implements Distribution {
	private final double rate;
	
	private Exponential(double rate) {
		this.rate = rate;
	}

	public static Exponential fromRate(double rate) throws InvalidExponentialException {
		if (rate <= 0 || Double.isInfinite(rate)) throw new InvalidExponentialException(rate);
		return new Exponential(rate);
	}
	
	public double getRate() { return rate; }

	public double mean() { return 1.0 / rate; }
	
	public double cfd(double t) { return t <= 0.0 ? 0.0 : 1.0 - Math.exp(-rate * t); }
	
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
