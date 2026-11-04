package qesm.project.graph.exception;

import qesm.project.graph.step.Step;

public class UnresolvedStepVertexException extends RuntimeException {
	
	private final Step step;

	private static final long serialVersionUID = -8834467851904945633L;
	
	public UnresolvedStepVertexException(Step step) {
		super("Step '" + step.getName() + "' is not a valid step");
		this.step = step;
	}
	
	public Step getStep() { return step; }
}
