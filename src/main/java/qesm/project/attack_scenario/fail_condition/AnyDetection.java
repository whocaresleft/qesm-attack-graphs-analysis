package qesm.project.attack_scenario.fail_condition;

public class AnyDetection implements FailCondition {
	
	private static final AnyDetection INSTANCE = new AnyDetection();
	
	private AnyDetection() {}
	
	public static AnyDetection get() { return INSTANCE; }
}