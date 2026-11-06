package qesm.project.xml.scenario;

import static org.assertj.core.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import qesm.project.xml.TestResourceHelper;
import qesm.project.xml.exception.XmlLoadingException;
import qesm.project.xml.scenario.dto.AttackScenarioXml;

public class AttackScenarioXmlLoaderTest {

	private static final double EPSILON = 1e-6;
	private static final String GRAPH_UUID = "00000000-0000-0000-0000-000000000001";
	private static final String SCENARIO_UUID = "00000000-0000-0000-0000-000000000002";
	private static final String ACCESS_A1_UUID = "00000000-0000-0000-0000-0000000000a1";
	private static final String SKILL_S1_UUID = "00000000-0000-0000-0000-0000000000c1";
	private static final String GOAL_G1_UUID = "00000000-0000-0000-0000-0000000000e1";
	private static final String GOAL_G2_UUID = "00000000-0000-0000-0000-0000000000e2";

	private AttackScenarioXmlLoader loader;

	@BeforeEach
	public void setup() {
		loader = AttackScenarioXmlLoader.get();
	}

	@Test
	public void testLoadWhenXmlIsMinimal() {
		AttackScenarioXml xml = loader.load(resource("01-valid-minimal.xml"));
		assertThat(xml).isNotNull();
		assertThat(xml.uuid).isEqualTo(SCENARIO_UUID);
		assertThat(xml.name).isEqualTo("scenario");
		assertThat(xml.strategy).isEqualTo("greedy");
		assertThat(xml.goalsRequired).isEqualTo("ANY");
		assertThat(xml.timeBound).isCloseTo(100.0, within(EPSILON));
		assertThat(xml.graph.ref).isEqualTo(GRAPH_UUID);
		assertThatIterable(xml.targets).extracting(target -> target.ref).containsExactly(GOAL_G1_UUID);
		assertThat(xml.initialMarking).isEmpty();
		assertThat(xml.failCondition.type).isEqualTo("anydetection");
	}

	@Test
	public void testLoadWhenXmlIsFull() {
		AttackScenarioXml xml = loader.load(resource("02-valid-full.xml"));
		assertThat(xml).isNotNull();
		assertThat(xml.uuid).isEqualTo(SCENARIO_UUID);
		assertThat(xml.name).isEqualTo("full-scenario");
		assertThat(xml.strategy).isEqualTo("cautious");
		assertThat(xml.goalsRequired).isEqualTo("ALL");
		assertThat(xml.timeBound).isCloseTo(100.5, within(EPSILON));
		assertThat(xml.graph.ref).isEqualTo(GRAPH_UUID);
		assertThatIterable(xml.targets)
			.extracting(target -> target.ref)
			.containsExactlyInAnyOrder(GOAL_G1_UUID, GOAL_G2_UUID);
		assertThatIterable(xml.initialMarking)
			.extracting(vertex -> vertex.ref)
			.containsExactlyInAnyOrder(ACCESS_A1_UUID, SKILL_S1_UUID);
		assertThat(xml.failCondition.type).isEqualTo("anydetection");
	}

	@Test
	public void testLoadWhenXmlIsSyntacticallyValidWithUnknownTargetUuid() {
		AttackScenarioXml xml = loader.load(resource("03-valid-semantically-wrong.xml"));
		assertThat(xml).isNotNull();
		assertThat(xml.uuid).isEqualTo(SCENARIO_UUID);
		assertThat(xml.name).isEqualTo("scenario");
		assertThat(xml.strategy).isEqualTo("greedy");
		assertThat(xml.goalsRequired).isEqualTo("ANY");
		assertThat(xml.timeBound).isCloseTo(100.0, within(EPSILON));
		assertThat(xml.graph.ref).isEqualTo(GRAPH_UUID);
		assertThatIterable(xml.targets)
			.extracting(target -> target.ref)
			.containsExactly("00000000-0000-0000-0000-0000000000ff");
		assertThat(xml.failCondition.type).isEqualTo("anydetection");
	}

	@Test
	public void testLoadWhenStrategyIsUnknownThrowsException() {
		assertLoadThrowsXmlLoadingException("04-invalid-unknown-strategy.xml");
	}

	@Test
	public void testLoadWhenTargetIsDuplicatedThrowsException() {
		assertLoadThrowsXmlLoadingException("05-invalid-duplicate-target.xml");
	}

	@Test
	public void testLoadWhenElementOrderIsInvalidThrowsException() {
		assertLoadThrowsXmlLoadingException("06-invalid-element-order.xml");
	}

	@Test
	public void testLoadWhenTargetsAreEmptyThrowsException() {
		assertLoadThrowsXmlLoadingException("07-invalid-empty-targets.xml");
	}

	@Test
	public void testLoadWhenFailConditionIsMissingThrowsException() {
		assertLoadThrowsXmlLoadingException("08-invalid-missing-failcondition.xml");
	}

	@Test
	public void testLoadWhenUuidIsMissingThrowsException() {
		assertLoadThrowsXmlLoadingException("09-invalid-missing-uuid.xml");
	}

	@Test
	public void testLoadWhenTimeBoundIsNaNThrowsException() {
		assertLoadThrowsXmlLoadingException("10-invalid-timebound-nan.xml");
	}

	@Test
	public void testLoadWhenTimeBoundIsZeroThrowsException() {
		assertLoadThrowsXmlLoadingException("11-invalid-timebound-zero.xml");
	}

	@Test
	public void testLoadWhenElementIsUnknownThrowsException() {
		assertLoadThrowsXmlLoadingException("12-invalid-unknown-element.xml");
	}

	@Test
	public void testLoadWhenFailConditionIsUnknownThrowsException() {
		assertLoadThrowsXmlLoadingException("13-invalid-unknown-failcondition.xml");
	}

	@Test
	public void testLoadWhenFileIsEmptyThrowsException() {
		assertLoadThrowsXmlLoadingException("15-io-empty.xml");
	}

	@Test
	public void testLoadWhenXmlIsMalformedThrowsException() {
		assertLoadThrowsXmlLoadingException("16-io-malformed.xml");
	}

	@Test
	public void testLoadWhenRootElementIsWrongThrowsException() {
		assertLoadThrowsXmlLoadingException("17-io-wrong-root.xml");
	}

	private void assertLoadThrowsXmlLoadingException(String fileName) {
		assertThatExceptionOfType(XmlLoadingException.class)
			.isThrownBy(() -> loader.load(resource(fileName)))
			.withMessage("Error while loading: XML is not valid: " + fileName);
	}

	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/scenario/load/", name);
	}
}
