package qesm.project.analysis.attack_graph.vertex;

import java.util.Objects;
import java.util.UUID;

public class Vertex {
	
	private UUID uuid;
	private String name;
	
	public Vertex(UUID uuid, String name) {
		setUuid(uuid);
		setName(name);
	}
	
	public UUID getUuid() { return uuid; }
	public String getName() { return name; }
	
	private void setUuid(UUID uuid) { this.uuid = uuid; }
	private void setName(String name) { this.name = name; }

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
