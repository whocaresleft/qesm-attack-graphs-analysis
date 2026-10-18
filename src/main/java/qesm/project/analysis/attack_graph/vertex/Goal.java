package qesm.project.analysis.attack_graph.vertex;

import java.util.Objects;
import java.util.UUID;
import qesm.project.analysis.attack_graph.step.Requirement;
import qesm.project.analysis.attack_graph.step.Outcome;

public class Goal extends Vertex implements Requirement, Outcome {

	private double value;
	private double detectionTime;
	
	public Goal(UUID uuid, String name, double value, double detectionTime) {
		super(uuid, name);
		setValue(value);
		setDetectionTime(detectionTime);
	}

	private void setValue(double value) { this.value = value; }
	private void setDetectionTime(double detectionTime) { this.detectionTime = detectionTime; }

	public double getValue() { return value; }
	public double getDetectionTime() { return detectionTime; }
	
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
		return Double.doubleToLongBits(detectionTime) == Double.doubleToLongBits(other.detectionTime)
				&& Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
	}

	@Override
	public String toString() {
		return "Goal [value=" + value + ", detectionTime=" + detectionTime + "]";
	}
}
