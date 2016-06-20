package crnt;

public interface EquivalenceRelation<A> {
	public boolean isEqual(A a, A b) throws Exception;
}
