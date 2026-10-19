package qesm.project.translator.scenario;

import java.nio.file.Path;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import qesm.project.attack_graph.graph.AttackGraph;
import qesm.project.attack_scenario.scenario.AttackScenario;
import qesm.project.translator.exception.AttackGraphLoadException;
import qesm.project.translator.graph.AttackGraphXmlLoader;
import qesm.project.translator.scenario.dto.scenario.AttackScenarioXml;

public class AttackScenarioXmlLoader {
	
	private static final JAXBContext CTX = createContext();
	
	private final AttackGraphXmlLoader graphLoader;
	private final AttackScenarioMapper mapper;
	
	private static JAXBContext createContext() {
		try { return JAXBContext.newInstance(AttackScenarioXml.class); }
		catch (JAXBException e) { throw new ExceptionInInitializerError(e); }
	}
	
	public AttackScenarioXmlLoader(AttackGraphXmlLoader graphLoader, AttackScenarioMapper mapper) {
		this.graphLoader = graphLoader;
		this.mapper = mapper;
	}
	
	public AttackScenarioXmlLoader() {
		this(new AttackGraphXmlLoader(), new AttackScenarioMapper());
	}
	
	public AttackScenario load(Path file) throws AttackGraphLoadException {
		Path abs = file.toAbsolutePath().normalize();
		AttackScenarioXml xml = unmarshal(abs);
		AttackGraph graph = resolveGraph(xml, abs);
		return mapper.toAttackScenario(xml, graph);
	}
	
	private AttackScenarioXml unmarshal(Path file) {
		try {
			Unmarshaller u = CTX.createUnmarshaller();
			return (AttackScenarioXml) u.unmarshal(file.toFile());
		} catch (JAXBException | ClassCastException e) {
            throw new AttackGraphLoadException("Invalid scenario XML: " + file, e);	
        }
	}
	
	private AttackGraph resolveGraph(AttackScenarioXml xml, Path scenarioFile) {
		if (xml.graph == null || xml.graph.file == null || xml.graph.file.isBlank())
			throw new AttackGraphLoadException("Scenario '" + xml.name + "' missing graph file in " + scenarioFile);
		Path graphFile = scenarioFile.getParent().resolve(xml.graph.file).normalize();
		return graphLoader.load(graphFile);
	}
}
