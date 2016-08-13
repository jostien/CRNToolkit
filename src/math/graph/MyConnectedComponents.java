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

import math.set.MyEquivalenceRelation;
import math.set.MyPair;
import math.set.MySet;
import system.parsers.simple.SimpleParser;
import miscellaneous.MyPriorityQueue;

public class MyConnectedComponents implements MyEquivalenceRelation<MyNode>{
	private MyGraph G;
	private HashMap<String,MySet<MyNode>> already_computed;	// for each node the corresponding "equivalence class", ...
	// ... since we do not want to traverse the graph every time two nodes are evaluated if being equal
	// might be redundant with respect to the final partition
	
	/**
	 * The constructor.
	 * 
	 * @param graph.
	 */
	public MyConnectedComponents(MyGraph G){
		this.G = G;
		this.already_computed = new HashMap<String,MySet<MyNode>>();
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
	public boolean isEqual(MyNode node1, MyNode node2) throws Exception{
		// if node1 is not in hash, the equivalence class was not computed yet
		if (!this.already_computed.containsKey(node1.toString())){
			MySet<MyNode> cc = makeConnectedComponents(node1);	// make equivalence class
			
			// put all elements of equivalence class into hash
			Iterator<MyNode> iterator = cc.iterator();
			while (iterator.hasNext())
				this.already_computed.put(iterator.next().toString(), cc);
		}
		
		return already_computed.get(node1.toString()).contains(node2);
	}
	
	/**
	 * Calculates the connected components recursively using method depthFirstSearch.
	 */
	public MySet<MyNode> makeConnectedComponents(MyNode node) throws Exception{
		MySet<MyNode> ret = new MySet<MyNode>();	// this empty set will later on consist of the linkage class
		this.depthFirstSearch(node, ret);			// find all nodes that can be reached from this node

		return ret;
	}
	
	/**
	 * Walks recursively along the edges of the graph.
	 * 
	 * @param node The current node which is to be analyzed.
	 * @param ret The already touched nodes.
	 */
	public void depthFirstSearch(MyNode node, MySet<MyNode> ret){
		ret.add(node);

		// get all untouched neighbours of this reaction
		MySet<MyNode> neighbours = this.G.getNodeNeighboursForwardBackward(node).difference(ret);		
		while (neighbours.size() > 0){						// as long as there is a neighbour which was not yet touched
			MyNode head_of_neighbours = neighbours.head();	// get the first neighbour
			neighbours.remove(head_of_neighbours);			// remove it from the neighbours list
		
			// walk along the edges of the graph
			depthFirstSearch(head_of_neighbours, ret);
		}
	}
}
