package qesm.project.translator.scenario;

import java.util.Set;
import java.util.stream.Collectors;

import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_graph.vertex.Vertex;
import qesm.project.attack_scenario.fail_condition.AnyDetection;
import qesm.project.attack_scenario.fail_condition.FailCondition;
import qesm.project.attack_scenario.fail_condition.NeverFail;
import qesm.project.attack_scenario.scenario.AttackScenario;
import qesm.project.attack_scenario.scenario.AttackScenarioBuilder;
import qesm.project.attack_scenario.strategy.AttackStrategy;
import qesm.project.attack_scenario.strategy.Cautious;
import qesm.project.attack_scenario.strategy.Greedy;
import qesm.project.attack_scenario.strategy.LowHangingFruit;
import qesm.project.attack_scenario.strategy.Random;
import qesm.project.translator.exception.AttackMapException;
import qesm.project.translator.scenario.dto.fail_condition.FailConditionXml;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;
import qesm.project.translator.utility.XmlHelper;
import qesm.project.utility.GoalCombination;

public class AttackScenarioMapper {
    public AttackScenario toAttackScenario(AttackScenarioXml xml, AttackGraph graph) {
    	if (xml == null) throw new AttackMapException("AttackScenario Xml is null");
    	if (xml.graph == null) throw new AttackMapException("AttackScenario Xml '<graph>' is null");
    	if (!XmlHelper.parseUuid(xml.graph.ref).equals(graph.getUuid())) {
    		throw new AttackMapException("Graph uuid is not coherent with given AttackGraph");
    	}
    	
        Set<Goal> targets = xml.targets.stream().map(r -> findGoal(graph, r.ref, xml.name)).collect(Collectors.toSet());
        Set<Vertex> initialMarking = xml.initialMarking.stream().map(r -> graph.getVertexByUuid(XmlHelper.parseUuid(r.ref))
                .orElseThrow(() -> new AttackMapException("Scenario '" + xml.name + "': vertex unknown " + r.ref))).collect(Collectors.toSet());
        
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
    	} catch(IllegalArgumentException e) {
    		throw new AttackMapException(e.getMessage(), e);
    	}
    }
    
    private static Goal findGoal(AttackGraph graph, String ref, String scenario) {
    	Vertex vertex = graph.getVertexByUuid(XmlHelper.parseUuid(ref))
			.orElseThrow(() -> new AttackMapException("Scenario '" + scenario + "': target unknown " + ref));
    	if (!(vertex instanceof Goal goal)) {
    		throw new AttackMapException("Vertex '" + vertex.getName() + "' is not a Goal");
    	}
    	return goal;
    }
    
    private static AttackStrategy toAttackStrategy(String as) {
    	try { return switch (as.trim().toLowerCase()) {
        	case "greedy" -> new Greedy();
        	case "cautious" -> new Cautious();
        	case "low-hanging-fruit" -> new LowHangingFruit();
        	case "random" -> new Random();
        	default -> throw new AttackMapException("Strategy unknown: " + as);
    	}; }
    	catch (NullPointerException e) {
    		throw new AttackMapException("Attack strategy is null", e);
    	}
    }
    
    private static FailCondition toFailCondition(FailConditionXml failCondition) throws AttackMapException {
    	if (failCondition == null) return NeverFail.get();
    	return switch (failCondition.type.trim().toLowerCase()) {
    		case "neverfail" -> NeverFail.get();
    		case "anydetection" -> AnyDetection.get();
    		default -> throw new AttackMapException("Unknown fail condition: " + failCondition.type);
    	};
    }
    
    private static GoalCombination toGoalCombination(String gr) {
    	try { return GoalCombination.valueOf(gr.trim().toUpperCase()); }
    	catch (IllegalArgumentException e) {
    		throw new AttackMapException("GoalCombination unknown: " + gr, e);
    	}
    }
}