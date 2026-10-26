package qesm.project.translator.graph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_graph.step.Outcome;
import qesm.project.attack_graph.step.Requirement;
import qesm.project.attack_graph.step.Step;
import qesm.project.attack_graph.vertex.Access;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_graph.vertex.Knowledge;
import qesm.project.attack_graph.vertex.Skill;
import qesm.project.attack_graph.vertex.Vertex;
import qesm.project.translator.exception.AttackMapException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.utility.TestResourceHelper;
import qesm.project.translator.utility.XmlHelper;
import qesm.project.utility.Exponential;
import qesm.project.utility.NoneDistribution;

class AttackGraphMapperTest {

	private static double EPSILON = 1e-6;
	
	private AttackGraphMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = new AttackGraphMapper();
	}
	
	@Nested
	class HappyCases {
		
		@Nested
		class NormalCases {
			@Test
			void xmlGraph01Minimal() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("01-valid-minimal.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("minimal");
				
				assertThat(graph.getVertices()).hasSize(2);
				
				Optional<Vertex> maybeAccess = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess)
					.isNotEmpty();
				Vertex v = maybeAccess.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access = (Access) v;
				assertThat(access.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access.getName()).isEqualTo("A1");
				assertThat(access.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);

				Optional<Vertex> maybeGoal = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(10.0 - EPSILON, 10.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				assertThat(graph.getSteps())
				    .hasSize(1)
				    .filteredOn(step -> step.getUuid().equals(UUID.fromString("00000000-0000-0000-0000-000000000051")))
				    .singleElement()
				    .satisfies(step -> {
				    	assertThat(step.getName()).isEqualTo("ST1");
				    	assertThat(step.getSuccessProbability()).isEqualTo(0.7);
				    	assertThat(step.getFailureProbability()).isEqualTo(0.2);
				    	assertThat(step.getFailureDetectedProbability()).isEqualTo(0.1);
				    	assertThat(step.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				    	assertThat(step.getDuration()).isExactlyInstanceOf(Exponential.class);
				    	assertThat(((Exponential)step.getDuration()).getRate()).isEqualTo(0.5);
				    	
				    	assertThat(step.getProduces())
				    	    .hasSize(1)
				    	    .containsExactly(goal);
				    	
				    	assertThat(step.getRequires())
				    	    .hasSize(1)
				    	    .containsExactly(access);
				    });
			}
			
			@Test
			void xmlGraph02Full() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("02-valid-full.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("full");
				
				assertThat(graph.getVertices()).hasSize(5);
				
				Optional<Vertex> maybeAccess1 = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess1).isNotEmpty();
				Vertex v = maybeAccess1.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access1 = (Access) v;
				assertThat(access1.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access1.getName()).isEqualTo("A1");
				assertThat(access1.getDetectionTime()).isExactlyInstanceOf(Exponential.class);
				assertThat(((Exponential)(access1.getDetectionTime())).getRate()).isEqualTo(0.05);

				Optional<Vertex> maybeAccess2 = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a2"));
				assertThat(maybeAccess2).isNotEmpty();
				v = maybeAccess2.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access2 = (Access) v;
				assertThat(access2.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a2"));
				assertThat(access2.getName()).isEqualTo("A2");
				assertThat(access2.getDetectionTime()).isExactlyInstanceOf(Exponential.class);
				assertThat(((Exponential)(access2.getDetectionTime())).getRate()).isEqualTo(0.06);
				
				Optional<Vertex> maybeGoal = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(10.5 - EPSILON, 10.5 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(Exponential.class);
				assertThat(((Exponential)(goal.getDetectionTime())).getRate()).isEqualTo(0.1);
				
				Optional<Vertex> maybeSkill = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000c1"));
				assertThat(maybeSkill).isNotEmpty();
				v = maybeSkill.get();
				assertThat(v).isExactlyInstanceOf(Skill.class);
				Skill skill = (Skill) v;
				assertThat(skill.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000c1"));
				assertThat(skill.getName()).isEqualTo("S1");

				Optional<Vertex> maybeKnowledge = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000d1"));
				assertThat(maybeKnowledge).isNotEmpty();
				v = maybeKnowledge.get();
				assertThat(v).isExactlyInstanceOf(Knowledge.class);
				Knowledge knowledge = (Knowledge) v;
				assertThat(knowledge.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000d1"));
				assertThat(knowledge.getName()).isEqualTo("K1");
				
				assertThat(graph.getSteps())
				    .hasSize(1)
				    .filteredOn(step -> step.getUuid().equals(UUID.fromString("00000000-0000-0000-0000-000000000051")))
				    .singleElement()
				    .satisfies(step -> {
				    	assertThat(step.getName()).isEqualTo("ST1");
				    	assertThat(step.getSuccessProbability()).isEqualTo(0.6);
				    	assertThat(step.getFailureProbability()).isEqualTo(0.3);
				    	assertThat(step.getFailureDetectedProbability()).isEqualTo(0.1);
				    	
				    	assertThat(step.getDuration()).isExactlyInstanceOf(Exponential.class);
				    	assertThat(((Exponential)(step.getDuration())).getRate()).isEqualTo(1.0);

				    	assertThat(step.getDetectionTime()).isExactlyInstanceOf(Exponential.class);
				    	assertThat(((Exponential)(step.getDetectionTime())).getRate()).isEqualTo(0.1);
				    	
				    	assertThat(step.getRequires())
				    	    .hasSize(3)
				    	    .containsExactlyInAnyOrder(access1, skill, knowledge);
				    	
				    	assertThat(step.getProduces())
				    	    .hasSize(2)
				    	    .containsExactlyInAnyOrder(access2, goal);
				    });
			}
			
			@Test
			void xmlGraph03ValidChain() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("03-valid-chain.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("chain");
				
				assertThat(graph.getVertices()).hasSize(3);
				
				Optional<Vertex> maybeAccess1 = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess1)
					.isNotEmpty();
				Vertex v = maybeAccess1.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access1 = (Access) v;
				assertThat(access1.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access1.getName()).isEqualTo("A1");
				assertThat(access1.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeAccess2 = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a2"));
				assertThat(maybeAccess2)
					.isNotEmpty();
				v = maybeAccess2.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access2 = (Access) v;
				assertThat(access2.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a2"));
				assertThat(access2.getName()).isEqualTo("A2");
				assertThat(access2.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);

				Optional<Vertex> maybeGoal = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(1.0 - EPSILON, 1.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Step expected1 = new Step(
					UUID.fromString("00000000-0000-0000-0000-000000000051"), "ST1", 0.8, 0.1, 0.1,
					Exponential.withRate(1.0), NoneDistribution.get(), Set.of((Requirement)access1), Set.of((Outcome)access2));
				
				Step expected2 = new Step(
					UUID.fromString("00000000-0000-0000-0000-000000000052"), "ST2", 0.5, 0.4, 0.1,
					Exponential.withRate(2.0), NoneDistribution.get(), Set.of((Requirement)access2), Set.of((Outcome)goal));
				
				assertThat(graph.getSteps()).containsExactlyInAnyOrder(expected1, expected2);
			}
			
			@Test
			void xmlGraph04TwoPaths() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("04-valid-two-paths.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("two-paths");
				
				assertThat(graph.getVertices()).hasSize(3);
				
				Optional<Vertex> maybeAccess = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess).isNotEmpty();
				Vertex v = maybeAccess.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access = (Access) v;
				assertThat(access.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access.getName()).isEqualTo("A1");
				assertThat(access.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeGoal = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(5.0 - EPSILON, 5.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeSkill = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000c1"));
				assertThat(maybeSkill).isNotEmpty();
				v = maybeSkill.get();
				assertThat(v).isExactlyInstanceOf(Skill.class);
				Skill skill = (Skill) v;
				assertThat(skill.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000c1"));
				assertThat(skill.getName()).isEqualTo("S1");
				
				Step expected1 = new Step(
					UUID.fromString("00000000-0000-0000-0000-000000000051"), "ST1", 0.5, 0.4, 0.1,
					Exponential.withRate(1.0), NoneDistribution.get(), Set.of((Requirement)access), Set.of((Outcome)goal));
					
				Step expected2 = new Step(
					UUID.fromString("00000000-0000-0000-0000-000000000052"), "ST2", 0.9, 0.0, 0.1,
					Exponential.withRate(0.2), NoneDistribution.get(), Set.of((Requirement)skill), Set.of((Outcome)goal));
					
				assertThat(graph.getSteps()).containsExactlyInAnyOrder(expected1, expected2);
			}
			
			@Test
			void xmlGraph05CertainSuccess() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("05-valid-certain-success.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("certain-success");
				
				assertThat(graph.getVertices()).hasSize(2);
				
				Optional<Vertex> maybeAccess = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess).isNotEmpty();
				Vertex v = maybeAccess.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access = (Access) v;
				assertThat(access.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access.getName()).isEqualTo("A1");
				assertThat(access.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeGoal = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(0.0 - EPSILON, 0.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Step expected = new Step(
					UUID.fromString("00000000-0000-0000-0000-000000000051"), "ST1", 1.0, 0.0, 0.0,
					Exponential.withRate(1.0), NoneDistribution.get(), Set.of((Requirement)access), Set.of((Outcome)goal));
					
				assertThat(graph.getSteps()).containsExactly(expected);
			}
			
			@Test
			void xmlGraph06UuidWithUpperCaseAndSpaces() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("06-valid-uuid-case-and-spaces.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("case");
				
				assertThat(graph.getVertices()).hasSize(2);
				
				Optional<Vertex> maybeAccess = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000A1"));
				assertThat(maybeAccess).isNotEmpty();
				Vertex v = maybeAccess.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access = (Access) v;
				assertThat(access.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000A1"));
				assertThat(access.getName()).isEqualTo("A1");
				assertThat(access.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeGoal = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000E1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000E1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(1.0 - EPSILON, 1.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Step expected = new Step(
						XmlHelper.parseUuid("00000000-0000-0000-0000-000000000051"), "ST1", 0.7, 0.2, 0.1,
					Exponential.withRate(1.0), NoneDistribution.get(), Set.of((Requirement)access), Set.of((Outcome)goal));
					
				assertThat(graph.getSteps()).containsExactly(expected);
			}
			
			@Test
			void xmlGraph20StepNoRequires() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("20-valid-step-no-requires.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("no-requires");
				
				assertThat(graph.getVertices()).hasSize(1);
				
				Optional<Vertex> maybeGoal = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				Vertex v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(10.0 - EPSILON, 10.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Step expected = new Step(
						XmlHelper.parseUuid("00000000-0000-0000-0000-000000000051"), "ST1", 0.7, 0.2, 0.1,
					Exponential.withRate(1.0), NoneDistribution.get(), Set.of(), Set.of((Outcome)goal));
					
				assertThat(graph.getSteps()).containsExactly(expected);
			}
			
			@Test
			void xmlGraph21StepNoProduces() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("21-valid-step-no-produces.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("no-produces");
				
				assertThat(graph.getVertices()).hasSize(1);
				
				Optional<Vertex> maybeAccess = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess)
					.isNotEmpty();
				Vertex v = maybeAccess.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access = (Access) v;
				assertThat(access.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access.getName()).isEqualTo("A1");
				assertThat(access.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Step expected = new Step(
						XmlHelper.parseUuid("00000000-0000-0000-0000-000000000051"), "ST1", 0.7, 0.2, 0.1,
					Exponential.withRate(1.0), NoneDistribution.get(), Set.of((Requirement)access), Set.of());
					
				assertThat(graph.getSteps()).containsExactly(expected);
			}
		}
		
		@Nested
		class EdgeCases {
			
			@Test
			void xmlGraph23NoSteps() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("23-edge-vertices-no-steps.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("no-steps");
				
				assertThat(graph.getVertices()).hasSize(2);
				
				Optional<Vertex> maybeAccess = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess)
					.isNotEmpty();
				Vertex v = maybeAccess.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access = (Access) v;
				assertThat(access.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access.getName()).isEqualTo("A1");
				assertThat(access.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeGoal = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(10.0 - EPSILON, 10.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
					
				assertThat(graph.getSteps()).isEmpty();
			}
			
			@Test
			void xmlGraph24UnreachableGoal() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("24-edge-unreachable-goal.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("unreachable-goal");
				
				assertThat(graph.getVertices()).hasSize(3);
				
				Optional<Vertex> maybeAccess = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess)
					.isNotEmpty();
				Vertex v = maybeAccess.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access = (Access) v;
				assertThat(access.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access.getName()).isEqualTo("A1");
				assertThat(access.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeGoal1 = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal1)
					.isNotEmpty();
				v = maybeGoal1.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal1 = (Goal) v;
				assertThat(goal1.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal1.getName()).isEqualTo("G1");
				assertThat(goal1.getValue()).isBetween(10.0 - EPSILON, 10.0 + EPSILON);
				assertThat(goal1.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
					
				Optional<Vertex> maybeGoal2 = graph.getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e2"));
				assertThat(maybeGoal2)
					.isNotEmpty();
				v = maybeGoal2.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal2 = (Goal) v;
				assertThat(goal2.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e2"));
				assertThat(goal2.getName()).isEqualTo("G2");
				assertThat(goal2.getValue()).isBetween(3.0 - EPSILON, 3.0 + EPSILON);
				assertThat(goal2.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Step expected = new Step(
						XmlHelper.parseUuid("00000000-0000-0000-0000-000000000051"), "ST1", 0.7, 0.2, 0.1,
						Exponential.withRate(1.0), NoneDistribution.get(), Set.of((Requirement)access), Set.of((Outcome)goal1));
				
				assertThat(graph.getSteps()).containsExactly(expected);
			}
			
			@Test
			void xmlGraph25ReadRequirement() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("25-edge-unused-access.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("unused-access");
				
				assertThat(graph.getVertices()).hasSize(3);
				
				Optional<Vertex> maybeAccess1 = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess1)
					.isNotEmpty();
				Vertex v = maybeAccess1.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access1 = (Access) v;
				assertThat(access1.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access1.getName()).isEqualTo("A1");
				assertThat(access1.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeAccess2 = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a2"));
				assertThat(maybeAccess2)
					.isNotEmpty();
				v = maybeAccess2.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access2 = (Access) v;
				assertThat(access2.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a2"));
				assertThat(access2.getName()).isEqualTo("A2");
				assertThat(access2.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);

				Optional<Vertex> maybeGoal = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(10.0 - EPSILON, 10.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Step expected = new Step(
						XmlHelper.parseUuid("00000000-0000-0000-0000-000000000051"), "ST1", 0.7, 0.2, 0.1,
						Exponential.withRate(1.0), NoneDistribution.get(), Set.of((Requirement)access2), Set.of((Outcome)goal));
				
				assertThat(graph.getSteps()).containsExactly(expected);
			}
			
			@Test
			void xmlGraph27DuplicateNames() {
				AttackGraph graph = mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("27-edge-duplicate-names.xml")));
				
				assertThat(graph.getUuid()).isEqualTo(XmlHelper.parseUuid("00000000-0000-0000-0000-000000000001"));
				assertThat(graph.getName()).isEqualTo("dup-names");
				
				assertThat(graph.getVertices()).hasSize(3);
				
				Optional<Vertex> maybeAccess1 = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(maybeAccess1)
					.isNotEmpty();
				Vertex v = maybeAccess1.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access1 = (Access) v;
				assertThat(access1.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a1"));
				assertThat(access1.getName()).isEqualTo("same");
				assertThat(access1.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Optional<Vertex> maybeAccess2 = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000a2"));
				assertThat(maybeAccess2)
					.isNotEmpty();
				v = maybeAccess2.get();
				assertThat(v).isExactlyInstanceOf(Access.class);
				Access access2 = (Access) v;
				assertThat(access2.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000a2"));
				assertThat(access2.getName()).isEqualTo("same");
				assertThat(access2.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);

				Optional<Vertex> maybeGoal = graph.getVertexByUuid(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(maybeGoal)
					.isNotEmpty();
				v = maybeGoal.get();
				assertThat(v).isExactlyInstanceOf(Goal.class);
				Goal goal = (Goal) v;
				assertThat(goal.getUuid()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-0000000000e1"));
				assertThat(goal.getName()).isEqualTo("G1");
				assertThat(goal.getValue()).isBetween(10.0 - EPSILON, 10.0 + EPSILON);
				assertThat(goal.getDetectionTime()).isExactlyInstanceOf(NoneDistribution.class);
				
				Step expected = new Step(
						XmlHelper.parseUuid("00000000-0000-0000-0000-000000000051"), "ST1", 0.7, 0.2, 0.1,
						Exponential.withRate(1.0), NoneDistribution.get(), Set.of(access1, access2), Set.of(goal));
				
				assertThat(graph.getSteps()).containsExactly(expected);
			}
		}
	}
	
	@Nested
	class ErrorCases{
		
		@Nested
		class NormalCases {
			
			@Test
			void xmlGraph07WithBadUuidShouldThrow() {
				assertThatExceptionOfType(IllegalArgumentException.class)
					.isThrownBy(() -> mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("07-invalid-bad-uuid.xml"))));
			}
			
			@ParameterizedTest(name = "{index}: ''{0}''")
			@CsvSource({
				"08-invalid-probabilities-over-one,Error while mapping: Step 'ST1' is not valid: The three probabilities are over 1.000000 (1.200000)",
				"09-invalid-probabilities-under-one,Error while mapping: Step 'ST1' is not valid: The three probabilities are under 1.000000 (0.800000)",
				"10-invalid-probability-out-of-range,Error while mapping: Step 'ST1' is not valid: Probability is negative (-0.500000)",
				"11-invalid-unknown-distribution,Error while mapping: Unknown distribution: weibull",
				"12-invalid-rate-zero,Error while mapping: Step 'ST1' is not valid: Rate is not valid (0.000000)",
				"13-invalid-rate-negative,Error while mapping: Step 'ST1' is not valid: Rate is not valid (-1.000000)",
				"14-invalid-unknown-ref,Error while mapping: Step 'ST1': unknown reference '00000000-0000-0000-0000-0000000000ff'",
				"15-invalid-skill-in-produces,Error while mapping: Step 'ST1': '00000000-0000-0000-0000-0000000000c1' is not of type Outcome",
				"16-invalid-knowledge-in-produces,Error while mapping: Step 'ST1': '00000000-0000-0000-0000-0000000000d1' is not of type Outcome",
				"17-invalid-duplicate-vertex-same-section,Error while mapping: Duplicate access with uuid 00000000-0000-0000-0000-0000000000a1",
				"18-invalid-duplicate-vertex-across-sections,Error while mapping: Duplicate goal with uuid 00000000-0000-0000-0000-0000000000a1",
				"19-invalid-duplicate-step,Error while mapping: Duplicate step with uuid 00000000-0000-0000-0000-000000000051",
			})
			void xmlGraphInvalidXmlShouldThrowAttackMapException(String filename, String expectedErrorMessage) {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource(filename + ".xml"));
				
				assertThatExceptionOfType(AttackMapException.class)
		    		.isThrownBy(() -> mapper.toAttackGraph(xml))
			    	.withMessage(expectedErrorMessage);
			}
		}
		
		@Nested
		class EdgeCases {
			
			@Test
			void xmlGraph26CycleShouldThrow() {
				assertThatExceptionOfType(AttackMapException.class)
					.isThrownBy(() -> mapper.toAttackGraph(AttackGraphXmlLoader.load(resource("26-edge-cycle.xml"))))
					.withMessage("Error while mapping: Cycle detected");
			}
		}
	}

	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/graph/map/", name);
	}
}
