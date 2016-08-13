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

package math.set;

public class MyPair<E,F> implements Comparable<MyPair<E,F>>{
	private E first_element;
	private F second_element;
	
	public MyPair(E first_element, F second_element){
		this.first_element = first_element;
		this.second_element = second_element;
	}
	
	public E getFirstElement(){
		return this.first_element;
	}
	
	public F getSecondElement(){
		return this.second_element;
	}
	
	public String toString(){
		return "(" + this.first_element.toString() + ", " + this.second_element.toString() + ")";
	}

	public int compareTo(MyPair<E, F> o) {
		return this.toString().compareTo(((MyPair<E,F>)o).toString());
	}
	public boolean equals(Object o){
		return this.toString().equals(((MyPair<E,F>)o).toString());
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
}
