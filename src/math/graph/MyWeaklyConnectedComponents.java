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

package math.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import crnt.Complex;
import crnt.ReactionNetwork;
import crnt.Species;
import math.set.MyEquivalenceClass;
import math.set.MyEquivalenceRelation;
import math.set.MyMultiset;
import math.set.MyPair;
import math.set.MyPartition;
import math.set.MySet;
import system.parsers.simple.SimpleParser;
import miscellaneous.MyPriorityQueue;

public class MyWeaklyConnectedComponents implements MyEquivalenceRelation<MySet<MyNode>>{
	private MyGraph G;												// the base graph
	private int distance;											// allowed distance between different node sets
	private HashMap<String,MySet<MySet<MyNode>>> already_computed;	// saves in which set of node sets a node set is an element of
	private MySet<MySet<MySet<MyNode>>> equivalence_classes;		// for each node the corresponding "equivalence class", ...
	// ... since we do not want to traverse the graph every time two nodes are evaluated if being equal
	// might be redundant with respect to the final partition
	
	/**
	 * The constructor.
	 * 
	 * @param graph.
	 */
	public MyWeaklyConnectedComponents(MyGraph G, int diameter){
		this.G = G;
		this.distance = diameter;
		this.already_computed = new HashMap<String,MySet<MySet<MyNode>>>();
		this.equivalence_classes = new MySet<MySet<MySet<MyNode>>>();
	}
	
	/**
	 * Compares whether two nodes are equal with respect to the equivalence
	 * relation.
	 * 
	 * @param node1 The first node.
	 * @param node2 The second node.
	 * @return True if nodes are equal, false otherwise.
	 * @throws Exception
	 */
	public boolean isEqual(MySet<MyNode> nodes1, MySet<MyNode> nodes2) throws Exception{
		if (!this.already_computed.containsKey(nodes1.toString()))
			this.add(nodes1);
		
		if (!this.already_computed.containsKey(nodes2.toString()))
			this.add(nodes2);
		
		return this.already_computed.get(nodes1.toString()).equals(this.already_computed.get(nodes2.toString()));
	}
	
	/**
	 * Add method, which always needs to be implements.
	 * 
	 * @param nodes The set of nodes to add.
	 */
	public void add(MySet<MyNode> nodes){
		// A potential partition of subgraphs:
		//
		// {{{node_1, node_2}, {node_3}}, {{node_4, node_5, node_6}, {node_7, node_8}}}
		//    \____________/    \____/      \____________________/    \____________/
		//          |              |                   |                     |
		//    set of nodes   set of nodes        set of nodes          set of nodes
		//   \________________________/    \________________________________________/
		//               |                                     |
		//       set of nodes sets                      set of node sets
		//  \________________________________________________________________________/
		//                                     |
		//                          set of sets of node sets
		//
		// (i)   element := set nodes
		// (ii)  equivalence class := set of node sets
		// (iii) partition := set of sets of nodes sets
		//
		// where {node_1, node_2}, {node_3} and so on represent the nodes of subgraphs
		// {{node_1, node_2}, {node_3}} is an equivalence class and {{node_4, node_5, node_6}, {node_7, node_8}} is another equivalence class.
		// the equivalence may be defined arbitrarily, here by the distance between the subgraphs (see below): this.G.isNodeSetsNeighbours(nodes, set, this.diameter)
		
		MySet<MySet<MySet<MyNode>>> to_unify = new MySet<MySet<MySet<MyNode>>>();					// keeps track of the sets of node sets which have non empty intersections
		Iterator<MySet<MySet<MyNode>>> set_of_sets_iterator = this.equivalence_classes.iterator();	// loop over all sets of node sets
		while (set_of_sets_iterator.hasNext()){
			MySet<MySet<MyNode>> set_of_sets = set_of_sets_iterator.next();							// get one set of node set
			Iterator<MySet<MyNode>> set_iterator = set_of_sets.iterator();							// loop over all node sets
			while (set_iterator.hasNext()){
				MySet<MyNode> set = set_iterator.next();											// get one node set
				if (this.G.isNodeSetsNeighbours(nodes, set, this.distance)){						// if this node set and nodes are at most diameter far away from each other ...
					set_of_sets.add(nodes);															// ... add nodes to the set of node sets and ...
					to_unify.add(set_of_sets);														// ... save this set of node sets to be potentially unified with other sets of node sets
					break;
				}
			}
		}

		// if there was no overlap with one of the sets of node sets ...
		if (to_unify.size() == 0){
			MySet<MySet<MyNode>> foo = new MySet<MySet<MyNode>>();									// ... create a new one for
			foo.add(nodes);																			// add nodes to this set of node sets
			to_unify.add(foo);																		// keep this set of node sets for unification
		}
			
		// unify the sets of node sets which were saved
		MySet<MySet<MyNode>> unified = new MySet<MySet<MyNode>>();									// for the new set of node sets
		set_of_sets_iterator = to_unify.iterator();													// loop over the sets of nodes sets which were saved
		while (set_of_sets_iterator.hasNext()){
			MySet<MySet<MyNode>> set_of_sets = set_of_sets_iterator.next();							// get one set of node sets
			unified = unified.union(set_of_sets);													// unify it with the rest
			this.equivalence_classes.remove(set_of_sets);											// remove the current set of sets from the already computed set of sets of node sets
		}
		
		this.equivalence_classes.add(unified);														// add unification to set of sets of node sets
		
		// update lookup hash
		Iterator<MySet<MyNode>> set_iterator = unified.iterator();									// loop over elements of unification
		while (set_iterator.hasNext()){
			MySet<MyNode> set = set_iterator.next();												// get one set of nodes
			this.already_computed.put(set.toString(), unified);										// save in which set of node sets this set of nodes is an element of
		}
	}
}
