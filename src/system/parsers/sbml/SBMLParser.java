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

package system.parsers.sbml;

import org.sbml.jsbml.*;

import crnt.*;
import system.parsers.*;

import java.io.*;

public class SBMLParser extends Parser{
	private Model model;
	
	public SBMLParser(){
	}
	
	public ReactionNetwork parse(File file) throws Exception{
		return this.parse(file.getAbsolutePath());
	}
	
	public ReactionNetwork parse(String file_name) throws Exception{
		SBMLReader sr = new SBMLReader();
		SBMLDocument sd = sr.readSBML(file_name);

		this.model = sd.getModel();
		ReactionNetwork ret = new ReactionNetwork();		
		for (int i = 0; i < this.model.getNumReactions(); i++){
			org.sbml.jsbml.Reaction sbml_reaction = this.model.getReaction(i);
			
			Complex substrate = this.getComplex(sbml_reaction, true);
			Complex product = this.getComplex(sbml_reaction, false);
			
			crnt.Reaction forward_reaction = new crnt.Reaction(sbml_reaction.getId(), sbml_reaction.getName(), substrate, product);
			forward_reaction.setNotes(sbml_reaction.getNotesString());
			if (sbml_reaction.isReversible()){
				crnt.Reaction backward_reaction = new crnt.Reaction(sbml_reaction.getId() + "_back", sbml_reaction.getName() + " (back)", product, substrate, forward_reaction);
				backward_reaction.setNotes(forward_reaction.getNotes());
				
				forward_reaction.setInverse(backward_reaction);
				ret.addReaction(forward_reaction);
				ret.addReaction(backward_reaction);
			} else
				ret.addReaction(forward_reaction);
		}
		
		return ret;
	}
	
	private crnt.Complex getComplex(org.sbml.jsbml.Reaction reaction, boolean is_substrate) throws Exception{
		int n;
		if (is_substrate)
			n = reaction.getNumReactants();
		else
			n = reaction.getNumProducts();

		crnt.Complex ret = new crnt.Complex();
		
		for (int j = 0; j < n; j++){
			SpeciesReference species_reference;
			if (is_substrate)
				species_reference = reaction.getReactant(j);
			else
				species_reference = reaction.getProduct(j);

			String compartment = species_reference.getSpeciesInstance().getCompartment();
			
			if (compartment.length() == 0)
				compartment = "default";
			
			crnt.Species species = new crnt.Species(
					species_reference.getSpeciesInstance().getId(),
					species_reference.getSpeciesInstance().getName(),
					compartment);
			
			ret.add(species, species_reference.getStoichiometry());
		}

		return ret;
	}
}
