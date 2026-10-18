package qesm.project.analysis.attack_graph.step;

import java.util.Objects;
import java.util.UUID;

import qesm.project.analysis.attack_graph.exception.OverallProbabilitiesOverOneException;

import java.util.Set;
import java.util.Collections;

public class Step {

	private UUID uuid;
	private String name;
	
	private double successProbability;
	private double failureProbability;
	private double failureDetectionProbability;
	
	private double duration;
	private double detectionTime;
	
	private Set<Requirement> requires;
	private Set<Outcome> produces;
	
	public Step(UUID uuid, String name, Set<Requirement> requires, Set<Outcome> produces) {
		setUuid(uuid);
		setName(name);
		setSuccessProbability(0.0);
		setFailureProbability(1.0);
		setFailureDetectionProbability(0.0);
		setDuration(0.0);
		setDetectionTime(0.0);
		setRequires(requires);
		setProduces(produces);
	}

	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	public double getSuccessProbability() { return successProbability; }
	public double getFailureProbability() { return failureProbability; }
	public double getFailureDetectionProbability() { return failureDetectionProbability; }
	public double getDuration() { return duration; }
	public double getDetectionTime() { return detectionTime; }
	public Set<Outcome> getProduces() { return Collections.unmodifiableSet(produces); }
	public Set<Requirement> getRequires() { return Collections.unmodifiableSet(requires); }
	
	private void setUuid(UUID uuid) { this.uuid = uuid; }
	private void setName(String name) { this.name = name; }
	public void setSuccessProbability(double successProbability) {
		validateProbabilities(successProbability, failureProbability, failureDetectionProbability);
		this.successProbability = successProbability;
	}
	public void setFailureProbability(double failureProbability) {
		this.failureProbability = failureProbability;
		validateProbabilities(successProbability, failureProbability, failureDetectionProbability);
	}
	public void setFailureDetectionProbability(double failureDetectionProbability) {
		validateProbabilities(successProbability, failureProbability, failureDetectionProbability);
		this.failureDetectionProbability = failureDetectionProbability;
	}
	public void setDuration(double duration) { this.duration = duration; }
	public void setDetectionTime(double detectionTime) { this.detectionTime = detectionTime; }
	public void setRequires(Set<Requirement> requires) { this.requires = requires; }
	public void setProduces(Set<Outcome> produces) { this.produces = produces; }
	
	private static void validateProbabilities(double p1, double p2, double p3) throws OverallProbabilitiesOverOneException {
		double sum = p1 + p2 + p3;
		if (sum > 1.0) throw new OverallProbabilitiesOverOneException(sum);
	}
	
	public void addRequirement(Requirement r) { if (!requires.contains(r)) requires.add(r); }
	public void addOutcome(Outcome o) { if (!produces.contains(o)) produces.add(o); }
	
	@Override
	public int hashCode() {
		return Objects.hash(detectionTime, duration, failureDetectionProbability, failureProbability, name,
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
		return Double.doubleToLongBits(detectionTime) == Double.doubleToLongBits(other.detectionTime)
				&& Double.doubleToLongBits(duration) == Double.doubleToLongBits(other.duration)
				&& Double.doubleToLongBits(failureDetectionProbability) == Double.doubleToLongBits(other.failureDetectionProbability)
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
				+ ", failureProbability=" + failureProbability + ", failureDetectionProbability="
				+ failureDetectionProbability + ", duration=" + duration + ", detectionTime=" + detectionTime
				+ ", requires=" + requires + ", produces=" + produces + "]";
	}
}
