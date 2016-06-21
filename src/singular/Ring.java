/* CRNToolkit, Copyright (c) 2010-2016 Jost Neigenfind  <jostie@gmx.de>,
 * Sergio Grimbs, Zoran Nikoloski
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

package singular;

public class Ring {
	private String name;
	private String field;
	private String ordering;
	private String[] variables;
	
	public Ring(){
	}
	
	public Ring(String name, String field, String ordering, String[] variables){
		this.name = name;
		this.field = field;
		this.ordering = ordering;
		this.variables = variables;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setField(String field){
		this.field = field;
	}
	
	public void setOrdering(String ordering){
		this.ordering = ordering;
	}
	
	public void setVariables(String[] variables){
		this.variables = variables;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getField(){
		return this.field;
	}
	
	public String getOrdering(){
		return this.ordering;
	}
	
	public String[] getVariables(){
		return this.variables;
	}
	
	public String getSingularString(){
		String vars = "";
		for (int i = 0; i < variables.length; i++)
			vars = vars + variables[i] + ", ";
		vars = vars.substring(0, vars.length() - 2);
		
		return "ring " + this.name + " = " + this.field + ", (" + vars + ")," + this.ordering + ";\n"; 
	}
}
