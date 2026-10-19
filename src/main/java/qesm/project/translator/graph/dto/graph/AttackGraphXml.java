package qesm.project.translator.graph.dto.graph;

import java.util.ArrayList;
import java.util.List;

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
    @XmlElement(name = "step") public List<StepXml> steps = new ArrayList<>();
}
