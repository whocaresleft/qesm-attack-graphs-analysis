package qesm.project.xml.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIterable;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

	private static double EPSILON = 1e-6;
	
	private AttackGraph graph;
	
	private AttackScenarioXmlMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = AttackScenarioXmlMapper.get();
		graph = AttackGraphXmlMapper.get().toAttackGraph(AttackGraphXmlLoader.get().load(resource("sampleGraph.xml")));
	}
	
	@Nested
	class HappyCases {
		
		@Test
		void xmlGraph01Minimal() {
			AttackScenario scenario = mapper.toAttackScenario(AttackScenarioXmlLoader.get().load(resource("01-valid-minimal.xml")), graph);
			assertThat(scenario).isNotNull();
			
			assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000001"));
			assertThat(scenario.getName()).isEqualTo("MinimalScenario");
			assertThat(scenario.getStrategy()).isExactlyInstanceOf(Greedy.class);
			assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ANY);
			assertThat(scenario.getTimeBound()).isBetween(100.0 - EPSILON, 100.0 + EPSILON);
			assertThat(scenario.getFailCondition()).isExactlyInstanceOf(NeverFail.class);
			assertThat(scenario.getInitialMarking())
				.isEmpty();
			assertThatIterable(scenario.getTargets())
				.hasSize(1)
				.filteredOn(goal -> goal.getUuid().equals(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1")))
				.singleElement()
				.satisfies(goal -> assertThat(scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1"))).contains(goal));
		}
		
		@Test
		void xmlGraph02Full() {
			AttackScenario scenario = mapper.toAttackScenario(AttackScenarioXmlLoader.get().load(resource("02-valid-full.xml")), graph);
			assertThat(scenario).isNotNull();
			
			assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000002"));
			assertThat(scenario.getName()).isEqualTo("FullScenario");
			assertThat(scenario.getStrategy()).isExactlyInstanceOf(Cautious.class);
			assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ALL);
			assertThat(scenario.getTimeBound()).isBetween(3600.5 - EPSILON, 3600.5 + EPSILON);
			assertThat(scenario.getFailCondition()).isExactlyInstanceOf(AnyDetection.class);
			assertThatIterable(scenario.getInitialMarking())
				.hasSize(1)
				.filteredOn(vertex -> vertex.getUuid().equals(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1")))
				.singleElement()
				.satisfies(vertex -> assertThat(scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1"))).contains(vertex));
			
			assertThatIterable(scenario.getTargets())
				.hasSize(2)
				.containsExactlyInAnyOrder(
					(Goal)scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1")).orElseThrow(),
					(Goal)scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e2")).orElseThrow());
		}
		
		@Test
		void xmlGraph03OtherStrategies() {
			AttackScenario scenario = mapper.toAttackScenario(AttackScenarioXmlLoader.get().load(resource("03-valid-other-strategies.xml")), graph);
			assertThat(scenario).isNotNull();
			
			assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000003"));
			assertThat(scenario.getName()).isEqualTo("OtherStrategies");
			assertThat(scenario.getStrategy()).isExactlyInstanceOf(LowHangingFruit.class);
			assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ANY);
			assertThat(scenario.getTimeBound()).isBetween(50.0 - EPSILON, 50.0 + EPSILON);
			assertThat(scenario.getFailCondition()).isExactlyInstanceOf(NeverFail.class);
			assertThatIterable(scenario.getInitialMarking())
				.hasSize(1)
				.filteredOn(vertex -> vertex.getUuid().equals(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1")))
				.singleElement()
				.satisfies(vertex -> assertThat(scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1"))).contains(vertex));
				
			assertThatIterable(scenario.getTargets())
				.hasSize(1)
				.filteredOn(goal -> goal.getUuid().equals(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1")))
				.singleElement()
				.satisfies(goal -> assertThat(scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1"))).contains(goal));
		}
	}
	
	@Nested
	class ErrorCases {
		
		@ParameterizedTest(name = "{index}: ''{0}''")
		@CsvSource({
			"04-invalid-unknown-target,Error while mapping: Scenario 'UnknownTarget': target unknown 00000000-0000-0000-0000-999999999999",
			"05-invalid-target-is-not-goal,Error while mapping: Vertex 'A1' is not a Goal",
			"06-invalid-unknown-initial-marking,Error while mapping: Scenario 'UnknownMarking': vertex unknown 00000000-0000-0000-0000-999999999999",
			"07-invalid-mismatched-graph-uuid,Error while mapping: Graph uuid is not coherent with given AttackGraph"
		})
		void xmlGraphInvalidXmlShouldThrowXmlMappingException(String fileName, String expectedErrorMessage) {
			AttackScenarioXml xml = AttackScenarioXmlLoader.get().load(resource(fileName + ".xml"));
		
			assertThatExceptionOfType(XmlMappingException.class)
				.isThrownBy(() -> mapper.toAttackScenario(xml, graph))
				.withMessage(expectedErrorMessage);
		}
	}
	
	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/scenario/map/", name);
	}
}
