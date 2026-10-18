package qesm.project.analysis.attack_graph.exception;

import qesm.project.analysis.attack_graph.step.Step;

public class StepNotValidException extends RuntimeException {

	private static final long serialVersionUID = -8834467851904945633L;
	
	private Step s;
	
	public StepNotValidException(Step s) {
		this.s = s;
	}
	
	@Override
	public String getMessage() {
		return String.format("%s is not a valid step", s.toString());
	}
}