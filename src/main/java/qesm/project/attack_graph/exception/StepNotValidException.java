package qesm.project.attack_graph.exception;

import qesm.project.attack_graph.step.Step;

public class StepNotValidException extends RuntimeException {

	private static final long serialVersionUID = -8834467851904945633L;
	
	private Step s;
	
	public StepNotValidException(Step s) {
		this.s = s;
	}
	
	public Step getStep() { return s; }
	
	@Override
	public String getMessage() {
		return String.format("%s is not a valid step", s.toString());
	}
}