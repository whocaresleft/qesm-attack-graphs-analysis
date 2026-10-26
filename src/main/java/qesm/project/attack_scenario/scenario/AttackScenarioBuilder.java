package qesm.project.attack_scenario.scenario;

import java.util.UUID;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import qesm.project.utility.GoalCombination;
import qesm.project.attack_graph.vertex.Vertex;
import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_scenario.fail_condition.FailCondition;
import qesm.project.attack_scenario.strategy.AttackStrategy;

public class AttackScenarioBuilder {
	
	private UUID uuid;
	private String name;
	
	private GoalCombination goalsRequired;
	private double timeBound;
	
	private AttackGraph graph;
	private final Map<UUID, Vertex> initialMarking;
	private final Map<UUID, Goal> targets;
	
	private AttackStrategy strategy;

	private FailCondition failCondition;
	
	private AttackScenarioBuilder() {
		initialMarking = new HashMap<>();
		targets = new HashMap<>();
	}
	
	public static AttackScenarioBuilder create() {
		return new AttackScenarioBuilder();
	}
	
	public AttackScenarioBuilder withUuid(UUID uuid) {
		this.uuid = uuid;
		return this;
	}
	
	public AttackScenarioBuilder withName(String name) {
		this.name = name;
		return this;
	}
	
	public AttackScenarioBuilder withGoalCombination(GoalCombination goalsRequired) {
		this.goalsRequired = goalsRequired;
		return this;
	}
	
	public AttackScenarioBuilder withTimeBound(double timeBound) {
		this.timeBound = timeBound;
		return this;
	}
	
	public AttackScenarioBuilder withGraph(AttackGraph graph) {
		this.graph = graph;
		return this;
	}
	
	public AttackScenarioBuilder withInitialMarking(Set<Vertex> initialMarking) {
		this.initialMarking.clear();
		if (initialMarking != null) initialMarking.forEach(this::addVertexToInitialMarking);
		return this;
	}
	
	public AttackScenarioBuilder addVertexToInitialMarking(Vertex vertex) {
		if (vertex != null) {
			Vertex old = initialMarking.put(vertex.getUuid(), vertex);
			if (old != null) initialMarking.put(vertex.getUuid(), old);
		}
		return this;
	}
	
	public AttackScenarioBuilder withTargets(Set<Goal> targets) {
		this.targets.clear();
		if (targets != null) targets.forEach(this::addTarget);
		return this;
	}
	
	public AttackScenarioBuilder addTarget(Goal target) {
		if (target != null) {
			Goal old = targets.put(target.getUuid(), target);
			if (old != null) targets.put(target.getUuid(), old);
		}
		return this;
	}
	
	public AttackScenarioBuilder withStrategy(AttackStrategy strategy) {
		this.strategy = strategy;
		return this;
	}
	
	public AttackScenarioBuilder withFailCondition(FailCondition failCondition) {
		this.failCondition = failCondition;
		return this;
	}
	
	public AttackScenario build() throws IllegalArgumentException {
		tryFixNulls();
		validateTimeBound();
		validateInitialMarking();
		validateTargets();
		return AttackScenario.create(uuid, name, graph, initialMarking, targets, goalsRequired, strategy, timeBound, failCondition);
	}
	
	private void tryFixNulls() throws IllegalArgumentException {
		if (uuid == null) withUuid(UUID.randomUUID());
		if (name == null) withName("Scenario@" + uuid.toString());
		if (graph == null) throw new IllegalArgumentException("Missing graph");
		if (strategy == null) throw new IllegalArgumentException("Missing strategy");
		if (failCondition == null) throw new IllegalArgumentException("Missing failCondition");
	}
	
	private void validateTimeBound() throws IllegalArgumentException {
		if (timeBound <= 0.0) throw new IllegalArgumentException("Illegal duration, must be positive or infinite: " + timeBound);
	}
	
	private void validateInitialMarking() throws IllegalArgumentException {
		validateVerticesInGraph(graph, initialMarking.keySet());
	}
	
	private void validateTargets() throws IllegalArgumentException {
		if (targets.isEmpty()) throw new IllegalArgumentException("Must contain at least 1 target");
		validateVerticesInGraph(graph, targets.keySet());
	}
	
	private static void validateVerticesInGraph(AttackGraph graph, Collection<UUID> uuids) throws IllegalArgumentException {
		uuids.forEach(uuid -> { if (graph.getVertexByUuid(uuid).isEmpty()) throw new IllegalArgumentException("Vertex is not in graph"); });
	}
	
	@Override
	public String toString() {
		return "AttackScenarioBuilder [uuid=" + uuid + ", name=" + name + ", goalsRequired=" + goalsRequired + ", timeBound="
				+ timeBound + ", graph=" + graph + ", initialMarking=" + initialMarking + ", targets=" + targets
				+ ", strategy=" + strategy + ", failCondition=" + failCondition + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(goalsRequired, graph, initialMarking, name, strategy, targets, timeBound, uuid, failCondition);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttackScenarioBuilder other = (AttackScenarioBuilder) obj;
		return goalsRequired == other.goalsRequired && Objects.equals(graph, other.graph)
				&& Objects.equals(initialMarking, other.initialMarking) && Objects.equals(name, other.name)
				&& Objects.equals(strategy, other.strategy) && Objects.equals(targets, other.targets)
				&& Objects.equals(timeBound, other.timeBound) && Objects.equals(uuid, other.uuid) && Objects.equals(failCondition, other.failCondition);
	}
}
