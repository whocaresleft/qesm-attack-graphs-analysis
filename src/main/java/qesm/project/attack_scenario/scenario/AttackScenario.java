package qesm.project.attack_scenario.scenario;

import java.util.UUID;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import qesm.project.utility.GoalCombination;
import qesm.project.attack_graph.vertex.Vertex;
import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_scenario.strategy.AttackStrategy;
import java.time.Duration;

public class AttackScenario {
	
	private UUID uuid;
	private String name;
	
	private GoalCombination goalsRequired;
	private Duration timeBound;
	
	private AttackGraph graph;
	private Set<Vertex> initialMarking;
	private Set<Goal> targets;
	
	private AttackStrategy strategy;

	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	public GoalCombination getGoalsRequired() { return goalsRequired; }
	public Duration getTimeBound() { return timeBound; }
	public AttackGraph getGraph() { return graph; }
	public Set<Vertex> getInitialMarking() { return Collections.unmodifiableSet(initialMarking); }
	public Set<Goal> getTargets() { return Collections.unmodifiableSet(targets); }
	public AttackStrategy getStrategy() { return strategy; }

	private void setUuid(UUID uuid) { this.uuid = uuid; }
	private void setName(String name) { this.name = name; }
	private void setGoalsRequired(GoalCombination goalsRequired) { this.goalsRequired = goalsRequired; }
	private void setTimeBound(Duration timeBound) { this.timeBound = timeBound; }
	private void setGraph(AttackGraph graph) { this.graph = graph; }
	private void setInitialMarking(Set<Vertex> initialMarking) { this.initialMarking = initialMarking; }
	private void setTargets(Set<Goal> targets) { this.targets = targets; }
	private void setStrategy(AttackStrategy strategy) { this.strategy = strategy; }
	
	public AttackScenario(UUID uuid, String name, AttackGraph graph, Set<Vertex> initialMarking, Set<Goal> targets, GoalCombination goalsRequired, AttackStrategy strategy, Duration timeBound) {
		setUuid(uuid);
		setName(name);
		setGraph(graph);
		setInitialMarking(initialMarking);
		setTargets(targets);
		setGoalsRequired(goalsRequired);
		setStrategy(strategy);
		setTimeBound(timeBound);
	}
	
	
	@Override
	public String toString() {
		return "AttackScenario [uuid=" + uuid + ", name=" + name + ", goalsRequired=" + goalsRequired + ", timeBound="
				+ timeBound + ", graph=" + graph + ", initialMarking=" + initialMarking + ", targets=" + targets
				+ ", strategy=" + strategy + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(goalsRequired, graph, initialMarking, name, strategy, targets, timeBound, uuid);
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
				&& Objects.equals(timeBound, other.timeBound) && Objects.equals(uuid, other.uuid);
	}
}
