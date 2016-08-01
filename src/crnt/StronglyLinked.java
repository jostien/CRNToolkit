/* CRNToolkit, Copyright (c) 2010-2016 Jost Neigenfind  <jostie@gmx.de>,
 * Sergio Grimbs, Zoran Nikoloski
 * 
 * A Java toolkit for Chemical Reaction Networks
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

import java.util.HashMap;
import java.util.Iterator;

import miscellaneous.MySet;

public class StronglyLinked implements EquivalenceRelation<Complex>{
	private ReactionNetwork reaction_network;					// the reaction network the equivalence relation is based on
	private HashMap<String,MySet<Complex>> already_computed;	// for each  complex the corresponding  "equivalence class",
																// since we do not want to traverse the graph every time two
																// complexes are evaluated if being equal might be redundant
																// with respect to the final partition
	private MySet<String> pairs_tested;							// which pairs of complexes were already tested
	
	/**
	 * The constructor.
	 * 
	 * @param reaction_network
	 */
	public StronglyLinked(ReactionNetwork reaction_network){
		this.reaction_network = reaction_network;
		this.already_computed = new HashMap<String,MySet<Complex>>();
		this.pairs_tested = new MySet<String>();
	}
	
	public boolean isEqual(Complex c1, Complex c2) throws Exception{
		// make key for already tested pairs
		String pair = c1.toString() + "_" + c2.toString();
		if (c1.toString().compareTo(c2.toString()) > 0)
			pair = c2.toString() + "_" + c1.toString();
		
		// if pair was not tested yet ...
		if (!this.pairs_tested.contains(pair)){
			// get the corresponding strongly connected components
			MySet<Complex> pslc1 = this.already_computed.get(c1.toString());
			MySet<Complex> pslc2 = this.already_computed.get(c2.toString());
		
			// if those are not available yet, the given complexes serve as starting point
			if (pslc1 == null)
				pslc1 = (new MySet<Complex>()).addElement(c1);
			if (pslc2 == null)
				pslc2 = (new MySet<Complex>()).addElement(c2);
		
			// which complexes can be reached using only directed edges
			MySet<Complex> forward = makeStrongLinkageClass(c1);	// forward direction
			MySet<Complex> backward = makeStrongLinkageClass(c2);	// backward direction

			// if c2 can be reached from c1, and c1 can be reached from c2
			if (forward.contains(c2) && backward.contains(c1)){
				// merge strongly connected components
				MySet<Complex> ret = pslc1.union(pslc2); 
				
				// and put all elements into lookup hash
				Iterator<Complex> iterator = ret.iterator();
				while (iterator.hasNext())
					this.already_computed.put(iterator.next().toString(), ret);
			}
			
			this.pairs_tested.add(pair);	// save the corresponding pair as being already tested
		}
		
		return this.already_computed.get(c1.toString()).contains(c2);
	}
	
	/**
	 * Calculates the linkage class recursively using method depthFirstSearch.
	 */
	public MySet<Complex> makeStrongLinkageClass(Complex c1) throws Exception{
		MySet<Complex> ret = new MySet<Complex>();	// this empty set will later on consist of the linkage class
		this.depthFirstSearch(c1, ret);				// find all complexes that can be reached from this complex
		
		return ret;
	}
	
	/**
	 * Walks recursively along the directed edges of the graph of the given reaction network.
	 * 
	 * @param complex The current complex which is to be analyzed.
	 * @param ret The already touched nodes.
	 */
	public void depthFirstSearch(Complex complex, MySet<Complex> ret){
		ret.add(complex);
		
		// get all untouched neighbours of this reaction
		MySet<Complex> neighbours = this.reaction_network.getComplexNeighboursForward(complex).difference(ret);
		while (neighbours.size() > 0){						// as long as there is a neighbour which was not yet touched
			Complex head_of_neighbours = neighbours.head();	// get the first neighbour
			neighbours.remove(head_of_neighbours);			// remove it from the neighbours list
		
			// walk along the edges of the graph
			depthFirstSearch(head_of_neighbours, ret);
		}
	}
}
