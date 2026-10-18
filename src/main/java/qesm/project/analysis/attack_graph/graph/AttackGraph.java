package qesm.project.analysis.attack_graph.graph;

import java.util.UUID;
import java.util.Set;
import java.util.Collections;
import java.util.Objects;

import qesm.project.analysis.attack_graph.exception.StepNotValidException;
import qesm.project.analysis.attack_graph.step.Step;
import qesm.project.analysis.attack_graph.vertex.Vertex;

public class AttackGraph {
	
	private UUID uuid;
	private String name;
	
	private Set<Step> steps;
	private Set<Vertex> components;
	
	public AttackGraph(UUID uuid, String name, Set<Step> steps, Set<Vertex> components) {
		setUuid(uuid);
		setName(name);
		setSteps(steps);
		setComponents(components);
	}
	
	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	public Set<Step> getSteps() { return Collections.unmodifiableSet(steps); }
	public Set<Vertex> getComponents() { return Collections.unmodifiableSet(components); }
	
	private void setUuid(UUID uuid) { this.uuid = uuid; }
	private void setName(String name) { this.name = name; }
	public void setSteps(Set<Step> steps) { this.steps = steps; }
	public void setComponents(Set<Vertex> components) { this.components = components; }
	
	public void addComponent(Vertex vertex) { components.add(vertex); }
	public void addStep(Step step) { steps.add(step); }
	
	public void validate() throws StepNotValidException {
		for (Step step : steps) {
			if (!step
				.getProduces()
				.stream()
				.filter(outcome -> !components.contains((Vertex)outcome))
				.toList()
				.isEmpty()
			) { throw new StepNotValidException(step); }
			if (!step
				.getRequires()
				.stream()
				.filter(requirement -> !components.contains((Vertex)requirement))
				.toList()
				.isEmpty()
			) { throw new StepNotValidException(step); }
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(components, name, steps, uuid);
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
		return Objects.equals(components, other.components) && Objects.equals(name, other.name)
				&& Objects.equals(steps, other.steps) && Objects.equals(uuid, other.uuid);
	}

	@Override
	public String toString() {
		return "AttackGraph [uuid=" + uuid + ", name=" + name + ", steps=" + steps + ", components=" + components + "]";
	}
}
