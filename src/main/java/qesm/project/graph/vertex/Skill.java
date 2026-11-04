package qesm.project.graph.vertex;

import java.util.UUID;

import qesm.project.graph.step.Requirement;

public class Skill extends Vertex implements Requirement {

	private Skill(UUID uuid, String name) {
		super(uuid, name);
	}
	
	public static Skill create(UUID uuid, String name) {
		return new Skill(uuid, name);
	}
}
