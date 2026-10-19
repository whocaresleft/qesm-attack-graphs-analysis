package qesm.project.translator.graph.dto.vertex;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SkillXml {
    @XmlAttribute(required = true) public String uuid;
    @XmlAttribute(required = true) public String name;
}