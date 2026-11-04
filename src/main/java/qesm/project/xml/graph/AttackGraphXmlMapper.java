package qesm.project.xml.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import qesm.project.common.Distribution;
import qesm.project.common.Exponential;
import qesm.project.common.NoneDistribution;
import qesm.project.common.exception.InvalidDistributionRateException;
import qesm.project.graph.AttackGraph;
import qesm.project.graph.AttackGraphBuilder;
import qesm.project.graph.exception.GraphCycleDetectedException;
import qesm.project.graph.exception.MissingStepDurationException;
import qesm.project.graph.exception.NegativeStepProbabilityException;
import qesm.project.graph.exception.StepProbabilitySumOverOneException;
import qesm.project.graph.exception.StepProbabilitySumUnderOneException;
import qesm.project.graph.exception.UnresolvedStepVertexException;
import qesm.project.graph.step.Outcome;
import qesm.project.graph.step.Requirement;
import qesm.project.graph.step.Step;
import qesm.project.graph.step.StepBuilder;
import qesm.project.graph.vertex.Access;
import qesm.project.graph.vertex.Goal;
import qesm.project.graph.vertex.Knowledge;
import qesm.project.graph.vertex.Skill;
import qesm.project.graph.vertex.Vertex;
import qesm.project.xml.XmlHelper;
import qesm.project.xml.common.DistributionXml;
import qesm.project.xml.common.RefXml;
import qesm.project.xml.exception.XmlMappingException;
import qesm.project.xml.graph.dto.AccessXml;
import qesm.project.xml.graph.dto.AttackGraphXml;
import qesm.project.xml.graph.dto.GoalXml;
import qesm.project.xml.graph.dto.KnowledgeXml;
import qesm.project.xml.graph.dto.SkillXml;
import qesm.project.xml.graph.dto.StepXml;

public class AttackGraphXmlMapper {

	private static final AttackGraphXmlMapper INSTANCE = new AttackGraphXmlMapper();
	
	private AttackGraphXmlMapper() { }
	
	public static AttackGraphXmlMapper get() { return INSTANCE; }
	
	public AttackGraph toAttackGraph(AttackGraphXml xml) throws XmlMappingException {
		if (xml == null) throw new XmlMappingException("AttackGraph Xml is null");
		
		Map<UUID, Vertex> vertices = new HashMap<>();

		if (xml.vertices != null) {
			if (xml.vertices.accesses != null) {
				for (AccessXml a : xml.vertices.accesses)
					if (vertices.put(XmlHelper.parseUuid(a.uuid), toAccess(a)) != null)
						throw new XmlMappingException("Duplicate access with uuid " + a.uuid);
			}
			
			if (xml.vertices.goals != null) {
				for (GoalXml g : xml.vertices.goals)
					if (vertices.put(XmlHelper.parseUuid(g.uuid), toGoal(g)) != null)
						throw new XmlMappingException("Duplicate goal with uuid " + g.uuid);
			}
			
			if (xml.vertices.skills != null) {
				for (SkillXml s : xml.vertices.skills)
					if (vertices.put(XmlHelper.parseUuid(s.uuid), toSkill(s)) != null)
						throw new XmlMappingException("Duplicate skill with uuid " + s.uuid);
			}
			
			if (xml.vertices.knowledge != null) {
				for (KnowledgeXml k : xml.vertices.knowledge)
					if (vertices.put(XmlHelper.parseUuid(k.uuid), toKnowledge(k)) != null)
						throw new XmlMappingException("Duplicate knowledge with uuid " + k.uuid);
			}
		}

		Map<UUID, Step> steps = new HashMap<>();
		if (xml.steps != null) {
			for (StepXml s : xml.steps)
				try {
					if (steps.put(XmlHelper.parseUuid(s.uuid), toStep(s, vertices)) != null)
						throw new XmlMappingException("Duplicate step with uuid " + s.uuid);
				} catch (MissingStepDurationException | InvalidDistributionRateException | NegativeStepProbabilityException
						| StepProbabilitySumOverOneException | StepProbabilitySumUnderOneException e) {
					throw new XmlMappingException("Step '" + s.name + "' is not valid: " + e.getMessage(), e);
				}
		}

		AttackGraphBuilder graphBuilder = AttackGraphBuilder.create()
				.withUuid(XmlHelper.parseUuid(xml.uuid))
				.withName(xml.name)
				.withSteps(new HashSet<>(steps.values()))
				.withVertices(new HashSet<>(vertices.values()));
		
		try {
			return graphBuilder.build();
		} catch (UnresolvedStepVertexException | GraphCycleDetectedException e) {
			throw new XmlMappingException(e.getMessage(), e);
		}
	}

