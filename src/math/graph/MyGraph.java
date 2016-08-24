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

import crnt.Species;
import math.set.MyMultiset;
import math.set.MyPair;
import math.set.MyPartition;
import math.set.MySet;
import miscellaneous.MyPriorityQueue;

public class MyGraph<E>{
	private MySet<MyEdge<E>> edges;
	private MySet<MyNode<E>> nodes;
	
	private HashMap<String, MySet<MyEdge<E>>> source_edges_lookup_table;	// lookup table: edges coming out of node
	private HashMap<String, MySet<MyEdge<E>>> sink_edges_lookup_table;		// lookup table: edges going into node
	
	private HashMap<String, MySet<MyNode<E>>> forward_neighbours;			// lookup table for forward neighbours of node
	private HashMap<String, MySet<MyNode<E>>> backward_neighbours;			// lookup table for backward neighbours of node
	
	public static void main(String[] args) throws Exception{
		// Feinberg1995a_example_4.7 as pure graph
		MyGraph<MyMultiset<String>> G = new MyGraph<MyMultiset<String>>();
		MyEdge<MyMultiset<String>> e01 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("0")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("A")));

		System.out.println("edge #1           : " + e01.toString());
		System.out.println();
		
		MyEdge<MyMultiset<String>> e02 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("A")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("0")));
		MyEdge<MyMultiset<String>> e03 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("A")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("A").addElement("A")));
		MyEdge<MyMultiset<String>> e04 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("A").addElement("A")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("A")));
		MyEdge<MyMultiset<String>> e05 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("A").addElement("B")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("C").addElement("C")));
		MyEdge<MyMultiset<String>> e06 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("C").addElement("C")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("A").addElement("B")));
		MyEdge<MyMultiset<String>> e07 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("C").addElement("C")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("D")));
		MyEdge<MyMultiset<String>> e08 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("D")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("C").addElement("C")));
		MyEdge<MyMultiset<String>> e09 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("D")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("E").addElement("E")));
		MyEdge<MyMultiset<String>> e10 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("E").addElement("E")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("D")));
		MyEdge<MyMultiset<String>> e11 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("C").addElement("C")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("C").addElement("E")));
		MyEdge<MyMultiset<String>> e12 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("C").addElement("E")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("C").addElement("C")));
		MyEdge<MyMultiset<String>> e13 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("B").addElement("C")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("F")));
		MyEdge<MyMultiset<String>> e14 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("F")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("B").addElement("C")));
		MyEdge<MyMultiset<String>> e15 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("F")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("G")));
		MyEdge<MyMultiset<String>> e16 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("G")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("F")));
		MyEdge<MyMultiset<String>> e17 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("G")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("B").addElement("C")));
		MyEdge<MyMultiset<String>> e18 = new MyEdge<MyMultiset<String>>(new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("B").addElement("C")), new MyNode<MyMultiset<String>>(new MyMultiset<String>().addElement("G")));

		G.addEdge(e01);
		G.addEdge(e02);
		G.addEdge(e03);
		G.addEdge(e04);
		G.addEdge(e05);
		G.addEdge(e06);
		G.addEdge(e07);
		G.addEdge(e08);
		G.addEdge(e09);
		G.addEdge(e10);
		G.addEdge(e11);
		G.addEdge(e12);
		G.addEdge(e13);
		G.addEdge(e14);
		G.addEdge(e15);
		G.addEdge(e16);
		G.addEdge(e17);
		G.addEdge(e18);
		
		System.out.println("size of graph     : " +  G.size());
		System.out.println("set of edges      : " + G.toString());
		System.out.println();
		
		MyPartition<MyNode<MyMultiset<String>>> partition_of_nodes = new MyPartition(new MyConnectedComponents(G));
		Iterator<MyNode<MyMultiset<String>>> iterator = (Iterator<MyNode<MyMultiset<String>>>)G.getNodes().iterator();
		while (iterator.hasNext())
			partition_of_nodes.addElementToEquivalenceClasses(iterator.next());
		
		System.out.println("connected components");
		System.out.println("partition of nodes: " + partition_of_nodes.toString());
		System.out.println();
		
		partition_of_nodes = new MyPartition(new MyStronglyConnectedComponents(G));
		iterator = (Iterator<MyNode<MyMultiset<String>>>)G.getNodes().iterator();
		while (iterator.hasNext())
			partition_of_nodes.addElementToEquivalenceClasses(iterator.next());
		
		System.out.println("strongly connected components");
		System.out.println("partition of nodes: " + partition_of_nodes.toString());
		System.out.println();
	}
	
	public MyGraph(){
		this.edges = new MySet<MyEdge<E>>();
		this.nodes = new MySet<MyNode<E>>();
		
		this.source_edges_lookup_table = new HashMap<String, MySet<MyEdge<E>>>();
		this.sink_edges_lookup_table = new HashMap<String, MySet<MyEdge<E>>>();
		
		this.forward_neighbours = new HashMap<String, MySet<MyNode<E>>>();
		this.backward_neighbours = new HashMap<String, MySet<MyNode<E>>>();
	}
	
	public boolean addEdge(MyEdge<E> edge){
		if (edge.getSource().equals(edge.getSink()))
			return false;
		
		MyEdge<E> edge_clone = edge.clone();
		
		this.edges.add(edge_clone);				// add the edge

		this.nodes.add(edge_clone.getSource());	// add the source
		this.nodes.add(edge_clone.getSink());	// add the sink

		//###################################################
		//#    everything with respect to edge's source     #
		//###################################################
		
		// add source and edge clone to source-edge-lookup table
		MyNode<E> source = edge_clone.getSource();
		MySet<MyEdge<E>> edges_out = new MySet<MyEdge<E>>();
		if (this.source_edges_lookup_table.containsKey(source.toString()))
			edges_out = this.source_edges_lookup_table.get(source.toString());
		edges_out.add(edge_clone);
		this.source_edges_lookup_table.put(source.toString(), edges_out);

		// source points to sinkt so add sink to source's directed neighbours
		MySet<MyNode<E>> neighbours = new MySet<MyNode<E>>();
		if (this.forward_neighbours.containsKey(source.toString()))
			neighbours = this.forward_neighbours.get(source.toString());
		neighbours.add(edge_clone.getSink());
		this.forward_neighbours.put(source.toString(), neighbours);
		
		//#################################################
		//#     everything with respect to edge's sink    #
		//#################################################
		
		// add sink and edge to sink-edge-lookup table
		MyNode<E> sink = edge_clone.getSink();
		MySet<MyEdge<E>> edges_in = new MySet<MyEdge<E>>();
		if (this.sink_edges_lookup_table.containsKey(sink.toString()))
			edges_in = this.sink_edges_lookup_table.get(sink.toString());
		// only add an reference from this reaction network
		edges_in.add(edge_clone);
		this.sink_edges_lookup_table.put(sink.toString(), edges_in);
		
		// product is connected to substrate so add substrate to product's undirected neighbours
		neighbours = new MySet<MyNode<E>>();
		if (this.backward_neighbours.containsKey(sink.toString()))
			neighbours = this.backward_neighbours.get(sink.toString());
		neighbours.add(edge_clone.getSource());
		this.backward_neighbours.put(sink.toString(), neighbours);
		
		return true;
	}
	
	public void removeEdge(MyEdge<E> edge){
		MyNode<E> source = edge.getSource();
		MyNode<E> sink = edge.getSink();
		
		// remove the neighbourhood references
		this.forward_neighbours.get(source.toString()).remove(sink);
		this.backward_neighbours.get(sink.toString()).remove(source);
		
		this.removeComplexReactionReferences(edge);
		
		// if source has no adjacent edges, then remove it
		if (!this.source_edges_lookup_table.containsKey(source.toString()) &&
			!this.sink_edges_lookup_table.containsKey(source.toString()))
			this.nodes.remove(source);
		
		// if sink has no adjacent edges, then remove it
		if (!this.source_edges_lookup_table.containsKey(sink.toString()) &&
			!this.sink_edges_lookup_table.containsKey(sink.toString()))
			this.nodes.remove(sink);
		
		// remove edge
		this.edges.remove(edge);
	}
	
	private void removeComplexReactionReferences(MyEdge<E> edge){
		MyNode<E> source = edge.getSource();
		if (this.source_edges_lookup_table.containsKey(source.toString())){
			MySet<MyEdge<E>> edges_out = this.source_edges_lookup_table.get(source.toString());
			edges_out.remove(edge);
			
			if (edges_out.isEmpty())
				this.source_edges_lookup_table.remove(source.toString());
		}
		
		MyNode<E> sink = edge.getSink();
		if (this.sink_edges_lookup_table.containsKey(sink.toString())){
			MySet<MyEdge<E>> edges_in = this.sink_edges_lookup_table.get(sink.toString());
			edges_in.remove(edge);
			
			if (edges_in.isEmpty())
				this.sink_edges_lookup_table.remove(sink.toString());
		}
	}
	
	public MyEdge<E> getEdge(MyEdge<E> edge){
		return this.edges.getElement(edge);
	}
	
	public MySet<MyEdge<E>> getEdges(){
		return this.edges;
	}
	
	public MySet<MyNode<E>> getNodes(){
		return this.nodes;
	}
	
	/**
	 * Returns the set of edges which point out of given node.
	 * 
	 * @param complex The node.
	 * @return The set of edges pointing out of given node.
	 */
	public MySet<MyEdge<E>> getEdgesOut(MyNode<E> node){
		if (this.source_edges_lookup_table.containsKey(node.toString()))
			return this.source_edges_lookup_table.get(node.toString());
		
		return new MySet<MyEdge<E>>();
	}
	
	/**
	 * Returns the set of edges which point out of given nodes.
	 * 
	 * @param complexes Set of nodes.
	 * @return The set of edges pointing out of given nodes.
	 */
	public MySet<MyEdge<E>> getEdgesOut(MySet<? extends MyNode> nodes){
		MySet<MyEdge<E>> ret = new MySet<MyEdge<E>>();
		
		Iterator<? extends MyNode> iterator = nodes.iterator();
		while (iterator.hasNext()){
			MyNode<E> node = iterator.next();
			ret = ret.union(this.source_edges_lookup_table.get(node.toString()));
		}
		
		return ret;
	}
	
	/**
	 * Returns the set of edges which point into given node.
	 * 
	 * @param node The node.
	 * @return The set of edges which point into given node.
	 */
	public MySet<MyEdge<E>> getEdgesIn(MyNode<E> node){
		if (this.sink_edges_lookup_table.containsKey(node.toString()))
			return this.sink_edges_lookup_table.get(node.toString());
		
		return new MySet<MyEdge<E>>();
	}
	
	/**
	 * Returns the set of edges which point into given nodes.
	 * 
	 * @param nodes Set of nodes.
	 * @return The set of nodes which point into given nodes.
	 */
	public MySet<MyEdge<E>> getEdgesIn(MySet<? extends MyNode> nodes){
		MySet<MyEdge<E>> ret = new MySet<MyEdge<E>>();
		
		Iterator<? extends MyNode> iterator = nodes.iterator();
		while (iterator.hasNext()){
			MyNode<E> node = iterator.next();
			ret = ret.union(this.sink_edges_lookup_table.get(node.toString()));
		}
		
		return ret;
	}
	
	/**
	 * Returns the set of reactions which consume or produce the given complex.
	 * 
	 * @param complex The complex.
	 * @return The set of reactions consuming or producing the given complex.
	 */
	public MySet<MyEdge<E>> getEdges(MyNode<E> node){
		MySet<MyEdge<E>> going_out = new MySet<MyEdge<E>>();
		if (this.source_edges_lookup_table.containsKey(node.toString()))
			going_out = this.source_edges_lookup_table.get(node.toString());
		
		MySet<MyEdge<E>> going_in = new MySet<MyEdge<E>>();
		if (this.sink_edges_lookup_table.containsKey(node.toString()))
			going_in = this.sink_edges_lookup_table.get(node.toString());
		
		return going_out.union(going_in);
	}
	
	/**
	 * Returns the set of reactions which consume or produce the set of given complexes.
	 * 
	 * @param complexes Set of complexes.
	 * @return The set of reactions consuming or producing the given complexes.
	 */
	public MySet<MyEdge<E>> getEdges(MySet<? extends MyNode> nodes){
		MySet<MyEdge<E>> ret = new MySet<MyEdge<E>>();
		
		Iterator<? extends MyNode> iterator = nodes.iterator();
		while (iterator.hasNext()){
			MyNode<E> node = iterator.next();
			ret = ret.union(this.source_edges_lookup_table.get(node.toString()).union(this.sink_edges_lookup_table.get(node.toString())));
		}
		
		return ret;
	}
	
	/**
	 * Returns directed neighbours of given complex.
	 * 
	 * @param complex The complex.
	 * @return Set of directed neighbours of the given complex.
	 */
	public MySet<MyNode<E>> getNodeNeighboursForward(MyNode<E> node){
		if (this.forward_neighbours.containsKey(node.toString()))
			return this.forward_neighbours.get(node.toString());
		
		return new MySet<MyNode<E>>();
	}
	
	public MySet<MyNode<E>> getNodeNeighboursBackward(MyNode<E> node){
		if (this.backward_neighbours.containsKey(node.toString()))
			return this.backward_neighbours.get(node.toString());
		
		return new MySet<MyNode<E>>();
	}
	
	/**
	 * Returns undirected neighbours of given complex.
	 * 
	 * @param complex The complex.
	 * @return Set of undirected neighbours of the given complex.
	 */
	public MySet<MyNode<E>> getNodeNeighboursForwardBackward(MyNode<E> node){
		MySet<MyNode<E>> forwards = new MySet<MyNode<E>>();
		MySet<MyNode<E>> backwards = new MySet<MyNode<E>>();
		
		if (this.forward_neighbours.containsKey(node.toString()))
			forwards = this.forward_neighbours.get(node.toString());
		
		if (this.backward_neighbours.containsKey(node.toString()))
			backwards = this.backward_neighbours.get(node.toString());
		
		return forwards.union(backwards);
	}
	
	/**
	 * Returns the subgraph corresponding to the edges adjacent to the given nodes.
	 * 
	 * @param nodes Set of nodes.
	 * @return The resulting subgraph.
	 */
	public MyGraph<E> getNodesAsGraph(MySet<MyNode<E>> nodes){
		MyGraph<E> ret = new MyGraph<E>();
		
		Iterator<MyNode<E>> iterator = nodes.iterator();
		while (iterator.hasNext()){
			MyNode<E> node = iterator.next();
			MySet<MyEdge<E>> edges = this.getEdges(node);
			Iterator<MyEdge<E>> iter = edges.iterator();
			while (iter.hasNext()){
				MyEdge<E> edge = iter.next();
				if (nodes.contains(edge.getSink()) && nodes.contains(edge.getSource())){
					ret.addEdge(edge);
				}
			}
		}
		
		return ret;
	}
	
