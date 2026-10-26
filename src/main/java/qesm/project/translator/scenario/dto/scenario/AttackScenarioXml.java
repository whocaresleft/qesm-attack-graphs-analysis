package qesm.project.translator.scenario.dto.scenario;

import java.util.LinkedHashSet;
import java.util.Objects;
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
    @XmlElement(name = "failCondition", required = true) public FailConditionXml failCondition;

	@Override
	public int hashCode() {
		return Objects.hash(failCondition, goalsRequired, graph, initialMarking, name, strategy, targets,
				Double.valueOf(timeBound), uuid);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttackScenarioXml other = (AttackScenarioXml) obj;
		return Objects.equals(failCondition, other.failCondition)
				&& Objects.equals(goalsRequired, other.goalsRequired) && Objects.equals(graph, other.graph)
				&& Objects.equals(initialMarking, other.initialMarking) && Objects.equals(name, other.name)
				&& Objects.equals(strategy, other.strategy) && Objects.equals(targets, other.targets)
				&& Double.doubleToLongBits(timeBound) == Double.doubleToLongBits(other.timeBound)
				&& Objects.equals(uuid, other.uuid);
	}
	
	@Override
	public String toString() {
		return "AttackScenarioXml [uuid=" + uuid + ", name=" + name + ", strategy=" + strategy + ", goalsRequired="
				+ goalsRequired + ", timeBound=" + timeBound + ", graph=" + graph + ", targets=" + targets
				+ ", initialMarking=" + initialMarking + ", failConditions=" + failCondition + "]";
	}
}