package qesm.project.xml.graph;

import static org.assertj.core.api.Assertions.*;

import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import qesm.project.common.Exponential;
import qesm.project.common.NoneDistribution;
import qesm.project.graph.AttackGraph;
import qesm.project.graph.step.Step;
import qesm.project.graph.step.StepBuilder;
import qesm.project.graph.vertex.Access;
import qesm.project.graph.vertex.Goal;
import qesm.project.graph.vertex.Knowledge;
import qesm.project.graph.vertex.Skill;
import qesm.project.xml.TestResourceHelper;
import qesm.project.xml.exception.InvalidUuidFormatException;
import qesm.project.xml.exception.XmlMappingException;
import qesm.project.xml.graph.dto.AttackGraphXml;

public class AttackGraphXmlMapperTest {

	private static final UUID GRAPH_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID ACCESS_A1_UUID = UUID.fromString("00000000-0000-0000-0000-0000000000a1");
	private static final UUID ACCESS_A2_UUID = UUID.fromString("00000000-0000-0000-0000-0000000000a2");
	private static final UUID SKILL_S1_UUID = UUID.fromString("00000000-0000-0000-0000-0000000000c1");
	private static final UUID KNOWLEDGE_K1_UUID = UUID.fromString("00000000-0000-0000-0000-0000000000d1");
	private static final UUID GOAL_G1_UUID = UUID.fromString("00000000-0000-0000-0000-0000000000e1");
	private static final UUID GOAL_G2_UUID = UUID.fromString("00000000-0000-0000-0000-0000000000e2");
	private static final UUID STEP_ST1_UUID = UUID.fromString("00000000-0000-0000-0000-000000000051");
	private static final UUID STEP_ST2_UUID = UUID.fromString("00000000-0000-0000-0000-000000000052");

	private AttackGraphXmlMapper mapper;

	@BeforeEach
	public void setup() {
		mapper = AttackGraphXmlMapper.get();
	}

