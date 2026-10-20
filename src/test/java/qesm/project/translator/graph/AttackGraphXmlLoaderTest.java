package qesm.project.translator.graph;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import qesm.project.translator.graph.dto.graph.AttackGraphXml;

public class AttackGraphXmlLoaderTest {

	@Nested
	class HappyCases {
		
		@Test
		void xmlGraph01() {
			AttackGraphXml xml = AttackGraphXmlLoader.load(resource("01-valid-minimal.xml"));
			assertThat(xml)
				.isOfAnyClassIn(AttackGraphXml.class);
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
		
		@Test
		void xmlGraph07() {}
		
		@Test
		void xmlGraph08() {}
		
		@Test
		void xmlGraph09() {}
		
		@Test
		void xmlGraph10() {}
		
		@Test
		void xmlGraph11() {}
		
		@Test
		void xmlGraph12() {}
		
		@Test
		void xmlGraph13() {}
		
		@Test
		void xmlGraph14() {}
		
		@Test
		void xmlGraph15() {}
		
		@Test
		void xmlGraph16() {}
		
		@Test
		void xmlGraph17() {}
		
		@Test
		void xmlGraph18() {}
		
		@Test
		void xmlGraph19() {}
		
		@Test
		void xmlGraph20() {}
		
		@Test
		void xmlGraph21() {}
		
		@Test
		void xmlGraph22() {}
		
		@Test
		void xmlGraph23() {}
		
		@Test
		void xmlGraph24() {}
		
		@Test
		void xmlGraph25() {}
		
		@Test
		void xmlGraph26() {}
		
		@Test
		void xmlGraph27() {}
		
		@Test
		void xmlGraph28() {}
		
		@Test
		void xmlGraph29() {}
		
		@Test
		void xmlGraph30() {}
		
		@Test
		void xmlGraph31() {}
		
		@Test
		void xmlGraph32() {}
		
		@Test
		void xmlGraph33() {}
		
		@Test
		void xmlGraph34() {}
		
		@Test
		void xmlGraph35() {}
		
		@Test
		void xmlGraph36() {}
		
		@Test
		void xmlGraph37() {}
		
		@Test
		void xmlGraph38() {}
		
		@Test
		void xmlGraph39() {}
	}

	@Nested
	class ErrorCases {}
	
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
