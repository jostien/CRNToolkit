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

package system.parsers.sbml.cobra;

import org.sbml.jsbml.*;

import java.io.*;
import java.util.*;

import crnt.*;
import math.set.MySet;
import miscellaneous.*;

/**
 * 
 * This class implements a rudimentary CobraToolbox SBML file writer. It does not handle each possible Cobra model,
 * but it works for at least one model from the Bigg database. Clearly, this class has to be extended correspondingly.
 * Most methods are based on convertCobraToSBML.m from the CobraToolbox. Might be very buggy.
 * 
 * All reactions are irreversible. Former reversible reactions are split into irreversible forward and backward reaction.
 * Information (notes, formulas and similar) is lost when loading a Cobra SBML file and writting it back to SBML.
 * 
 * Quite inelegant because of ignoring potential former SBML information.
 * 
 * @author jostie
 *
 */
public class COBRAWriter {
	private SBMLDocument doc;
	private Model model;
	
	// list of compartments, see convertCobraToSBML.m from CobraToolbox
    private String[] compSymbolList = new String[]{"c","m","v","x","e","t","g","r","n","p","l"};
    private String[] compNameList = new String[]{"Cytoplasm","Mitochondrion","Vacuole","Peroxisome","Extracellular","Pool","Golgi","Endoplasmic_reticulum","Nucleus","Periplasm","Lysosome"};
    private HashMap<String,String> compartments;
    
    /**
     * For testing.
     * 
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception{
		System.out.print("Importing human model ... ");
		ReactionNetwork reaction_network = (new COBRAParser()).parse("/home/jostie/workspace/box_of_pandora/examples/SBML/13_01_15_human_biggdb.xml");
		System.out.println("finished");
		
		System.out.println("Number of reactions:\t" + reaction_network.getReactions().size());
		System.out.println("Number of complexes:\t" + reaction_network.getComplexes().size());
		System.out.println("Number of species:\t" + reaction_network.getSpecies().size());
		
		System.out.print("Exporting human model ... ");
		(new COBRAWriter()).write(reaction_network, "/home/jostie/test2.xml");
		System.out.println("finished");
		System.out.print("Importing human model ... ");
		reaction_network = (new COBRAParser()).parse("/home/jostie/test2.xml");
		System.out.println("finished");
		
		System.out.println("Number of reactions:\t" + reaction_network.getReactions().size());
		System.out.println("Number of complexes:\t" + reaction_network.getComplexes().size());
		System.out.println("Number of species:\t" + reaction_network.getSpecies().size());
		
		System.out.print("Exporting human model ... ");
		(new COBRAWriter()).write(reaction_network, "/home/jostie/test2.xml");
		System.out.println("finished");
		System.out.print("Importing human model ... ");
		reaction_network = (new COBRAParser()).parse("/home/jostie/test2.xml");
		System.out.println("finished");
		
		System.out.println("Number of reactions:\t" + reaction_network.getReactions().size());
		System.out.println("Number of complexes:\t" + reaction_network.getComplexes().size());
		System.out.println("Number of species:\t" + reaction_network.getSpecies().size());
	}
	
	/**
	 * Constructor. Creates the list of unique compartments and their keys.
	 */
	public COBRAWriter(){
		this.compartments = new HashMap<String,String>();
		for (int i = 0; i < this.compSymbolList.length; i++)
			this.compartments.put(this.compSymbolList[i], this.compNameList[i]);
	}
	
	public void write(ReactionNetwork reaction_network, File file) throws Exception{
		this.write(reaction_network, file.getAbsolutePath());
	}
	
