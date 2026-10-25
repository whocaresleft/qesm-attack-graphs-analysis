package qesm.project.attack_graph.exception;

import java.util.Collection;
import java.util.Collections;

import qesm.project.attack_graph.step.Step;

public class CycleException extends RuntimeException {
	
	private Collection<Step> cycle;

	private static final long serialVersionUID = -8833367851904945633L;
	
	public CycleException(Collection<Step> cycle) {
		super("Cycle detected");
		this.cycle = Collections.unmodifiableCollection(cycle);
	}
	
	public Collection<Step> getCycle() { return this.cycle; }
}