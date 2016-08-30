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

package crnt;

public class Species implements Comparable<Object>{
	private String id;
	private String name;
	private String compartment;
	
	public Species(String id) throws Exception{
		this.id = id;
		this.name = "";
		this.compartment = "default";
		
		this.checkId();
	}
	
	public Species(String id, String name) throws Exception{
		this.id = id;
		this.name = name;
		this.compartment = "default";
		
		this.checkId();
	}

	public Species(String id, String name, String compartment) throws Exception{
		this.id = id;
		this.name = name;
		this.compartment = compartment;
		
		this.checkId();
	}
	
	public void setId(String id) throws Exception{
		this.id = id;
		
		this.checkId();
	}
	
	public void checkId() throws Exception{
		if (this.id == null || this.id.length() == 0)
			throw new Exception("No valid species identifier.");
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setCompartment(String compartment) throws Exception{
		if (compartment == null || compartment.length() == 0)
			throw new Exception("No valid compartment identifier.");
		this.compartment = compartment;
	}
	
	public String getCompartment(){
		return this.compartment;
	}
	
	public String toString(){
		return this.id;
	}
	
	public String toLaTeXString(){
		return "\\text{" + this.toString() + "}";
	}
	
	public int compareTo(Object o){
		return this.toString().compareTo(((Species)o).getId());
	}
		
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	public boolean equals(Object o){
		return this.toString().equals(((Species)o).getId());
	}
	
	public Species clone(){
		try{
			return new Species(this.id, this.name, this.compartment);
		} catch(Exception e){
			return null;
		}
	}
	
	public static String ID_PREFIX = "S_";
}
