package qesm.project.translator.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_scenario.scenario.AttackScenario;
import qesm.project.translator.exception.AttackMapException;
import qesm.project.translator.graph.AttackGraphMapper;
import qesm.project.translator.model.dto.AttackModel;
import qesm.project.translator.model.dto.AttackModelXml;
import qesm.project.translator.scenario.AttackScenarioMapper;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;

public class AttackModelMapper {
	
	private final AttackGraphMapper graphMapper;
	private final AttackScenarioMapper scenarioMapper;
	
	private AttackModelMapper(AttackGraphMapper graphMapper, AttackScenarioMapper scenarioMapper) {
		this.graphMapper = graphMapper;
		this.scenarioMapper = scenarioMapper;
	}
	
	public static AttackModelMapper create(AttackGraphMapper graphMapper, AttackScenarioMapper scenarioMapper) {
		return new AttackModelMapper(graphMapper, scenarioMapper);
	}
	
	public AttackModel toAttackModel(AttackModelXml xml) throws AttackMapException {
    	if (xml == null) throw new AttackMapException("AttackModel Xml is null");
    	if (xml.graph == null) throw new AttackMapException("Model has no graph");
    	
		AttackGraph graph = graphMapper.toAttackGraph(xml.graph);
		List<AttackScenario> scenarios = new ArrayList<>();
		Set<UUID> uuids = new HashSet<>();
		
		for (AttackScenarioXml scenarioXml : xml.scenarios) {
			AttackScenario scenario = scenarioMapper.toAttackScenario(scenarioXml, graph);
			if (!uuids.add(scenario.getUuid()))
				throw new AttackMapException("Duplicate scenario uuid: " + scenario.getUuid());
			scenarios.add(scenario);
		}
		
		return new AttackModel(graph, scenarios);
	}
}
