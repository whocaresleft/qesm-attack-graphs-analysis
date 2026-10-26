package qesm.project.translator.scenario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;
import qesm.project.translator.utility.TestResourceHelper;

public class AttackScenarioXmlLoaderTest {

	private static double EPSILON = 1e-6;
	
	@Nested
	class HappyCases {
		
		@Test
		void xmlGraph01Minimal() {
			AttackScenarioXml xml = AttackScenarioXmlLoader.load(resource("01-valid-minimal.xml"));
			
			assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000002");
			assertThat(xml.name).isEqualTo("scenario");
			
			assertThat(xml.strategy).isEqualTo("greedy");
			
			assertThat(xml.goalsRequired).isEqualTo("ANY");
			
			assertThat(xml.timeBound).isBetween(100.0 - EPSILON, 100.0 + EPSILON);
			
			assertThat(xml.graph.ref).isNull();
			assertThat(xml.graph.file).isEqualTo("graph.xml");
			
			assertThat(xml.targets)
				.hasSize(1)
				.filteredOn(target -> target.ref.equals("00000000-0000-0000-0000-0000000000e1"))
				.singleElement()
				.isNotNull();
			
			assertThat(xml.initialMarking).isEmpty();
			
			assertThat(xml.failCondition.type).isEqualTo("anydetection");
		}
		
		@Test
		void xmlGraph02Full() {
			AttackScenarioXml xml = AttackScenarioXmlLoader.load(resource("02-valid-full.xml"));
			
			assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000002");
			assertThat(xml.name).isEqualTo("full-scenario");
			
			assertThat(xml.strategy).isEqualTo("cautious");
			
			assertThat(xml.goalsRequired).isEqualTo("ALL");
			
			assertThat(xml.timeBound).isBetween(100.5 - EPSILON, 100.5 + EPSILON);
			
			assertThat(xml.graph.ref).isNull();
			assertThat(xml.graph.file).isEqualTo("graph.xml");
			
			assertThat(xml.targets)
				.hasSize(2)
				.filteredOn(target -> target.ref.equals("00000000-0000-0000-0000-0000000000e1"))
				.singleElement()
				.isNotNull();
			assertThat(xml.targets)
				.filteredOn(target -> target.ref.equals("00000000-0000-0000-0000-0000000000e2"))
				.singleElement()
				.isNotNull();
			
			assertThat(xml.initialMarking)
				.hasSize(2)
				.filteredOn(vertex -> vertex.ref.equals("00000000-0000-0000-0000-0000000000a1"))
				.singleElement()
				.isNotNull();
			assertThat(xml.initialMarking)
				.filteredOn(vertex -> vertex.ref.equals("00000000-0000-0000-0000-0000000000c1"))
				.singleElement()
				.isNotNull();
			
			assertThat(xml.failCondition.type).isEqualTo("anydetection");
		}
		
		@Test
		void xmlGraph03SemanticallyWrong() {
			AttackScenarioXml xml = AttackScenarioXmlLoader.load(resource("03-valid-semantically-wrong.xml"));
			
			assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000002");
			assertThat(xml.name).isEqualTo("scenario");
			
			assertThat(xml.strategy).isEqualTo("greedy");
			
			assertThat(xml.goalsRequired).isEqualTo("ANY");
			
			assertThat(xml.timeBound).isBetween(100 - EPSILON, 100 + EPSILON);
			
			assertThat(xml.graph.ref).isNull();
			assertThat(xml.graph.file).isEqualTo("missing.xml");
			
			assertThat(xml.targets)
				.hasSize(1)
				.filteredOn(target -> target.ref.equals("00000000-0000-0000-0000-0000000000ff"))
				.singleElement()
				.isNotNull();
			
			assertThat(xml.failCondition.type).isEqualTo("anydetection");
		}
	}

	@Nested
	class ErrorCases {
			
		@ParameterizedTest(name = "{index}: ''{0}''")
		@CsvSource({
			"04-invalid-bad-uuid-ref",
			"05-invalid-duplicate-target",
			"06-invalid-element-order",
			"07-invalid-empty-targets",
			"08-invalid-missing-failcondition",
			"09-invalid-missing-uuid",
			"10-invalid-timebound-nan",
			"11-invalid-timebound-zero",
			"12-invalid-unknown-element",
			"13-invalid-unknown-failcondition",
			"14-invalid-unknown-strategy",
			"15-io-empty",
			"16-io-malformed",
			"17-io-wrong-root",
		})
		void xmlGraphInvalidXmlShouldThrowAttackLoadException(String filename) {
			String completeFilename = filename + ".xml";
			assertThatExceptionOfType(AttackLoadException.class)
	    		.isThrownBy(() -> AttackScenarioXmlLoader.load(resource(completeFilename)))
		    	.withMessage("Error while loading: XML is not valid: " + completeFilename);
		}
	}
	
	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/scenario/load/", name);
	}
}
