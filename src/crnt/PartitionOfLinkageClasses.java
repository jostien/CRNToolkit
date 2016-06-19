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
	/**
	 * standard serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor. This class is a partition of complexes.
	 * 
	 * @throws Exception
	 */
	public PartitionOfLinkageClasses() throws Exception{
		super();
	}
	
	/**
	 * Add linkage class. The same as adding an equivalence class.
	 * 
	 * @param linkage_class The linkage class to add.
	 * @throws Exception Throws an exception if linkage classes are not mutually exclusive.
	 */
	public void addLinkageClass(LinkageClass linkage_class) throws Exception{
		super.addEquivalenceClass(linkage_class);
	}
	
	/**
	 * Gets MySet of linkage classes. Must loop over equivalence classes for casting
	 * them to class linkage class.
	 * 
	 * @return MySet of linkage classes.
	 */
	public MySet<LinkageClass> getLinkageClasses(){
		MySet<LinkageClass> ret = new MySet<LinkageClass>();
		Iterator<EquivalenceClass<Complex>> iter = this.iterator();
		while (iter.hasNext())
			ret.add((LinkageClass)iter.next());
			
		return ret;
	}
	
	/**
	 * Gets the linkage class of a given complex.
	 * 
	 * @param complex The complex whose linkage class is wanted.
	 * @return Linkage class for given complex.
	 */
	public LinkageClass getLinkageClassByComplex(Complex complex){
		return (LinkageClass)super.getEquivalenceClassByElement(complex);
	}
}
