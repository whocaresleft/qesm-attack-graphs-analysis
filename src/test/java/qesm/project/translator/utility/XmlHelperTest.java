package qesm.project.translator.utility;

import org.junit.jupiter.api.Test;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

class XmlHelperTest {

	@XmlRootElement(name = "String")
	public class DummyStringAttack {
		@XmlElement String value;
	}
	
	@Test
	void parseUuidWithInvalidRawUuidThrowsException() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> XmlHelper.parseUuid("00000000000000000000000000000000"))
			.withMessage("Invalid UUID: '00000000000000000000000000000000'");
	}
	
	@Test
	void parseUuidWithValidRawUuidIsOk() {
		String validUuid = "00000000-0000-0000-0000-000000000000";
		assertThat(XmlHelper.parseUuid(validUuid))
			.isEqualTo(java.util.UUID.fromString(validUuid));
	}
	
	@Test
	void testUnmarshalWithInvalidXmlThrows() {
		assertThatExceptionOfType(AttackLoadException.class)
			.isThrownBy(() -> XmlHelper.unmarshal(resource("invalid-xml.xml"), AttackGraphXml.class))
			.withMessage("Error while loading: XML is not valid: invalid-xml.xml");
	}
	
	private static Path resource(String name) {
		try {
			URL url = XmlHelperTest.class.getResource("/xml/" + name);
			if (url == null) throw new IllegalArgumentException("Missing test resource: " + name);
			return Path.of(url.toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
}
