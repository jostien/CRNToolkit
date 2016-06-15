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

public class Variable {
	private String name;
	private int    power;
	private boolean is_rate_constant;
	
	public Variable(String name, int power){
		this.name = name;
		this.power = power;
		this.is_rate_constant = false;
	}

	public Variable(String name, int power, boolean is_rate_constant){
		this.name = name;
		this.power = power;
		this.is_rate_constant = is_rate_constant;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getPower(){
		return this.power;
	}
	
	public String toString(){
		String var = "c_";
		if (this.is_rate_constant)
			var = "k_";
		if (this.power == 0)
			return "1";
		else if (this.power == 1)
			return var + this.name;
		return var + this.name + "^" + this.power;
	}
	
	public String toLatexString(){
		String var_start = "c_{\\text{";
		String var_end = "}}";
		
		if (this.is_rate_constant){
			var_start = "k_{";
			var_end = "}";
		}
		if (this.power == 0)
			return "1";
		else if (this.power == 1)
			return var_start + this.name + var_end;
		return var_start + this.name  + var_end + "^" + this.power;
	}
	
	public boolean isRateConstant(){
		return this.is_rate_constant;
	}
}
