package qesm.project.attack_graph.exception;

import qesm.project.attack_graph.step.Step;

public class StepNotValidException extends RuntimeException {
	
	private final Step s;

	private static final long serialVersionUID = -8834467851904945633L;
	
	public StepNotValidException(Step s) {
		super("Step '" + s.getName() + "' is not a valid step");
		this.s = s;
	}
	
	public Step getStep() { return s; }
}