package qesm.project.scenario.failcondition;

public class AnyDetection implements FailCondition {
	
	private static final AnyDetection INSTANCE = new AnyDetection();
	
	private AnyDetection() {}
	
	public static AnyDetection get() { return INSTANCE; }
}
