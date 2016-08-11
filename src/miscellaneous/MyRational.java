/* CRNToolkit, Copyright (c) 2010-2016 Jost Neigenfind  <jostie@gmx.de>
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

public class MyRational<FirstD,SecondD> extends MyEntry<MyRationalNumber,FirstD,SecondD>{
	public MyRational(){
	}
	
	public MyRational(MyRationalNumber ratio, FirstD fd, SecondD sd){
		super(ratio, fd, sd);
	}
	
	public MyRationalNumber getMyRationalNumber(){
		return this.getEntry();
	}
	
	//--------------------------------------------------------------------
	
	public Boolean isNumber(){
		return new Boolean(true);
	}
	
	public Boolean isZero(){
		return new Boolean(this.getMyRationalNumber().isZero());
	}
	
	public MyRational<FirstD,SecondD> getZero(){
		return new MyRational<FirstD,SecondD>(new MyRationalNumber(0),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public Boolean isOne(){
		return new Boolean(this.getMyRationalNumber().isOne());
	}
	
	public MyRational<FirstD,SecondD> getOne(){
		return new MyRational<FirstD,SecondD>(new MyRationalNumber(1),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyInteger<FirstD,SecondD> floor(){
		return new MyInteger<FirstD,SecondD>(new Integer(new Double(this.getMyRationalNumber().getRatio()).intValue()),this.getFirstDimension(),this.getSecondDimension());
	}
	
	public MyRational<FirstD,SecondD> clone(){
		MyRational<FirstD,SecondD> ret = new MyRational<FirstD,SecondD>(this.getMyRationalNumber(),this.getFirstDimension(),this.getSecondDimension());
		ret.setComparator(this.getComparator());
		
		return ret;
	}
	
	public MyRational<SecondD,FirstD> transpose(){
		MyRational<SecondD,FirstD> ret = new MyRational<SecondD,FirstD>(this.getEntry(),this.getSecondDimension(),this.getFirstDimension());
		ret.setComparator(this.getComparator());
		
		return ret;
	}
	
	//--------------------------------------------------------------------
	//						MyRationalNum x MyInteger
	//--------------------------------------------------------------------
	
	public MyRational<FirstD,?> mul(MyInteger<SecondD,?> factor){
		MyRationalNumber new_ratio = new MyRationalNumber(factor.getInteger());
		
		return new MyRational(this.getMyRationalNumber().clone().mul(new_ratio),this.getFirstDimension(),factor.getSecondDimension());
	}

	public MyRational<FirstD,SecondD> add(MyInteger<FirstD,SecondD> summand){
		MyRationalNumber new_ratio = new MyRationalNumber(summand.getInteger());
		
		return new MyRational<FirstD,SecondD>(this.getMyRationalNumber().clone().add(new_ratio),this.getFirstDimension(),this.getSecondDimension());
	}
	
	//--------------------------------------------------------------------
	//						MyRationalNum x MyRationalNum
	//--------------------------------------------------------------------
	
	public MyRational<FirstD,?> mul(MyRational<SecondD,?> factor){
		return new MyRational(this.getMyRationalNumber().clone().mul(factor.getMyRationalNumber()),this.getFirstDimension(),factor.getSecondDimension());
	}

	public MyRational<FirstD,SecondD> add(MyRational<FirstD,SecondD> summand){
		return new MyRational<FirstD,SecondD>(this.getMyRationalNumber().clone().add(summand.getMyRationalNumber()),this.getFirstDimension(),this.getSecondDimension());
	}
	
	//--------------------------------------------------------------------
	//						MyRationalNum x MyDouble
	//--------------------------------------------------------------------
	
	public MyDouble<FirstD,?> mul(MyDouble<SecondD,?> factor){
		return new MyDouble(this.getMyRationalNumber().getRatio()*factor.getDouble(),this.getFirstDimension(),factor.getSecondDimension());
	}

	public MyDouble<FirstD,SecondD> add(MyDouble<FirstD,SecondD> summand){
		return new MyDouble<FirstD,SecondD>(this.getMyRationalNumber().getRatio() + summand.getDouble(),this.getFirstDimension(),this.getSecondDimension());
	}
}
