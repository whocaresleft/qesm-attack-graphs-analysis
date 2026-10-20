package qesm.project.translator.utility;

import java.nio.file.Path;
import java.util.UUID;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
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
		try {
			Object root = CTX.createUnmarshaller().unmarshal(file.toFile());
			if (!type.isInstance(root)) {
				throw new AttackLoadException(file + ": expected <" + type.getSimpleName() + ">, found " + root.getClass().getSimpleName());
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
