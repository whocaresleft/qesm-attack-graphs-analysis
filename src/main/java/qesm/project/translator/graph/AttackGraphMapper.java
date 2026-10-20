package qesm.project.translator.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import qesm.project.attack_graph.exception.OverallProbabilitiesOverOneException;
import qesm.project.attack_graph.exception.OverallProbabilitiesUnderOneException;
import qesm.project.attack_graph.exception.StepNotValidException;
import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_graph.step.Outcome;
import qesm.project.attack_graph.step.Requirement;
import qesm.project.attack_graph.step.Step;
import qesm.project.attack_graph.vertex.Access;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_graph.vertex.Knowledge;
import qesm.project.attack_graph.vertex.Skill;
import qesm.project.attack_graph.vertex.Vertex;
import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.graph.dto.step.StepXml;
import qesm.project.translator.graph.dto.vertex.AccessXml;
import qesm.project.translator.graph.dto.vertex.GoalXml;
import qesm.project.translator.graph.dto.vertex.KnowledgeXml;
import qesm.project.translator.graph.dto.vertex.SkillXml;
import qesm.project.translator.utility.DistributionXml;
import qesm.project.translator.utility.XmlHelper;
import qesm.project.utility.Distribution;
import qesm.project.utility.Exponential;

public class AttackGraphMapper {

    public AttackGraph toAttackGraph(AttackGraphXml xml) throws AttackLoadException {
        Map<String, Vertex> vertices = new HashMap<>();

        for (AccessXml a : xml.vertices.accesses)
        	vertices.put(a.uuid, toAccess(a));
        
        for (GoalXml g : xml.vertices.goals)
        	vertices.put(g.uuid, toGoal(g));
        
        for (SkillXml s : xml.vertices.skills)
        	vertices.put(s.uuid, toSkill(s));
        
        for (KnowledgeXml k : xml.vertices.knowledge)
        	vertices.put(k.uuid, toKnowledge(k));

        Set<Step> steps = new HashSet<>();
        for (StepXml s : xml.steps)
        	try {
        		steps.add(toStep(s, vertices));
        	} catch(OverallProbabilitiesOverOneException | OverallProbabilitiesUnderOneException e) {
        		throw new AttackLoadException("Step '" + s.name + "' is not valid", e);
        	}
        
        AttackGraph g = new AttackGraph(
        			XmlHelper.parseUuid(xml.uuid),
        			xml.name,
        			steps,
        			vertices.values().stream().collect(Collectors.toSet())
        		);
        
        try {
        	g.validate();
    	} catch(StepNotValidException e) {
    		throw new AttackLoadException("Step '" + e.getStep() + "' is not valid", e);
    	}
        
        return g;
    }

    private static <T> T resolve(Map<String, Vertex> byId, String ref, Class<T> type, StepXml s) {
        Vertex v = byId.get(ref);
        if (v == null)
            throw new AttackLoadException("Step '" + s.name + "': unknown reference '" + ref + "'");
        if (!type.isInstance(v))
            throw new AttackLoadException("Step '" + s.name + "': '" + ref + "' is not of type " + type.getSimpleName());
        return type.cast(v);
    }
    
    private static Distribution toDistribution(DistributionXml d) {
        if (d == null) return null;
        return switch (d.type) {
            case "exponential" -> new Exponential(d.rate);
            default -> throw new AttackLoadException("Unknown distribution: " + d.type);
        };
    }
    
    private static Skill toSkill(SkillXml s) {
    	return new Skill(XmlHelper.parseUuid(s.uuid), s.name); }
    
    private static Knowledge toKnowledge(KnowledgeXml k) {
    	return new Knowledge(XmlHelper.parseUuid(k.uuid), k.name);
    }
    
    private static Access toAccess(AccessXml a) {
    	return new Access(XmlHelper.parseUuid(a.uuid), a.name, toDistribution(a.detectionTime));
    }
    
    private static Goal toGoal(GoalXml g) {
    	return new Goal(XmlHelper.parseUuid(g.uuid), g.name, g.value, toDistribution(g.detectionTime));
    }
    
    private static Step toStep(StepXml s, Map<String, Vertex> vertices) {
    	Set<Requirement> requirements = s.requires.stream().map(r -> resolve(vertices, r.ref, Requirement.class, s)).collect(Collectors.toSet());
    	Set<Outcome> outcomes = s.produces.stream().map(r -> resolve(vertices, r.ref, Outcome.class, s)).collect(Collectors.toSet());
    	
    	return new Step(
    			XmlHelper.parseUuid(s.uuid),
    			s.name,
    			s.successProbability,
    			s.failureProbability,
    			s.failureDetectedProbability,
    			toDistribution(s.duration),
    			toDistribution(s.detectionTime),
    			requirements,
    			outcomes
			);
    }
}