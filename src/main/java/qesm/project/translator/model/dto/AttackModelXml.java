package qesm.project.translator.model.dto;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;

@XmlRootElement(name = "attackModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class AttackModelXml {
	@XmlElement(name = "attackGraph")    public AttackGraphXml graph;
	@XmlElement(name = "attackScenario") public List<AttackScenarioXml> scenarios;
}
