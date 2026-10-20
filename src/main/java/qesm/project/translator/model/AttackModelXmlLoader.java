package qesm.project.translator.model;

import java.nio.file.Path;
import java.util.List;

import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_scenario.scenario.AttackScenario;
import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.AttackGraphMapper;
import qesm.project.translator.model.dto.AttackModel;
import qesm.project.translator.model.dto.AttackModelXml;
import qesm.project.translator.scenario.AttackScenarioMapper;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;
import qesm.project.translator.utility.XmlHelper;

public class AttackModelXmlLoader {
	private final AttackGraphMapper graphMapper;
	private final AttackScenarioMapper scenarioMapper;
	
	public AttackModelXmlLoader() {
		this(new AttackGraphMapper(), new AttackScenarioMapper());
	}
	
	public AttackModelXmlLoader(AttackGraphMapper graphMapper, AttackScenarioMapper scenarioMapper) {
		this.graphMapper = graphMapper;
		this.scenarioMapper = scenarioMapper;
	}
	
	public AttackModel load(Path file) throws AttackLoadException {
		Path abs = file.toAbsolutePath().normalize();
		AttackModelXml modelXml = XmlHelper.unmarshal(abs, AttackModelXml.class);
		
		for (AttackScenarioXml scenarioXml : modelXml.scenarios) {
			if (scenarioXml.graph != null)
                throw new AttackLoadException("Scenario '" + scenarioXml.name + "': <graph> is not allowed inside <attackModel>");
		}
		
		AttackGraph graph = graphMapper.toAttackGraph(modelXml.graph);
		List<AttackScenario> scenarios = modelXml.scenarios.stream()
			.map(scenarioXml -> scenarioMapper.toAttackScenario(scenarioXml, graph)).toList();
	
		return new AttackModel(graph, scenarios);
	}
}