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

import java.util.*;
import miscellaneous.*;

/**
 * 
 * @author neigenfind
 *
 * The class StrogLinkageClass.
 * This class implements the mathematical structure strong linkage class and extends the class MySet.
 */
public class StrongLinkageClass extends EquivalenceClass<Complex>{
	/**
	 * standard serial version UID
	 */	
	private static final long serialVersionUID = 1L;
	
	private MySet<Reaction> reactions;	// the set of reactions of the linkage class this strong linkage class in contained in or the reactions of the whole reaction network
	
	/**
	 * The constructor.
	 * 
	 * @param reactions The set of reactions of the linkage class this strong linkage class in contained in or the reactions of the whole reaction network.
	 */
	public StrongLinkageClass(MySet<Reaction> reactions){
		this.reactions = reactions;
	}
	
	/**
	 * Computes whether this strong linkage class is terminal.
	 * 
	 * @return True if this strong linkage class is terminal, otherwise false.
	 */
	public boolean isTerminal(){
		Iterator<Complex> iterator = this.iterator();	// get the iterator of all complexes of this strong linkage class
		while (iterator.hasNext()){						// loop over all complexes of this strong linkage class
			Complex complex = iterator.next();
			MySet<Complex> neighbours = Reaction.getComplexNeighboursForward(complex, this.reactions);	// get all neighbouring complexes of the current complex in the direction of the edges adjacent to the current complex
			
			Iterator<Complex> neighbour_iterator = neighbours.iterator();	// get the iterator of the neighbouring complexes
			while (neighbour_iterator.hasNext()){							// loop over all neighbouring complexes
				Complex neighbour = neighbour_iterator.next();
				if (!this.contains(neighbour))								// if there exists at least one neighbouring complex which is not element of this strong linkage class, then this strong linkage class cannot be terminal
					return false;	// therefore, return false
			}
		}
		
		return true; // if all complexes which can be reached from inside this strong linkage class are elements of this strong linkage class, then this strong linkage class must be terminal
	}
}