	@Test
	public void testToAttackGraphWhenXmlIsMinimal() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("01-valid-minimal.xml"));
		Access expectedAccess = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 10.0, NoneDistribution.get());
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.7)
			.withFailureProbability(0.2)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(0.5))
			.withRequirements(Set.of(expectedAccess))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("minimal");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess, expectedGoal);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenXmlIsFull() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("02-valid-full.xml"));
		Access expectedAccess1 = Access.create(ACCESS_A1_UUID, "A1", Exponential.fromRate(0.05));
		Access expectedAccess2 = Access.create(ACCESS_A2_UUID, "A2", Exponential.fromRate(0.06));
		Skill expectedSkill = Skill.create(SKILL_S1_UUID, "S1");
		Knowledge expectedKnowledge = Knowledge.create(KNOWLEDGE_K1_UUID, "K1");
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 10.5, Exponential.fromRate(0.1));
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.6)
			.withFailureProbability(0.3)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withDetectionTime(Exponential.fromRate(0.1))
			.withRequirements(Set.of(expectedAccess1, expectedSkill, expectedKnowledge))
			.withOutcomes(Set.of(expectedAccess2, expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("full");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(
			expectedAccess1, expectedAccess2, expectedSkill, expectedKnowledge, expectedGoal
		);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenXmlContainsChainOfSteps() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("03-valid-chain.xml"));
		Access expectedAccess1 = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Access expectedAccess2 = Access.create(ACCESS_A2_UUID, "A2", NoneDistribution.get());
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 1.0, NoneDistribution.get());
		Step expectedStep1 = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.8)
			.withFailureProbability(0.1)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withRequirements(Set.of(expectedAccess1))
			.withOutcomes(Set.of(expectedAccess2))
			.build();
		Step expectedStep2 = StepBuilder.create()
			.withUuid(STEP_ST2_UUID)
			.withName("ST2")
			.withSuccessProbability(0.5)
			.withFailureProbability(0.4)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(2.0))
			.withRequirements(Set.of(expectedAccess2))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("chain");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess1, expectedAccess2, expectedGoal);
		assertThatIterable(graph.getSteps()).containsExactlyInAnyOrder(expectedStep1, expectedStep2);
	}

	@Test
	public void testToAttackGraphWhenXmlContainsTwoPathsToGoal() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("04-valid-two-paths.xml"));
		Access expectedAccess = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Skill expectedSkill = Skill.create(SKILL_S1_UUID, "S1");
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 5.0, NoneDistribution.get());
		Step expectedStep1 = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.5)
			.withFailureProbability(0.4)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withRequirements(Set.of(expectedAccess))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		Step expectedStep2 = StepBuilder.create()
			.withUuid(STEP_ST2_UUID)
			.withName("ST2")
			.withSuccessProbability(0.9)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(0.2))
			.withRequirements(Set.of(expectedSkill))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("two-paths");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess, expectedSkill, expectedGoal);
		assertThatIterable(graph.getSteps()).containsExactlyInAnyOrder(expectedStep1, expectedStep2);
	}

	@Test
	public void testToAttackGraphWhenStepHasCertainSuccess() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("05-valid-certain-success.xml"));
		Access expectedAccess = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 0.0, NoneDistribution.get());
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(1.0)
			.withDuration(Exponential.fromRate(1.0))
			.withRequirements(Set.of(expectedAccess))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("certain-success");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess, expectedGoal);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenUuidsHaveUpperCaseAndSpaces() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("06-valid-uuid-case-and-spaces.xml"));
		Access expectedAccess = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 1.0, NoneDistribution.get());
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.7)
			.withFailureProbability(0.2)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withRequirements(Set.of(expectedAccess))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("case");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess, expectedGoal);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenStepHasNoRequirements() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("20-valid-step-no-requires.xml"));
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 10.0, NoneDistribution.get());
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.7)
			.withFailureProbability(0.2)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("no-requires");
		assertThatIterable(graph.getVertices()).containsExactly(expectedGoal);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenStepHasNoOutcomes() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("21-valid-step-no-produces.xml"));
		Access expectedAccess = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.7)
			.withFailureProbability(0.2)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withRequirements(Set.of(expectedAccess))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("no-produces");
		assertThatIterable(graph.getVertices()).containsExactly(expectedAccess);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenGraphHasVerticesAndNoSteps() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("23-edge-vertices-no-steps.xml"));
		Access expectedAccess = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 10.0, NoneDistribution.get());
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("no-steps");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess, expectedGoal);
		assertThat(graph.getSteps()).isEmpty();
	}

	@Test
	public void testToAttackGraphWhenGraphContainsUnreachableGoal() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("24-edge-unreachable-goal.xml"));
		Access expectedAccess = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Goal expectedGoal1 = Goal.create(GOAL_G1_UUID, "G1", 10.0, NoneDistribution.get());
		Goal expectedGoal2 = Goal.create(GOAL_G2_UUID, "G2", 3.0, NoneDistribution.get());
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.7)
			.withFailureProbability(0.2)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withRequirements(Set.of(expectedAccess))
			.withOutcomes(Set.of(expectedGoal1))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("unreachable-goal");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess, expectedGoal1, expectedGoal2);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenGraphContainsUnusedAccess() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("25-edge-unused-access.xml"));
		Access expectedAccess1 = Access.create(ACCESS_A1_UUID, "A1", NoneDistribution.get());
		Access expectedAccess2 = Access.create(ACCESS_A2_UUID, "A2", NoneDistribution.get());
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 10.0, NoneDistribution.get());
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.7)
			.withFailureProbability(0.2)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withRequirements(Set.of(expectedAccess2))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("unused-access");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess1, expectedAccess2, expectedGoal);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenVerticesHaveDuplicateNames() {
		AttackGraph graph = mapper.toAttackGraph(loadGraphXml("27-edge-duplicate-names.xml"));
		Access expectedAccess1 = Access.create(ACCESS_A1_UUID, "same", NoneDistribution.get());
		Access expectedAccess2 = Access.create(ACCESS_A2_UUID, "same", NoneDistribution.get());
		Goal expectedGoal = Goal.create(GOAL_G1_UUID, "G1", 10.0, NoneDistribution.get());
		Step expectedStep = StepBuilder.create()
			.withUuid(STEP_ST1_UUID)
			.withName("ST1")
			.withSuccessProbability(0.7)
			.withFailureProbability(0.2)
			.withFailureDetectedProbability(0.1)
			.withDuration(Exponential.fromRate(1.0))
			.withRequirements(Set.of(expectedAccess1, expectedAccess2))
			.withOutcomes(Set.of(expectedGoal))
			.build();
		assertThat(graph.getUuid()).isEqualTo(GRAPH_UUID);
		assertThat(graph.getName()).isEqualTo("dup-names");
		assertThatIterable(graph.getVertices()).containsExactlyInAnyOrder(expectedAccess1, expectedAccess2, expectedGoal);
		assertThatIterable(graph.getSteps()).containsExactly(expectedStep);
	}

	@Test
	public void testToAttackGraphWhenUuidIsInvalidThrowsException() {
		AttackGraphXml xml = loadGraphXml("07-invalid-bad-uuid.xml");
		assertThatExceptionOfType(InvalidUuidFormatException.class)
			.isThrownBy(() -> mapper.toAttackGraph(xml));
	}

	@Test
	public void testToAttackGraphWhenStepProbabilitiesSumOverOneThrowsException() {
		assertMapThrowsXmlMappingException(
			"08-invalid-probabilities-over-one.xml",
			"Error while mapping: Step 'ST1' is not valid: The three probabilities are over 1.000000 (1.200000)"
		);
	}

	@Test
	public void testToAttackGraphWhenStepProbabilitiesSumUnderOneThrowsException() {
		assertMapThrowsXmlMappingException(
			"09-invalid-probabilities-under-one.xml",
			"Error while mapping: Step 'ST1' is not valid: The three probabilities are under 1.000000 (0.800000)"
		);
	}

	@Test
	public void testToAttackGraphWhenStepProbabilityIsNegativeThrowsException() {
		assertMapThrowsXmlMappingException(
			"10-invalid-probability-out-of-range.xml",
			"Error while mapping: Step 'ST1' is not valid: Probability is negative (-0.500000)"
		);
	}

	@Test
	public void testToAttackGraphWhenDistributionIsUnknownThrowsException() {
		assertMapThrowsXmlMappingException(
			"11-invalid-unknown-distribution.xml",
			"Error while mapping: Unknown distribution: weibull"
		);
	}

	@Test
	public void testToAttackGraphWhenDistributionRateIsZeroThrowsException() {
		assertMapThrowsXmlMappingException(
			"12-invalid-rate-zero.xml",
			"Error while mapping: Step 'ST1' is not valid: Rate is not valid (0.000000)"
		);
	}

	@Test
	public void testToAttackGraphWhenDistributionRateIsNegativeThrowsException() {
		assertMapThrowsXmlMappingException(
			"13-invalid-rate-negative.xml",
			"Error while mapping: Step 'ST1' is not valid: Rate is not valid (-1.000000)"
		);
	}

	@Test
	public void testToAttackGraphWhenStepRefIsUnknownThrowsException() {
		assertMapThrowsXmlMappingException(
			"14-invalid-unknown-ref.xml",
			"Error while mapping: Step 'ST1': unknown reference '00000000-0000-0000-0000-0000000000ff'"
		);
	}

	@Test
	public void testToAttackGraphWhenSkillIsInProducesThrowsException() {
		assertMapThrowsXmlMappingException(
			"15-invalid-skill-in-produces.xml",
			"Error while mapping: Step 'ST1': '00000000-0000-0000-0000-0000000000c1' is not of type Outcome"
		);
	}

	@Test
	public void testToAttackGraphWhenKnowledgeIsInProducesThrowsException() {
		assertMapThrowsXmlMappingException(
			"16-invalid-knowledge-in-produces.xml",
			"Error while mapping: Step 'ST1': '00000000-0000-0000-0000-0000000000d1' is not of type Outcome"
		);
	}

	@Test
	public void testToAttackGraphWhenDuplicateVertexInSameSectionThrowsException() {
		assertMapThrowsXmlMappingException(
			"17-invalid-duplicate-vertex-same-section.xml",
			"Error while mapping: Duplicate access with uuid 00000000-0000-0000-0000-0000000000a1"
		);
	}

	@Test
	public void testToAttackGraphWhenDuplicateVertexAcrossSectionsThrowsException() {
		assertMapThrowsXmlMappingException(
			"18-invalid-duplicate-vertex-across-sections.xml",
			"Error while mapping: Duplicate goal with uuid 00000000-0000-0000-0000-0000000000a1"
		);
	}

	@Test
	public void testToAttackGraphWhenDuplicateStepThrowsException() {
		assertMapThrowsXmlMappingException(
			"19-invalid-duplicate-step.xml",
			"Error while mapping: Duplicate step with uuid 00000000-0000-0000-0000-000000000051"
		);
	}

	@Test
	public void testToAttackGraphWhenGraphContainsCycleThrowsException() {
		assertMapThrowsXmlMappingException(
			"26-edge-cycle.xml",
			"Error while mapping: Cycle detected"
		);
	}

	private void assertMapThrowsXmlMappingException(String fileName, String expectedMessage) {
		AttackGraphXml xml = loadGraphXml(fileName);
		assertThatExceptionOfType(XmlMappingException.class)
			.isThrownBy(() -> mapper.toAttackGraph(xml))
			.withMessage(expectedMessage);
	}

	private static AttackGraphXml loadGraphXml(String fileName) {
		return AttackGraphXmlLoader.get().load(resource(fileName));
	}

	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/graph/map/", name);
	}
}
