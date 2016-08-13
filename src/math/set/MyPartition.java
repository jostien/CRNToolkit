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

// NOTE : This implementation is not fail safe yet.
// This means, it is not guaranteed that the equivalence classes are
// non-overlapping. Furthermore, there is no control over the 
// underlying set of elements and whether all elements are covered
// by exactly one equivalence class!

import java.util.*;

public class MyPartition<E> extends MySet<MyEquivalenceClass<E>> {
	/**
	 * standard serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	protected MySet<E> basic_set;							// MySet of the overall elements in the partition
	private MyEquivalenceRelation<E> equivalence_relation;
	
	/**
	 * Constructor of an empty partition.
	 */
	public MyPartition(MyEquivalenceRelation<E> equivalence_relation){
		this.basic_set = new MySet<E>();
		this.equivalence_relation = equivalence_relation;
	}
	
	/** Constructor of an partition given a set of equivalence classes.
	 * 
	 * @param equivalence_classes The set of equivalence classes of partition.
	 * @throws Exception If equivalence classes overlap, an exception is thrown.
	 */
	public MyPartition(MySet<MyEquivalenceClass<E>> equivalence_classes) throws Exception{
		this.basic_set = new MySet<E>();
		
		Iterator<MyEquivalenceClass<E>> ec_iterator = equivalence_classes.iterator();
		while (ec_iterator.hasNext()){
			MyEquivalenceClass<E> equivalence_class = ec_iterator.next();
			// test if equivalence class to add is disjoint to the already added elements
			if (!this.basic_set.intersection(equivalence_class).isEmpty())
				throw new Exception("equivalence class is not disjoint");
			this.basic_set.union(equivalence_class);
		}
		
		this.addAll(equivalence_classes);
	}
	
	public void addElementToEquivalenceClasses(E e) throws Exception{
		Iterator<MyEquivalenceClass<E>> iterator = this.iterator();
		
		boolean was_added = false;
		while (iterator.hasNext()){
			MyEquivalenceClass<E> ec = iterator.next();
			was_added = was_added | ec.add(e);
		}
		if (!was_added){
			MyEquivalenceClass<E> ec = new MyEquivalenceClass<E>(this.equivalence_relation);
			ec.add(e);
			this.addEquivalenceClass(ec);
		}
	}
	
	/**
	 * Adds an equivalence class to partition.
	 * 
	 * @param equivalence_class The equivalence class to add
	 * @throws Exception If equivalence classes overlap, an exception is thrown.
	 */
	public void addEquivalenceClass(MyEquivalenceClass<E> equivalence_class) throws Exception{
		if (!this.basic_set.intersection(equivalence_class).isEmpty())
			throw new Exception("equivalence class is not disjoint");
		
		this.add(equivalence_class);
		this.basic_set = this.basic_set.union(equivalence_class);
	}
	
	/**
	 * Get set of equivalence classes.
	 * 
	 * @return MySet of equivalence classes.
	 */
	public MySet<MyEquivalenceClass<E>> getEquivalenceClasses(){
		return this;
	}
	
	/**
	 * Get basic set, the union of equivalence classes.
	 * 
	 * @return The union of equivalence classes.
	 */
	public MySet<E> getBasicSet(){
		return this.basic_set;
	}
	
	/**
	 * Checks if other partition is finer.
	 * 
	 * @param other_partition The other partition.
	 * @return True if finer, false otherwise.
	 * @throws Exception
	 */
	public boolean isFiner(MyPartition<E> other_partition) throws Exception{
		boolean result = true;
		if (this.getBasicSet().equals(other_partition.getBasicSet())){
			Iterator<MyEquivalenceClass<E>> iter = this.iterator();
			while (iter.hasNext()){
				boolean class_has_upper_class = false;
				MyEquivalenceClass<E> current_class = iter.next();
				Iterator<MyEquivalenceClass<E>> inner_iter = other_partition.iterator();
				while (inner_iter.hasNext()){
					MyEquivalenceClass<E> inner_class = inner_iter.next();
					if (current_class.isSubset(inner_class)){
						class_has_upper_class = true;
					}
				}
				if (class_has_upper_class==false){
					result = false;
				}
			}
		}
		else{
			throw new Exception("basic sets are different");
		}		
		return result;
	}
	
	/**
	 * Get the equivalence class for a given element.
	 * 
	 * @param element The element whose equivalence class is wanted.
	 * @return The corresponding equivalence class.
	 */
	public MyEquivalenceClass<E> getEquivalenceClassByElement(E element){
		Iterator<MyEquivalenceClass<E>> iter = this.iterator();
		while (iter.hasNext()){
			MyEquivalenceClass<E> current_class = iter.next();
			if (current_class.contains(element))
				return current_class;
		}		
		return null;
	}
}
