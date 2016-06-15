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

package singular;

import java.util.*;

public class Polynomial {
	private ArrayList<Monomial> monomials;
	private String name;
	private String polynomial;
	
	public Polynomial(String name){
		this.name = name;
		
		this.monomials = new ArrayList<Monomial>();
	}
	
	public void addMonomial(Monomial monomial){
		this.monomials.add(monomial);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getPolynomial(){
		return this.polynomial;
	}
	
	public String getSingularString(){
		return "poly " + this.name + " = " + this.polynomial + ";\n";
	}
	
	public Polynomial sort(){
		Monomial[] array = new Monomial[this.monomials.size()];
		for (int i = 0; i < this.monomials.size(); i++)
			array[i] = this.monomials.get(i);
		
		Arrays.sort(array, new MonomialComparator());
		
		this.monomials = new ArrayList<Monomial>();
		for (int i = 0; i < array.length; i++)
			this.monomials.add(array[i]);
		
		return this;
	}
	
	public String toString(){
		String ret = "";
		for (int i = 0; i < this.monomials.size(); i++){
			Monomial monomial = this.monomials.get(i);

			String sign = "+";
			String monomial_string = monomial.toString();
			if (monomial.getCoefficient() < 0){
				sign = "-";
				monomial_string = monomial_string.substring(1, monomial_string.length());
			}
			
			if (i == 0 && monomial.getCoefficient() < 0)
				ret = ret + sign + monomial_string;
			else if (i == 0 && monomial.getCoefficient() > 0)
				ret = ret + monomial_string;
			else
				ret = ret + " " + sign + " " + monomial_string;
		}
		
		return ret;
	}
	
	public String toLaTeXString(){
		String ret = "";
		for (int i = 0; i < this.monomials.size(); i++){
			Monomial monomial = this.monomials.get(i);

			String sign = "+";
			String monomial_string = monomial.toLaTeXString();
			if (monomial.getCoefficient() < 0){
				sign = "-";
				monomial_string = monomial_string.substring(1, monomial_string.length());
			}
			
			if (i == 0 && monomial.getCoefficient() < 0)
				ret = ret + sign + " & " + monomial_string;
			else if (i == 0 && monomial.getCoefficient() > 0)
				ret = ret + " & " + monomial_string;
			else
				ret = ret + " & " + sign + " & " + monomial_string;
		}
		
		return ret;
	}
	
	public ArrayList<Monomial> getMonomials(){
		return this.monomials;
	}
	
//	public String toLatexString(){
//		String ret = "";
//		for (int i = 0; i < this.monomials.size(); i++){
//			Monomial monomial = this.monomials.get(i);
//
//			if (monomial.getCoefficient() < 0)
//				ret = ret +  " " + monomial.toLatexString();
//			else
//				ret = ret + " +" + monomial.toLatexString();
//		}
//		
//		ret = ret.replaceAll("-1", "-");
//		ret = ret.replaceAll("-", "- ");
//		ret = ret.replaceAll("\\+1", "+");
//		ret = ret.replaceAll("\\+", "+ ");
//		ret = ret.replaceAll("\\+", "& + &");
//		ret = ret.replaceAll("-", "& - &");
//		ret = ret.replaceFirst("& \\+ ", "");
////		
////		if (ret.charAt(1) == '+')
////			ret = ret.substring(2, ret.length());
////		
////		if (ret.charAt(1) == '-'){
////			ret = ret.substring(2, ret.length());
////			ret = "-" + ret;
////		}
//		
//		return ret;
//	}
}
