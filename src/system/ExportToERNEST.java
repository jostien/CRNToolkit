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

package system;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import crnt.Complex;
import crnt.Reaction;
import crnt.ReactionNetwork;
import crnt.Species;



public class ExportToERNEST {
	public ExportToERNEST(){
		
	}
	public void writeFile (ReactionNetwork reactionNetwork, String filename){
		try{
			FileWriter outFile = new FileWriter(filename);
			PrintWriter out = new PrintWriter(outFile);

			Iterator<Species> iteratorSpecies = reactionNetwork.getSpecies().iterator();
			Iterator<Reaction> iteratorReaction = reactionNetwork.getReactions().iterator();
			Integer reactionCounter = 0;
			
			out.println("model.id = 'export from BoP';");
			out.println("model.name = 'export from BoP';");
			out.print("model.species = struct('id', {");
			while (iteratorSpecies.hasNext()){
				Species species = iteratorSpecies.next();
				out.print("'" + species.toString() + "'");				
				if (iteratorSpecies.hasNext()){
					out.print(",");
				}
			}
			out.println("});");
			while (iteratorReaction.hasNext()){
				Reaction reaction = iteratorReaction.next();
				reactionCounter +=1;
				out.print("model.reaction(");
				out.print(reactionCounter.toString());
				out.print(") = struct('id', '");
				out.print(reaction.getName());
				out.print("', 'reactant' ,struct('species', {");
				Complex complexSubstrate = reaction.getSubstrate();
				Iterator<Species> iteratorSubstrateSpecies = complexSubstrate.getSpecies().iterator();
				String substrateSpeciesString = "";
				String substrateStoichiometryString ="";
				while (iteratorSubstrateSpecies.hasNext()){					
					Species substrateSpecies = iteratorSubstrateSpecies.next();					
					substrateSpeciesString = substrateSpeciesString + "'" + substrateSpecies.toString() + "'";
					substrateStoichiometryString = substrateStoichiometryString + complexSubstrate.getObject().getNumberOfOccurences(substrateSpecies);
					if (iteratorSubstrateSpecies.hasNext()){
						substrateSpeciesString = substrateSpeciesString + ",";
						substrateStoichiometryString = substrateStoichiometryString + " ";
					}					
				}
				out.print(substrateSpeciesString);
				out.print("}, 'stoichiometry', {");
				out.print(substrateStoichiometryString);
				out.print("}), 'product' ,struct('species', {");
				Complex complexProduct = reaction.getProduct();
				Iterator<Species> iteratorProductSpecies = complexProduct.getSpecies().iterator();
				String productSpeciesString = "";
				String productStoichiometryString ="";
				while (iteratorProductSpecies.hasNext()){					
					Species productSpecies = iteratorProductSpecies.next();					
					productSpeciesString = productSpeciesString + "'" + productSpecies.toString() + "'";
					productStoichiometryString = productStoichiometryString + complexProduct.getObject().getNumberOfOccurences(productSpecies);
					if (iteratorProductSpecies.hasNext()){
						productSpeciesString = productSpeciesString + ",";
						productStoichiometryString = productStoichiometryString + " ";
					}					
				}
				out.print(productSpeciesString);
				out.print("}, 'stoichiometry', {");
				out.print(productStoichiometryString);				
				out.println("}), 'reversible', false);");
			}
			out.close();
		} catch (IOException e){
		              e.printStackTrace();
		}
	}
}
