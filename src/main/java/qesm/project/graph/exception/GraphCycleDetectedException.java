package qesm.project.graph.exception;

import java.util.Collection;
import java.util.Collections;

import qesm.project.graph.step.Step;

public class GraphCycleDetectedException extends RuntimeException {
	
	private final Collection<Step> cycle;

	private static final long serialVersionUID = -8833367851904945633L;
	
	public GraphCycleDetectedException(Collection<Step> cycle) {
		super("Cycle detected");
		this.cycle = Collections.unmodifiableCollection(cycle);
	}
	
	public Collection<Step> getCycle() { return this.cycle; }
}
