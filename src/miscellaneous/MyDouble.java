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

public class MyDouble<FirstD,SecondD> extends MyEntry<Double,FirstD,SecondD>{
	public MyDouble(){
	}
	
	public MyDouble(Double doubl, FirstD fd, SecondD sd){
		super(doubl, fd, sd);
	}
	
	public Double getDouble(){
		return this.getEntry();
	}
	
	//--------------------------------------------------------------------
	
	public Boolean isNumber(){
		return new Boolean(true);
	}
	
	public Boolean isZero(){
		return new Boolean(this.getDouble() == 0);
	}
	
	public MyDouble<FirstD,SecondD> getZero(){
		return new MyDouble<FirstD,SecondD>(new Double(0),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public Boolean isOne(){
		return new Boolean(this.getDouble() == 1);
	}
	
	public MyDouble<FirstD,SecondD> getOne(){
		return new MyDouble<FirstD,SecondD>(new Double(1),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyInteger<FirstD,SecondD> round(){
		return new MyInteger<FirstD,SecondD>(new Integer(this.getDouble().intValue()),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyDouble<FirstD,SecondD> clone(){
		MyDouble<FirstD,SecondD> ret = new MyDouble<FirstD,SecondD>(this.getEntry(),this.getFirstDimension(),this.getSecondDimension());
		ret.setComparator(this.getComparator());
		
		return ret;
	}
	
	public MyDouble<SecondD,FirstD> transpose(){
		MyDouble<SecondD,FirstD> ret = new MyDouble<SecondD,FirstD>(this.getEntry(),this.getSecondDimension(),this.getFirstDimension());
		ret.setComparator(this.getComparator());
		
		return ret;
	}
	
	//--------------------------------------------------------------------
	//						MyDouble x MyInteger
	//--------------------------------------------------------------------
	
	public MyDouble<FirstD,?> mul(MyInteger<SecondD,?> factor){
		return new MyDouble(this.getDouble()*factor.getInteger(),this.getFirstDimension(),factor.getSecondDimension());
	}

	public MyDouble<FirstD,SecondD> add(MyInteger<FirstD,SecondD> summand){
		return new MyDouble<FirstD,SecondD>(this.getDouble() + summand.getInteger(),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyDouble<FirstD,SecondD> sub(MyInteger<FirstD,SecondD> subtrahend){
		return new MyDouble<FirstD,SecondD>(this.getDouble() - subtrahend.getInteger(),this.getFirstDimension(),this.getSecondDimension());
	}
	
	//--------------------------------------------------------------------
	//						MyInteger x MyDouble
	//--------------------------------------------------------------------
	
	public MyDouble<FirstD,?> mul(MyDouble<SecondD,?> factor){
		return new MyDouble(this.getDouble()*factor.getDouble(),this.getFirstDimension(),factor.getSecondDimension());
	}

	public MyDouble<FirstD,SecondD> add(MyDouble<FirstD,SecondD> summand){
		return new MyDouble<FirstD,SecondD>(this.getDouble() + summand.getDouble(),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyDouble<FirstD,SecondD> sub(MyDouble<FirstD,SecondD> subtrahend){
		return new MyDouble<FirstD,SecondD>(this.getDouble() - subtrahend.getDouble(),this.getFirstDimension(),this.getSecondDimension());
	}
}
