package qesm.project.attack_graph.graph;

import java.util.UUID;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import qesm.project.attack_graph.step.Step;
import qesm.project.attack_graph.vertex.Vertex;

public final class AttackGraph {
	
	private final UUID uuid;
	private final String name;
	
	private final Map<UUID, Step> steps;
	private final Map<UUID, Vertex> vertices;
	
	private AttackGraph(UUID uuid, String name, Map<UUID, Step> steps, Map<UUID, Vertex> vertices) {
		this.uuid = uuid;
		this.name = name;
		this.steps = steps;
		this.vertices = vertices;
	}
	
	static AttackGraph create(UUID uuid, String name, Map<UUID, Step> steps, Map<UUID, Vertex> vertices) {
		return new AttackGraph(uuid, name, steps, vertices);
	}
	
	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	public Collection<Step> getSteps() { return Collections.unmodifiableCollection(steps.values()); }
	public Collection<Vertex> getVertices() { return Collections.unmodifiableCollection(vertices.values()); }
	
	public Optional<Vertex> getVertexByUuid(UUID uuid) {
		return Optional.ofNullable(vertices.get(uuid));
	}
	
	public Optional<Step> getStepByUuid(UUID uuid) {
		return Optional.ofNullable(steps.get(uuid));
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
