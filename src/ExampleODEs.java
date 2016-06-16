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

import crnt.*;
import system.parsers.simple.SimpleParser;

public class ExampleODEs {
	public static void main(String[] args) throws Exception{
		ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/bifunctional_general");
		ChemicalReactionNetwork crn = new ChemicalReactionNetwork(reaction_network);

		// show ODEs
		System.out.println(crn.toReadableODEString());
		System.out.println();
		
		// the result of the following can be used in LaTeX
		System.out.println(crn.toLaTeXODEString());
		System.out.println();
		
		// the result of the following can be pasted into Singular, ...
		System.out.println(crn.toSingularODEString());
		// ... then call groebner(I);
	}
}
