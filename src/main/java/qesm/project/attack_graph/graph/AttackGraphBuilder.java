package qesm.project.attack_graph.graph;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import qesm.project.attack_graph.exception.CycleException;
import qesm.project.attack_graph.exception.StepNotValidException;
import qesm.project.attack_graph.step.Outcome;
import qesm.project.attack_graph.step.Requirement;
import qesm.project.attack_graph.step.Step;
import qesm.project.attack_graph.vertex.Vertex;

public class AttackGraphBuilder {
	
	private enum State {UNVISITED, VISITING, VISITED};
	
	private UUID uuid;
	private String name;
	
	private Map<UUID, Step> steps;
	private Map<UUID, Vertex> vertices;
	
	public AttackGraphBuilder() {}
	
	public AttackGraphBuilder withUuid(UUID uuid) {
		this.uuid = uuid;
		return this;
	}
	
	public AttackGraphBuilder withName(String name) {
		this.name = name;
		return this;
	}
	
	public AttackGraphBuilder withSteps(Set<Step> steps) {
		if (steps == null)
			this.steps = Collections.emptyMap();
		else
			this.steps = steps.stream().collect(Collectors.toMap(Step::getUuid, step -> step));
		return this;
	}
	
	public AttackGraphBuilder withVertices(Set<Vertex> vertices) {
		if (steps == null)
			this.vertices = Collections.emptyMap();
		else
			this.vertices = vertices.stream().collect(Collectors.toMap(Vertex::getUuid, vertex -> vertex));
		return this;
	}
	
	public AttackGraphBuilder addVertex(Vertex vertex) {
		vertices.put(vertex.getUuid(), vertex);
		return this;
	}
	public AttackGraphBuilder addStep(Step step) {
		steps.put(step.getUuid(), step);
		return this;
	}
	
	public AttackGraph build() throws StepNotValidException, CycleException {
		fixNulls();
		validateStepsVertices();
		validateAcyclic();
		return new AttackGraph(uuid, name, steps, vertices);
	}
	
	private void fixNulls() {
		if (uuid == null)
			withUuid(UUID.randomUUID());
		if (name == null)
			withName("Graph@" + uuid.toString());
	}
	
	private void validateStepsVertices() throws StepNotValidException {
		for (Step step : steps.values()) {
			if (!step
				.getProduces()
				.stream()
				.filter(outcome -> !vertices.containsValue((Vertex)outcome))
				.toList()
				.isEmpty()
			) { throw new StepNotValidException(step); }
			if (!step
				.getRequires()
				.stream()
				.filter(requirement -> !vertices.containsValue((Vertex)requirement))
				.toList()
				.isEmpty()
			) { throw new StepNotValidException(step); }
		}
	}
	
	private void validateAcyclic() throws CycleException {
		Map<Step, Set<Step>> adjacencyList = new HashMap<>();
		steps.values().forEach(step -> adjacencyList.put(step, new HashSet<>()));
		
		Map<UUID, List<Step>> vertexToRequiringSteps = new HashMap<>();
		for (Step step : steps.values())
			for (Requirement req : step.getRequires())
				vertexToRequiringSteps.computeIfAbsent(((Vertex)req).getUuid(), _ -> new ArrayList<>()).add(step);

		for (Step first : steps.values()) {
			for(Outcome out : first.getProduces()) {
				List<Step> consumers = vertexToRequiringSteps.getOrDefault(((Vertex)out).getUuid(), Collections.emptyList());
				
				for (Step second : consumers)
					adjacencyList.get(first).add(second);
			}
		}
		
		Map<Step, State> states = new HashMap<>();
		for (Step step : steps.values())
			states.put(step, State.UNVISITED);
		
		Deque<Step> pathStack = new ArrayDeque<>();
		
		for (Step step : steps.values()) {
			if (states.get(step) == State.UNVISITED) {
				Collection<Step> cycle = findCycle(step, adjacencyList, states, pathStack);
				if (cycle != null) {
					throw new CycleException(cycle);
				}
			}
		}
	}
	
	private static Collection<Step> findCycle(Step current, Map<Step, Set<Step>> adj, Map<Step, State> states, Deque<Step> pathStack) {
	
		states.put(current, State.VISITING);
		pathStack.addLast(current);
		
		for (Step neighbor : adj.getOrDefault(current, Collections.emptySet())) {
			State neighborState = states.get(neighbor);
			
			if (neighborState == State.VISITING) {
				List<Step> cycle = new ArrayList<>();
				boolean foundStart = false;
				for (Step step : pathStack) {
					if (step.equals(neighbor))
						foundStart = true;
					
					if (foundStart)
						cycle.add(step);
				}
				cycle.add(neighbor);
				return cycle;
			}
			
			if (neighborState == State.UNVISITED) {
				Collection<Step> cycle = findCycle(neighbor, adj, states, pathStack);
				if (cycle != null) {
					return cycle;
				}
			}
		}
		
		pathStack.removeLast();
		states.put(current, State.VISITED);
		return null;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(vertices, name, steps, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttackGraphBuilder other = (AttackGraphBuilder) obj;
		return Objects.equals(vertices, other.vertices) && Objects.equals(name, other.name)
				&& Objects.equals(steps, other.steps) && Objects.equals(uuid, other.uuid);
	}

	@Override
	public String toString() {
		return "AttackGraphBuilder [uuid=" + uuid + ", name=" + name + ", steps=" + steps + ", vertices=" + vertices + "]";
	}
}
