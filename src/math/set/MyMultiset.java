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

package math.set;

import java.util.*;
import java.lang.reflect.*;

public class MyMultiset<E> implements Comparable<MyMultiset<E>>{
	private MySet<E> multiset;
	private HashMap<E,Double> occurences;
	
	public MyMultiset(){
		this.multiset = new MySet<E>();
		this.occurences = new HashMap<E,Double>();
	}
	
	public boolean add(E e){
		boolean ret = false;
		
		if (!this.multiset.contains(e)){
			ret = this.multiset.add(e);
			if (ret)
				this.occurences.put(e, new Double(1));
		} else {
			Double c = this.occurences.get(e);
			this.occurences.put(e, c + 1);
			ret = true;
		}
		
		return ret;
	}

	public boolean add(E e, Double occurences){
		boolean ret = false;
		
		if (!this.multiset.contains(e)){
			ret = this.multiset.add(e);
			if (ret)
				this.occurences.put(e, occurences);
		} else {
			Double c = this.occurences.get(e);
			this.occurences.put(e, c + occurences);
			ret = true;
		}
		
		return ret;
	}	
	
	public MyMultiset<E> addElement(E e){
		boolean b = false;
		
		if (!this.multiset.contains(e)){
			b = this.multiset.add(e);
			if (b)
				this.occurences.put(e, new Double(1));
		} else {
			Double c = this.occurences.get(e);
			this.occurences.put(e, c + 1);
		}
		
		return this;
	}
	
	public Double size(){
		Double ret = 0.0;
		
		Iterator<E> iterator = this.occurences.keySet().iterator();
		while(iterator.hasNext()){
			E e = iterator.next();
			Double c = this.occurences.get(e);
			ret = ret + c;
		}
		
		return ret;
	}
	
	public int numberOfDistinctElements(){
		return this.multiset.size();
	}
	
	public Object[] getListOfDistinctElements(){
		return this.multiset.toArray();
	}
	
	public String toString(){
		Iterator<E> iterator = this.multiset.iterator();
		StringBuffer buffer = new StringBuffer("{");
		while (iterator.hasNext()){
			E e = iterator.next();
			
			buffer = buffer.append(this.occurences.get(e)).append(" ").append(e.toString()).append(", ");
			//int c = this.occurences.get(e);
			//for (int i = 0; i < c; i++)
			//	ret = ret + e.toString() + ", ";
		}
		if (buffer.length() > 1)
			buffer.setLength(buffer.length() - 2);
		buffer = buffer.append("}");
	
		return buffer.toString();
	}
	
	public String toLaTeXString() throws Exception{
		Class c = this.multiset.head().getClass();
		Method m = null;
		try{
			m = c.getMethod("toLaTeXString");
		} catch (NoSuchMethodException nsme){
		}
				
		Iterator<E> iterator = this.multiset.iterator();
		StringBuffer buffer = new StringBuffer();
		while (iterator.hasNext()){
			E e = iterator.next();
			
			if (this.occurences.get(e) > 1)
				buffer = buffer.append(this.occurences.get(e)).append(" ").append((String)m.invoke(e, null)).append(" + ");
			else 
				buffer = buffer.append((String)m.invoke(e, null)).append(" + ");
		}
		if (buffer.length() > 1)
			buffer.setLength(buffer.length() - 3);
	
		return buffer.toString();
	}
	
	public Iterator<E> iterator(){
		return this.multiset.iterator();
	}
	
	public MyMultiset<E> clone(){
		MyMultiset<E> ret = new MyMultiset<E>();
		
		Iterator<E> iterator = this.iterator();
		while (iterator.hasNext()){
			E e = iterator.next();
			for (int i = 0; i < this.getNumberOfOccurences(e); i++)
				ret.add(e);
		}
		
		return ret;
	}
	
	public Double getNumberOfOccurences(E e){
		return this.occurences.get(e);
	}
	
	public MySet<E> toMySet(){
		return this.multiset;
	}
	
	public int compareTo(MyMultiset<E> B){
		return this.toString().compareTo(B.toString());
	}	
	
	public boolean equals(Object o){
		return this.toString().equals(((MyMultiset<E>)o).toString());
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	public boolean contains(E e){
		return this.multiset.contains(e);
	}
	
	/**
	 * Removes an element from multiset resulting in a by one decreased number of occurences.
	 * 
	 * @param e The element to be removed.
	 */
	public void remove(E e){
		if (this.contains(e)){					// if e is element of this multiset
			Double c = this.occurences.get(e) - 1;	// calculate its new number of occurences in multiset
			if (c == 0){						// if its new number of occurences is 0, then remove it
				this.occurences.remove(e);
				this.multiset.remove(e);
			} else {
				this.occurences.put(e, c);		// otherwise save the new number of occurences
			}
		}
	}
	
	/**
	 * Removes the given number of the given element from multiset.
	 * Can also result in negative number of occurences.
	 * 
	 * @param e The element to be removed
	 * @param occurences How many instances of this element are to be removed
	 */
	public void remove(E e, Double occurences){
		if (!this.contains(e))			// if the mulitset does not contain this element, add it with occurence 0
			this.occurences.put(e, new Double(0));

		Double c = this.occurences.get(e);	// get the number of occurences
		c = c - occurences;				// calculate the new number of occurences
		if (c == 0){					// if the new number of occurences is 0, then remove it
			this.occurences.remove(e);
			this.multiset.remove(e);
		} else {
			this.occurences.put(e, c);	// otherwise save the new number of occurences
		}
	}

	public void sub(E e, Double occurences){
		if (!this.contains(e)){
			this.multiset.add(e);
			this.occurences.put(e, -occurences);
			return;
		}

		Double c = this.occurences.get(e);	// get the number of occurences
		c = c - occurences;				// calculate the new number of occurences
		if (c == 0){					// if the new number of occurences is 0, then remove it
			this.occurences.remove(e);
			this.multiset.remove(e);
		} else {
			this.occurences.put(e, c);	// otherwise save the new number of occurences
		}
	}
	
	/**
	 * Computes the difference of two multisets.
	 * Can also contain elements with negative number of occurences.
	 * 
	 * @param B The second multiset.
	 * @return The multiset representing the difference.
	 */
	public MyMultiset<E> difference(MyMultiset<E> B){
		MyMultiset<E> ret = this.clone();		// make a copy of this multiset
		
		Iterator<E> iterator = B.iterator();	// loop over all elements of the second multiset
		while (iterator.hasNext()){
			E e = iterator.next();
			Double c = B.getNumberOfOccurences(e);	// get the number of occurences of the current element
			
			ret.remove(e, c);					// remove this number of occurences
		}
		
		return ret;
	}
	
	public MyMultiset<E> sub(MyMultiset<E> B){
		MyMultiset<E> ret = this.clone();		// make a copy of this multiset
		
		Iterator<E> iterator = B.iterator();	// loop over all elements of the second multiset
		while (iterator.hasNext()){
			E e = iterator.next();
			Double c = B.getNumberOfOccurences(e);	// get the number of occurences of the current element
			
			ret.sub(e, c);						// remove this number of occurences
		}
		
		return ret;
	}
	
	public E head() {
		E ret = null;

		Iterator<E> iterator = this.iterator();
		if (iterator.hasNext())
			ret = iterator.next();

		return ret;
	}
}
