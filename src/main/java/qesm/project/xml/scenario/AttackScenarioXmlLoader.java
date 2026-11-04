package qesm.project.xml.scenario;

import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import qesm.project.xml.XmlHelper;
import qesm.project.xml.exception.XmlLoadingException;
import qesm.project.xml.scenario.dto.AttackScenarioXml;

public class AttackScenarioXmlLoader {
	
	private static final AttackScenarioXmlLoader INSTANCE = new AttackScenarioXmlLoader();
	
	private static final Schema SCHEMA;
	
	static {
		try {
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			SCHEMA = sf.newSchema(AttackScenarioXmlLoader.class.getResource("/schemas/attackScenario.xsd"));
		} catch (Exception e) {
			throw new ExceptionInInitializerError("Could not find AttackScenario Xml schema: " + e.getMessage());
		}
	}
	
	private AttackScenarioXmlLoader() { }
	
	public static AttackScenarioXmlLoader get() { return INSTANCE; }
	
	public AttackScenarioXml load(Path file) throws XmlLoadingException {
		Path abs = file.toAbsolutePath().normalize();
		return XmlHelper.unmarshal(abs, AttackScenarioXml.class, SCHEMA);
	}
}