	/**
	 * Writes the Cobra model to file.
	 * 
	 * @param reaction_network The reaction network representing a Cobra model.
	 * @param file_name Where to put the data.
	 * @throws Exception
	 */
	public void write(ReactionNetwork reaction_network, String file_name) throws Exception{
		this.makeDocument(reaction_network.getName());
		this.addUniqueCompartmentsToModel(reaction_network.getSpecies());
		
		MyRegexp regexp = new MyRegexp();
		Iterator<crnt.Reaction> reaction_iterator = reaction_network.getReactions().iterator();
		while (reaction_iterator.hasNext()){
			crnt.Reaction reaction = reaction_iterator.next();
			crnt.Complex substrate = reaction.getSubstrate();
			crnt.Complex product = reaction.getProduct();
//			
//			// check if current reaction has an id and, if not, create one
			String reaction_id = this.getSBMLReactionId(reaction.getId());
			
			if (!reaction.isReversible() || (reaction.isReversible() && regexp.matches(reaction.getId(),"back$").length == 0)){
				org.sbml.jsbml.Reaction sbml_reaction = this.model.createReaction(reaction_id);
				// Systems Biology Ontology (SBO) actually not necessary yet ...
				sbml_reaction.setSBOTerm(176);	// SBO0000176 = biochemical reaction
				sbml_reaction.setName(reaction.getName());
				
				String notes = reaction.getNotes();
				//System.out.println(notes);
				notes = notes.replaceAll("<html:p>", "<html>");
				notes = notes.replaceAll("</html:p>", "</html>");
				notes = notes.replaceAll("<notes>", "<notes><body xmlns=\"http://www.w3.org/1999/xhtml\">");
				notes = notes.replaceAll("</notes>", "</body></notes>");
				//System.out.println(notes);
				
				sbml_reaction.setNotes(notes);
				if (reaction.isReversible())
					sbml_reaction.setReversible(true);
				else
					sbml_reaction.setReversible(false);

				this.setComplex(sbml_reaction, substrate, true);
				this.setComplex(sbml_reaction, product, false);
			}
		}
		
		// Write the SBML document to a file.
		org.sbml.jsbml.SBMLWriter.write(doc, file_name, "", "");
	}
	
	/**
	 * Creates the SBML document.
	 * 
	 * @param reaction_network The name of the given reaction network.
	 */
	private void makeDocument(String reaction_network_name){
		// Create a new SBMLDocument object, using SBML Level 2 Version 4.
		this.doc = new SBMLDocument(2, 4);
		
		// Create a new SBML model, and add a compartment to it.
		this.model = this.doc.createModel(reaction_network_name);
		this.model.setMetaId("" + (new Random()).nextLong());
		
		// Create a model history object and add author information to it.
		History hist = this.model.getHistory(); // Will create the History, if it does not exist
		Creator creator = new Creator("Given Name", "Family Name", "Organisation", "My@EMail.com");
		hist.addCreator(creator);
	}
	
	/**
	 * Create the list (HashMap) of unique compartment names and their corresponding keys.
	 */
	public void addUniqueCompartmentsToModel(MySet<crnt.Species> species_set){
		HashMap<String,String> unique_compartments = new HashMap<String,String>();
		
		Iterator<crnt.Species> species_iterator = species_set.iterator();
		while (species_iterator.hasNext()){
			crnt.Species species = species_iterator.next();
			
			unique_compartments.put(this.getSBMLCompartmentId(species.getId()), this.getSBMLCompartmentId(species.getId()));
		}
		
		// create compartments
		Iterator<String> compartment_iterator = unique_compartments.keySet().iterator();
		while (compartment_iterator.hasNext()){
			String compartment_id = compartment_iterator.next();
			Compartment compartment = this.model.createCompartment("C_" + compartment_id);
			compartment.setName(this.compartments.get(compartment_id));
			compartment.setSize(1d);
		}
	}
	
