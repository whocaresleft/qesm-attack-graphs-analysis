package qesm.project.translator.graph.dto.vertex;

import java.util.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SkillXml {
    @XmlAttribute(required = true) public String uuid;
    @XmlAttribute(required = true) public String name;
    
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
		SkillXml other = (SkillXml) obj;
		return Objects.equals(name, other.name) && Objects.equals(uuid, other.uuid);
	}
	
	@Override
	public String toString() {
		return "SkillXml [uuid=" + uuid + ", name=" + name + "]";
	}
}