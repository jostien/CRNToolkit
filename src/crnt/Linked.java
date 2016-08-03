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
import java.util.LinkedList;

import system.parsers.simple.SimpleParser;
import miscellaneous.MyPair;
import miscellaneous.MyPriorityQueue;
import miscellaneous.MySet;

public class Linked implements EquivalenceRelation<Complex>{
	private ReactionNetwork reaction_network;					// the reaction network the equivalence relation is based on
	private HashMap<String,MySet<Complex>> already_computed;	// for each complex the corresponding "equivalence class", ...
	// ... since we do not want to traverse the graph every time two complexes are evaluated if being equal
	// might be redundant with respect to the final partition
	
	/**
	 * The constructor.
	 * 
	 * @param reaction_network
	 */
	public Linked(ReactionNetwork reaction_network){
		this.reaction_network = reaction_network;
		this.already_computed = new HashMap<String,MySet<Complex>>();
	}
	
	/**
	 * Compares whether two complexes are equal with respect to the equivalence
	 * relation.
	 * 
	 * @param c1 The first complex.
	 * @param c2 The second complex.
	 * @return True if complexes are equal, false otherwise.
	 * @throws Exception
	 */
	public boolean isEqual(Complex c1, Complex c2) throws Exception{
		// if complex c1 is not in hash, the equivalence class was not computed yet
		if (!this.already_computed.containsKey(c1.toString())){
			MySet<Complex> lc = makeLinkageClass(c1);	// make equivalence class
			
			// put all elements of equivalence class into hash
			Iterator<Complex> iterator = lc.iterator();
			while (iterator.hasNext())
				this.already_computed.put(iterator.next().toString(), lc);
		}
		
		return already_computed.get(c1.toString()).contains(c2);
	}
	
	/**
	 * Calculates the linkage class recursively using method depthFirstSearch.
	 */
	public MySet<Complex> makeLinkageClass(Complex c1) throws Exception{
		MySet<Complex> ret = new MySet<Complex>();	// this empty set will later on consist of the linkage class
		this.depthFirstSearch(c1, ret);				// find all complexes that can be reached from this complex

		return ret;
	}
	
	/**
	 * Walks recursively along the edges of the graph of the given reaction network.
	 * 
	 * @param complex The current complex which is to be analyzed.
	 * @param ret The already touched nodes.
	 */
	public void depthFirstSearch(Complex complex, MySet<Complex> ret){
		ret.add(complex);

		// get all untouched neighbours of this reaction
		MySet<Complex> neighbours = this.reaction_network.getComplexNeighboursForwardBackward(complex).difference(ret);		
		while (neighbours.size() > 0){						// as long as there is a neighbour which was not yet touched
			Complex head_of_neighbours = neighbours.head();	// get the first neighbour
			neighbours.remove(head_of_neighbours);			// remove it from the neighbours list
		
			// walk along the edges of the graph
			depthFirstSearch(head_of_neighbours, ret);
		}
	}
}