	private static <T> T resolve(Map<UUID, Vertex> byId, String ref, Class<T> type, StepXml s) {
		Vertex v = byId.get(XmlHelper.parseUuid(ref));
		if (v == null)
			throw new XmlMappingException("Step '" + s.name + "': unknown reference '" + ref + "'");
		if (!type.isInstance(v))
			throw new XmlMappingException("Step '" + s.name + "': '" + ref + "' is not of type " + type.getSimpleName());
		return type.cast(v);
	}
	
	private static Distribution toDistribution(DistributionXml d) throws XmlMappingException {
		if (d.type == null) throw new XmlMappingException("Distribution type is null");
		return switch (d.type) {
			case "exponential" -> {
				if (d.rate == null) throw new XmlMappingException("Missing rate for exponential distribution");
				yield Exponential.fromRate(d.rate);
			}
			default -> throw new XmlMappingException("Unknown distribution: " + d.type);
		};
	}
	
	private static Distribution toDetectionTime(DistributionXml d) throws XmlMappingException {
		return d == null ? NoneDistribution.get() : toDistribution(d);
	}
	
	private static Distribution toDuration(DistributionXml d) throws XmlMappingException {
		if (d == null) throw new XmlMappingException("Duration is required");
		return toDistribution(d);
	}
	
	private static Skill toSkill(SkillXml s) {
		return Skill.create(XmlHelper.parseUuid(s.uuid), s.name);
	}
	
	private static Knowledge toKnowledge(KnowledgeXml k) {
		return Knowledge.create(XmlHelper.parseUuid(k.uuid), k.name);
	}
	
	private static Access toAccess(AccessXml a) {
		return Access.create(XmlHelper.parseUuid(a.uuid), a.name, toDetectionTime(a.detectionTime));
	}
	
	private static Goal toGoal(GoalXml g) {
		return Goal.create(XmlHelper.parseUuid(g.uuid), g.name, g.value, toDetectionTime(g.detectionTime));
	}
	
	private static Step toStep(StepXml s, Map<UUID, Vertex> vertices)
			throws MissingStepDurationException, InvalidDistributionRateException, NegativeStepProbabilityException,
			StepProbabilitySumOverOneException, StepProbabilitySumUnderOneException {
		Set<Requirement> requirements = new HashSet<>();
		if (s.requires != null) {
			for (RefXml r : s.requires) {
				requirements.add(resolve(vertices, r.ref, Requirement.class, s));
			}
		}
		Set<Outcome> outcomes = new HashSet<>();
		if (s.produces != null) {
			for (RefXml r : s.produces) {
				outcomes.add(resolve(vertices, r.ref, Outcome.class, s));
			}
		}
		
		return StepBuilder.create()
				.withUuid(XmlHelper.parseUuid(s.uuid))
				.withName(s.name)
				.withSuccessProbability(s.successProbability)
				.withFailureProbability(s.failureProbability)
				.withFailureDetectedProbability(s.failureDetectedProbability)
				.withDuration(toDuration(s.duration))
				.withDetectionTime(toDetectionTime(s.detectionTime))
				.withRequirements(requirements)
				.withOutcomes(outcomes)
				.build();
	}
}
