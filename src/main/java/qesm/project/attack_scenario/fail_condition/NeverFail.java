package qesm.project.attack_scenario.fail_condition;

public class NeverFail implements FailCondition {
	
	private static final NeverFail INSTANCE = new NeverFail();
	
	private NeverFail() {}
	
	public static NeverFail get() { return INSTANCE; }
}
