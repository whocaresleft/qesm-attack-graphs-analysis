package qesm.project.attack_scenario.scenario;

import java.util.UUID;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import qesm.project.utility.GoalCombination;
import qesm.project.attack_graph.vertex.Vertex;
import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_scenario.fail_condition.FailCondition;
import qesm.project.attack_scenario.strategy.AttackStrategy;

public class AttackScenario {
	
	private final UUID uuid;
	private final String name;
	
	private final GoalCombination goalsRequired;
	private final double timeBound;
	
	private final AttackGraph graph;
	private final Set<Vertex> initialMarking;
	private final Set<Goal> targets;
	
	private final AttackStrategy strategy;

	private final FailCondition failCondition;
	
	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	public GoalCombination getGoalsRequired() { return goalsRequired; }
	public double getTimeBound() { return timeBound; }
	public AttackGraph getGraph() { return graph; }
	public Set<Vertex> getInitialMarking() { return Collections.unmodifiableSet(initialMarking); }
	public Set<Goal> getTargets() { return Collections.unmodifiableSet(targets); }
	public AttackStrategy getStrategy() { return strategy; }
	public FailCondition getFailCondition() { return failCondition; }
	
	public AttackScenario(UUID uuid, String name, AttackGraph graph, Set<Vertex> initialMarking, Set<Goal> targets, GoalCombination goalsRequired, AttackStrategy strategy, double timeBound, FailCondition failCondition) {
		this.uuid = uuid;
		this.name = name;
		this.graph = graph;
		this.initialMarking = initialMarking;
		if (targets.isEmpty()) throw new IllegalArgumentException("Must contain at least 1 target");
		this.targets = targets;
		this.goalsRequired = goalsRequired;
		this.strategy = strategy;
		if (!(timeBound > 0.0) || Double.isInfinite(timeBound)) throw new IllegalArgumentException("Illegal duration, must be positive and finite: " + timeBound);
		this.timeBound = timeBound;
		this.failCondition = failCondition;
	}
	
	
	@Override
	public String toString() {
		return "AttackScenario [uuid=" + uuid + ", name=" + name + ", goalsRequired=" + goalsRequired + ", timeBound="
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
		AttackScenario other = (AttackScenario) obj;
		return goalsRequired == other.goalsRequired && Objects.equals(graph, other.graph)
				&& Objects.equals(initialMarking, other.initialMarking) && Objects.equals(name, other.name)
				&& Objects.equals(strategy, other.strategy) && Objects.equals(targets, other.targets)
				&& Objects.equals(timeBound, other.timeBound) && Objects.equals(uuid, other.uuid) && Objects.equals(failCondition, other.failCondition);
	}
}
