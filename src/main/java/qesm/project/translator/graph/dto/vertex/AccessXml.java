package qesm.project.translator.graph.dto.vertex;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import qesm.project.translator.utility.DistributionXml;

@XmlAccessorType(XmlAccessType.FIELD)
public class AccessXml {
    @XmlAttribute(required = true) public String uuid;
    @XmlAttribute(required = true) public String name;
    @XmlElement public DistributionXml detectionTime;
    
	@Override
	public int hashCode() {
		return Objects.hash(detectionTime, name, uuid);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessXml other = (AccessXml) obj;
		return Objects.equals(detectionTime, other.detectionTime) && Objects.equals(name, other.name)
				&& Objects.equals(uuid, other.uuid);
	}
}