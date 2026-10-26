package qesm.project.translator.scenario;

import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.AttackGraphXmlLoader;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;
import qesm.project.translator.utility.XmlHelper;

public class AttackScenarioXmlLoader {
	
	private static final AttackScenarioXmlLoader INSTANCE = new AttackScenarioXmlLoader();
	
	private static final Schema SCHEMA;
	
	static {
		
		try {
			
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			SCHEMA = sf.newSchema(AttackGraphXmlLoader.class.getResource("/schemas/attackScenario.xsd"));
		} catch (Exception e) {
			
			throw new ExceptionInInitializerError("Could not find AttackGraph Xml schema: " +  e.getMessage());
		}
	}
	
	private AttackScenarioXmlLoader() { }
	
	public static AttackScenarioXmlLoader get() { return INSTANCE; }
	
	public AttackScenarioXml load(Path file) throws AttackLoadException {
		Path abs = file.toAbsolutePath().normalize();
		return XmlHelper.unmarshal(abs, AttackScenarioXml.class, SCHEMA);
	}
}
