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

package miscellaneous;

import java.util.*;
import java.lang.reflect.*;

/**
 * 
 * @author neigenfind
 * 
 *         The class MySet. This class implements the mathematical structure
 *         set.
 * 
 * @param <E>
 *            The type of class which is element of the MySet object.
 */
public class MySet<E> extends TreeSet<E> implements Comparable<MySet<E>> {
	/**
	 * standard serial version UID
	 */
	private static final long serialVersionUID = 1L;
	private boolean has_changed;
	private HashMap<String,Integer> element_indices; 
	private ArrayList<E> element_array_list;
	
	// ----------------------------------------------------
	//  methods from super class which must be overwritten
	public MySet(){
		super();
		
		this.element_indices = new HashMap<String,Integer>();
		this.element_array_list = new ArrayList<E>();
		
		this.has_changed = false;
	}
	
	public boolean add(E e){
		this.has_changed = true;
		
		return super.add(e);
	}
	
	public boolean addAll(Collection<? extends E> c){
		this.has_changed = true;
		
		return super.addAll(c);
	}
	
	public void clear(){
		this.has_changed = true;
		
		super.clear();
	}
	
	public boolean remove(Object o){
		this.has_changed = true;
		
		return super.remove(o);
	}
	// --------------------------------------------------
	
	/**
	 * Adds an element e to a MySet object and returns the resulting object.
	 * 
	 * @param e The element which is added to the MySet object.
	 *           
	 * @return The modified MySet object.
	 */
	public MySet<E> addElement(E e) {
		this.add(e);

		this.has_changed = true;
		
		return this;
	}

	/**
	 * Clones this MySet object.
	 * 
	 * @return A copy of this MySet object.
	 */
	public MySet<E> clone() {
		MySet<E> ret = new MySet<E>();

		Iterator<E> iterator = this.iterator();
		while (iterator.hasNext())
			ret.add(iterator.next());

		return ret;
	}

	/**
	 * Compares this MySet object with a second MySet object.
	 * 
	 * @param B
	 *            The second MySet object.
	 * @return Returns 0 if both objects are equal, else -1 or 1.
	 */
	public int compareTo(MySet<E> B) {
		return this.toString().compareTo(B.toString());
	}

	public boolean equals(Object o) {
		return this.compareTo((MySet<E>) o) == 0;
	}

	/**
	 * Creates the difference of this MySet object and a second MySet object B.
	 * 
	 * @param B
	 *            The second MySet object.
	 * @return The MySet object that represents the difference.
	 */
	public MySet<E> difference(MySet<E> B) {
		MySet<E> ret = this.clone();
		ret.removeAll(B);

		return ret;
	}

	/**
	 * Returns the hash code of this MySet object. This hash code is derived by
	 * the String return by the toString method.
	 * 
	 * @return The hash code of this MySet object.
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * Creates the intersection of this MySet object and a second MySet object
	 * B.
	 * 
	 * @param B
	 *            The second MySet object.
	 * @return The MySet object that represents the intersection.
	 */
	public MySet<E> intersection(MySet<E> B) {
		MySet<E> ret = this.clone();
		ret.retainAll(B);

		return ret;
	}

	/**
	 * Checks if this MySet object is a subset of a second MySet object B.
	 * 
	 * @param B
	 *            The second MySet object.
	 * @return True if this MySet object is a subset of B, false otherwise.
	 */
	public boolean isSubset(MySet<E> B) {
		MySet<E> C = this.intersection(B);

		return C.size() == this.size();
	}

	/**
	 * Transforms the MySet object to a readable string.
	 * 
	 * @return The String object representing the MySet object in readable form.
	 */
	public String toString() {
		if (this.size() == 0)
			return "{}";

		Iterator<E> iterator = this.iterator();
		String ret = "{";
		while (iterator.hasNext()) {
			E e = iterator.next();

			ret = ret + e.toString() + ", ";
		}
		ret = ret.substring(0, ret.length() - 2);
		ret = ret + "}";

		return ret;
	}
	
	public String toLaTeXString() throws Exception{
		Class c = this.head().getClass();
		Method m = null;
		try{
			m = c.getMethod("toLaTeXString");
		} catch (NoSuchMethodException nsme){
		}
				
		Iterator<E> iterator = this.iterator();
		StringBuffer buffer = new StringBuffer();
		while (iterator.hasNext()){
			E e = iterator.next();

			buffer = buffer.append((String)m.invoke(e, null)).append(" ");
		}
		if (buffer.length() > 1)
			buffer.setLength(buffer.length() - 1);
	
		return buffer.toString();
	}

	/**
	 * Creates the union of this MySet object and a second MySet object B.
	 * 
	 * @param B
	 *            The second MySet object.
	 * @return The MySet object that represents the union.
	 */
	public MySet<E> union(MySet<E> B) {
		MySet<E> ret = this.clone();
		ret.addAll(B);

		return ret;
	}

	public E head() {
		E ret = null;

		Iterator<E> iterator = this.iterator();
		if (iterator.hasNext())
			ret = iterator.next();

		return ret;
	}
	
	public void removeEmptySet(){
		E e = null;
		
		// check if this set is not the empty set
		// if not take one element
		Iterator<E> iterator = this.iterator();
		if (!iterator.hasNext())
			return;
		else
			e = iterator.next();
		
		// check if the elements of this set are
		// also instances of MySet
		if (!(e instanceof MySet))
			return;
		else{
			// loop over the elements of this set
			// and remove the elements of size zero
			iterator = this.iterator();
			while (iterator.hasNext()){
				e = iterator.next();
				if (((MySet)e).size() == 0)
					this.remove(e);
			}
		}
		
		this.has_changed = true;
	}
	
	public E getElement(E e){
		E he = this.higher(e);		// test if there exists a higher element
		if (he == null)				// if not, e must be highest
			return this.last();		// return highest
		
		E le = this.lower(e);		// test if there exists a lower element
		if (le == null)				// if not, e must be lowest
			return this.first();	// return lowest
		
		// else there is a higher and lower element, get return e
		return this.subSet(e, he).first();

//		Iterator<E> iterator = this.iterator();
//		while (iterator.hasNext()){
//			E ret = iterator.next();
//			if (ret.equals(e))
//				return ret;
//		}
//		return null;
	}
	
	public ArrayList<E> toArrayList(){
		if (this.has_changed){
			this.makeIndices();
			this.has_changed = false;
		}
		
		return this.element_array_list;
	}
	
	public int getIndex(E e){
		if (this.has_changed){
			this.makeIndices();
			this.has_changed = false;
		}
		
		if (this.element_indices.containsKey(e.toString()))
			return this.element_indices.get(e.toString());
		else
			return -1;

//		ArrayList<E> list = this.toArrayList();
//		
//		return list.indexOf(e);
	}
	
	
	private ArrayList<E> makeArrayList() {
		ArrayList<E> ret = new ArrayList<E>();

		Iterator<E> iterator = this.iterator();
		while (iterator.hasNext()) {
			E e = iterator.next();
			ret.add(e);
		}

		return ret;
	}
	
	private void makeIndices(){
		this.element_array_list = this.makeArrayList();
		for (int i = 0; i < this.element_array_list.size(); i++)
			this.element_indices.put(this.element_array_list.get(i).toString(), new Integer(i));
	}
}
