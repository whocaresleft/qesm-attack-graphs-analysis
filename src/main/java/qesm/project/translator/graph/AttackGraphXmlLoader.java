package qesm.project.translator.graph;

import java.nio.file.Path;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.graph.dto.graph.AttackGraphXml;
import qesm.project.translator.utility.XmlHelper;

public class AttackGraphXmlLoader {
	
	public static AttackGraphXml load(Path file) throws AttackLoadException {
		Path abs = file.toAbsolutePath().normalize();
		return XmlHelper.unmarshal(abs, AttackGraphXml.class);
	}
}
