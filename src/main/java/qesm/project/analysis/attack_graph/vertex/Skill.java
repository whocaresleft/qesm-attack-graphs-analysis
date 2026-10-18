package qesm.project.analysis.attack_graph.vertex;

import java.util.UUID;
import qesm.project.analysis.attack_graph.step.Requirement;

public class Skill extends Vertex implements Requirement {

	public Skill(UUID uuid, String name) {
		super(uuid, name);
	}
}
