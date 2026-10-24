package qesm.project.translator.graph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.graph.dto.step.StepXml;
import qesm.project.translator.graph.dto.vertex.AccessXml;
import qesm.project.translator.graph.dto.vertex.GoalXml;

public class AttackGraphXmlLoaderTest {

	@Nested
	class HappyCases {
		
		@Nested
		class NormalCases {
		
			@Test
			void xmlGraph01() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("01-valid-minimal.xml"));
				assertThat(xml)
					.isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
				assertThat(xml.name).isEqualTo("minimal");
				
				assertThat(xml.vertices.accesses)
				    .hasSize(1)
				    .extracting(
			    		access -> access.uuid,
			    		access -> access.name,
			    		access -> access.detectionTime
		    		)
				    .containsExactly(tuple("00000000-0000-0000-0000-0000000000a1", "A1", null));
				
				assertThat(xml.vertices.goals)
				    .hasSize(1)
				    .extracting(
			    		goal -> goal.uuid,
			    		goal -> goal.name,
			    		goal -> goal.value,
			    		goal -> goal.detectionTime
		    		)
				    .containsExactly(tuple("00000000-0000-0000-0000-0000000000e1", "G1", 10.0, null));
				
				assertThat(xml.vertices.skills).isEmpty();
				assertThat(xml.vertices.knowledge).isEmpty();
				
				assertThat(xml.steps)
				    .hasSize(1)
				    .filteredOn(step -> step.uuid.equals("00000000-0000-0000-0000-000000000051"))
				    .singleElement()
				    .satisfies(step -> {
				    	assertThat(step.name).isEqualTo("ST1");
				    	assertThat(step.successProbability).isEqualTo(0.7);
				    	assertThat(step.failureProbability).isEqualTo(0.2);
				    	assertThat(step.failureDetectedProbability).isEqualTo(0.1);
				    	assertThat(step.detectionTime).isNull();
				    	assertThat(step.duration.type).isEqualTo("exponential");
				    	assertThat(step.duration.rate).isEqualTo(0.5);
				    	
				    	assertThat(step.produces)
				    	    .hasSize(1)
				    	    .extracting(produces -> produces.ref)
				    	    .containsExactly("00000000-0000-0000-0000-0000000000e1");
				    	
				    	assertThat(step.requires)
				    	    .hasSize(1)
				    	    .extracting(requires -> requires.ref)
				    	    .containsExactly("00000000-0000-0000-0000-0000000000a1");
				    });
			}
			
			@Test
			void xmlGraph02() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("02-valid-full.xml"));
				assertThat(xml)
				    .isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
				assertThat(xml.name).isEqualTo("full");
				
				assertThat(xml.vertices.accesses);
				
				assertThat(xml.vertices.goals);
				
				assertThat(xml.vertices.skills);
				
				assertThat(xml.vertices.knowledge);
				
				assertThat(xml.steps);
			}
			
			@Test
			void xmlGraph03() {}
			
			@Test
			void xmlGraph04() {}
			
			@Test
			void xmlGraph05() {}
			
			@Test
			void xmlGraph06() {}
		}
		
		@Nested
		class EdgeCases {
			
			@Test
			void xmlGraphEdgeCaseInXmlEmptyGraphIsOk() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("16-edge-empty-graph.xml"));
				assertThat(xml)
					.isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
				assertThat(xml.name).isEqualTo("empty");
				
				assertThat(xml.vertices.accesses).isEmpty();
				assertThat(xml.vertices.goals).isEmpty();
				assertThat(xml.vertices.skills).isEmpty();
				assertThat(xml.vertices.knowledge).isEmpty();
				
				assertThat(xml.steps).isEmpty();
			}
		}
	}

	@Nested
	class ErrorCases {
		
		@Nested
		class NormalCases {
			
			@ParameterizedTest(name = "{index}: ''{0}''")
			@CsvSource({
				"07-invalid-malformed-xml",
				"08-invalid-wrong-root",
				"09-invalid-empty-file",
				"10-invalid-missing-graph-uuid",
				"11-invalid-missing-graph-name",
				"13-invalid-non-numeric",
				"14-invalid-exponential-missing-rate",
				"15-invalid-missing-duration",
			})
			void xmlGraphInvalidXmlShouldThrowAttackLoadException(String filename) {
				String completeFilename = filename + ".xml";
				assertThatExceptionOfType(AttackLoadException.class)
		    		.isThrownBy(() -> AttackGraphXmlLoader.load(resource(completeFilename)))
			    	.withMessage("XML is not valid: " + completeFilename);
			}
		}
		
		@Nested
		class EdgeCases {
			
			@ParameterizedTest(name = "{index}: ''{0}''")
			@CsvSource({
				"17-edge-unknown-element",
				"18-edge-nan-and-infinity",
			})
			void xmlGraphEdgeCaseInXmlShouldThrow(String filename) {
				String completeFilename = filename + ".xml";
				assertThatExceptionOfType(AttackLoadException.class)
		    		.isThrownBy(() -> AttackGraphXmlLoader.load(resource(completeFilename)))
			    	.withMessage("XML is not valid: " + completeFilename);
			}
		}
	}
	
	private static Path resource(String name) {
		try {
			URL url = AttackGraphXmlLoaderTest.class.getResource("/xml/graph/load/" + name);
			if (url == null) throw new IllegalArgumentException("Missing test resource: " + name);
			return Path.of(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
}
