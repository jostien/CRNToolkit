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

import java.lang.reflect.Method;

public class MyNode<E> implements Comparable<MyNode<E>>{
	private String id;
	private E node; 

	public MyNode(){
		this.id = null;
		this.node = null;
	}
	
	public MyNode(String id){
		this.node = null;
		this.id = id;
	}
	
	public MyNode(E node){
		this.id = null;
		this.node = node;
	}
	
	public MyNode(String id, E node){
		this.id = id;
		this.node = node;
	}
	
	public String getId(){
		return this.id;
	}
	
	public E getNode(){
		return this.node;
	}
	
	public MyNode<E> clone(){
		// if there is a clone method in node object ...
		try {
			Method method = this.node.getClass().getMethod("clone", new Class[]{});
			if (method != null)
				return new MyNode<E>(this.id, (E)method.invoke(this.node, new Object[]{}));
		} catch(Exception e){
		}
		
		// ... copy reference
		return new MyNode<E>(this.id, this.node);
	}
	
	public String toString(){
		// nodes are returned in brackets "(...)" to distinguish it from the contained object
		if (this.id != null && this.node == null)
			return "(" + this.id.toString() + ")";
		
		if (this.id == null && this.node != null)
			return "(" + this.node.toString() + ")";
		
		return "(" + this.id + " := " + this.node.toString() + ")";
	}
	
	public int compareTo(MyNode<E> node) {
		return this.toString().compareTo(node.toString());
	}
	
	public boolean equals(Object o) {
		return this.toString().equals(((MyNode<E>)o).toString());
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
}
