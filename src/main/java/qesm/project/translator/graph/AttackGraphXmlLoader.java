package qesm.project.translator.graph;

import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.utility.XmlHelper;

public class AttackGraphXmlLoader {
	
	private static final AttackGraphXmlLoader INSTANCE = new AttackGraphXmlLoader();
	
	private static final Schema SCHEMA;
	
	static {
		
		try {
			
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			SCHEMA = sf.newSchema(AttackGraphXmlLoader.class.getResource("/schemas/attackGraph.xsd"));
		} catch (Exception e) {
			
			throw new ExceptionInInitializerError("Could not find AttackGraph Xml schema: " +  e.getMessage());
		}
	}
	
	private AttackGraphXmlLoader() { }
	
	public static AttackGraphXmlLoader get() { return INSTANCE; }
	
	public AttackGraphXml load(Path file) throws AttackLoadException {
		Path abs = file.toAbsolutePath().normalize();
		return XmlHelper.unmarshal(abs, AttackGraphXml.class, SCHEMA);
	}
}
