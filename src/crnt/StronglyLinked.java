package crnt;

import miscellaneous.MySet;

public class StronglyLinked implements EquivalenceRelation<Complex>{
	private ReactionNetwork reaction_network;
	
	public StronglyLinked(ReactionNetwork reaction_network){
		this.reaction_network = reaction_network;
	}
	
	public boolean isEqual(Complex c1, Complex c2) throws Exception{
		MySet<Complex> lc = makeStrongLinkageClass(c1);
		
		if (!lc.contains(c2))
			return false;
		
		lc = makeStrongLinkageClass(c2);
		
		return lc.contains(c1);
	}
	/**
	 * Calculates the linkage class recursively using method depthFirstSearch.
	 */
	public MySet<Complex> makeStrongLinkageClass(Complex c1) throws Exception{
		MySet<Complex> rest_of_complexes = this.reaction_network.getComplexes().clone();	// get a copy of all complexes of the network
		MySet<Reaction> rest_of_reactions = this.reaction_network.getReactions().clone();	// get a copy of all reactions of the network
		MySet<Complex> ret = new MySet<Complex>();											// this empty set will later on consist of the linkage class
		
		rest_of_complexes.remove(c1);														// remove this complex from this set
			
		this.depthFirstSearch(c1, ret, rest_of_complexes, rest_of_reactions);				// find all complexes that can be reached from this complex
		
		return ret;
	}
	
	/**
	 * Walks recursively along the edges of the hypergraph of the given metabolic network.
	 * 
	 * @param complex The current complex which is to be analyzed.
	 * @param lc The current set of complexes which are members of the current linkage class.
	 * @param rest The set of complexes which were not yet touched
	 */
	public void depthFirstSearch(Complex complex, MySet<Complex> ret, MySet<Complex> rest_of_complexes, MySet<Reaction> rest_of_reactions){
		ret.add(complex);
		
		// get all the neighbours of this reaction
		MySet<Complex> neighbours = Reaction.getComplexNeighboursForward(complex, rest_of_complexes, rest_of_reactions);
		rest_of_complexes.removeAll(neighbours);			// remove these complexes from the set of untouched complexes
		
		while (neighbours.size() > 0){						// as long as there is a neighbour which was not yet touched
			Complex head_of_neighbours = neighbours.head();	// get the first neighbour
			neighbours.remove(head_of_neighbours);			// remove it from the neighbours list
		
			// walk along the edges of the hypergraph
			depthFirstSearch(head_of_neighbours, ret, rest_of_complexes, rest_of_reactions);
		}
	}
}
