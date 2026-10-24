package qesm.project.translator.graph.dto.graph;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import qesm.project.translator.graph.dto.step.StepXml;
import qesm.project.translator.graph.dto.vertex.VerticesXml;

@XmlRootElement(name = "attackGraph")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttackGraphXml {
    @XmlAttribute(required = true) public String uuid;
    @XmlAttribute(required = true) public String name;
    @XmlElement(name = "vertices") public VerticesXml vertices = new VerticesXml();
    @XmlElementWrapper(name = "steps")
    @XmlElement(name = "step") public Set<StepXml> steps = new LinkedHashSet<>();
    
	@Override
	public int hashCode() {
		return Objects.hash(name, steps, uuid, vertices);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttackGraphXml other = (AttackGraphXml) obj;
		return Objects.equals(name, other.name) && Objects.equals(steps, other.steps)
				&& Objects.equals(uuid, other.uuid) && Objects.equals(vertices, other.vertices);
	}
}
