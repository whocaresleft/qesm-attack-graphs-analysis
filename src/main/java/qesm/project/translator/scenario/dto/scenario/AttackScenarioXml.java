package qesm.project.translator.scenario.dto.scenario;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
    @XmlAttribute public Duration timeBound;
    @XmlElement(required = true) public GraphRefXml graph;
    @XmlElementWrapper(name = "targets")        @XmlElement(name = "goalRef")   public List<RefXml> targets = new ArrayList<>();
    @XmlElementWrapper(name = "initialMarking") @XmlElement(name = "vertexRef") public List<RefXml> initialMarking = new ArrayList<>();
    @XmlElementWrapper(name = "failConditions") public List<FailConditionXml> failConditions = new ArrayList<>();
}