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

public class Monomial {
	private ArrayList<Variable> variables;
	private int coefficient;
	
	public Monomial(){
		this.variables = new ArrayList<Variable>();
	}
	
	public void addVariable(Variable variable){
		this.variables.add(variable);
	}
	
	public void addVariable(int index, Variable variable){
		this.variables.add(index, variable);
	}	
	
	public void setCoefficient(int coefficient){
		this.coefficient = coefficient;
	}
	
	public Variable getVariable(int i){
		return this.variables.get(i);
	}
	
	public int getCoefficient(){
		return this.coefficient;
	}
	
	public int getPower(String name){
		for (int i = 0; i < this.variables.size(); i++){
			Variable variable = this.variables.get(i);
			if (variable.getName().equals(name))
				return variable.getPower();
		}
		
		return -1;
	}
	
//	public void mul(int c){
//		this.coefficient = this.coefficient*c;
//	}
	
	public String toString(){
		String ret = this.coefficient + "";
		for (int i = 0; i < this.variables.size(); i++)
			ret = ret + "*" + this.variables.get(i).toString();

		return ret;
	}
	
	public String toLaTeXString(){
		String ret = "";
		if (this.coefficient*this.coefficient != 1)
			ret = this.coefficient + "";
		if (this.coefficient == -1)
			ret = "-";
		
		for (int i = 0; i < this.variables.size(); i++)
			ret = ret + this.variables.get(i).toLatexString();

		return ret;
	}
	
	public int size(){
		return this.variables.size();
	}
	
	public Monomial clone(){
		Monomial ret = new Monomial();
		
		ret.setCoefficient(this.coefficient);
		for (int i = 0; i < this.variables.size(); i++)
			ret.addVariable(this.variables.get(i));
		
		return ret;
	}
	
	public ArrayList<Variable> getVariables(){
		return this.variables;
	}
}

class MonomialComparator implements Comparator<Monomial>{
	public int compare(Monomial m1, Monomial m2) {
		return -m1.toString().compareTo(m2.toString());
	}
}
