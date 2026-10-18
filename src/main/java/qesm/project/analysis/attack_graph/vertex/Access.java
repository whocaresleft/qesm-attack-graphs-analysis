package qesm.project.analysis.attack_graph.vertex;

import java.util.Objects;
import java.util.UUID;
import qesm.project.analysis.attack_graph.step.Requirement;
import qesm.project.analysis.attack_graph.step.Outcome;

public class Access extends Vertex implements Requirement, Outcome {
	private double detectionTime;
	
	public Access(UUID uuid, String name, double detectionTime) {
		super(uuid, name);
		setDetectionTime(detectionTime);
	}
	
	public double getDetectionTime() { return detectionTime; }

	private void setDetectionTime(double detectionTime) { this.detectionTime = detectionTime; }

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
		return Double.doubleToLongBits(detectionTime) == Double.doubleToLongBits(other.detectionTime);
	}

	@Override
	public String toString() {
		return "Access [detectionTime=" + detectionTime + "]";
	}
}