//	/**
//	 * Computes the shortest path between two subnetworks.
//	 * 
//	 * @param subset1 First subset.
//	 * @param subset2 Second subset.
//	 * @param superset The superset.
//	 * @return MySet<Complex> containing the complexes connecting the two networks or null if superset is not a superset of one of the two subsets.
//	 * @throws Exception
//	 */
//	public MySet<MyNode<E>> shortestPath(MySet<? extends MyNode> subset1, MySet<? extends MyNode> subset2, MySet<? extends MyNode> superset) throws Exception{
//		if (!superset.containsAll(subset1) || !superset.containsAll(subset2))
//			return null;
//		
//		MyNode<E> node1 = subset1.head();
//		MyNode<E> node2 = subset2.head();
//		
//		MyNode<E> source = new MyNode<E>("source");
//		MyNode<E> sink = new MyNode<E>("sink");
//		
//		MyEdge<E> edge1 = new MyEdge<E>(source, node1);
//		MyEdge<E> edge2 = new MyEdge<E>(node2, sink);
//		
//		this.addEdge(edge1);
//		this.addEdge(edge2);
//		
//		MySet<MyNode<E>> route = this.dijkstra(source, sink);
//		route.remove(source);
//		route.remove(sink);
//		
//		this.removeEdge(edge1);
//		this.removeEdge(edge2);
//		
//		return route;
//	}
	
