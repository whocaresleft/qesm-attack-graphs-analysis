package qesm.project.translator.graph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;


import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.graph.dto.step.StepXml;
import qesm.project.translator.utility.DistributionXml;
import qesm.project.translator.utility.RefXml;
import qesm.project.translator.utility.TestResourceHelper;

public class AttackGraphXmlLoaderTest {

	@Nested
	class HappyCases {
		
		@Nested
		class NormalCases {
		
			@Test
			void xmlGraph01Minimal() {
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
			void xmlGraph02Full() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("02-valid-full.xml"));
				assertThat(xml)
				    .isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
				assertThat(xml.name).isEqualTo("full");
				
				assertThat(xml.vertices.accesses)
					.hasSize(2)
					.extracting(
						access -> access.uuid,
						access -> access.name,
						access -> access.detectionTime.type,
						access -> access.detectionTime.rate
					)
					.containsExactlyInAnyOrder(
						tuple("00000000-0000-0000-0000-0000000000a1", "A1", "exponential", 0.05),
						tuple("00000000-0000-0000-0000-0000000000a2", "A2", "exponential", 0.06)
					);
				
				assertThat(xml.vertices.goals)
					.hasSize(1)
					.extracting(
						goal -> goal.uuid,
						goal -> goal.name,
						goal -> goal.value,
						goal -> goal.detectionTime.type,
						goal -> goal.detectionTime.rate
					)
					.containsExactly(tuple(
						"00000000-0000-0000-0000-0000000000e1",
						"G1",
						10.5,
						"exponential",
						0.1
					));
				
				assertThat(xml.vertices.skills)
					.hasSize(1)
					.extracting(skill -> skill.uuid, skill -> skill.name)
					.containsExactly(tuple("00000000-0000-0000-0000-0000000000c1", "S1"));
				
				assertThat(xml.vertices.knowledge)
					.hasSize(1)
					.extracting(knowledge -> knowledge.uuid, knowledge -> knowledge.name)
					.containsExactly(tuple("00000000-0000-0000-0000-0000000000d1", "K1"));
				
				assertThat(xml.steps)
				    .hasSize(1)
				    .filteredOn(step -> step.uuid.equals("00000000-0000-0000-0000-000000000051"))
				    .singleElement()
				    .satisfies(step -> {
				    	assertThat(step.name).isEqualTo("ST1");
				    	assertThat(step.successProbability).isEqualTo(0.6);
				    	assertThat(step.failureProbability).isEqualTo(0.3);
				    	assertThat(step.failureDetectedProbability).isEqualTo(0.1);
				    	
				    	assertThat(step.duration.type).isEqualTo("exponential");
				    	assertThat(step.duration.rate).isEqualTo(1.0);
				    	
				    	assertThat(step.detectionTime.type).isEqualTo("exponential");
				    	assertThat(step.detectionTime.rate).isEqualTo(0.1);
				    	
				    	assertThat(step.requires)
				    	    .hasSize(3)
				    	    .extracting(requires -> requires.ref)
				    	    .containsExactlyInAnyOrder(
			    	    		"00000000-0000-0000-0000-0000000000a1",
			    	    		"00000000-0000-0000-0000-0000000000c1",
			    	    		"00000000-0000-0000-0000-0000000000d1"
		    	    		);
				    	
				    	assertThat(step.produces)
				    	    .hasSize(2)
				    	    .extracting(produces -> produces.ref)
				    	    .containsExactlyInAnyOrder(
			    	    		"00000000-0000-0000-0000-0000000000a2",
			    	    		"00000000-0000-0000-0000-0000000000e1"
		    	    		);
				    });
			}
			
			@Test
			void xmlGraph03ValidChain() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("03-valid-chain.xml"));
				assertThat(xml)
				    .isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
				assertThat(xml.name).isEqualTo("chain");
				
