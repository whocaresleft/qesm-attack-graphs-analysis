package qesm.project.translator.graph.dto.vertex;

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
}