package qesm.project.attack_graph.vertex;

import java.util.Objects;
import java.util.UUID;

import qesm.project.attack_graph.step.Outcome;
import qesm.project.attack_graph.step.Requirement;
import qesm.project.utility.Distribution;

public class Goal extends Vertex implements Requirement, Outcome {

	private final double value;
	private final Distribution detectionTime;
	
	public Goal(UUID uuid, String name, double value, Distribution detectionTime) {
		super(uuid, name);
		this.value = value;
		this.detectionTime = detectionTime;
	}

	public double getValue() { return value; }
	public Distribution getDetectionTime() { return detectionTime; }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(detectionTime, value);
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
		Goal other = (Goal) obj;
		return Objects.equals(detectionTime, other.detectionTime)
				&& Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
	}

	@Override
	public String toString() {
		return "Goal [value=" + value + ", detectionTime=" + detectionTime + "]";
	}
}
