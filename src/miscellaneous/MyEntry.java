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

package miscellaneous;

public class MyEntry<Entry,FirstD,SecondD> implements Comparable<MyEntry<Entry,FirstD,SecondD>>{
	private Entry entry;
	private FirstD first_dimension;
	private SecondD second_dimension;
	private int comparator;
	
	public MyEntry(){
	}
	
	public MyEntry(Entry entry, FirstD first_dimension, SecondD second_dimension){
		this.entry = entry;
		this.first_dimension = first_dimension;
		this.second_dimension = second_dimension;
	}

	public Entry getEntry(){
		return this.entry;
	}
	
	public void setEntry(Entry entry){
		this.entry = entry;
	}
	
	public FirstD getFirstDimension(){
		return this.first_dimension;
	}
	
	public SecondD getSecondDimension(){
		return this.second_dimension;
	}
	
	public MyEntry<Entry,FirstD,SecondD> setComparatorToGeneral(){
		this.comparator = 0;
		
		return this;
	}
	
	public MyEntry<Entry,FirstD,SecondD> setComparatorToFirstDimension(){
		this.comparator = 1;
		
		return this;
	}
	
	public MyEntry<Entry,FirstD,SecondD> setComparatorToSecondDimension(){
		this.comparator = 2;
		
		return this;
	}
	
	public boolean isComparatorToGeneral(){
		return this.comparator == 0;
	}
	
	public boolean isComparatorToFirstDimension(){
		return this.comparator == 1;
	}
	
	public boolean isComparatorToSecondDimension(){
		return this.comparator == 2;
	}
	
	public int getComparator(){
		return this.comparator;
	}
	
	public void setComparator(int comparator){
		this.comparator = comparator;
	}
	
	public int compareTo(MyEntry<Entry,FirstD,SecondD> entry){
		return this.toString().compareTo(entry.toString());
	}
	
	public String toString(){
		// not sure anymore, why to add the entry string for comparison, seems to be not necessary and therefore switched to comment
		if (this.isComparatorToGeneral())
			return "" + this.first_dimension.toString() + " " + this.second_dimension.toString();// + " " + this.entry.toString();
		if (this.isComparatorToFirstDimension())
			return "" + this.first_dimension.toString();// + " " + this.entry.toString();
		if (this.isComparatorToSecondDimension())
			return "" + this.second_dimension.toString();// + " " + this.entry.toString();
		return null;
	}
	
//	public int compareToGeneral(MyEntry<Entry,FirstD,SecondD> entry){
//		return this.toString().compareTo(entry.toString());
//	}
//	
//	public int compareToFirstDimension(MyEntry<Entry,FirstD,SecondD> entry) {
//		boolean b = false;
//		
//		Class c = this.first_dimension.getClass();
//		Method m = null;
//		try{	// test if compareTo uses MySet as parameter
//			m = c.getMethod("compareTo", new Class<?>[]{MySet.class});
//		} catch (NoSuchMethodException nsme){
//			try{	// if not, test if compareTo uses MyMultiset as parameter
//				m = c.getMethod("compareTo", new Class<?>[]{MyMultiset.class});
//			} catch (NoSuchMethodException nsme2){	// if not, return something
//				if (this.first_dimension.equals(entry.getFirstDimension()))
//					return 0;
//				return -1;
//			}
//		}		
//		
//		try {
//			return ((Integer)m.invoke(this.first_dimension, new Object[]{entry.getFirstDimension()})).intValue();
//		} catch (Exception e){	// if not possible, return something
//			if (this.first_dimension.equals(entry.getFirstDimension()))
//				return 0;
//			return -1;
//		}
//	}
//
//	public int compareToSecondDimension(MyEntry<Entry,FirstD,SecondD> entry) {
//		boolean b = false;
//		
//		Class c = this.second_dimension.getClass();
//		Method m = null;
//		try{	// test if compareTo uses MySet as parameter
//			m = c.getMethod("compareTo", new Class<?>[]{MySet.class});
//		} catch (NoSuchMethodException nsme){
//			try{	// if not, test if compareTo uses MyMultiset as parameter
//				m = c.getMethod("compareTo", new Class<?>[]{MyMultiset.class});
//			} catch (NoSuchMethodException nsme2){	// if not, return something
//				if (this.second_dimension.equals(entry.getSecondDimension()))
//					return 0;
//				return -1;
//			}
//		}		
//		
//		try {
//			return ((Integer)m.invoke(this.second_dimension, new Object[]{entry.getSecondDimension()})).intValue();
//		} catch (Exception e){	// if not possible, return something
//			if (this.second_dimension.equals(entry.getSecondDimension()))
//				return 0;
//			return -1;
//		}
//	}
	
	public boolean equals(Object o) {
		return this.compareTo((MyEntry<Entry,FirstD,SecondD>) o) == 0;
	}
	
	//-------------------------------------------------------------------------------
	//						methods that have to be implemented
	//-------------------------------------------------------------------------------
	
	public Boolean isZero(){
		return new Boolean(false);
	}
	
	public MyEntry<Entry,FirstD,SecondD> getZero(){
		return null;
	}
	
	public Boolean isOne(){
		return new Boolean(false);
	}
	
	public MyEntry<Entry,FirstD,SecondD> getOne(){
		return null;
	}

	public Boolean isNumber(){
		return new Boolean(false);
	}
	
	public MyEntry<Entry,FirstD,SecondD> clone(){
		return new MyEntry<Entry,FirstD,SecondD>(this.getEntry(), this.getFirstDimension(), this.getSecondDimension());
	}
	
	public MyEntry<Entry,SecondD,FirstD> transpose(){
		return new MyEntry<Entry,SecondD,FirstD>(this.getEntry(), this.getSecondDimension(), this.getFirstDimension());
	}
	
//	abstract public MyEntry<?,FirstD,?> mul(MyEntry<?,SecondD,?> factor);
	
//	abstract public Object div(Object divisor);
	
//	abstract public MyEntry<?,FirstD,SecondD> add(MyEntry<?,FirstD,SecondD> summand);
	
//	abstract public MyEntry<?,FirstD,SecondD> sub(MyEntry<?,FirstD,SecondD> subtrahend);

//	abstract public MyEntry<?,FirstD,SecondD> round();
}
