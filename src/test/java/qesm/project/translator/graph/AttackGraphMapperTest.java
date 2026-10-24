package qesm.project.translator.graph;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import qesm.project.translator.exception.AttackMapException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.utility.TestResourceHelper;

class AttackGraphMapperTest {

	private AttackGraphMapper mapper;
	
	@BeforeEach
	void setup() {
		mapper = new AttackGraphMapper();
	}
	
	@Nested
	class HappyCases {
		
		@Nested
		class NormalCases {
			
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
	}

	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/graph/map/", name);
	}
}
