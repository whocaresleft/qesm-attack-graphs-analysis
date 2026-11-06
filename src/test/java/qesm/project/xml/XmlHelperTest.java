package qesm.project.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import qesm.project.xml.exception.InvalidUuidFormatException;
import qesm.project.xml.exception.XmlLoadingException;
import qesm.project.xml.graph.dto.AttackGraphXml;

public class XmlHelperTest {

	private static final String VALID_UUID = "00000000-0000-0000-0000-000000000000";
	private static final String INVALID_UUID = "00000000000000000000000000000000";

	@Test
	public void testParseUuidWhenUuidIsValid() {
		UUID parsedUuid = XmlHelper.parseUuid(VALID_UUID);

		assertThat(parsedUuid).isEqualTo(UUID.fromString(VALID_UUID));
	}

	@Test
	public void testParseUuidWhenUuidHasLeadingAndTrailingSpaces() {
		UUID parsedUuid = XmlHelper.parseUuid("  " + VALID_UUID + "  ");

		assertThat(parsedUuid).isEqualTo(UUID.fromString(VALID_UUID));
	}

	@Test
	public void testParseUuidWhenUuidIsInvalidThrowsException() {
		assertThatExceptionOfType(InvalidUuidFormatException.class)
			.isThrownBy(() -> XmlHelper.parseUuid(INVALID_UUID))
			.withMessage("Invalid UUID: '" + INVALID_UUID + "'");
	}

	@Test
	public void testUnmarshalWhenXmlIsInvalidThrowsException() {
		Path invalidXmlPath = TestResourceHelper.resource("/xml/", "invalid-xml.xml");

		assertThatExceptionOfType(XmlLoadingException.class)
			.isThrownBy(() -> XmlHelper.unmarshal(invalidXmlPath, AttackGraphXml.class))
			.withMessage("Error while loading: XML is not valid: invalid-xml.xml");
	}
}
