package qesm.project.translator.graph.dto.step;

import java.util.ArrayList;
import java.util.List;

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
    @XmlElement(name = "requires") public List<RefXml> requires = new ArrayList<>();
    @XmlElement(name = "produces") public List<RefXml> produces = new ArrayList<>();
}