	/**
	 * Sets the species of one side of a reaction (either substrate or product) of a SBML reaction.
	 * 
	 * @param sbml_reaction Where to set the species.
	 * @param complex The complex containing the species.
	 * @param is_substrate If substrate or product is to be set.
	 */
	private void setComplex(org.sbml.jsbml.Reaction sbml_reaction, Complex complex, boolean is_substrate){
		Iterator<crnt.Species> species_iterator = complex.getSpecies().iterator();
		while (species_iterator.hasNext()){
			crnt.Species crnt_species = species_iterator.next();
			
			String compartment = this.getSBMLCompartmentId(crnt_species.getId());
			String sbml_species_id = this.getSBMLSpeciesId(crnt_species.getId());
			
			org.sbml.jsbml.Species sbml_species = null;					// empty sbml species reference
			if (!this.model.containsSpecies(sbml_species_id)){			// check if model already contains species with given id, if not do the following
				sbml_species = this.model.createSpecies(sbml_species_id);
				sbml_species.setCompartment("C_" + compartment);
				sbml_species.setName(crnt_species.getName());
			} else
				sbml_species = this.model.getSpecies(sbml_species_id);
			
			Double number_of_occurences = complex.getObject().getNumberOfOccurences(crnt_species);
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
	
	/**
	 * Returns the SBML reaction id for a given Cobra reaction id.
	 * 
	 * @param cobra_reaction_id The given Cobra reaction id.
	 * 
	 * @return The SBML reaction id.
	 */
	public String getSBMLReactionId(String cobra_reaction_id){
		return this.cleanUpFormatting("R_" + cobra_reaction_id);
	}
	
	/**
	 * Returns the SBML species id for a given Cobra species id.
	 * 
	 * @param cobra_species_id The given Cobra species id.
	 * 
	 * @return The SBML species id.
	 */
	public String getSBMLSpeciesId(String cobra_species_id){
		return this.cleanUpFormatting("M_" + this.getTmpSpeciesId(cobra_species_id) + "_" + this.getSBMLCompartmentId(cobra_species_id));
	}
	
	/**
	 * Returns the SBML compartment id for a given Cobra species id.
	 * 
	 * @param cobra_species_id The given Cobra species id.
	 * 
	 * @return The SBML compartment id.
	 */
	public String getSBMLCompartmentId(String cobra_species_id){
		// the following is not general enough, must be extended further
		return cobra_species_id.substring(cobra_species_id.length() - 2, cobra_species_id.length() - 1);
	}
	
	/**
	 * Returns a temporary species id for a given Cobra species id.
	 * 
	 * @param cobra_species_id The given Cobra species id.
	 * 
	 * @return The temporary species id.
	 */
	public String getTmpSpeciesId(String cobra_species_id){
		// the following is not general enough, must be extended further
		return cobra_species_id.substring(0, cobra_species_id.length() - 3);
	}
	
	/**
	 * See convertCobraToSBML.m from CobraToolbox.
	 * 
	 * @param str Input string.
	 * 
	 * @return Resulting string.
	 */
	public String cleanUpFormatting(String str){
		str = str.replaceAll("-"	,	"_DASH_");		// correct
		str = str.replaceAll("/"	,	"_FSLASH_");	// correct
		str = str.replaceAll("\\\\"	,	"_BSLASH_");	// correct
		str = str.replaceAll("\\("	,	"_LPAREN_");	// correct
		str = str.replaceAll("\\)"	,	"_RPAREN_");	// correct
		str = str.replaceAll("\\["	,	"_LSQBKT_");	// correct
		str = str.replaceAll("\\]"	,	"_RSQBKT_");	// correct
		str = str.replaceAll(","	,	"_COMMA_");		// correct
		str = str.replaceAll("\\."	,	"_PERIOD_");	// correct
		str = str.replaceAll("''"	,	"_APOS_");		// correct
		str = str.replaceAll("\\(e\\)$"	,	"_e$");		// correct
		str = str.replaceAll("&"	,	"&amp;");		// correct
		str = str.replaceAll("<"	,	"&lt;");		// correct
		str = str.replaceAll(">"	,	"&gt;");		// correct
		str = str.replaceAll("\""	,	"&quot;");		// correct
		
		return str;
	}
}