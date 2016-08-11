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
	private E e; 

	public MyNode(){
		this.id = null;
		this.e = null;
	}
	
	public MyNode(String id){
		this.e = null;
		this.id = id;
	}
	
	public MyNode(E e){
		this.id = null;
		this.e = e;
	}
	
	public MyNode(String id, E e){
		this.id = id;
		this.e = e;
	}
	
	public String getId(){
		return this.id;
	}
	
	public E getObject(){
		return this.e;
	}
	
	public MyNode<E> clone(){
		// if there is a clone method in node object ...
		try {
			Method method = this.e.getClass().getMethod("clone", new Class[]{});
			if (method != null)
				return new MyNode<E>(this.id, (E)method.invoke(this.e, new Object[]{}));
		} catch(Exception e){
		}
		
		// ... copy reference
		return new MyNode<E>(this.id, this.e);
	}
	
	public String toString(){
		// nodes are returned in brackets "(...)" to distinguish it from the contained object
		if (this.id != null && this.e == null)
			return "(" + this.id.toString() + ")";
		
		if (this.id == null && this.e != null)
			return "(" + this.e.toString() + ")";
		
		return "(" + this.id + " := " + this.e.toString() + ")";
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
