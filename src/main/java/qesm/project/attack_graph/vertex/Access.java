package qesm.project.attack_graph.vertex;

import java.util.Objects;
import java.util.UUID;

import qesm.project.attack_graph.step.Outcome;
import qesm.project.attack_graph.step.Requirement;
import qesm.project.utility.Distribution;

public class Access extends Vertex implements Requirement, Outcome {
	
	private final Distribution detectionTime;
	
	private Access(UUID uuid, String name, Distribution detectionTime) {
		super(uuid, name);
		this.detectionTime = detectionTime;
	}
	
	public static Access create(UUID uuid, String name, Distribution detectionTime) {
		return new Access(uuid, name, detectionTime);
	}
	
	public Distribution getDetectionTime() { return detectionTime; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(detectionTime);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Access other = (Access) obj;
		return Objects.equals(detectionTime, other.detectionTime);
	}

	@Override
	public String toString() {
		return "Access [detectionTime=" + detectionTime + "]";
	}
}
