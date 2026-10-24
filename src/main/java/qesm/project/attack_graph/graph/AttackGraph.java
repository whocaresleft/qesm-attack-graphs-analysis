package qesm.project.attack_graph.graph;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import qesm.project.attack_graph.exception.StepNotValidException;
import qesm.project.attack_graph.step.Step;
import qesm.project.attack_graph.vertex.Vertex;

public class AttackGraph {
	
	private UUID uuid;
	private String name;
	
	private Map<UUID, Step> steps;
	private Map<UUID, Vertex> vertices;
	
	public AttackGraph(UUID uuid, String name, Set<Step> steps, Set<Vertex> vertices) {
		setUuid(uuid);
		setName(name);
		setSteps(steps);
		setVertices(vertices);
	}
	
	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	public Set<Step> getSteps() { return steps.values().stream().collect(Collectors.toSet()); }
	public Set<Vertex> getVertices() { return vertices.values().stream().collect(Collectors.toSet()); }
	
	private void setUuid(UUID uuid) { this.uuid = uuid; }
	private void setName(String name) { this.name = name; }
	public void setSteps(Set<Step> steps) { this.steps = steps.stream().collect(Collectors.toMap(Step::getUuid, step -> step)); }
	public void setVertices(Set<Vertex> vertices) { this.vertices = vertices.stream().collect(Collectors.toMap(Vertex::getUuid, vertex -> vertex)); }
	
	public void addVertex(Vertex vertex) { vertices.put(vertex.getUuid(), vertex); }
	public void addStep(Step step) { steps.put(step.getUuid(), step); }
	
	public Optional<Vertex> getVertexByUuid(UUID uuid) {
		return Optional.ofNullable(vertices.get(uuid));
	}
	
	public Optional<Step> getStepByUuid(UUID uuid) {
		return Optional.ofNullable(steps.get(uuid));
	}
	
	public void validate() throws StepNotValidException {
		for (Step step : steps.values()) {
			System.out.println("S-- " + step + " --S");
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
		AttackGraph other = (AttackGraph) obj;
		return Objects.equals(vertices, other.vertices) && Objects.equals(name, other.name)
				&& Objects.equals(steps, other.steps) && Objects.equals(uuid, other.uuid);
	}

	@Override
	public String toString() {
		return "AttackGraph [uuid=" + uuid + ", name=" + name + ", steps=" + steps + ", vertices=" + vertices + "]";
	}
}
