package qesm.project.scenario.failcondition;

public class NeverFail implements FailCondition {
	
	private static final NeverFail INSTANCE = new NeverFail();
	
	private NeverFail() {}
	
	public static NeverFail get() { return INSTANCE; }
}
