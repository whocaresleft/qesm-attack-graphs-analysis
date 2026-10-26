package qesm.project.attack_graph.vertex;

import java.util.Objects;
import java.util.UUID;

public abstract class Vertex {
	
	private final UUID uuid;
	private final String name;
	
	public Vertex(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}
	
	public UUID getUuid() { return uuid; }
	public String getName() { return name; }

	@Override
	public int hashCode() {
		return Objects.hash(name, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		return Objects.equals(name, other.name) && Objects.equals(uuid, other.uuid);
	}

	@Override
	public String toString() {
		return "Vertex [uuid=" + uuid + ", name=" + name + "]";
	}
}
