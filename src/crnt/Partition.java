/* CRNToolbox, Copyright (c) 2010-2016 Jost Neigenfind  <jostie@gmx.de>,
 * Sergio Grimbs, Zoran Nikoloski
 * 
 * A Java toolbox for Chemical Reaction Networks
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

package crnt;

// NOTE : This implementation is not fail safe yet.
// This means, it is not guaranteed that the equivalence classes are
// non-overlapping. Furthermore, there is no control over the 
// underlying set of elements and whether all elements are covered
// by exactly one equivalence class!

import java.util.*;

import miscellaneous.MySet;

public class Partition<E> implements Comparable<Partition<E>> {
	private MySet<EquivalenceClass<E>> equivalence_classes;	// MySet of equivalence classes
	protected MySet<E> basic_set;	// MySet of the overall elements in the partition
	
	/**
	 * Constructor of an empty partition.
	 */
	public Partition(){
		this.equivalence_classes = new MySet<EquivalenceClass<E>>();
		this.basic_set = new MySet<E>();
	}
	
	/** Constructor of an partition given a set of equivalence classes.
	 * 
	 * @param equivalence_classes The set of equivalence classes of partition.
	 * @throws Exception If equivalence classes overlap, an exception is thrown.
	 */
	public Partition(MySet<EquivalenceClass<E>> equivalence_classes) throws Exception{
		this.equivalence_classes = equivalence_classes;
		this.basic_set = new MySet<E>();
		
		Iterator<EquivalenceClass<E>> ec_iterator = equivalence_classes.iterator();
		while (ec_iterator.hasNext()){
			EquivalenceClass<E> equivalence_class = ec_iterator.next();
			// test if equivalence class to add is disjoint to the already added elements
			if (!this.basic_set.intersection(equivalence_class).isEmpty())
				throw new Exception("equivalence class is not disjoint");
			this.basic_set.union(equivalence_class);
		}
	}
	
	/**
	 * Adds an equivalence class to partition.
	 * 
	 * @param equivalence_class The equivalence class to add
	 * @throws Exception If equivalence classes overlap, an exception is thrown.
	 */
	public void addEquivalenceClass(EquivalenceClass<E> equivalence_class) throws Exception{
		if (!this.basic_set.intersection(equivalence_class).isEmpty())
			throw new Exception("equivalence class is not disjoint");
		
		this.equivalence_classes.add(equivalence_class);
		this.basic_set = this.basic_set.union(equivalence_class);
	}
	
	/**
	 * Get set of equivalence classes.
	 * 
	 * @return MySet of equivalence classes.
	 */
	public MySet<EquivalenceClass<E>> getEquivalenceClasses(){
		return this.equivalence_classes;
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
	 * Get number of equivalence classes.
	 * 
	 * @return Number of equivalence classes.
	 */
	public int size(){
		return this.equivalence_classes.size();
	}
	
	/**
	 * Checks if other partition is finer.
	 * 
	 * @param other_partition The other partition.
	 * @return True if finer, false otherwise.
	 * @throws Exception
	 */
	public boolean isFiner(Partition<E> other_partition) throws Exception{
		boolean result = true;
		if (this.getBasicSet().equals(other_partition.getBasicSet())){
			Iterator<EquivalenceClass<E>> iter = this.equivalence_classes.iterator();
			while (iter.hasNext()){
				boolean class_has_upper_class = false;
				EquivalenceClass<E> current_class = iter.next();
				Iterator<EquivalenceClass<E>> inner_iter = other_partition.equivalence_classes.iterator();
				while (inner_iter.hasNext()){
					EquivalenceClass<E> inner_class = inner_iter.next();
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
	public EquivalenceClass<E> getEquivalenceClassFromMember(E element){
		EquivalenceClass<E> result = new EquivalenceClass<E>();
		Iterator<EquivalenceClass<E>> iter = this.equivalence_classes.iterator();
		while (iter.hasNext()){
			EquivalenceClass<E> current_class = iter.next();
			if (current_class.contains(element)){
				result = current_class;
			}
		}		
		return result;
	}
	
	/**
	 * EquivalenceClass to ArrayList.
	 * 
	 * @return ArrayList representing EquivalenceClass.
	 */
	public ArrayList<EquivalenceClass<E>> toArrayList(){
		return this.equivalence_classes.toArrayList();
	}
	
	/**
	 * Remove given equivalence class from partition.
	 * 
	 * @param equivalence_class The equivalence class to remove.
	 */
	public void removeEquivalenceClass(EquivalenceClass<E> equivalence_class){
		this.equivalence_classes.remove(equivalence_class);
		this.basic_set.removeAll(equivalence_class);
	}
	
	/**
	 * Make readable string from partition.
	 * 
	 * @return String representing partition.
	 */
	public String toString(){
		return this.equivalence_classes.toString();
	}
	
	/**
	 * Get iterator over equivalence classes.
	 * 
	 * @return Iterator over equivalence classes.
	 */
	public Iterator<EquivalenceClass<E>> iterator(){
		return this.equivalence_classes.iterator();
	}
	
	/**
	 * Remove empty set from partition.
	 */
	public void removeEmptySet(){
		this.equivalence_classes.removeEmptySet();
	}
	
	/**
	 * Get index of equivalence class in ArrayList.
	 * 
	 * @param equivalence_class Equivalence class whose index is wanted.
	 * @return The index of this equivalence class.
	 */
	public int getIndex(EquivalenceClass<E> equivalence_class){
		return this.equivalence_classes.getIndex(equivalence_class);
	}
	
	/**
	 * Get equivalence class.
	 * 
	 * @param equivalence_class Equivalence class which is wanted.
	 * @return The equivalence class
	 */
	public EquivalenceClass<E> getEquivalenceClass(E equivalence_class){
		Iterator<EquivalenceClass<E>> iterator = this.equivalence_classes.iterator();
		while (iterator.hasNext()){
			EquivalenceClass<E> equivalence_class_ = iterator.next();
			if (equivalence_class_.contains(equivalence_class))
				return equivalence_class_;
		}
		
		return null;
	}
	
	/**
	 * Clones partition.
	 * 
	 * @return Clone of partition.
	 */
	public Partition<E> clone(){
		Partition<E> ret = new Partition<E>();
		
		Iterator<EquivalenceClass<E>> ec_iterator = this.iterator();
		while (ec_iterator.hasNext()){
			EquivalenceClass<E> equivalence_class = new EquivalenceClass<E>();
			equivalence_class.addAll(ec_iterator.next().clone());
			try {
				ret.addEquivalenceClass(equivalence_class);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/**
	 * Compares this partition with another partition
	 * 
	 * @return 0 if equal, -1 or 1 if unequal.
	 */
	public int compareTo(Partition<E> partition) {
		return this.toString().compareTo(partition.toString());
	}

	/**
	 * Is partition equal to another partition.
	 * 
	 * @return True if equal, false otherwise.
	 */
	public boolean equals(Object o) {
		return this.compareTo((Partition<E>) o) == 0;
	}
}
