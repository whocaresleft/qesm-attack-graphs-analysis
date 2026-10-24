package qesm.project.translator.graph.dto.step;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import qesm.project.translator.utility.DistributionXml;
import qesm.project.translator.utility.RefXml;

@XmlAccessorType(XmlAccessType.FIELD)
public class StepXml {
    @XmlAttribute(required = true) public String uuid;
    @XmlAttribute(required = true) public String name;
    @XmlAttribute(required = true) public double successProbability;
    @XmlAttribute(required = true) public double failureProbability;
    @XmlAttribute(required = true) public double failureDetectedProbability;
    @XmlElement(required = true) public DistributionXml duration;
    @XmlElement public DistributionXml detectionTime;
    @XmlElement(name = "requires") public Set<RefXml> requires = new LinkedHashSet<>();
    @XmlElement(name = "produces") public Set<RefXml> produces = new LinkedHashSet<>();
    
	@Override
	public int hashCode() {
		return Objects.hash(detectionTime, duration, Double.valueOf(failureDetectedProbability),
				Double.valueOf(failureProbability), name, produces, requires, Double.valueOf(successProbability), uuid);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StepXml other = (StepXml) obj;
		return Objects.equals(detectionTime, other.detectionTime) && Objects.equals(duration, other.duration)
				&& Double.doubleToLongBits(failureDetectedProbability) == Double
						.doubleToLongBits(other.failureDetectedProbability)
				&& Double.doubleToLongBits(failureProbability) == Double.doubleToLongBits(other.failureProbability)
				&& Objects.equals(name, other.name) && Objects.equals(produces, other.produces)
				&& Objects.equals(requires, other.requires)
				&& Double.doubleToLongBits(successProbability) == Double.doubleToLongBits(other.successProbability)
				&& Objects.equals(uuid, other.uuid);
	}
	
	@Override
	public String toString() {
		return "StepXml [uuid=" + uuid + ", name=" + name + ", successProbability=" + successProbability
				+ ", failureProbability=" + failureProbability + ", failureDetectedProbability="
				+ failureDetectedProbability + ", duration=" + duration + ", detectionTime=" + detectionTime
				+ ", requires=" + requires + ", produces=" + produces + "]";
	}
}
