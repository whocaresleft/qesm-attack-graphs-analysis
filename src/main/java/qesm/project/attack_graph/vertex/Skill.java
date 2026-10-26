package qesm.project.attack_graph.vertex;

import java.util.UUID;

import qesm.project.attack_graph.step.Requirement;

public class Skill extends Vertex implements Requirement {

	private Skill(UUID uuid, String name) {
		super(uuid, name);
	}
	
	public static Skill create(UUID uuid, String name) {
		return new Skill(uuid, name);
	}
}
