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

public class Ideal {
	private String name;
	private Ring ring;
	private ArrayList<Polynomial> polynomials;
	
	public Ideal(String name){
		this.name = name;
		this.polynomials = new ArrayList<Polynomial>();		
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setRing(Ring ring){
		this.ring = ring;
	}
	
	public void addPoylnomial(Polynomial polynomial){
		this.polynomials.add(polynomial);
	}
	
	public String getName(){
		return this.name;
	}
	
	public Ring getRing(){
		return this.ring;
	}
	
	public String toReadableString(){
		String ret = "";
		for (int i = 0; i < this.polynomials.size(); i++){
			Polynomial polynomial = this.polynomials.get(i);
			
			ret = ret + "d" + polynomial.getName() + "_dt = ";
			ret = ret + polynomial.toString() + ";\n";
		}
		
		return ret;
	}
	
	public String toLaTeXString(){
		String ret = "\\begin{array}{h}{ccr";
		for (int i = 0; i < this.polynomials.size(); i++)
			ret = ret + "lc";
		ret = ret + "}\n\t";
		
		for (int i = 0; i < this.polynomials.size(); i++){
			Polynomial polynomial = this.polynomials.get(i);
			
			ret = ret + "\\mydiff{c_{\\text{" + polynomial.getName() + "}}} & = & ";
			ret = ret + polynomial.toLaTeXString() + "\\\\ \n\t";
		}
		ret = ret.substring(0, ret.length() - 1);
		ret = ret + "\\end{array}";
		
		return ret;
	}	
	public String toSingularString(){
		HashMap<String, String> hm = new HashMap<String, String>();
		for (int i = 0; i < this.polynomials.size(); i++){
			Polynomial polynomial = this.polynomials.get(i);
			ArrayList<Monomial> monomial_array = polynomial.getMonomials();
			for (int j = 0; j < monomial_array.size(); j++){
				Monomial monomial = monomial_array.get(j);
				ArrayList<Variable> variable_array = monomial.getVariables();
				for (int k = 0; k < variable_array.size(); k++){
					Variable variable = variable_array.get(k);
					hm.put(variable.getName(), variable.getName());
				}
			}
		}
		
		Object[] key_array = hm.keySet().toArray();
		Arrays.sort(key_array);
		
		String variable_list = "(";
		for (int i = 0; i < key_array.length; i++)
			variable_list = variable_list + key_array[i] + ", ";
		variable_list = variable_list.substring(0, variable_list.length() - 2) + ")";
		
		String ret = "ring r = 0, " + variable_list + ", lp;\n";
		for (int i = 0; i < this.polynomials.size(); i++){
			Polynomial polynomial = this.polynomials.get(i);
			
			ret = ret + "poly p" + polynomial.getName() + " = ";
			ret = ret + polynomial.toString() + ";\n";
		}
		
		String polynomial_list = "";
		for (int i = 0; i < this.polynomials.size(); i++){
			Polynomial polynomial = this.polynomials.get(i);
			
			polynomial_list = polynomial_list + "p" + polynomial.getName() + ", ";
		}
		polynomial_list = polynomial_list.substring(0, polynomial_list.length() - 2);
		
		ret = ret + "ideal " + this.getName() + " = " + polynomial_list + ";\n";
		
		return ret;
	}
//	
//	public static String getSingularGetSubIdeal(Ideal[] old_ideals, Ideal new_ideal, String variable){
//		String ret = "LIB \"/home/jostie/workspace/Singular/getSubIdeal.lib\";\nideal " + new_ideal.getName() + " = ";
//		for (int i = 0; i < old_ideals.length; i++)
//			ret = ret + "getSubIdeal(" + old_ideals[i].getName() + ", " + variable + ") + ";
//		ret = ret.substring(0, ret.length() - 2) + ";\n";
//		
//		return ret;
//	}
//	
//	public static String getSingularImapString(Ideal old_ideal, Ideal new_ideal){
//		return "ideal " + new_ideal.getName() + " = imap(" + old_ideal.getRing().getName() + ", " + old_ideal.getName() + ");\n";
//	}
//	
//	public static String getSingularGroebnerString(Ideal old_ideal, Ideal new_ideal, boolean verbose){
//		if (verbose)
//			return "option(redSB);\n" + "option(prot);\n" + "ideal " + new_ideal.getName() + " = groebner(" + old_ideal.getName() + ", \"slimgb\");\n";
//		return "option(redSB);\n" + "ideal " + new_ideal.getName() + " = groebner(" + old_ideal.getName() + ", \"slimgb\");\n";
//	}
//	
//	public Ideal groebner(String name){
//		Ideal ideal = new Ideal();
//		
//		Polynomial[] p = null;
//		if (this.polynomials != null){
//			p = new Polynomial[this.polynomials.length];
//			for (int i = 0; i < this.polynomials.length; i++)
//				p[i] = this.polynomials[i];
//		}
//		
//		ideal.setName(name);
//		ideal.setRing(this.ring);
//		ideal.setPoylnomials(p);
//		
//		return ideal;
//	}
//
//	public Ideal changeRing(String name, Ring ring_){
//		Ideal ideal = new Ideal();
//		
//		Polynomial[] p = null;
//		if (this.polynomials != null){
//			p = new Polynomial[this.polynomials.length];
//			for (int i = 0; i < this.polynomials.length; i++)
//				p[i] = this.polynomials[i];
//		}
//		
//		ideal.setName(name);
//		ideal.setRing(ring_);
//		ideal.setPoylnomials(p);
//		
//		return ideal;
//	}
}
