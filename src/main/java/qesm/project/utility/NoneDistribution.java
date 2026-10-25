package qesm.project.utility;

public class NoneDistribution extends Distribution {
	
	private static final NoneDistribution INSTANCE = new NoneDistribution();
	
	private NoneDistribution() {}
	
	public static NoneDistribution get() {
		return INSTANCE;
	}
	
	@Override
	public int hashCode() {
		return NoneDistribution.class.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof NoneDistribution;
	}
}
