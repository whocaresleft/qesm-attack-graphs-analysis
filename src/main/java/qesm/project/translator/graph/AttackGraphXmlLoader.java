package qesm.project.translator.graph;

import java.nio.file.Path;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.translator.exception.AttackGraphLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;

public class AttackGraphXmlLoader {

	private static final JAXBContext CTX = createContext();
	
	private static JAXBContext createContext() {
		try { return JAXBContext.newInstance(AttackGraphXml.class); }
		catch (JAXBException e) { throw new ExceptionInInitializerError(e); }
	}
	
	public AttackGraph load(Path file) throws AttackGraphLoadException {
		Path abs = file.toAbsolutePath().normalize();
		try {
			Unmarshaller u = CTX.createUnmarshaller();
			AttackGraphXml xml = (AttackGraphXml) u.unmarshal(abs.toFile());
			return AttackGraphMapper.toAttackGraph(xml);
		} catch (JAXBException e) {
			throw new AttackGraphLoadException("XML is not valid: " + file, e);
		}
	}
}
