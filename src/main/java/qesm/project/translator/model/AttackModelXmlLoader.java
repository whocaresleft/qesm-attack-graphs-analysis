package qesm.project.translator.model;

import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.AttackGraphXmlLoader;
import qesm.project.translator.model.dto.AttackModelXml;
import qesm.project.translator.utility.XmlHelper;

public class AttackModelXmlLoader {
	
	private static final Schema SCHEMA;
	
	static {
		
		try {
			
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			SCHEMA = sf.newSchema(AttackGraphXmlLoader.class.getResource("/schemas/attackModel.xsd"));
		} catch (Exception e) {
			
			throw new ExceptionInInitializerError("Could not find AttackModel Xml schema: " +  e.getMessage());
		}
	}
	
	public static AttackModelXml load(Path file) throws AttackLoadException {
		Path abs = file.toAbsolutePath().normalize();
		return XmlHelper.unmarshal(abs, AttackModelXml.class, SCHEMA);
	}
}