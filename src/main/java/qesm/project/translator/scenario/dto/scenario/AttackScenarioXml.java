package qesm.project.translator.scenario.dto.scenario;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import qesm.project.translator.scenario.dto.fail_condition.FailConditionXml;
import qesm.project.translator.utility.GraphRefXml;
import qesm.project.translator.utility.RefXml;

@XmlRootElement(name = "attackScenario")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttackScenarioXml {
    @XmlAttribute(required = true) public String uuid;
    @XmlAttribute(required = true) public String name;
    @XmlAttribute(required = true) public String strategy;
    @XmlAttribute public String goalsRequired = "ANY";
    @XmlAttribute public double timeBound;
    @XmlElement(required = true) public GraphRefXml graph;
    @XmlElementWrapper(name = "targets")        @XmlElement(name = "goalRef")   public Set<RefXml> targets = new LinkedHashSet<>();
    @XmlElementWrapper(name = "initialMarking") @XmlElement(name = "vertexRef") public Set<RefXml> initialMarking = new LinkedHashSet<>();
    @XmlElementWrapper(name = "failConditions") public Set<FailConditionXml> failConditions = new LinkedHashSet<>();
}