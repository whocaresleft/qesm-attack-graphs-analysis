package qesm.project.translator.scenario;

import java.util.Set;
import java.util.stream.Collectors;

import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_graph.vertex.Vertex;
import qesm.project.attack_scenario.scenario.AttackScenario;
import qesm.project.attack_scenario.strategy.AttackStrategy;
import qesm.project.attack_scenario.strategy.Cautious;
import qesm.project.attack_scenario.strategy.Greedy;
import qesm.project.attack_scenario.strategy.LowHangingFruit;
import qesm.project.attack_scenario.strategy.Random;
import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;
import qesm.project.translator.utility.XmlHelper;
import qesm.project.utility.GoalCombination;

public class AttackScenarioMapper {
    public AttackScenario toAttackScenario(AttackScenarioXml xml, AttackGraph graph) {
        Set<Goal> targets = xml.targets.stream().map(r -> findGoal(graph, r.ref, xml.name)).collect(Collectors.toSet());
        Set<Vertex> initialMarking = xml.initialMarking.stream().map(r -> graph.getVertexByUuid(XmlHelper.parseUuid(r.ref))
                .orElseThrow(() -> new AttackLoadException("Scenario '" + xml.name + "': vertex unknown " + r.ref))).collect(Collectors.toSet());
        
        return new AttackScenario(
        		XmlHelper.parseUuid(xml.uuid),
        		xml.name,
        		graph,
        		initialMarking,
        		targets,
        		toGoalCombination(xml.goalsRequired),
        		toAttackStrategy(xml.strategy),
        		xml.timeBound
    		);
    }
    
    private static Goal findGoal(AttackGraph graph, String ref, String scenario) {
    	Vertex vertex = graph.getVertexByUuid(XmlHelper.parseUuid(ref))
			.orElseThrow(() -> new AttackLoadException("Scenario '" + scenario + "': target unknown " + ref));
    	if (!(vertex instanceof Goal goal)) {
    		throw new AttackLoadException("Vertex '" + vertex.getName() + "' is not a Goal");
    	}
    	return goal;
    }
    
    private static AttackStrategy toAttackStrategy(String as) {
    	try { return switch (as.trim().toLowerCase()) {
        	case "greedy" -> new Greedy();
        	case "cautious" -> new Cautious();
        	case "low-hanging-fruit" -> new LowHangingFruit();
        	case "random" -> new Random();
        	default -> throw new AttackLoadException("Strategy unknown: " + as);
    	}; }
    	catch (NullPointerException e) {
    		throw new AttackLoadException("Attack strategy is null", e);
    	}
    }
    
    private static GoalCombination toGoalCombination(String gr) {
    	try { return GoalCombination.valueOf(gr.trim().toUpperCase()); }
    	catch (IllegalArgumentException e) {
    		throw new AttackLoadException("GoalCombination unknown: " + gr, e);
    	}
    }
}