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

import java.util.*;

public class MyVector<Entry,FirstD,SecondD> implements Comparable<MyVector<Entry,FirstD,SecondD>>{

	private static final long serialVersionUID = 1L;

	private MySet<MyEntry<Entry,FirstD,SecondD>> data;
	private int comparator = 0;	
	
	public MyVector(){
		this.data = new MySet<MyEntry<Entry,FirstD,SecondD>>();
	}
	
	public MyVector<Entry,FirstD,SecondD> setComparatorToFirstDimension(){
		this.comparator = 0;
		
		return this;
	}
	
	public MyVector<Entry,FirstD,SecondD> setComparatorToSecondDimension(){
		this.comparator = 1;
		
		return this;
	}
	
	public boolean isComparatorToFirstDimension(){
		return this.comparator == 0;
	}
	
	public boolean isComparatorToSecondDimnension(){
		return this.comparator == 1;
	}
	
	public void add(MyEntry<Entry,FirstD,SecondD> entry){
		this.data.add(entry);
	}
	
	public FirstD getFirstDimension(){
		return this.data.head().getFirstDimension();
	}
	
	public SecondD getSecondDimension(){
		return this.data.head().getSecondDimension();
	}
	
	public MyEntry<Entry,FirstD,SecondD> getEntry(int i){
		return this.data.toArrayList().get(i);
	}
	
	public int compareTo(MyVector<Entry,FirstD,SecondD> vector){
		if (this.isComparatorToFirstDimension())
			return this.compareToFirstDimension(vector);
		if (this.isComparatorToSecondDimnension())
			return this.compareToSecondDimension(vector);
		return -1;
	}
	
	public int compareToFirstDimension(MyVector<Entry,FirstD,SecondD> vector){
		return this.getEntry(0).getFirstDimension().toString().compareTo(vector.getEntry(0).getFirstDimension().toString());
	}
	
	public int compareToSecondDimension(MyVector<Entry,FirstD,SecondD> vector){
		return this.getEntry(0).getSecondDimension().toString().compareTo(vector.getEntry(0).getSecondDimension().toString());
	}
	
	public boolean equals(Object o) {
		return this.compareTo((MyVector<Entry,FirstD,SecondD>) o) == 0;
	}

	public int size(){
		return this.data.size();
	}
	
	public ArrayList<MyEntry<Entry,FirstD,SecondD>> toArrayList(){
		return this.data.toArrayList();
	}
	
	public Iterator<MyEntry<Entry,FirstD,SecondD>> iterator(){
		return this.data.iterator();
	}
	
	public String toString(){
		String ret = "";
		ArrayList<MyEntry<Entry,FirstD,SecondD>> list = this.data.toArrayList();
		for (int i = 0; i < list.size(); i++){
			ret = ret + list.get(i).getEntry().toString() + " ";
		}
		
		return ret;
	}
}