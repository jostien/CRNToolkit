/* CRNToolbox, Copyright (c) 2010-2016 Jost Neigenfind  <jostie@gmx.de>,
 * Sergio Grimbs, Zoran Nikoloski
 * 
 * A Java toolbox for Chemical Reaction Networks
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package crnt;

import java.util.*;
import miscellaneous.*;

/**
 * 
 * @author neigenfind
 *
 * The class LinkageClass.
 * This class implements the mathematical structure linkage class and extends the class MySet.
 */
public class LinkageClass extends EquivalenceClass<Complex>{
	/**
	 * standard serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	private MySet<Reaction> reactions;					// consists in the moment of the reactions of the corresponding reaction network
	private Partition<Complex> strong_linkage_classes;	// a list of the strong linkage classes
	
	/**
	 * The constructor.
	 * 
	 * @param reactions The set of reactions of the corresponding linkage class or the whole reaction network. 
	 */
	public LinkageClass(MySet<Reaction> reactions){
		this.reactions = reactions;
	}
	
	/**
	 * Computes the strong linkage classes.
	 */
	public void makeStrongLinkageClasses() throws Exception{
		if (this.size() > 0){	// is this object non empty
			this.strong_linkage_classes = new Partition<Complex>();	// make a new empty set for the strong linkage classes
			ArrayList<Complex> array = this.toArrayList();			// get all the complexes of this linkage class in form of an array
			boolean[] touched = new boolean[array.size()];			// make an array which is used for marking complexes which were already found to be a member of a strong linkage class
			
			// test all pairs of complexes if they are member of the same strong linkage class
			for (int i = 0; i < array.size(); i++){
				if (!touched[i]){	// if the current complex is not yet a member of a strong linkage class, create a new strong linkage class
					StrongLinkageClass strong_linkage_class = new StrongLinkageClass(this.reactions);
					strong_linkage_class.add(array.get(i));			// add the current complex
					for (int j = 0; j < array.size(); j++){			// take a second complex
						Complex start_complex = array.get(i);		// start complex
						Complex finish_complex = array.get(j);		// finish complex
						MySet<Complex> forward_rest = this.clone();	// unvisited complexes for the forward direction
						MySet<Complex> backward_rest = this.clone();// unvisited complexes for the backward direction

						// check if there exists a directed path from the start complex to the finish complex
						// and if there exists a directed path from the finish complex to the start complex
						if (depthFirstSearch(start_complex, finish_complex, forward_rest) &&
							depthFirstSearch(finish_complex, start_complex, backward_rest)){
							
							strong_linkage_class.add(array.get(j));	// if true add this complex to the current strong linkage class
							touched[j] = true;						// mark this complex as a member of a strong linkage class
						}
					}
					this.strong_linkage_classes.addEquivalenceClass(strong_linkage_class);	// add the strong linkage class to the corresponding list
				}
			}
		}
	}
	
	/**
	 * Gets two complexes as input and decides, using a depth first search in the graph of
	 * the linkage class, if there exists a directed path from the first to the second complex. 
	 * 
	 * @param current_complex The complex from which it is started.
	 * @param finish_complex The complex which is to be reached.
	 * @param rest The set of not yet visited complexes.
	 * @return True if there exists a directed paths, otherwise false.
	 */
	public Boolean depthFirstSearch(Complex current_complex, Complex finish_complex, MySet<Complex> rest){
		Boolean ret = false;	// finish complex is not found yet
		
		if (current_complex.equals(finish_complex)) // if the current complex is identical to the finish complex
			return true;
		else {	// otherwise go on with the search in the graph
			MySet<Complex> neighbours = Reaction.getComplexNeighboursForward(current_complex, rest, this.reactions);	// get all neighbouring complexes of the current complex in the direction of the edges adjacent to the current complex
			rest.removeAll(neighbours);	// remove the neighbours because they are now visited
			
			while (neighbours.size() > 0){						// as long as there are none handled neighbours
				Complex head_of_neighbours = neighbours.head();	// take the first of the neighbours
				neighbours.remove(head_of_neighbours);			// this neighbour is processed now and can be removed
				
				ret = ret || this.depthFirstSearch(head_of_neighbours, finish_complex, rest);	// walk on on the edges of the linkage class graph
			}
		}
	
		return ret;	// return if the finish complex was found
	}
	
	/**
	 * Returns the set of strong linkage classes.
	 * 
	 * @return The set of strong linkage classes.
	 */
	public Partition<Complex> getStrongLinkageClasses(){
		return this.strong_linkage_classes;
	}
	
	public ReactionNetwork toReactionNetwork() throws Exception{
		ReactionNetwork ret = new ReactionNetwork();
		
		MySet<Reaction> set = new MySet<Reaction>();
		Iterator<Complex> complex_iterator = this.iterator();
		while (complex_iterator.hasNext()){
			Complex complex = complex_iterator.next();
			MySet<Reaction> complex_set = Reaction.getReactionsConsumingOrProducingComplex(complex, this.reactions);
			set.addAll(complex_set);
		}
		
		Iterator<Reaction> reaction_iterator = set.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			ret.addReaction(reaction);
		}
		
		return ret;
	}
	
}
