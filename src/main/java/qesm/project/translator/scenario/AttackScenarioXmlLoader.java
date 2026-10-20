package qesm.project.translator.scenario;

import java.nio.file.Path;

import qesm.project.translator.exception.AttackLoadException;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;
import qesm.project.translator.utility.XmlHelper;

public class AttackScenarioXmlLoader {
	
	public static AttackScenarioXml load(Path file) throws AttackLoadException {
		Path abs = file.toAbsolutePath().normalize();
		return XmlHelper.unmarshal(abs, AttackScenarioXml.class);
	}
}
