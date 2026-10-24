package qesm.project.translator.graph;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.graph.dto.step.StepXml;
import qesm.project.translator.graph.dto.vertex.AccessXml;
import qesm.project.translator.graph.dto.vertex.GoalXml;

public class AttackGraphXmlLoaderTest {

	@Nested
	class HappyCases {
		
		@Test
		void xmlGraph01() {
			AttackGraphXml xml = AttackGraphXmlLoader.load(resource("01-valid-minimal.xml"));
			assertThat(xml)
				.isOfAnyClassIn(AttackGraphXml.class);
			
			assertThat(xml.uuid).isEqualTo("00000000-0000-0000-0000-000000000001");
			assertThat(xml.name).isEqualTo("minimal");
			
			assertThat(xml.vertices.accesses.size()).isOne();
			AccessXml a = xml.vertices.accesses.get(0);
			assertThat(a.uuid).isEqualTo("00000000-0000-0000-0000-0000000000a1");
			assertThat(a.name).isEqualTo("A1");
			assertThat(a.detectionTime).isNull();
			
			assertThat(xml.vertices.goals.size()).isOne();
			GoalXml g = xml.vertices.goals.get(0);
			assertThat(g.uuid).isEqualTo("00000000-0000-0000-0000-0000000000e1");
			assertThat(g.name).isEqualTo("G1");
			assertThat(g.value).isEqualTo(10);
			assertThat(g.detectionTime).isNull();
			
			assertThat(xml.steps.size()).isOne();
			StepXml s = xml.steps.get(0);
			assertThat(s.uuid).isEqualTo("00000000-0000-0000-0000-000000000051");
			assertThat(s.name).isEqualTo("ST1");
			assertThat(s.successProbability).isEqualTo(0.7);
			assertThat(s.failureProbability).isEqualTo(0.2);
			assertThat(s.failureDetectedProbability).isEqualTo(0.1);
			
			assertThat(s.duration.type).isEqualTo("exponential");
			assertThat(s.duration.rate).isEqualTo(0.5);			
			assertThat(s.detectionTime).isNull();
			
			assertThat(s.produces.get(0).ref).isEqualTo(g.uuid);
			assertThat(s.requires.get(0).ref).isEqualTo(a.uuid);
		}
		
		@Test
		void xmlGraph02() {}
		
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
	class ErrorCases {
		
		@Test
		void xmlGraph07() {
			assertThatExceptionOfType(AttackLoadException.class)
			    .isThrownBy(() -> AttackGraphXmlLoader.load(resource("07-invalid-malformed-xml.xml")))
			    .withMessage("XML is not valid: 07-invalid-malformed-xml.xml");
		}
		
		@Test
		void xmlGraph08() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("XML is not valid: 08-invalid-wrong-root.xml");
		}
		
		@Test
		void xmlGraph09() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("09-invalid-empty-file.xml")))
			    .withMessage("XML is not valid: 09-invalid-empty-file.xml");
		}
		
		@Test
		void xmlGraph10() {
			assertThatExceptionOfType(AttackLoadException.class)
	    		.isThrownBy(() -> AttackGraphXmlLoader.load(resource("10-invalid-missing-graph-uuid.xml")))
	    		.withMessage("XML is not valid: 10-invalid-missing-graph-uuid.xml");
		}
		/*
		@Test
		void xmlGraph11() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph12() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph13() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph14() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph15() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph16() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph17() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph18() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph19() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph20() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph21() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph22() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph23() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph24() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph25() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph26() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph27() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph28() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph29() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		
		@Test
		void xmlGraph30() {
			assertThatExceptionOfType(AttackLoadException.class)
		    	.isThrownBy(() -> AttackGraphXmlLoader.load(resource("08-invalid-wrong-root.xml")))
			    .withMessage("");
		}
		*/
		@Test
		void xmlGraph31() {
			
		}
		
		@Test
		void xmlGraph32() {
			
		}
		
		@Test
		void xmlGraph33() {
			
		}
		
		@Test
		void xmlGraph34() {
			
		}
		
		@Test
		void xmlGraph35() {
			
		}
		
		@Test
		void xmlGraph36() {
			
		}
		
		@Test
		void xmlGraph37() {
			
		}
		
		@Test
		void xmlGraph38() {
			
		}
		
		@Test
		void xmlGraph39() {
			
		}
	}
	
	private static Path resource(String name) {
		try {
			URL url = AttackGraphXmlLoaderTest.class.getResource("/xml/graph/" + name);
			if (url == null) throw new IllegalArgumentException("Missing test resource: " + name);
			return Path.of(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
}
