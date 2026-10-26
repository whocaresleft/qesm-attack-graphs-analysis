package qesm.project.translator.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_graph.vertex.Goal;
import qesm.project.attack_scenario.fail_condition.AnyDetection;
import qesm.project.attack_scenario.fail_condition.NeverFail;
import qesm.project.attack_scenario.scenario.AttackScenario;
import qesm.project.attack_scenario.strategy.Cautious;
import qesm.project.attack_scenario.strategy.Greedy;
import qesm.project.attack_scenario.strategy.LowHangingFruit;
import qesm.project.translator.exception.AttackMapException;
import qesm.project.translator.graph.AttackGraphMapper;
import qesm.project.translator.graph.AttackGraphXmlLoader;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;
import qesm.project.translator.utility.TestResourceHelper;
import qesm.project.translator.utility.XmlHelper;
import qesm.project.utility.GoalCombination;

public class AttackScenarioMapperTest {

	private static double EPSILON = 1e-6;
	
	private AttackGraph graph;
	
	private AttackScenarioMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = new AttackScenarioMapper();
		graph = new AttackGraphMapper().toAttackGraph(AttackGraphXmlLoader.load(resource("sampleGraph.xml")));
	}
	
	@Nested
	class HappyCases {
		
		@Test
		void xmlGraph01Minimal() {
			AttackScenario scenario = mapper.toAttackScenario(AttackScenarioXmlLoader.load(resource("01-valid-minimal.xml")), graph);
			
			assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000001"));
			assertThat(scenario.getName()).isEqualTo("MinimalScenario");
			assertThat(scenario.getStrategy()).isExactlyInstanceOf(Greedy.class);
			assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ANY);
			assertThat(scenario.getTimeBound()).isBetween(100.0 - EPSILON, 100.0 + EPSILON);
			assertThat(scenario.getFailCondition()).isExactlyInstanceOf(NeverFail.class);
			assertThat(scenario.getInitialMarking())
				.isEmpty();
			assertThat(scenario.getTargets())
				.hasSize(1)
				.filteredOn(goal -> goal.getUuid().equals(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1")))
				.singleElement()
				.satisfies(goal -> scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1")).get().equals(goal));
		}
		
		@Test
		void xmlGraph02Full() {
			AttackScenario scenario = mapper.toAttackScenario(AttackScenarioXmlLoader.load(resource("02-valid-full.xml")), graph);
			
			assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000002"));
			assertThat(scenario.getName()).isEqualTo("FullScenario");
			assertThat(scenario.getStrategy()).isExactlyInstanceOf(Cautious.class);
			assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ALL);
			assertThat(scenario.getTimeBound()).isBetween(3600.5 - EPSILON, 3600.5 + EPSILON);
			assertThat(scenario.getFailCondition()).isExactlyInstanceOf(AnyDetection.class);
			assertThat(scenario.getInitialMarking())
				.hasSize(1)
				.filteredOn(vertex -> vertex.getUuid().equals(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1")))
				.singleElement()
				.satisfies(vertex -> scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1")).get().equals(vertex));
			
			assertThat(scenario.getTargets())
				.hasSize(2)
				.containsExactlyInAnyOrder(
					(Goal)scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1")).get(),
					(Goal)scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e2")).get());
		}
		
		@Test
		void xmlGraph03OtherStrategies() {
			AttackScenario scenario = mapper.toAttackScenario(AttackScenarioXmlLoader.load(resource("03-valid-other-strategies.xml")), graph);
			
			assertThat(scenario.getUuid()).isEqualTo(XmlHelper.parseUuid("10000000-0000-0000-0000-000000000003"));
			assertThat(scenario.getName()).isEqualTo("OtherStrategies");
			assertThat(scenario.getStrategy()).isExactlyInstanceOf(LowHangingFruit.class);
			assertThat(scenario.getGoalsRequired()).isEqualTo(GoalCombination.ANY);
			assertThat(scenario.getTimeBound()).isBetween(50.0 - EPSILON, 50.0 + EPSILON);
			assertThat(scenario.getFailCondition()).isExactlyInstanceOf(NeverFail.class);
			assertThat(scenario.getInitialMarking())
				.hasSize(1)
				.filteredOn(vertex -> vertex.getUuid().equals(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1")))
				.singleElement()
				.satisfies(vertex -> scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000a1")).get().equals(vertex));
				
			assertThat(scenario.getTargets())
				.hasSize(1)
				.filteredOn(goal -> goal.getUuid().equals(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1")))
				.singleElement()
				.satisfies(goal -> scenario.getGraph().getVertexByUuid(XmlHelper.parseUuid("00000000-0000-0000-0000-0000000000e1")).get().equals(goal));
		}
	}
	
	@Nested
	class ErrorCases {
		
		@ParameterizedTest(name = "{index}: ''{0}''")
		@CsvSource({
			"04-invalid-unknown-target,Error while mapping: Scenario 'UnknownTarget': target unknown 00000000-0000-0000-0000-999999999999",
			"05-invalid-target-is-not-goal,Error while mapping: Vertex 'A1' is not a Goal",
			"06-invalid-unknown-initial-marking,Error while mapping: Scenario 'UnknownMarking': vertex unknown 00000000-0000-0000-0000-999999999999"
		})
		void xmlGraphInvalidXmlShouldThrowAttackMapException(String fileName, String expectedErrorMessage) {
			AttackScenarioXml xml = AttackScenarioXmlLoader.load(resource(fileName + ".xml"));
		
			assertThatExceptionOfType(AttackMapException.class)
				.isThrownBy(() -> mapper.toAttackScenario(xml, graph))
				.withMessage(expectedErrorMessage);
		}
	}
	
	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/scenario/map/", name);
	}
}
