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

public class ExampleEFMs {
	public static void main(String[] args) throws Exception{
		ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/calvin_cycle");

		// ##############################################################
		// # for the following you need the polco library               #
		// #                                                            #
		// # go to http://www.csb.ethz.ch/tools/software/polco.html,    #
		// # download the jar-file and add it to the project            #
		// ##############################################################
		
		// compute extreme rays (for irreversible reactions EFMs and extreme rays are equivalent) and show them
		System.out.println(reaction_network.getNMatrix().getExtremeRays().toString());
	}
}
