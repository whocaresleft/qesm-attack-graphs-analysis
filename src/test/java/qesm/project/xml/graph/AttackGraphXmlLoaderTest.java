package qesm.project.xml.graph;

import static org.assertj.core.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import qesm.project.xml.TestResourceHelper;
import qesm.project.xml.common.DistributionXml;
import qesm.project.xml.common.RefXml;
import qesm.project.xml.exception.XmlLoadingException;
import qesm.project.xml.graph.dto.AttackGraphXml;
import qesm.project.xml.graph.dto.StepXml;

public class AttackGraphXmlLoaderTest {

	private static final String GRAPH_UUID = "00000000-0000-0000-0000-000000000001";
	private static final String ACCESS_A1_UUID = "00000000-0000-0000-0000-0000000000a1";
	private static final String ACCESS_A2_UUID = "00000000-0000-0000-0000-0000000000a2";
	private static final String SKILL_S1_UUID = "00000000-0000-0000-0000-0000000000c1";
	private static final String KNOWLEDGE_K1_UUID = "00000000-0000-0000-0000-0000000000d1";
	private static final String GOAL_G1_UUID = "00000000-0000-0000-0000-0000000000e1";
	private static final String STEP_ST1_UUID = "00000000-0000-0000-0000-000000000051";
	private static final String STEP_ST2_UUID = "00000000-0000-0000-0000-000000000052";

	private AttackGraphXmlLoader loader;

	@BeforeEach
	public void setup() {
		loader = AttackGraphXmlLoader.get();
	}

	@Test
	public void testLoadWhenXmlIsMinimal() {
		AttackGraphXml xml = loader.load(resource("01-valid-minimal.xml"));

		assertThat(xml.uuid).isEqualTo(GRAPH_UUID);
		assertThat(xml.name).isEqualTo("minimal");
		assertThatIterable(xml.vertices.accesses)
			.extracting(access -> access.uuid, access -> access.name, access -> access.detectionTime)
			.containsExactly(tuple(ACCESS_A1_UUID, "A1", null));
		assertThatIterable(xml.vertices.goals)
			.extracting(goal -> goal.uuid, goal -> goal.name, goal -> goal.value, goal -> goal.detectionTime)
			.containsExactly(tuple(GOAL_G1_UUID, "G1", 10.0, null));
		assertThat(xml.vertices.skills).isEmpty();
		assertThat(xml.vertices.knowledge).isEmpty();

		StepXml expectedStep = createExpectedStep(
			STEP_ST1_UUID, "ST1", 0.7, 0.2, 0.1,
			exponentialXml(0.5), null,
			new String[] { ACCESS_A1_UUID },
			new String[] { GOAL_G1_UUID }
		);
		assertThatIterable(xml.steps).containsExactly(expectedStep);
	}

	@Test
	public void testLoadWhenXmlIsFull() {
		AttackGraphXml xml = loader.load(resource("02-valid-full.xml"));

		assertThat(xml.uuid).isEqualTo(GRAPH_UUID);
		assertThat(xml.name).isEqualTo("full");
		assertThatIterable(xml.vertices.accesses)
			.extracting(access -> access.uuid, access -> access.name, access -> access.detectionTime.type, access -> access.detectionTime.rate)
			.containsExactlyInAnyOrder(
				tuple(ACCESS_A1_UUID, "A1", "exponential", 0.05),
				tuple(ACCESS_A2_UUID, "A2", "exponential", 0.06)
			);
		assertThatIterable(xml.vertices.goals)
			.extracting(goal -> goal.uuid, goal -> goal.name, goal -> goal.value, goal -> goal.detectionTime.type, goal -> goal.detectionTime.rate)
			.containsExactly(tuple(GOAL_G1_UUID, "G1", 10.5, "exponential", 0.1));
		assertThatIterable(xml.vertices.skills)
			.extracting(skill -> skill.uuid, skill -> skill.name)
			.containsExactly(tuple(SKILL_S1_UUID, "S1"));
		assertThatIterable(xml.vertices.knowledge)
			.extracting(knowledge -> knowledge.uuid, knowledge -> knowledge.name)
			.containsExactly(tuple(KNOWLEDGE_K1_UUID, "K1"));

		StepXml expectedStep = createExpectedStep(
			STEP_ST1_UUID, "ST1", 0.6, 0.3, 0.1,
			exponentialXml(1.0), exponentialXml(0.1),
			new String[] { ACCESS_A1_UUID, SKILL_S1_UUID, KNOWLEDGE_K1_UUID },
			new String[] { ACCESS_A2_UUID, GOAL_G1_UUID }
		);
		assertThatIterable(xml.steps).containsExactly(expectedStep);
	}

