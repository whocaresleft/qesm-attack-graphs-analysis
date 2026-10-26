package qesm.project.attack_graph.step;

import java.util.Objects;
import java.util.UUID;

import qesm.project.attack_graph.exception.NegativeProbabilityException;
import qesm.project.attack_graph.exception.OverallProbabilitiesOverOneException;
import qesm.project.attack_graph.exception.OverallProbabilitiesUnderOneException;
import qesm.project.utility.Distribution;

import java.util.Set;
import java.util.Collections;

public class Step {

	private final UUID uuid;
	private final String name;
	
	private final double successProbability;
	private final double failureProbability;
	private final double failureDetectedProbability;
	
	private final Distribution duration;
	private final Distribution detectionTime;
	
	private final Set<Requirement> requires;
	private final Set<Outcome> produces;
	
	public Step(
		UUID uuid,
		String name,
		double successProbability,
		double failureProbability,
		double failureDetectedProbability,
		Distribution duration,
		Distribution detectionTime,
		Set<Requirement> requires,
		Set<Outcome> produces
	) {
		this.uuid = uuid;
		this.name = name;
		this.successProbability = successProbability;
		this.failureProbability = failureProbability;
		this.failureDetectedProbability = failureDetectedProbability;
		this.duration = duration;
		this.detectionTime = detectionTime;
		this.requires = requires;
		this.produces = produces;
		
		validateProbabilities(successProbability, failureProbability, failureDetectedProbability);
	}

	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	public double getSuccessProbability() { return successProbability; }
	public double getFailureProbability() { return failureProbability; }
	public double getFailureDetectedProbability() { return failureDetectedProbability; }
	public Distribution getDuration() { return duration; }
	public Distribution getDetectionTime() { return detectionTime; }
	public Set<Outcome> getProduces() { return Collections.unmodifiableSet(produces); }
	public Set<Requirement> getRequires() { return Collections.unmodifiableSet(requires); }
	
	private static void validateProbabilities(double p1, double p2, double p3) throws NegativeProbabilityException, OverallProbabilitiesOverOneException, OverallProbabilitiesUnderOneException {
		if (p1 < 0) throw new NegativeProbabilityException(p1);
		if (p2 < 0) throw new NegativeProbabilityException(p2);
		if (p3 < 0) throw new NegativeProbabilityException(p3);
		
		double sum = p1 + p2 + p3;
		double epsilon = 1e-6;
		
		if (sum > 1.0 + epsilon) throw new OverallProbabilitiesOverOneException(sum);
		if (sum < 1.0 - epsilon) throw new OverallProbabilitiesUnderOneException(sum);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(detectionTime, duration, failureDetectedProbability, failureProbability, name,
				successProbability, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Step other = (Step) obj;
		return Objects.equals(detectionTime, other.detectionTime)
				&& Objects.equals(duration, other.duration)
				&& Double.doubleToLongBits(failureDetectedProbability) == Double.doubleToLongBits(other.failureDetectedProbability)
				&& Double.doubleToLongBits(failureProbability) == Double.doubleToLongBits(other.failureProbability)
				&& Objects.equals(name, other.name)
				&& Double.doubleToLongBits(successProbability) == Double.doubleToLongBits(other.successProbability)
				&& Objects.equals(uuid, other.uuid)
				&& Objects.equals(requires, other.requires)
				&& Objects.equals(produces, other.produces);
	}

	@Override
	public String toString() {
		return "Step [uuid=" + uuid + ", name=" + name + ", successProbability=" + successProbability
				+ ", failureProbability=" + failureProbability + ", failureDetectedProbability="
				+ failureDetectedProbability + ", duration=" + duration + ", detectionTime=" + detectionTime
				+ ", requires=" + requires + ", produces=" + produces + "]";
	}
}
