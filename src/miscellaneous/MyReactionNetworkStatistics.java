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

package miscellaneous;

import crnt.*;

import java.util.*;

public class MyReactionNetworkStatistics {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	public MyReactionNetworkStatistics(){
	}
	
	public String getReactionInformation(ReactionNetwork reaction_network){
		StringBuffer ret = new StringBuffer();
		
		String[] reaction_ids = reaction_network.getReactionIds();
		String[] reaction_names = reaction_network.getReactionNames();
		
		int id_max = MyRegexp.max(reaction_ids);
		int name_max = MyRegexp.max(reaction_names);
		
		ret.append(MyRegexp.complete("Reaction id", id_max) + "\t" + MyRegexp.complete("Reaction name", name_max) + "\n");
		for (int i = 0; i < reaction_ids.length; i++)
			ret.append(MyRegexp.complete(reaction_ids[i], id_max) + "\t" + MyRegexp.complete(reaction_names[i], name_max) + "\n");
		
		return ret.toString();
	}
	
	public String getSpeciesInformation(ReactionNetwork reaction_network){
		StringBuffer ret = new StringBuffer();
		
		String[] species_ids = reaction_network.getSpeciesIds();
		String[] species_names = reaction_network.getSpeciesNames();
		
		int id_max = MyRegexp.max(species_ids);
		int name_max = MyRegexp.max(species_names);
		
		ret.append(MyRegexp.complete("Species id", id_max) + "\t" + MyRegexp.complete("Species name", name_max) + "\n");
		for (int i = 0; i < species_ids.length; i++)
			ret.append(MyRegexp.complete(species_ids[i], id_max) + "\t" + MyRegexp.complete(species_names[i], name_max) + "\n");
		
		return ret.toString();		
	}

	public String getReactionStatistics(ReactionNetwork reaction_network){
		int export_irreversible = 0;
		int export = 0;
		Iterator<Reaction> reaction_iterator = reaction_network.getReactions().iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			if (reaction.isExportReaction()){
				export++;
				if (!reaction.isReversible())
					export_irreversible++;
			}
		}
		
		int irreversible = 0;
		reaction_iterator = reaction_network.getReactions().iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			if (!reaction.isReversible())
				irreversible++;
		}
		
		return "Reaction statistics:\n" +
				" total number of reactions:               " + reaction_network.getReactions().size() + "\n" +
				" number of irreversible reactions:        " + irreversible + "\n" +
				" number of reversible reactions:          " + (reaction_network.getReactions().size() - irreversible)/2 + "\n\n" +
				" total number of export reactions:        " + export + "\n" +
				" number of irreversible export reactions: " + export_irreversible + "\n" +
				" number of reversible export reactions:   " + (export - export_irreversible)/2 + "\n";
	}
	
	public String getMiscStatistics(ReactionNetwork reaction_network) throws Exception{
		System.out.print("Computing rank of stoichiometric matrix ... ");
		int q = reaction_network.getSimpleNMatrix().getRankUsingOctave("/tmp");
		System.out.println("finished\n");
		
		return "Rank of stoichiometric matrix: " + q + "\n";
	}
	
	public String getSpeciesStatistics(ReactionNetwork reaction_network){
		String[] species_ids = reaction_network.getSpeciesIds();
		
		return "Species statistics:\n" + 
				" total number of species: " + species_ids.length + "\n";
	}
}
