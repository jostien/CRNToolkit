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
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_1.1");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.1");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.10");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.11");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.2");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.3");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.7");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.8");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.9");
		ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_4.7");
		//ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_4.9");
		System.out.println(reaction_network.toString());
		
		// get number of complexes n
		System.out.println("number of complexes is " + reaction_network.getComplexes().size());
		System.out.println();
		
		// compute number of linkage classes l and show them
		System.out.println("number of linkage classes is " + reaction_network.getLinkageClasses().size());
		Iterator<MyEquivalenceClass<Complex>> lc_iterator = reaction_network.getLinkageClasses().iterator();
		while (lc_iterator.hasNext())
			System.out.println(lc_iterator.next().toString());
		System.out.println();
		
		// ##############################################################
		// # for the following you need octave installed on your system #
		// ##############################################################
		
		// compute rank of stoichiometric matrix q
		System.out.println("rank of stoichiometric matrix is " + reaction_network.getNMatrix().getRankUsingOctave("/tmp/"));
		System.out.println();
		
		// compute deficiency of reaction network \delta := n - l - q
		System.out.println("deficiency of network is " + reaction_network.getDeficiency());
		System.out.println();

		// compute number of strong linkage classes and show them
		System.out.println("number of strong linkage classes is " + reaction_network.getStrongLinkageClasses().size());
		MyPartition<Complex> slcs = reaction_network.getStrongLinkageClasses();
		Iterator<MyEquivalenceClass<Complex>> slc_iterator = slcs.iterator();
		while (slc_iterator.hasNext()){
			MyEquivalenceClass<Complex> slc = slc_iterator.next();
			System.out.println(slc.toString());
			System.out.println("strong linkage class is terminal: " + reaction_network.isTerminal(slc));
		}		
		System.out.println();
		
		// is network weakly reversible?
		System.out.println("reaction network is weakly reversible: " + reaction_network.isWeaklyReversible());
	}
}
