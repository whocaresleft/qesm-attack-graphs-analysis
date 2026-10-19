package qesm.project.attack_graph.vertex;

import java.util.UUID;

import qesm.project.attack_graph.step.Requirement;

public class Skill extends Vertex implements Requirement {

	public Skill(UUID uuid, String name) {
		super(uuid, name);
	}
}