	@Test
	public void testLoadWhenXmlContainsChainOfSteps() {
		AttackGraphXml xml = loader.load(resource("03-valid-chain.xml"));

		assertThat(xml.uuid).isEqualTo(GRAPH_UUID);
		assertThat(xml.name).isEqualTo("chain");
		assertThatIterable(xml.vertices.accesses)
			.extracting(access -> access.uuid, access -> access.name, access -> access.detectionTime)
			.containsExactlyInAnyOrder(
				tuple(ACCESS_A1_UUID, "A1", null),
				tuple(ACCESS_A2_UUID, "A2", null)
			);
		assertThatIterable(xml.vertices.goals)
			.extracting(goal -> goal.uuid, goal -> goal.name, goal -> goal.value, goal -> goal.detectionTime)
			.containsExactly(tuple(GOAL_G1_UUID, "G1", 1.0, null));
		assertThat(xml.vertices.skills).isEmpty();
		assertThat(xml.vertices.knowledge).isEmpty();

		StepXml expectedStep1 = createExpectedStep(
			STEP_ST1_UUID, "ST1", 0.8, 0.1, 0.1,
			exponentialXml(1.0), null,
			new String[] { ACCESS_A1_UUID },
			new String[] { ACCESS_A2_UUID }
		);
		StepXml expectedStep2 = createExpectedStep(
			STEP_ST2_UUID, "ST2", 0.5, 0.4, 0.1,
			exponentialXml(2.0), null,
			new String[] { ACCESS_A2_UUID },
			new String[] { GOAL_G1_UUID }
		);
		assertThatIterable(xml.steps).containsExactlyInAnyOrder(expectedStep1, expectedStep2);
	}

	@Test
	public void testLoadWhenXmlContainsTwoPathsToGoal() {
		AttackGraphXml xml = loader.load(resource("04-valid-two-paths.xml"));

		assertThat(xml.uuid).isEqualTo(GRAPH_UUID);
		assertThat(xml.name).isEqualTo("two-paths");
		assertThatIterable(xml.vertices.accesses)
			.extracting(access -> access.uuid, access -> access.name, access -> access.detectionTime)
			.containsExactly(tuple(ACCESS_A1_UUID, "A1", null));
		assertThatIterable(xml.vertices.goals)
			.extracting(goal -> goal.uuid, goal -> goal.name, goal -> goal.value, goal -> goal.detectionTime)
			.containsExactly(tuple(GOAL_G1_UUID, "G1", 5.0, null));
		assertThatIterable(xml.vertices.skills)
			.extracting(skill -> skill.uuid, skill -> skill.name)
			.containsExactly(tuple(SKILL_S1_UUID, "S1"));
		assertThat(xml.vertices.knowledge).isEmpty();

		StepXml expectedStep1 = createExpectedStep(
			STEP_ST1_UUID, "ST1", 0.5, 0.4, 0.1,
			exponentialXml(1.0), null,
			new String[] { ACCESS_A1_UUID },
			new String[] { GOAL_G1_UUID }
		);
		StepXml expectedStep2 = createExpectedStep(
			STEP_ST2_UUID, "ST2", 0.9, 0.0, 0.1,
			exponentialXml(0.2), null,
			new String[] { SKILL_S1_UUID },
			new String[] { GOAL_G1_UUID }
		);
		assertThatIterable(xml.steps).containsExactlyInAnyOrder(expectedStep1, expectedStep2);
	}

	@Test
	public void testLoadWhenStepHasCertainSuccess() {
		AttackGraphXml xml = loader.load(resource("05-valid-certain-success.xml"));

		assertThat(xml.uuid).isEqualTo(GRAPH_UUID);
		assertThat(xml.name).isEqualTo("certain-success");
		assertThatIterable(xml.vertices.accesses)
			.extracting(access -> access.uuid, access -> access.name, access -> access.detectionTime)
			.containsExactly(tuple(ACCESS_A1_UUID, "A1", null));
		assertThatIterable(xml.vertices.goals)
			.extracting(goal -> goal.uuid, goal -> goal.name, goal -> goal.value, goal -> goal.detectionTime)
			.containsExactly(tuple(GOAL_G1_UUID, "G1", 0.0, null));

		StepXml expectedStep = createExpectedStep(
			STEP_ST1_UUID, "ST1", 1.0, 0.0, 0.0,
			exponentialXml(1.0), null,
			new String[] { ACCESS_A1_UUID },
			new String[] { GOAL_G1_UUID }
		);
		assertThatIterable(xml.steps).containsExactly(expectedStep);
	}

	@Test
	public void testLoadWhenUuidsContainUpperCaseAndSpaces() {
		AttackGraphXml xml = loader.load(resource("06-valid-uuid-case-and-spaces.xml"));

		assertThat(xml.uuid).isEqualTo(" 00000000-0000-0000-0000-000000000001 ");
		assertThat(xml.name).isEqualTo("case");
		assertThatIterable(xml.vertices.accesses)
			.extracting(access -> access.uuid, access -> access.name, access -> access.detectionTime)
			.containsExactly(tuple("00000000-0000-0000-0000-0000000000A1", "A1", null));
		assertThatIterable(xml.vertices.goals)
			.extracting(goal -> goal.uuid, goal -> goal.name, goal -> goal.value, goal -> goal.detectionTime)
			.containsExactly(tuple("00000000-0000-0000-0000-0000000000E1", "G1", 1.0, null));
	}

