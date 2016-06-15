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
	private static final long serialVersionUID = 1L;
	
	private MySet<EquivalenceClass<E>> equivalenceClasses; 
	protected MySet<E> basicSet;
	
	/**
	 * The constructor.
	 */
	public Partition(){
		this.equivalenceClasses = new MySet<EquivalenceClass<E>>();
		this.basicSet = new MySet<E>();
	}
	
	public Partition(MySet<EquivalenceClass<E>> equivalence_classes){
		this.equivalenceClasses = equivalence_classes;
		this.basicSet = new MySet<E>();
		
		Iterator<EquivalenceClass<E>> ec_iterator = equivalence_classes.iterator();
		while (ec_iterator.hasNext()){
			this.basicSet.addAll(ec_iterator.next());
		}
	}
	
	public void addEquivalenceClass(EquivalenceClass<E> newEquivalenceClass) throws Exception{
		MySet<E> intersection = newEquivalenceClass.intersection(this.basicSet);
		if (intersection.isEmpty()){
			this.equivalenceClasses.add(newEquivalenceClass);
			this.basicSet = this.basicSet.union(newEquivalenceClass);
		}
		else{			
			throw new Exception("equivalence class is not disjoint");
		}
	}
	
	public MySet<EquivalenceClass<E>> getEquivalenceClasses(){
		return this.equivalenceClasses;
	}
	
	public int size(){
		return this.equivalenceClasses.size();
	}
	
	public MySet<E> getBasicSet(){
		return this.basicSet;
	}
	
	public boolean isFiner(Partition<E> otherPartition) throws Exception{
		boolean result = true;
		if (this.getBasicSet().equals(otherPartition.getBasicSet())){
			Iterator<EquivalenceClass<E>> iter = this.equivalenceClasses.iterator();
			while (iter.hasNext()){
				boolean classHasUpperClass = false;
				EquivalenceClass<E> currentClass = iter.next();
				Iterator<EquivalenceClass<E>> innerIter = otherPartition.equivalenceClasses.iterator();
				while (innerIter.hasNext()){
					EquivalenceClass<E> innerClass = innerIter.next();
					if (currentClass.isSubset(innerClass)){
						classHasUpperClass = true;
					}
				}
				if (classHasUpperClass==false){
					result = false;
				}
			}
		}
		else{
			throw new Exception("basic sets are different");
		}		
		return result;
	}
	
	public EquivalenceClass<E> getEquivalenceClassFromMember(E member){
		EquivalenceClass<E> result = new EquivalenceClass<E>();
		Iterator<EquivalenceClass<E>> iter = this.equivalenceClasses.iterator();
		while (iter.hasNext()){
			EquivalenceClass<E> currentClass = iter.next();
			if (currentClass.contains(member)){
				result = currentClass;
			}
		}		
		return result;
	}
	
	public ArrayList<EquivalenceClass<E>> toArrayList(){
		return this.equivalenceClasses.toArrayList();
	}
	
	public void removeEquivalenceClass(EquivalenceClass<E> element){
		this.equivalenceClasses.remove(element);
		this.basicSet.removeAll(element);
	}
	
	public String toString(){
		return this.equivalenceClasses.toString();
	}
	
	public Iterator<EquivalenceClass<E>> iterator(){
		return this.equivalenceClasses.iterator();
	}
	
	public void removeEmptySet(){
		this.equivalenceClasses.removeEmptySet();
	}
	
	public int getIndex(EquivalenceClass<E> ec){
		return this.equivalenceClasses.getIndex(ec);
	}
	
	public EquivalenceClass<E> getEquivalenceClass(E e){
		Iterator<EquivalenceClass<E>> iterator = this.equivalenceClasses.iterator();
		while (iterator.hasNext()){
			EquivalenceClass<E> ec = iterator.next();
			if (ec.contains(e))
				return ec;
		}
		
		return null;
	}
	
	public Partition<E> clone(){
		Partition<E> ret = new Partition<E>();
		
		Iterator<EquivalenceClass<E>> ec_iterator = this.iterator();
		while (ec_iterator.hasNext()){
			EquivalenceClass<E> ec = new EquivalenceClass<E>();
			ec.addAll(ec_iterator.next().clone());
			try {
				ret.addEquivalenceClass(ec);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	public int compareTo(Partition<E> partition) {
		return this.toString().compareTo(partition.toString());
	}

	public boolean equals(Object o) {
		return this.compareTo((Partition<E>) o) == 0;
	}
}