				assertThat(xml.vertices.accesses)
					.hasSize(2)
					.extracting(
						access -> access.uuid,
						access -> access.name,
						access -> access.detectionTime
					)
					.containsExactlyInAnyOrder(
						tuple("00000000-0000-0000-0000-0000000000a1", "A1", null),
						tuple("00000000-0000-0000-0000-0000000000a2", "A2", null)
					);
				
				assertThat(xml.vertices.goals)
					.hasSize(1)
					.extracting(
						goal -> goal.uuid,
						goal -> goal.name,
						goal -> goal.value,
						goal -> goal.detectionTime
					)
					.containsExactly(tuple(
						"00000000-0000-0000-0000-0000000000e1",
						"G1",
						1.0,
						null
					));
				
				assertThat(xml.vertices.skills).isEmpty();
				assertThat(xml.vertices.knowledge).isEmpty();
				
			    StepXml expected1 = new StepXml();
			    expected1.uuid = "00000000-0000-0000-0000-000000000051";
			    expected1.name = "ST1";
				expected1.successProbability = 0.8;
				expected1.failureProbability = 0.1;
				expected1.failureDetectedProbability = 0.1;
				expected1.duration = new DistributionXml();
				expected1.duration.type = "exponential";
				expected1.duration.rate = 1.0;
				RefXml requires1 = new RefXml();
				requires1.ref = "00000000-0000-0000-0000-0000000000a1";
				RefXml produces1 = new RefXml();
				produces1.ref = "00000000-0000-0000-0000-0000000000a2";
				expected1.requires.add(requires1);
				expected1.produces.add(produces1);
			    
			    StepXml expected2 = new StepXml();
			    expected2.uuid = "00000000-0000-0000-0000-000000000052";
			    expected2.name = "ST2";
				expected2.successProbability = 0.5;
				expected2.failureProbability = 0.4;
				expected2.failureDetectedProbability = 0.1;
				expected2.duration = new DistributionXml();
				expected2.duration.type = "exponential";
				expected2.duration.rate = 2.0;
				RefXml requires2 = new RefXml();
				requires2.ref = "00000000-0000-0000-0000-0000000000a2";
				RefXml produces2 = new RefXml();
				produces2.ref = "00000000-0000-0000-0000-0000000000e1";
				expected2.requires.add(requires2);
				expected2.produces.add(produces2);
				
