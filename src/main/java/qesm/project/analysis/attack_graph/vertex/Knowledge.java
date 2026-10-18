package qesm.project.analysis.attack_graph.vertex;

import java.util.UUID;
import qesm.project.analysis.attack_graph.step.Requirement;

public class Knowledge extends Vertex implements Requirement {

	public Knowledge(UUID uuid, String name) {
		super(uuid, name);
	}
}