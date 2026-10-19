package qesm.project.attack_graph.vertex;

import java.util.Objects;
import java.util.UUID;

import qesm.project.attack_graph.step.Outcome;
import qesm.project.attack_graph.step.Requirement;
import qesm.project.utility.Distribution;

public class Goal extends Vertex implements Requirement, Outcome {

	private double value;
	private Distribution detectionTime;
	
	public Goal(UUID uuid, String name, double value, Distribution detectionTime) {
		super(uuid, name);
		setValue(value);
		setDetectionTime(detectionTime);
	}

	private void setValue(double value) { this.value = value; }
	private void setDetectionTime(Distribution detectionTime) { this.detectionTime = detectionTime; }

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
