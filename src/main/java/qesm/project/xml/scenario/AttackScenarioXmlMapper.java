package qesm.project.xml.scenario;

import java.util.Set;
import java.util.stream.Collectors;

import qesm.project.common.GoalCombination;
import qesm.project.graph.AttackGraph;
import qesm.project.graph.vertex.Goal;
import qesm.project.graph.vertex.Vertex;
import qesm.project.scenario.AttackScenario;
import qesm.project.scenario.AttackScenarioBuilder;
import qesm.project.scenario.exception.EmptyScenarioTargetsException;
import qesm.project.scenario.exception.InvalidScenarioTimeBoundException;
import qesm.project.scenario.exception.MissingScenarioAttributeException;
import qesm.project.scenario.exception.UnresolvedScenarioVertexException;
import qesm.project.scenario.failcondition.AnyDetection;
import qesm.project.scenario.failcondition.FailCondition;
import qesm.project.scenario.failcondition.NeverFail;
import qesm.project.scenario.strategy.AttackStrategy;
import qesm.project.scenario.strategy.Cautious;
import qesm.project.scenario.strategy.Greedy;
import qesm.project.scenario.strategy.LowHangingFruit;
import qesm.project.scenario.strategy.Random;
import qesm.project.xml.XmlHelper;
import qesm.project.xml.exception.XmlMappingException;
import qesm.project.xml.scenario.dto.AttackScenarioXml;
import qesm.project.xml.scenario.dto.FailConditionXml;

public class AttackScenarioXmlMapper {
	
	private static final AttackScenarioXmlMapper INSTANCE = new AttackScenarioXmlMapper();
	
	private AttackScenarioXmlMapper() { }
	
	public static AttackScenarioXmlMapper get() { return INSTANCE; }
	
	public AttackScenario toAttackScenario(AttackScenarioXml xml, AttackGraph graph) throws XmlMappingException {
		if (xml == null) throw new XmlMappingException("AttackScenario Xml is null");
		if (graph == null) throw new XmlMappingException("AttackGraph is null");
		if (xml.graph == null) throw new XmlMappingException("AttackScenario Xml '<graph>' is null");
		if (!XmlHelper.parseUuid(xml.graph.ref).equals(graph.getUuid())) {
			throw new XmlMappingException("Graph uuid is not coherent with given AttackGraph");
		}
		
		Set<Goal> targets = xml.targets == null ? Set.of() : xml.targets.stream().map(r -> findGoal(graph, r.ref, xml.name)).collect(Collectors.toSet());
		Set<Vertex> initialMarking = xml.initialMarking == null ? Set.of() : xml.initialMarking.stream().map(r -> graph.getVertexByUuid(XmlHelper.parseUuid(r.ref))
				.orElseThrow(() -> new XmlMappingException("Scenario '" + xml.name + "': vertex unknown " + r.ref))).collect(Collectors.toSet());
		
		AttackScenarioBuilder scenarioBuilder = AttackScenarioBuilder.create()
				.withUuid(XmlHelper.parseUuid(xml.uuid))
				.withName(xml.name)
				.withGraph(graph)
				.withInitialMarking(initialMarking)
				.withTargets(targets)
				.withGoalCombination(toGoalCombination(xml.goalsRequired))
				.withStrategy(toAttackStrategy(xml.strategy))
				.withTimeBound(xml.timeBound)
				.withFailCondition(toFailCondition(xml.failCondition));
				
		try {
			return scenarioBuilder.build();
		} catch (MissingScenarioAttributeException | InvalidScenarioTimeBoundException | EmptyScenarioTargetsException
				| UnresolvedScenarioVertexException e) {
			throw new XmlMappingException(e.getMessage(), e);
		}
	}
	
	private static Goal findGoal(AttackGraph graph, String ref, String scenario) {
		Vertex vertex = graph.getVertexByUuid(XmlHelper.parseUuid(ref))
			.orElseThrow(() -> new XmlMappingException("Scenario '" + scenario + "': target unknown " + ref));
		if (!(vertex instanceof Goal goal)) {
			throw new XmlMappingException("Vertex '" + vertex.getName() + "' is not a Goal");
		}
		return goal;
	}
	
	private static AttackStrategy toAttackStrategy(String as) {
		if (as == null) throw new XmlMappingException("Attack strategy is null");
		return switch (as.trim().toLowerCase()) {
			case "greedy" -> new Greedy();
			case "cautious" -> new Cautious();
			case "low-hanging-fruit" -> new LowHangingFruit();
			case "random" -> new Random();
			default -> throw new XmlMappingException("Strategy unknown: " + as);
		};
	}
	
	private static FailCondition toFailCondition(FailConditionXml failCondition) throws XmlMappingException {
		if (failCondition == null || failCondition.type == null) return NeverFail.get();
		return switch (failCondition.type.trim().toLowerCase()) {
			case "neverfail" -> NeverFail.get();
			case "anydetection" -> AnyDetection.get();
			default -> throw new XmlMappingException("Unknown fail condition: " + failCondition.type);
		};
	}
	
	private static GoalCombination toGoalCombination(String gr) {
		if (gr == null) throw new XmlMappingException("GoalCombination is null");
		try {
			return GoalCombination.valueOf(gr.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new XmlMappingException("GoalCombination unknown: " + gr, e);
		}
	}
}
