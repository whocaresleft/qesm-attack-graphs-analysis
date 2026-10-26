package qesm.project.utility;

import java.util.Objects;

import qesm.project.attack_graph.exception.InvalidExponentialException;

public class Exponential extends Distribution {
	private final double rate;
	
	private Exponential(double rate) {
		this.rate = rate;
	}

	public static Exponential withRate(double rate) throws InvalidExponentialException {
		if (rate <= 0) throw new InvalidExponentialException(rate);
		return new Exponential(rate);
	}
	
	public double getRate() { return rate; }

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
