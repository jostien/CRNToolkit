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

public class MyEdge<E> implements Comparable<MyEdge<E>>{
	private MyNode<E> source;
	private MyNode<E> sink;
	
	public MyEdge(MyNode<E> source, MyNode<E> sink){
		this.source = source;
		this.sink = sink;
	}
	
	public MyNode<E> getSource(){
		return this.source;
	}
	
	public MyNode<E> getSink(){
		return this.sink;
	}

	public void setSource(MyNode<E> source){
		this.source = source;
	}
	
	public void setSink(MyNode<E> sink){
		this.sink = sink;
	}
	
	public MyEdge<E> clone(){
		return new MyEdge<E>(source, sink);
	}
	
	public String toString(){
		return this.source.toString() + " -> " + this.sink.toString();
	}
	
	public int compareTo(MyEdge<E> node) {
		return this.toString().compareTo(node.toString());
	}
	
	public boolean equals(Object o) {
		return this.toString().equals(((MyEdge<E>)o).toString());
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
}
