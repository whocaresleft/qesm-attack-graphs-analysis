package qesm.project.attack_graph.vertex;

import java.util.UUID;

import qesm.project.attack_graph.step.Requirement;

public class Knowledge extends Vertex implements Requirement {

	private Knowledge(UUID uuid, String name) {
		super(uuid, name);
	}
	
	public static Knowledge create(UUID uuid, String name) {
		return new Knowledge(uuid, name);
	}
}