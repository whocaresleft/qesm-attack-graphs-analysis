package qesm.project.translator.utility;

import java.nio.file.Path;
import java.util.UUID;

import javax.xml.validation.Schema;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;
import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;

public class XmlHelper {
	
	private static final JAXBContext CTX = createContext();
	
	public static JAXBContext createContext() {
		try { return JAXBContext.newInstance(AttackGraphXml.class, AttackScenarioXml.class); }
		catch (JAXBException e) { throw new ExceptionInInitializerError(e); }
	}
	
	public static <T> T unmarshal (Path file, Class<T> type) throws AttackLoadException {
		return unmarshal(file, type, null);
	}
	
	public static <T> T unmarshal (Path file, Class<T> type, Schema schema) throws AttackLoadException {
		try {
			Unmarshaller unmarshaller = CTX.createUnmarshaller();
			if (schema != null) {
				unmarshaller.setSchema(schema);
				unmarshaller.setEventHandler(event -> event.getSeverity() < ValidationEvent.ERROR);
			}
			
			Object root = unmarshaller.unmarshal(file.toFile());
			if (!type.isInstance(root)) {
				throw new AttackLoadException("XML is not valid: " + file.getFileName() + ", expected <" + type.getSimpleName() + ">, found <" + root.getClass().getSimpleName() + ">");
			}
			return type.cast(root);
		} catch (JAXBException e) {
			throw new AttackLoadException("XML is not valid: " + file.getFileName(), e);
		}
	}
	
	public static UUID parseUuid(String raw) {
		try {
			return UUID.fromString(raw);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new IllegalArgumentException("Invalid UUID: '" + raw + "'", e);
		}
	}
}
