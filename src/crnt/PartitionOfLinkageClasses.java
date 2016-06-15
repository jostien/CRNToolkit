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

package crnt;

import java.util.Iterator;

import miscellaneous.MySet;

public class PartitionOfLinkageClasses extends Partition<Complex>{
	private static final long serialVersionUID = 1L;

	// this should override the corresponding attribute from the class Partition
	private MySet<LinkageClass> equivalenceClasses;

	public PartitionOfLinkageClasses(){
		this.equivalenceClasses = new MySet<LinkageClass>();		
	}
	
	public void addLinkageClass(LinkageClass newLinkageClass) throws Exception{
		MySet<Complex> intersection = newLinkageClass.intersection(this.basicSet);
		if (intersection.isEmpty()){
			this.equivalenceClasses.add(newLinkageClass);
			super.addEquivalenceClass(newLinkageClass);
			// without this super call the attribute equivalenceClass would be empty
			// if you access an instance of PartitionOfLinkageClass as a Partition
			this.basicSet = this.basicSet.union(newLinkageClass);
		}
		else{			
			throw new Exception("tritratrullallalla");
		}				
	}
	
	public void addEquivalenceClass(LinkageClass newEquivalenceClass) throws Exception{
		this.addLinkageClass(newEquivalenceClass);
	}			
			
	
	public MySet<LinkageClass> getLinkageClasses(){
		return this.equivalenceClasses;
	}

	public int size(){
		return equivalenceClasses.size();
	}
	
	public LinkageClass getLinkageClassFromComplex(Complex member){
		LinkageClass result = null;
		Iterator<LinkageClass> iter = this.equivalenceClasses.iterator();
		while (iter.hasNext()){
			LinkageClass currentClass = iter.next();
			if (currentClass.contains(member)){
				result = currentClass;
			}
		}		
		return result;
	}
}
