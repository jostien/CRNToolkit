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

package system.parsers.sbml;

import org.sbml.jsbml.*;

import java.io.*;
import java.util.*;

import crnt.*;
import miscellaneous.*;

public class SBMLWriter {
	private SBMLDocument doc;
	private Model model;
	
	public SBMLWriter(){
	}
	
	public void write(ReactionNetwork reaction_network, File file) throws Exception{
		this.write(reaction_network, file.getAbsolutePath());
	}
	
	public void write(ReactionNetwork reaction_network, String file_name) throws Exception{
		this.makeDocument(reaction_network);
		
		MyRegexp regexp = new MyRegexp();
		int reaction_counter = 0;
		Iterator<crnt.Reaction> reaction_iterator = reaction_network.getReactions().iterator();
		while (reaction_iterator.hasNext()){
			crnt.Reaction reaction = reaction_iterator.next();
			crnt.Complex substrate = reaction.getSubstrate();
			crnt.Complex product = reaction.getProduct();
			
			// check if current reaction has an id and, if not, create one
			String reaction_id = reaction.getId();
			if (reaction_id == null || reaction_id.length() == 0){
				reaction_id = crnt.Reaction.ID_PREFIX + reaction_counter;
				reaction_counter++;
			}
				
			if (!reaction.isReversible() || (reaction.isReversible() && regexp.matches(reaction.getId(),"back$").length == 0)){
				org.sbml.jsbml.Reaction sbml_reaction = this.model.createReaction(reaction_id);
				// Systems Biology Ontology (SBO) actually not necessary yet ...
				sbml_reaction.setSBOTerm(176);	// SBO0000176 = biochemical reaction
				sbml_reaction.setName(reaction.getName());
				sbml_reaction.setNotes(reaction.getNotes());
				
				if (reaction.isReversible())
					sbml_reaction.setReversible(true);
				else
					sbml_reaction.setReversible(false);

				this.setSubstrates(sbml_reaction, substrate, true);
				this.setSubstrates(sbml_reaction, product, false);
			}
		}
		
		// Write the SBML document to a file.
		org.sbml.jsbml.SBMLWriter.write(doc, file_name, "", "");
	}
	
	private void makeDocument(ReactionNetwork reaction_network){
		// Create a new SBMLDocument object, using SBML Level 2 Version 4.
		this.doc = new SBMLDocument(2, 4);
		
		// Create a new SBML model, and add a compartment to it.
		this.model = this.doc.createModel(reaction_network.getName());
		this.model.setMetaId("" + (new Random()).nextLong());
		
		// create compartments
		Iterator<String> compartment_iterator = reaction_network.getCompartments().iterator();
		while (compartment_iterator.hasNext()){
			String compartment_name = compartment_iterator.next();
			Compartment compartment = this.model.createCompartment(compartment_name);
			compartment.setSize(1d);
		}
		
		// Create a model history object and add author information to it.
		History hist = this.model.getHistory(); // Will create the History, if it does not exist
		Creator creator = new Creator("Given Name", "Family Name", "Organisation", "My@EMail.com");
		hist.addCreator(creator);
	}
	
	private void setSubstrates(org.sbml.jsbml.Reaction sbml_reaction, Complex complex, boolean is_substrate){
		Iterator<crnt.Species> species_iterator = complex.getSpecies().iterator();
		while (species_iterator.hasNext()){
			crnt.Species crnt_species = species_iterator.next();
			
			// check if current species is element of a defined compartment, if not, set to "default"
			String species_compartment = crnt_species.getCompartment();
			if (species_compartment.length() == 0)
				species_compartment = "default";
			
			if (!this.model.containsCompartment(crnt_species.getCompartment())){	// check if model already contains compartment (actually it should)
				// if not create compartment and add it to model
				Compartment compartment = this.model.createCompartment(crnt_species.getCompartment());
				this.model.addCompartment(compartment);
			}
			
			org.sbml.jsbml.Species sbml_species = null;				// empty sbml species reference
			if (!this.model.containsSpecies(crnt_species.getId())){		// check if model already contains species with given id, if not do the following
				sbml_species = this.model.createSpecies(crnt_species.getId());
				sbml_species.setCompartment(crnt_species.getCompartment());
				sbml_species.setName(crnt_species.getName());
			} else
				sbml_species = this.model.getSpecies(crnt_species.getId());
			
			Double number_of_occurences = complex.getNumberOfOccurences(crnt_species);
			org.sbml.jsbml.SpeciesReference sbml_sr;
			if (is_substrate){
				sbml_sr = sbml_reaction.createReactant(sbml_species);
				sbml_sr.setStoichiometry(number_of_occurences);
				sbml_sr.setSBOTerm(15);	// SBO0000015 = substrate
			} else {
				sbml_sr = sbml_reaction.createProduct(sbml_species);
				sbml_sr.setStoichiometry(number_of_occurences);
				sbml_sr.setSBOTerm(11);	// SBO0000011 = product
			}
		}
	}
}
