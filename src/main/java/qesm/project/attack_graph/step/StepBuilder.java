package qesm.project.attack_graph.step;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import qesm.project.attack_graph.exception.NegativeProbabilityException;
import qesm.project.attack_graph.exception.OverallProbabilitiesOverOneException;
import qesm.project.attack_graph.exception.OverallProbabilitiesUnderOneException;
import qesm.project.utility.Distribution;
import qesm.project.utility.NoneDistribution;

public class StepBuilder {
	
	private UUID uuid;
	private String name;
	
	private double successProbability;
	private double failureProbability;
	private double failureDetectedProbability;
	
	private Distribution duration;
	private Distribution detectionTime;
	
	private final Map<UUID, Requirement> requires;
	private final Map<UUID, Outcome> produces;
	
	private StepBuilder() {
		successProbability = 0.0;
		failureProbability = 0.0;
		failureDetectedProbability = 0.0;
		requires = new HashMap<>();
		produces = new HashMap<>();
	}
	
	public static StepBuilder create() { return new StepBuilder(); }
	
	public StepBuilder withUuid(UUID uuid) {
		this.uuid = uuid;
		return this;
	}
	
	public StepBuilder withName(String name) {
		this.name = name;
		return this;
	}
	
	public StepBuilder withSuccessProbability(double probability) {
		successProbability = probability;
		return this;
	}
	
	public StepBuilder withFailureProbability(double probability) {
		failureProbability = probability;
		return this;
	}
	
	public StepBuilder withFailureDetectedProbability(double probability) {
		failureDetectedProbability = probability;
		return this;
	}
	
	public StepBuilder withDuration(Distribution duration) {
		this.duration = duration;
		return this;
	}
	
	public StepBuilder withDetectionTime(Distribution detectionTime) {
		this.detectionTime = detectionTime;
		return this;
	}
	
	public StepBuilder withRequirements(Set<Requirement> requirements) {
		requires.clear();
		if (requirements != null) requirements.forEach(this::addRequirement);
		return this;
	}
	
	public StepBuilder withOutcomes(Set<Outcome> outcomes) {
		produces.clear();
		if (outcomes != null) outcomes.forEach(this::addOutcome);
		return this;
	}
	
	public StepBuilder addRequirement(Requirement requirement) {
		if (requirement != null) {
			Requirement old = requires.put(requirement.getUuid(), requirement);
			if (old != null) requires.put(requirement.getUuid(), old);
		}
		return this;
	}
	
	public StepBuilder addOutcome(Outcome outcome) {
		if (outcome != null) {
			Outcome old = produces.put(outcome.getUuid(), outcome);
			if (old != null) produces.put(outcome.getUuid(), old);
		}
		return this;
	}
	
	public Step build() throws IllegalArgumentException, NegativeProbabilityException, OverallProbabilitiesOverOneException, OverallProbabilitiesUnderOneException  {
		tryFixNulls();
		validateProbabilities(successProbability, failureProbability, failureDetectedProbability);
		return Step.create(uuid, name, successProbability, failureProbability, failureDetectedProbability, duration, detectionTime, requires, produces);
	}
	
	private void tryFixNulls() throws IllegalArgumentException {
		if (uuid == null) withUuid(UUID.randomUUID());
		if (name == null) withName("Graph@" + uuid.toString());
		if (detectionTime == null) withDetectionTime(NoneDistribution.get());
		if (duration == null) throw new IllegalArgumentException("Missing duration");
	}
	
	private static void validateProbabilities(double p1, double p2, double p3) throws NegativeProbabilityException, OverallProbabilitiesOverOneException, OverallProbabilitiesUnderOneException {
		if (p1 < 0) throw new NegativeProbabilityException(p1);
		if (p2 < 0) throw new NegativeProbabilityException(p2);
		if (p3 < 0) throw new NegativeProbabilityException(p3);
		
		double sum = p1 + p2 + p3;
		double epsilon = 1e-6;
		
		if (sum > 1.0 + epsilon) throw new OverallProbabilitiesOverOneException(sum);
		if (sum < 1.0 - epsilon) throw new OverallProbabilitiesUnderOneException(sum);
	}
}