	@Test
	public void testLoadWhenStepContainsDuplicateRefsDeduplicatesThem() {
		AttackGraphXml xml = loader.load(resource("18-valid-duplicate-ref-in-step.xml"));

		assertThat(xml.uuid).isEqualTo(GRAPH_UUID);
		assertThat(xml.name).isEqualTo("dup-ref");
		StepXml expectedStep = createExpectedStep(
			STEP_ST1_UUID, "ST1", 0.7, 0.2, 0.1,
			exponentialXml(1.0), null,
			new String[] { ACCESS_A1_UUID },
			new String[] { GOAL_G1_UUID }
		);
		assertThatIterable(xml.steps).containsExactly(expectedStep);
	}

	@Test
	public void testLoadWhenGraphHasNoVerticesAndNoSteps() {
		AttackGraphXml xml = loader.load(resource("15-edge-empty-graph.xml"));

		assertThat(xml.uuid).isEqualTo(GRAPH_UUID);
		assertThat(xml.name).isEqualTo("empty");
		assertThat(xml.vertices.accesses).isEmpty();
		assertThat(xml.vertices.goals).isEmpty();
		assertThat(xml.vertices.skills).isEmpty();
		assertThat(xml.vertices.knowledge).isEmpty();
		assertThat(xml.steps).isEmpty();
	}

	@Test
	public void testLoadWhenXmlIsMalformedThrowsException() {
		assertLoadThrowsXmlLoadingException("07-invalid-malformed-xml.xml");
	}

	@Test
	public void testLoadWhenRootElementIsWrongThrowsException() {
		assertLoadThrowsXmlLoadingException("08-invalid-wrong-root.xml");
	}

	@Test
	public void testLoadWhenFileIsEmptyThrowsException() {
		assertLoadThrowsXmlLoadingException("09-invalid-empty-file.xml");
	}

	@Test
	public void testLoadWhenGraphUuidIsMissingThrowsException() {
		assertLoadThrowsXmlLoadingException("10-invalid-missing-graph-uuid.xml");
	}

	@Test
	public void testLoadWhenGraphNameIsMissingThrowsException() {
		assertLoadThrowsXmlLoadingException("11-invalid-missing-graph-name.xml");
	}

	@Test
	public void testLoadWhenNumericAttributeIsNonNumericThrowsException() {
		assertLoadThrowsXmlLoadingException("12-invalid-non-numeric.xml");
	}

	@Test
	public void testLoadWhenExponentialDistributionIsMissingRateThrowsException() {
		assertLoadThrowsXmlLoadingException("13-invalid-exponential-missing-rate.xml");
	}

	@Test
	public void testLoadWhenStepIsMissingDurationThrowsException() {
		assertLoadThrowsXmlLoadingException("14-invalid-missing-duration.xml");
	}

	@Test
	public void testLoadWhenXmlContainsUnknownElementThrowsException() {
		assertLoadThrowsXmlLoadingException("16-edge-unknown-element.xml");
	}

	@Test
	public void testLoadWhenXmlContainsNaNOrInfinityThrowsException() {
		assertLoadThrowsXmlLoadingException("17-edge-nan-and-infinity.xml");
	}

	private void assertLoadThrowsXmlLoadingException(String fileName) {
		assertThatExceptionOfType(XmlLoadingException.class)
			.isThrownBy(() -> loader.load(resource(fileName)))
			.withMessage("Error while loading: XML is not valid: " + fileName);
	}

	private static DistributionXml exponentialXml(double rate) {
		DistributionXml distribution = new DistributionXml();
		distribution.type = "exponential";
		distribution.rate = rate;
		return distribution;
	}

	private static StepXml createExpectedStep(
		String uuid,
		String name,
		double successProbability,
		double failureProbability,
		double failureDetectedProbability,
		DistributionXml duration,
		DistributionXml detectionTime,
		String[] requiresRefs,
		String[] producesRefs
	) {
		StepXml step = new StepXml();
		step.uuid = uuid;
		step.name = name;
		step.successProbability = successProbability;
		step.failureProbability = failureProbability;
		step.failureDetectedProbability = failureDetectedProbability;
		step.duration = duration;
		step.detectionTime = detectionTime;
		for (String ref : requiresRefs) {
			RefXml refXml = new RefXml();
			refXml.ref = ref;
			step.requires.add(refXml);
		}
		for (String ref : producesRefs) {
			RefXml refXml = new RefXml();
			refXml.ref = ref;
			step.produces.add(refXml);
		}
		return step;
	}

	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/graph/load/", name);
	}
}
