package math.linalg;

import java.util.ArrayList;
import java.util.Iterator;

import math.field.MyDouble;
import math.field.MyEntry;
import math.set.MySet;

public class MyVector<FirstD,SecondD> extends MySet<MyEntry<?,FirstD,SecondD>>{
	private static final long serialVersionUID = 1L;

	private int comparator = 0;
	
	public static void main(String[] args) throws Exception{
		MyDouble<String,String> val0 = new MyDouble<String,String>(1.0, "0", "0");
		MyDouble<String,String> val1 = new MyDouble<String,String>(1.0, "1", "0");
		MyDouble<String,String> val2 = new MyDouble<String,String>(1.0, "2", "0");
		
		MyVector<String,String> vec = new MyVector<String,String>();
		vec.add(val0);
		vec.addElement(val1);
		vec.addElement(val2);
		
		System.out.println(vec.toString());
	}
	
	public MyVector(){
		super();
	}
	
	public FirstD getFirstDimension(){
		return this.head().getFirstDimension();
	}
	
	public SecondD getSecondDimension(){
		return (SecondD)this.head().getSecondDimension();
	}

	public MyVector<FirstD,SecondD> setComparatorToFirstDimension(){
		this.comparator = 0;
		
		return this;
	}
	
	public MyVector<FirstD,SecondD> setComparatorToSecondDimension(){
		this.comparator = 1;
		
		return this;
	}
	
	public boolean isComparatorToFirstDimension(){
		return this.comparator == 0;
	}
	
	public boolean isComparatorToSecondDimnension(){
		return this.comparator == 1;
	}
	
	public MyEntry<?,FirstD,SecondD> getEntry(int i){
		return this.toArrayList().get(i);
	}
	
	public int compareTo(MyVector<FirstD,SecondD> vector){
		if (this.isComparatorToFirstDimension())
			return this.compareToFirstDimension(vector);
		if (this.isComparatorToSecondDimnension())
			return this.compareToSecondDimension(vector);
		return -1;
	}
	
	public int compareToFirstDimension(MyVector<FirstD,SecondD> vector){
		return this.getEntry(0).getFirstDimension().toString().compareTo(vector.getEntry(0).getFirstDimension().toString());
	}
	
	public int compareToSecondDimension(MyVector<FirstD,SecondD> vector){
		return this.getEntry(0).getSecondDimension().toString().compareTo(vector.getEntry(0).getSecondDimension().toString());
	}
	
	public boolean equals(Object o) {
		return this.compareTo((MyVector<FirstD,SecondD>)o) == 0;
	}
	
	public String toString(){
		String ret = "";
		Iterator<MyEntry<?,FirstD,SecondD>> iterator = this.iterator();
		boolean b = false;
		while(iterator.hasNext()){
			MyEntry<?,FirstD,SecondD> entry = iterator.next();
			if (!b){
				ret = ret + entry.getSecondDimension() + "\n";
				b = true;
			}
			ret = ret + entry.getEntry() + " " + entry.getFirstDimension() + "\n";
		}
		return ret.substring(0, ret.length() - 1);
	}
}
