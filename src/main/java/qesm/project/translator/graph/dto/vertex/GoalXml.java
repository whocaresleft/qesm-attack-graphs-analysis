package qesm.project.translator.graph.dto.vertex;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import qesm.project.translator.utility.DistributionXml;

@XmlAccessorType(XmlAccessType.FIELD)
public class GoalXml {
    @XmlAttribute(required = true) public String uuid;
    @XmlAttribute(required = true) public String name;
    @XmlAttribute(required = true) public double value;
    @XmlElement public DistributionXml detectionTime;
    
	@Override
	public int hashCode() {
		return Objects.hash(detectionTime, name, uuid, Double.valueOf(value));
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoalXml other = (GoalXml) obj;
		return Objects.equals(detectionTime, other.detectionTime) && Objects.equals(name, other.name)
				&& Objects.equals(uuid, other.uuid)
				&& Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
	}
}