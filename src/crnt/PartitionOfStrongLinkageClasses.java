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

public class PartitionOfStrongLinkageClasses extends Partition<Complex>{
	/**
	 * standard serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor. This class is a partition of complexes.
	 * 
	 * @throws Exception
	 */
	public PartitionOfStrongLinkageClasses(){
		super();
	}
	
	/**
	 * Add strong linkage class. The same as adding an equivalence class.
	 * 
	 * @param strong_linkage_class The strong linkage class to add.
	 * @throws Exception Throws an exception if strong linkage classes are not mutually exclusive.
	 */
	public void addStrongLinkageClass(StrongLinkageClass strong_linkage_class) throws Exception{
		super.addEquivalenceClass(strong_linkage_class);
	}			
	
	/**
	 * Gets MySet of strong linkage classes. Must loop over equivalence classes
	 * for casting them to class StrongLinkageClass.
	 * 
	 * @return MySet of strong linkage classes.
	 */
	public MySet<StrongLinkageClass> getStrongLinkageClasses(){
		MySet<StrongLinkageClass> ret = new MySet<StrongLinkageClass>();
		Iterator<EquivalenceClass<Complex>> iter = this.iterator();
		while (iter.hasNext())
			ret.add((StrongLinkageClass)iter.next());
			
		return ret;
	}
	
	/**
	 * Gets the strong linkage class of a given complex.
	 * 
	 * @param complex The complex whose strong linkage class is wanted.
	 * @return Strong linkage class for given complex.
	 */
	public StrongLinkageClass getStrongLinkageClassByComplex(Complex complex){
		return (StrongLinkageClass)super.getEquivalenceClassByElement(complex);
	}
}
