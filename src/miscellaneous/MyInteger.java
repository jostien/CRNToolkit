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

public class MyInteger<FirstD,SecondD> extends MyEntry<Integer,FirstD,SecondD>{
	public MyInteger(){
	}
	
	public MyInteger(Integer integer, FirstD fd, SecondD sd){
		super(integer, fd, sd);
	}
	
	public Integer getInteger(){
		return this.getEntry();
	}
	
	//--------------------------------------------------------------------
	
	public Boolean isNumber(){
		return new Boolean(true);
	}
	
	public Boolean isZero(){
		return new Boolean(this.getInteger() == 0);
	}
	
	public MyInteger<FirstD,SecondD> getZero(){
		return new MyInteger<FirstD,SecondD>(new Integer(0),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public Boolean isOne(){
		return new Boolean(this.getInteger() == 1);
	}
	
	public MyInteger<FirstD,SecondD> getOne(){
		return new MyInteger<FirstD,SecondD>(new Integer(1),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyInteger<FirstD,SecondD> round(){
		return this.clone();
	}
	
	public MyInteger<FirstD,SecondD> clone(){
		MyInteger<FirstD,SecondD> ret = new MyInteger<FirstD,SecondD>(this.getEntry(),this.getFirstDimension(),this.getSecondDimension());
		ret.setComparator(this.getComparator());
		
		return ret;
	}
	
	public MyInteger<SecondD,FirstD> transpose(){
		MyInteger<SecondD,FirstD> ret = new MyInteger<SecondD,FirstD>(this.getEntry(),this.getSecondDimension(),this.getFirstDimension());
		ret.setComparator(this.getComparator());
		
		return ret;
	}
	
	//--------------------------------------------------------------------
	//						MyInteger x MyInteger
	//--------------------------------------------------------------------
	
	public MyInteger<FirstD,?> mul(MyInteger<SecondD,?> factor){
		return new MyInteger(this.getInteger()*factor.getInteger(),this.getFirstDimension(),factor.getSecondDimension());
	}

	public MyInteger<FirstD,SecondD> add(MyInteger<FirstD,SecondD> summand){
		return new MyInteger<FirstD,SecondD>(this.getInteger() + summand.getInteger(),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyInteger<FirstD,SecondD> sub(MyInteger<FirstD,SecondD> subtrahend){
		return new MyInteger<FirstD,SecondD>(this.getInteger() - subtrahend.getInteger(),this.getFirstDimension(),this.getSecondDimension());
	}
	
	//--------------------------------------------------------------------
	//						MyInteger x MyDouble
	//--------------------------------------------------------------------
	
	public MyDouble<FirstD,?> mul(MyDouble<SecondD,?> factor){
		return new MyDouble(this.getInteger()*factor.getDouble(),this.getFirstDimension(),factor.getSecondDimension());
	}

	public MyDouble<FirstD,SecondD> add(MyDouble<FirstD,SecondD> summand){
		return new MyDouble<FirstD,SecondD>(this.getInteger() + summand.getDouble(),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyDouble<FirstD,SecondD> sub(MyDouble<FirstD,SecondD> subtrahend){
		return new MyDouble<FirstD,SecondD>(this.getInteger() - subtrahend.getDouble(),this.getFirstDimension(),this.getSecondDimension());
	}
}
