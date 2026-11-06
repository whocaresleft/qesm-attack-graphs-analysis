package qesm.project.xml.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIterable;
import static org.assertj.core.api.Assertions.within;

import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import qesm.project.common.GoalCombination;
import qesm.project.graph.AttackGraph;
import qesm.project.graph.vertex.Goal;
import qesm.project.scenario.AttackScenario;
import qesm.project.scenario.failcondition.AnyDetection;
import qesm.project.scenario.failcondition.NeverFail;
import qesm.project.scenario.strategy.Cautious;
import qesm.project.scenario.strategy.Greedy;
import qesm.project.scenario.strategy.LowHangingFruit;
import qesm.project.xml.TestResourceHelper;
import qesm.project.xml.XmlHelper;
import qesm.project.xml.exception.XmlMappingException;
import qesm.project.xml.graph.AttackGraphXmlLoader;
import qesm.project.xml.graph.AttackGraphXmlMapper;
import qesm.project.xml.scenario.dto.AttackScenarioXml;

public class AttackScenarioXmlMapperTest {

	private static final double EPSILON = 1e-6;
	private static final UUID ACCESS_A1_UUID = XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1");
	private static final UUID GOAL_G1_UUID = XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1");
	private static final UUID GOAL_G2_UUID = XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e2");

	private AttackGraph graph;
	private AttackScenarioXmlMapper mapper;

	@BeforeEach
	public void setup() {
		mapper = AttackScenarioXmlMapper.get();
		graph = AttackGraphXmlMapper.get().toAttackGraph(AttackGraphXmlLoader.get().load(resource("sampleGraph.xml")));
	}

	@Test
	public void testToAttackScenarioWhenXmlIsMinimal() {
		AttackScenario scenario = mapper.toAttackScenario(loadScenarioXml("01-valid-minimal.xml"), graph);

		assertThat(scenario).isNotNull();
		assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000001"));
		assertThat(scenario.getName()).isEqualTo("MinimalScenario");
		assertThat(scenario.getStrategy()).isExactlyInstanceOf(Greedy.class);
		assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ANY);
		assertThat(scenario.getTimeBound()).isCloseTo(100.0, within(EPSILON));
		assertThat(scenario.getFailCondition()).isExactlyInstanceOf(NeverFail.class);
		assertThat(scenario.getInitialMarking()).isEmpty();
		assertThatIterable(scenario.getTargets())
			.containsExactly((Goal) graph.getVertexByUuid(GOAL_G1_UUID).orElseThrow());
	}

	@Test
	public void testToAttackScenarioWhenXmlIsFull() {
		AttackScenario scenario = mapper.toAttackScenario(loadScenarioXml("02-valid-full.xml"), graph);

		assertThat(scenario).isNotNull();
		assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000002"));
		assertThat(scenario.getName()).isEqualTo("FullScenario");
		assertThat(scenario.getStrategy()).isExactlyInstanceOf(Cautious.class);
		assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ALL);
		assertThat(scenario.getTimeBound()).isCloseTo(3600.5, within(EPSILON));
		assertThat(scenario.getFailCondition()).isExactlyInstanceOf(AnyDetection.class);
		assertThatIterable(scenario.getInitialMarking())
			.containsExactly(graph.getVertexByUuid(ACCESS_A1_UUID).orElseThrow());
		assertThatIterable(scenario.getTargets())
			.containsExactlyInAnyOrder(
				(Goal) graph.getVertexByUuid(GOAL_G1_UUID).orElseThrow(),
				(Goal) graph.getVertexByUuid(GOAL_G2_UUID).orElseThrow());
	}

	@Test
	public void testToAttackScenarioWhenStrategyIsLowHangingFruit() {
		AttackScenario scenario = mapper.toAttackScenario(loadScenarioXml("03-valid-other-strategies.xml"), graph);

		assertThat(scenario).isNotNull();
		assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000003"));
		assertThat(scenario.getName()).isEqualTo("OtherStrategies");
		assertThat(scenario.getStrategy()).isExactlyInstanceOf(LowHangingFruit.class);
		assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ANY);
		assertThat(scenario.getTimeBound()).isCloseTo(50.0, within(EPSILON));
		assertThat(scenario.getFailCondition()).isExactlyInstanceOf(NeverFail.class);
		assertThatIterable(scenario.getInitialMarking())
			.containsExactly(graph.getVertexByUuid(ACCESS_A1_UUID).orElseThrow());
		assertThatIterable(scenario.getTargets())
			.containsExactly((Goal) graph.getVertexByUuid(GOAL_G1_UUID).orElseThrow());
	}

	@Test
	public void testToAttackScenarioWhenTargetDoesNotExistInGraphThrowsException() {
		AttackScenarioXml xml = loadScenarioXml("04-invalid-unknown-target.xml");

		assertThatExceptionOfType(XmlMappingException.class)
			.isThrownBy(() -> mapper.toAttackScenario(xml, graph))
			.withMessage("Error while mapping: Scenario 'UnknownTarget': target unknown 00000000-0000-0000-0000-999999999999");
	}

	@Test
	public void testToAttackScenarioWhenTargetIsNotAGoalThrowsException() {
		AttackScenarioXml xml = loadScenarioXml("05-invalid-target-is-not-goal.xml");

		assertThatExceptionOfType(XmlMappingException.class)
			.isThrownBy(() -> mapper.toAttackScenario(xml, graph))
			.withMessage("Error while mapping: Vertex 'A1' is not a Goal");
	}

	@Test
	public void testToAttackScenarioWhenInitialMarkingVertexDoesNotExistInGraphThrowsException() {
		AttackScenarioXml xml = loadScenarioXml("06-invalid-unknown-initial-marking.xml");

		assertThatExceptionOfType(XmlMappingException.class)
			.isThrownBy(() -> mapper.toAttackScenario(xml, graph))
			.withMessage("Error while mapping: Scenario 'UnknownMarking': vertex unknown 00000000-0000-0000-0000-999999999999");
	}

	@Test
	public void testToAttackScenarioWhenGraphUuidDoesNotMatchThrowsException() {
		AttackScenarioXml xml = loadScenarioXml("07-invalid-mismatched-graph-uuid.xml");

		assertThatExceptionOfType(XmlMappingException.class)
			.isThrownBy(() -> mapper.toAttackScenario(xml, graph))
			.withMessage("Error while mapping: Graph uuid is not coherent with given AttackGraph");
	}

	private static AttackScenarioXml loadScenarioXml(String fileName) {
		return AttackScenarioXmlLoader.get().load(resource(fileName));
	}

	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/scenario/map/", name);
	}
}