				assertThat(xml.steps).containsExactlyInAnyOrder(expected1, expected2);
			}
			
			@Test
			void xmlGraph04TwoPaths() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("04-valid-two-paths.xml"));
				assertThat(xml)
				    .isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
				assertThat(xml.name).isEqualTo("two-paths");
				
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
					.containsExactly(tuple("00000000-0000-0000-0000-0000000000e1", "G1", 5.0, null));
				
				
				assertThat(xml.vertices.skills)
					.hasSize(1)
					.extracting(
						skill -> skill.uuid,
						skill -> skill.name
					)
					.containsExactly(tuple("00000000-0000-0000-0000-0000000000c1", "S1"));
				
				assertThat(xml.vertices.knowledge).isEmpty();
				
			    StepXml expected1 = new StepXml();
			    expected1.uuid = "00000000-0000-0000-0000-000000000051";
			    expected1.name = "ST1";
				expected1.successProbability = 0.5;
				expected1.failureProbability = 0.4;
				expected1.failureDetectedProbability = 0.1;
				expected1.duration = new DistributionXml();
				expected1.duration.type = "exponential";
				expected1.duration.rate = 1.0;
				RefXml requires1 = new RefXml();
				requires1.ref = "00000000-0000-0000-0000-0000000000a1";
				RefXml produces1 = new RefXml();
				produces1.ref = "00000000-0000-0000-0000-0000000000e1";
				expected1.requires.add(requires1);
				expected1.produces.add(produces1);
			    
			    StepXml expected2 = new StepXml();
			    expected2.uuid = "00000000-0000-0000-0000-000000000052";
			    expected2.name = "ST2";
				expected2.successProbability = 0.9;
				expected2.failureProbability = 0.0;
				expected2.failureDetectedProbability = 0.1;
				expected2.duration = new DistributionXml();
				expected2.duration.type = "exponential";
				expected2.duration.rate = 0.2;
				RefXml requires2 = new RefXml();
				requires2.ref = "00000000-0000-0000-0000-0000000000c1";
				RefXml produces2 = new RefXml();
				produces2.ref = "00000000-0000-0000-0000-0000000000e1";
				expected2.requires.add(requires2);
				expected2.produces.add(produces2);
				
				assertThat(xml.steps).containsExactlyInAnyOrder(expected1, expected2);
			}
			
			@Test
			void xmlGraph05CertainSuccess() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("05-valid-certain-success.xml"));
				assertThat(xml)
				    .isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
				assertThat(xml.name).isEqualTo("certain-success");
				
				assertThat(xml.vertices.accesses)
					.hasSize(1)
					.extracting(
						access -> access.uuid,
						access -> access.name,
						access -> access.detectionTime
					)
					.containsExactlyInAnyOrder(
						tuple("00000000-0000-0000-0000-0000000000a1", "A1", null)
					);
				
				assertThat(xml.vertices.goals)
					.hasSize(1)
					.extracting(
						goal -> goal.uuid,
						goal -> goal.name,
						goal -> goal.value,
						goal -> goal.detectionTime
					)
					.containsExactly(tuple(
						"00000000-0000-0000-0000-0000000000e1",
						"G1",
						0.0,
						null
					));
				
				assertThat(xml.vertices.skills).isEmpty();
				
				assertThat(xml.vertices.knowledge).isEmpty();
				
				assertThat(xml.steps)
				    .hasSize(1)
				    .filteredOn(step -> step.uuid.equals("00000000-0000-0000-0000-000000000051"))
				    .singleElement()
				    .satisfies(step -> {
				    	assertThat(step.name).isEqualTo("ST1");
				    	assertThat(step.successProbability).isEqualTo(1.0);
				    	assertThat(step.failureProbability).isEqualTo(0.0);
				    	assertThat(step.failureDetectedProbability).isEqualTo(0.0);
				    	
				    	assertThat(step.duration.type).isEqualTo("exponential");
				    	assertThat(step.duration.rate).isEqualTo(1.0);
				    	
				    	assertThat(step.detectionTime).isNull();
				    	
				    	assertThat(step.requires)
				    	    .hasSize(1)
				    	    .extracting(requires -> requires.ref)
				    	    .containsExactly(
			    	    		"00000000-0000-0000-0000-0000000000a1"
		    	    		);
				    	
				    	assertThat(step.produces)
				    	    .hasSize(1)
				    	    .extracting(produces -> produces.ref)
				    	    .containsExactly(
			    	    		"00000000-0000-0000-0000-0000000000e1"
		    	    		);
				    });
			}
			
			@Test
			void xmlGraph06UuidWithUpperCaseAndSpaces() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("06-valid-uuid-case-and-spaces.xml"));
				assertThat(xml)
				    .isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo(" 00000000-0000-0000-0000-000000000001 ");
				assertThat(xml.name).isEqualTo("case");
				
				assertThat(xml.vertices.accesses)
					.hasSize(1)
					.extracting(
						access -> access.uuid,
						access -> access.name,
						access -> access.detectionTime
					)
					.containsExactlyInAnyOrder(
						tuple("00000000-0000-0000-0000-0000000000A1", "A1", null)
					);
				
				assertThat(xml.vertices.goals)
					.hasSize(1)
					.extracting(
						goal -> goal.uuid,
						goal -> goal.name,
						goal -> goal.value,
						goal -> goal.detectionTime
					)
					.containsExactly(tuple(
						"00000000-0000-0000-0000-0000000000E1",
						"G1",
						1.0,
						null
					));
				
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
				    	
				    	assertThat(step.duration.type).isEqualTo("exponential");
				    	assertThat(step.duration.rate).isEqualTo(1.0);
				    	
				    	assertThat(step.detectionTime).isNull();
				    	
				    	assertThat(step.requires)
				    	    .hasSize(1)
				    	    .extracting(requires -> requires.ref)
				    	    .containsExactly(
			    	    		"00000000-0000-0000-0000-0000000000a1"
		    	    		);
				    	
				    	assertThat(step.produces)
				    	    .hasSize(1)
				    	    .extracting(produces -> produces.ref)
				    	    .containsExactly(
			    	    		"00000000-0000-0000-0000-0000000000e1"
		    	    		);
				    });
			}
			
			@Test
			void xmlGraph18DuplicateRefInStep() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("18-valid-duplicate-ref-in-step.xml"));
				assertThat(xml)
				    .isOfAnyClassIn(AttackGraphXml.class);
				
				assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
				assertThat(xml.name).isEqualTo("dup-ref");
				
				assertThat(xml.vertices.accesses)
					.hasSize(1)
					.extracting(
						access -> access.uuid,
						access -> access.name,
						access -> access.detectionTime
					)
					.containsExactlyInAnyOrder(
						tuple("00000000-0000-0000-0000-0000000000a1", "A1", null)
					);
				
				assertThat(xml.vertices.goals)
					.hasSize(1)
					.extracting(
						goal -> goal.uuid,
						goal -> goal.name,
						goal -> goal.value,
						goal -> goal.detectionTime
					)
					.containsExactly(tuple(
						"00000000-0000-0000-0000-0000000000e1",
						"G1",
						10.0,
						null
					));
				
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
				    	
				    	assertThat(step.duration.type).isEqualTo("exponential");
				    	assertThat(step.duration.rate).isEqualTo(1.0);
				    	
				    	assertThat(step.detectionTime).isNull();
				    	
				    	assertThat(step.requires)
				    	    .hasSize(1)
				    	    .extracting(requires -> requires.ref)
				    	    .containsExactly(
			    	    		"00000000-0000-0000-0000-0000000000a1"
		    	    		);
				    	
				    	assertThat(step.produces)
				    	    .hasSize(1)
				    	    .extracting(produces -> produces.ref)
				    	    .containsExactly(
			    	    		"00000000-0000-0000-0000-0000000000e1"
		    	    		);
				    });
			}
		}
		
		@Nested
		class EdgeCases {
			
			@Test
			void xmlGraph15EdgeCaseInXmlEmptyGraphIsOk() {
				AttackGraphXml xml = AttackGraphXmlLoader.load(resource("15-edge-empty-graph.xml"));
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
				"12-invalid-non-numeric",
				"13-invalid-exponential-missing-rate",
				"14-invalid-missing-duration",
			})
			void xmlGraphInvalidXmlShouldThrowAttackLoadException(String filename) {
				String completeFilename = filename + ".xml";
				assertThatExceptionOfType(AttackLoadException.class)
		    		.isThrownBy(() -> AttackGraphXmlLoader.load(resource(completeFilename)))
			    	.withMessage("Error while loading: XML is not valid: " + completeFilename);
			}
		}
		
		@Nested
		class EdgeCases {
			
			@ParameterizedTest(name = "{index}: ''{0}''")
			@CsvSource({
				"16-edge-unknown-element",
				"17-edge-nan-and-infinity",
			})
			void xmlGraphEdgeCaseInXmlShouldThrow(String filename) {
				String completeFilename = filename + ".xml";
				assertThatExceptionOfType(AttackLoadException.class)
		    		.isThrownBy(() -> AttackGraphXmlLoader.load(resource(completeFilename)))
			    	.withMessage("Error while loading: XML is not valid: " + completeFilename);
			}
		}
	}
	
	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/graph/load/", name);
	}
}
