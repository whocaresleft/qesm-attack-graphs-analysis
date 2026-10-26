package qesm.project.attack_graph.step;

import java.util.Objects;
import java.util.UUID;
import qesm.project.utility.Distribution;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Step {

	private final UUID uuid;
	private final String name;
	
	private final double successProbability;
	private final double failureProbability;
	private final double failureDetectedProbability;
	
	private final Distribution duration;
	private final Distribution detectionTime;
	
	private final Map<UUID, Requirement> requires;
	private final Map<UUID, Outcome> produces;
	
	private Step(
		UUID uuid,
		String name,
		double successProbability,
		double failureProbability,
		double failureDetectedProbability,
		Distribution duration,
		Distribution detectionTime,
		Map<UUID, Requirement> requires,
		Map<UUID, Outcome> produces
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
	}

	static Step create(
		UUID uuid,
		String name,
		double successProbability,
		double failureProbability,
		double failureDetectedProbability,
		Distribution duration,
		Distribution detectionTime,
		Map<UUID, Requirement> requires,
		Map<UUID, Outcome> produces
	)
	{
		return new Step(uuid, name, successProbability, failureProbability, failureDetectedProbability, duration, detectionTime, requires, produces);
	}
	
	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	public double getSuccessProbability() { return successProbability; }
	public double getFailureProbability() { return failureProbability; }
	public double getFailureDetectedProbability() { return failureDetectedProbability; }
	public Distribution getDuration() { return duration; }
	public Distribution getDetectionTime() { return detectionTime; }
	public Collection<Outcome> getProduces() { return Collections.unmodifiableCollection(produces.values()); }
	public Collection<Requirement> getRequires() { return Collections.unmodifiableCollection(requires.values()); }
	
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
