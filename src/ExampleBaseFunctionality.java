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

import java.util.Iterator;

import crnt.*;
import miscellaneous.*;
import system.parsers.simple.SimpleParser;

/*
 * Octave is needed for this example to work.
 */
public class ExampleBaseFunctionality {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// build a set A and add three elements
		MySet<String> A = new MySet<String>();
		A.addElement("1").addElement("2").addElement("3");
		System.out.println("A = " + A.toString());
		
		// build a set B and add two elements
		MySet<String> B = new MySet<String>();
		B.addElement("3").addElement("4");
		System.out.println("B = " + B.toString());
		System.out.println();
		
		// apply some set operations
		System.out.println("A union B = " + (A.union(B)).toString());
		System.out.println("A intersection B = " + (A.intersection(B)).toString());
		System.out.println("A difference B = " + (A.difference(B)).toString());
		System.out.println();
		
		// build a multiset M and add two elements with differing multiplicty
		MyMultiset<String> M = new MyMultiset<String>();
		M.addElement("1").addElement("2").addElement("2");
		M.add("2");
		System.out.println(M.toString());
		MyMultiset<String> M2 = M.clone();
		System.out.println(M2.toString());
		System.out.println();
		
		// build a reaction R1 := 2 A -> B
		Reaction reaction = new Reaction("R1", (new Complex()).addSpecies(new Species("A")).addSpecies(new Species("A")), (new Complex()).addSpecies(new Species("B")));
		System.out.println(reaction.toString());
		
		// build a reaction R2 := 1 F -> 1 A via parsing a string (everything behind A is ignored)
		reaction = (new SimpleParser()).parseString("R2             1 F = 1 A | # 0 1");
		System.out.println(reaction.toString());
		System.out.println();
		
		// build a reaction network by reading file
		ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_4.7");
		System.out.println(reaction_network.toString());
		
		// get number of complexes n
		System.out.println("number of complexes is " + reaction_network.getComplexes().size());
		System.out.println();
		
		// compute number of linkage classes l
		System.out.println("number of linkage classes is " + reaction_network.getLinkageClasses().size());
		System.out.println();
		
		// ##############################################################
		// # for the following you need octave installed on your system #
		// ##############################################################
		
		// compute rank of stoichiometric matrix q
		System.out.println("rank of stoichiometric matrix is " + reaction_network.getNMatrix().getRankUsingOctave("/tmp/"));
		System.out.println();
		
		// compute deficiency of reaction network \delta := n - l - q
		System.out.println("deficiency of network is " + reaction_network.getDeficiency());

		// some additional info
		// compute strong linkage classes
		int c = 1;
		// get iterator over linkage classes
		Iterator<LinkageClass> lc_iterator = reaction_network.getLinkageClasses().getLinkageClasses().iterator();
		while (lc_iterator.hasNext()){
			LinkageClass lc = lc_iterator.next();	// get linkage class
			System.out.println(c + ". linkage class is " + lc.toString());
			System.out.println("number of complexes is " + lc.size());
			
			// get strong linkage classes of linkage class
			MySet<StrongLinkageClass> slcs = lc.getStrongLinkageClasses().getStrongLinkageClasses();
			System.out.println("number of strong linkage classes is " + slcs.size());
			// get iterator over strong linkage classes
			Iterator<StrongLinkageClass> slc_iterator = slcs.iterator();
			while (slc_iterator.hasNext()){
				StrongLinkageClass slc = slc_iterator.next();	// get strong linkage class
				System.out.println("   strong linkage class is " + lc.toString());
				System.out.println("   number of complexes is " + slc.size());
				System.out.println("   strong linkage class is terminal: " + slc.isTerminal());
			}
			System.out.println();
			c++;
		}
	}
}