//	/**
//	 * Computes shortest path between to complexes in reaction network.
//	 * 
//	 * @param source The source complex.
//	 * @param sink The sink complex.
//	 * @return MySet<Complex> containing the complexes between source and sink.
//	 */
//	public MySet<MyNode<E>> dijkstra(MyNode<E> source, MyNode<E> sink){
//		MySet<MyNode<E>> nodes = this.getNodes();
//		
//		int[] dist = new int[nodes.size()];
//		int[] prev = new int[nodes.size()];
//		
//		dist[nodes.getIndex(source)] = 0;
//		MyPriorityQueue<MyNodeDistance> pq = new MyPriorityQueue<MyNodeDistance>();
//		
//		// fill priority queue
//		for (int i = 0; i < nodes.size(); i++){
//			if (i != nodes.getIndex(source)){
//				dist[i] = Integer.MAX_VALUE;
//				prev[i] = -1;
//			}
//			pq.add(new MyNodeDistance(nodes.toArrayList().get(i), dist[i]));
//		}
//		
//		// traverse graph, collect distances, precursors and update queue
//		while (pq.size() > 0){
//			MyNodeDistance cd = pq.poll();
//			MyNode<E> u = (MyNode<E>)cd.getFirstElement();
//			if (u.equals(sink))
//				break;
//			
//			MySet<MyNode<E>> neighbours = this.getNodeNeighboursForwardBackward(u);
//			Iterator<MyNode<E>> iterator = neighbours.iterator();
//			while (iterator.hasNext()){
//				MyNode<E> v = iterator.next();
//				int alt = dist[nodes.getIndex(u)] + 1;
//				if (alt < dist[nodes.getIndex(v)]){
//					// save data about old pair
//					MyNodeDistance cd_rm = new MyNodeDistance(v, dist[nodes.getIndex(v)]);
//
//					// update data
//					dist[nodes.getIndex(v)] = alt;
//					prev[nodes.getIndex(v)] = nodes.getIndex(u);
//					
//					// remove old pair and insert updated pair
//					pq.remove(cd_rm);
//					pq.add(new MyNodeDistance(v, alt));
//				}
//			}
//		}
//
//		MySet<MyNode<E>> route = new MySet<MyNode<E>>();
//		MyNode<E> node = sink;
//		route.add(node);
//		while (!node.equals(source)){
//			if (prev[nodes.getIndex(node)] < 0)
//				return null;
//			
//			node = nodes.toArrayList().get(prev[nodes.getIndex(node)]);
//			route.add(node);
//		}
//		return route;
//	}
	
	/**
	 * Computes shortest path between to nodes.
	 * 
	 * @param source The source node.
	 * @param sink The sink node.
	 * @param directed If false, graph is treated as being an undirected graph. 
	 * @return MySet<MyEdge<E>> The set containing the edges between source and sink.
	 */
	public MySet<MyEdge<E>> dijkstra(MyNode<E> source, MyNode<E> sink, boolean directed) throws Exception{
	MySet<MyNode<E>> nodes = this.getNodes();
	
	int[] dist = new int[nodes.size()];
	int[] prev = new int[nodes.size()];
	
	dist[nodes.getIndex(source)] = 0;
	MyPriorityQueue<MyNodeDistance> pq = new MyPriorityQueue<MyNodeDistance>();
	
	// fill priority queue
	for (int i = 0; i < nodes.size(); i++){
		if (i != nodes.getIndex(source)){
			dist[i] = Integer.MAX_VALUE;
			prev[i] = -1;
		}
		pq.add(new MyNodeDistance(nodes.toArrayList().get(i), dist[i]));
	}
	
	// traverse graph, collect distances, precursors and update queue
	while (pq.size() > 0){
		MyNodeDistance cd = pq.poll();
		MyNode<E> u = (MyNode<E>)cd.getFirstElement();
		if (u.equals(sink))
			break;
		
		MySet<MyNode<E>> neighbours = null;
		if (directed)
			neighbours = this.getNodeNeighboursForward(u);
		else
			neighbours = this.getNodeNeighboursForwardBackward(u);
		
		Iterator<MyNode<E>> iterator = neighbours.iterator();
		while (iterator.hasNext()){
			MyNode<E> v = iterator.next();
			int alt = dist[nodes.getIndex(u)] + 1;
			if (alt < dist[nodes.getIndex(v)]){
				// save data about old pair
				MyNodeDistance cd_rm = new MyNodeDistance(v, dist[nodes.getIndex(v)]);

				// update data
				dist[nodes.getIndex(v)] = alt;
				prev[nodes.getIndex(v)] = nodes.getIndex(u);
				
				// remove old pair and insert updated pair
				pq.remove(cd_rm);
				pq.add(new MyNodeDistance(v, alt));
			}
		}
	}

	MySet<MyEdge<E>> route = new MySet<MyEdge<E>>();
	MyNode<E> sink_ = sink;
	System.out.println(sink_.toString());
	while (!sink_.equals(source)){
		if (prev[nodes.getIndex(sink_)] < 0)
			return null;
		
		MyNode<E> source_ = nodes.toArrayList().get(prev[nodes.getIndex(sink_)]);
		
		MySet<MyEdge<E>> going_out = this.getEdgesOut(source_);
		MySet<MyEdge<E>> going_in  = this.getEdgesIn(sink_);
		
		MySet<MyEdge<E>> edge = going_in.intersection(going_out);
		if (edge.size() == 0)
			throw new Exception("there is something wrong: intersection of edges is empty");
		
		sink_ = source_;
		route.add(edge.head());
	}
	return route;
}
	
	/**
	 * Computes shortest path between to nodes. Alternative version.
	 * 
	 * @param source The source node.
	 * @param sink The sink node.
	 * @param directed If false, graph is treated as being an undirected graph. 
	 * @return MySet<MyEdge<E>> The set containing the edges between source and sink.
	 */
	public MySet<MyEdge<E>> dijkstra2(MyNode<E> source, MyNode<E> sink, boolean directed) throws Exception{
		MySet<MyNode<E>> nodes = this.getNodes();
		
		int[] dist = new int[nodes.size()];
		MyEdge<E>[] prev = new MyEdge[nodes.size()];
		
		dist[nodes.getIndex(source)] = 0;
		MyPriorityQueue<MyNodeDistance> pq = new MyPriorityQueue<MyNodeDistance>();
		
		// fill priority queue
		for (int i = 0; i < nodes.size(); i++){
			if (i != nodes.getIndex(source)){
				dist[i] = Integer.MAX_VALUE;
				prev[i] = null;
			}
			pq.add(new MyNodeDistance(nodes.toArrayList().get(i), dist[i]));
		}
		
		// traverse graph, collect distances, precursors and update queue
		while (pq.size() > 0){
			MyNodeDistance cd = pq.poll();
			MyNode<E> u = (MyNode<E>)cd.getFirstElement();
			if (u.equals(sink))
				break;
			
			MySet<MyEdge<E>> connecting_edges = null;
			if (directed)
				connecting_edges = this.getEdgesOut(u);
			else
				connecting_edges = this.getEdgesOut(u).union(this.getEdgesIn(u));
			
			Iterator<MyEdge<E>> iterator = connecting_edges.iterator();
			while (iterator.hasNext()){
				MyEdge<E> edge = iterator.next();
				MyNode<E> v = edge.getSink();
				int alt = dist[nodes.getIndex(u)] + 1;
				if (alt < dist[nodes.getIndex(v)]){
					// save data about old pair
					MyNodeDistance cd_rm = new MyNodeDistance(v, dist[nodes.getIndex(v)]);

					// update data
					dist[nodes.getIndex(v)] = alt;
					prev[nodes.getIndex(v)] = edge;
					
					// remove old pair and insert updated pair
					pq.remove(cd_rm);
					pq.add(new MyNodeDistance(v, alt));
				}
			}
		}

		MySet<MyEdge<E>> route = new MySet<MyEdge<E>>();
		MyNode<E> sink_ = sink;
		while (!sink_.equals(source)){
			if (prev[nodes.getIndex(sink_)] == null)
				return null;
			
			MyEdge<E> edge = prev[nodes.getIndex(sink_)];
			route.add(edge);
			
			sink_ = edge.getSource();
		}
		
		return route;
	}
	
	public String toString(){
		String ret = "{";
		
		Iterator<MyEdge<E>> iterator = this.edges.iterator();
		while (iterator.hasNext()){
			MyEdge<E> edge = iterator.next();
			ret = ret + edge.toString() + ", ";
		}
		
		return ret.substring(0, ret.length() - 2) + "}";
	}
	
	public int size(){
		return this.edges.size();
	}
	
	/**
	 * Pair of node and distance.
	 * 
	 * @author neigenfind
	 *
	 */
	static class MyNodeDistance extends MyPair{
		/**
		 * Constructor.
		 * 
		 * @param first_element The node.
		 * @param second_element The initial distance.
		 */
		public MyNodeDistance(MyNode first_element, Integer second_element) {
			super(first_element, second_element);
		}
		
		/**
		 * Makes string from object.
		 */
		public String toString(){
			return "(" + this.getFirstElement() + ", " + this.getSecondElement().toString() + ")";
		}
		
		/**
		 * Compares two MyNodeDistance objects with respect to distance.
		 * 
		 * @param cd1 First MyNodeDistance object.
		 * @param cd2 Second MyNodeDistance object.
		 * @return -1, 0 or 1.
		 */
		public int compare(MyNodeDistance nd1, MyNodeDistance nd2) {
			return nd1.compareTo(nd2);
		}
		
		/**
		 * Compares this MyNodeDistance object with another one.
		 * 
		 * @param cd The other MyNodeDistance object.
		 * @return -1, 0 or 1.
		 */
		public int compareTo(Object o) {
			return (Integer)this.getSecondElement() - (Integer)((MyNodeDistance)o).getSecondElement();
		}
		
		/**
		 * Compares two MyNodeDistance objects with respect to its string.
		 * 
		 * @param The object to compare with.
		 * return -1, 0 or 1.
		 */
		public boolean equals(Object o) {
			return this.toString().equals(((MyNodeDistance)o).toString());
		}
		
		/**
		 * Returns hash code of this MyNodeDistance object with respect to its string.
		 * 
		 * @return The hash code.
		 */
		public int hashCode(){
			return (this.toString()).hashCode();
		}
	}
}
