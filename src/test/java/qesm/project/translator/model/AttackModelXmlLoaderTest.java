package qesm.project.translator.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.model.dto.AttackModelXml;
import qesm.project.translator.utility.TestResourceHelper;

public class AttackModelXmlLoaderTest {
	
	@Nested
	class HappyCases {
		
		@Test
		void xmlGraph01() {
			AttackModelXml xml = AttackModelXmlLoader.load(resource("01-valid.xml"));
			
			assertThat(xml.graph).isNotNull();
			
			assertThat(xml.scenarios).hasSize(2);
			assertThat(xml.scenarios.get(0)).isNotNull();
			assertThat(xml.scenarios.get(1)).isNotNull();

			assertThat(xml.graph.uuid).isEqualTo(xml.scenarios.get(0).graph.ref);
			assertThat(xml.graph.uuid).isNotEqualTo(xml.scenarios.get(1).graph.ref);
		}
	}

	@Nested
	class ErrorCases {
			
		@Test
		void xmlGraph02() {
			assertThatExceptionOfType(AttackLoadException.class)
	    		.isThrownBy(() -> AttackModelXmlLoader.load(resource("02-invalid-model-no-graph.xml")))
		    	.withMessage("Error while loading: XML is not valid: 02-invalid-model-no-graph.xml");
		}
	}
	
	private static Path resource(String name) {
		return TestResourceHelper.resource("/xml/model/load/", name);
	}
}
