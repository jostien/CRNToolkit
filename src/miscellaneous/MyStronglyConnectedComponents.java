/* CRNToolkit, Copyright (c) 2010-2016 Jost Neigenfind  <jostie@gmx.de>
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

package miscellaneous;

import java.util.HashMap;
import java.util.Iterator;

import miscellaneous.MySet;

public class MyStronglyConnectedComponents implements MyEquivalenceRelation<MyNode>{
	private MyGraph G;											// the graph the equivalence relation is based on
	private HashMap<String,MySet<MyNode>> already_computed;		// for each node the corresponding "equivalence class",
																// since we do not want to traverse the graph every time two
																// nodes are evaluated if being equal might be redundant
																// with respect to the final partition
	private MySet<String> pairs_tested;							// which pairs of nodes were already tested
	
	/**
	 * The constructor.
	 * 
	 * @param reaction_network
	 */
	public MyStronglyConnectedComponents(MyGraph G){
		this.G = G;
		this.already_computed = new HashMap<String,MySet<MyNode>>();
		this.pairs_tested = new MySet<String>();
	}
	
	public boolean isEqual(MyNode node1, MyNode node2) throws Exception{
		// make key for already tested pairs
		String pair = node1.toString() + "_" + node2.toString();
		if (node1.toString().compareTo(node2.toString()) > 0)
			pair = node2.toString() + "_" + node1.toString();
		
		// if pair was not tested yet ...
		if (!this.pairs_tested.contains(pair)){
			// get the corresponding strongly connected components
			MySet<MyNode> pslc1 = this.already_computed.get(node1.toString());
			MySet<MyNode> pslc2 = this.already_computed.get(node2.toString());
		
			// if those are not available yet, the given nodes serve as starting point
			if (pslc1 == null)
				pslc1 = (new MySet<MyNode>()).addElement(node1);
			if (pslc2 == null)
				pslc2 = (new MySet<MyNode>()).addElement(node2);
		
			// which nodes can be reached using only directed edges
			MySet<MyNode> forward = makeStronglyConnectedComponents(node1);		// forward direction
			MySet<MyNode> backward = makeStronglyConnectedComponents(node2);	// backward direction

			// if node2 can be reached from node1, and node1 can be reached from node2
			if (forward.contains(node2) && backward.contains(node1)){
				// merge strongly connected components
				MySet<MyNode> ret = pslc1.union(pslc2); 
				
				// and put all elements into lookup hash
				Iterator<MyNode> iterator = ret.iterator();
				while (iterator.hasNext())
					this.already_computed.put(iterator.next().toString(), ret);
			}
			
			this.pairs_tested.add(pair);	// save the corresponding pair as being already tested
		}
		
		return this.already_computed.get(node1.toString()).contains(node2);
	}
	
	/**
	 * Calculates the strongly connected component recursively using method depthFirstSearch.
	 */
	public MySet<MyNode> makeStronglyConnectedComponents(MyNode node) throws Exception{
		MySet<MyNode> ret = new MySet<MyNode>();	// this empty set will later on consist of the elements of the strongly connected components
		this.depthFirstSearch(node, ret);			// find all nodes that can be reached from this node
		
		return ret;
	}
	
	/**
	 * Walks recursively along the directed edges of the graph.
	 * 
	 * @param node The current node which is to be analyzed.
	 * @param ret The already touched nodes.
	 */
	public void depthFirstSearch(MyNode node, MySet<MyNode> ret){
		ret.add(node);
		
		// get all untouched neighbours of this edge
		MySet<MyNode> neighbours = this.G.getNodeNeighboursForward(node).difference(ret);
		while (neighbours.size() > 0){						// as long as there is a neighbour which was not yet touched
			MyNode head_of_neighbours = neighbours.head();	// get the first neighbour
			neighbours.remove(head_of_neighbours);			// remove it from the neighbours list
		
			// walk along the edges of the graph
			depthFirstSearch(head_of_neighbours, ret);
		}
	}
}
