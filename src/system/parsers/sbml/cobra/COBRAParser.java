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

import crnt.*;
import miscellaneous.*;

import org.sbml.jsbml.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * 
 * This class implements a rudimentary CobraToolbox SBML file parser. It does not handle each possible CobraSBML file,
 * but it works for at least one model from the Bigg database. Clearly, this class has to be extended correspondingly.
 * Most methods are based on convertSBMLToCobra.m from the CobraToolbox. Might be very buggy.
 * 
 * All reactions are irreversible. Reversible reactions are split into irreversible forward and backward reaction.
 * Information (notes, formulas and similar) is lost when loading a Cobra SBML file.
 * 
 * Quite inelegant because of ignoring potential SBML information.
 * 
 * @author jostie
 *
 */
public class COBRAParser{
	private Model model;
	private MyRegexp regexp;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		File[] files = new File[10];
		files[0] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_01_15_H_sapiens_Recon_1.xml");
		files[1] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_E_coli_iAF1260.xml");
		files[2] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_E_coli_iJR904.xml");
		files[3] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_E_coli_textbook.xml");
		files[4] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_H_pylori_iIT341.xml");
		files[5] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_M_barkeri_iAF692.xml");
		files[6] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_M_tuberculosis_iNJ661.xml");
		files[7] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_P_putida_iJN746_curated.xml");
		files[8] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_S_aureus_iSB619.xml");
		files[9] = new File(System.getProperty("user.dir") + "/examples/SBML/BiGG/13_03_15_S_cerevisiae_iND750.xml");
		
		int[] cobra_stoichiometric_matrix_ranks = new int[]{2674, 1630, 743, 67, 465, 586, 786, 869, 596, 984};
		
		for (int i = 0; i < files.length; i++){
			File file = files[i];
			
			if (i > 0)
				System.out.println("-----------------------------------------------------------------------\n");
			
			System.out.print("Importing model from \"" + file.getName() + "\" ... ");
			ReactionNetwork reaction_network = (new COBRAParser()).parse(file);
			System.out.println("finished\n");
			
			String new_file_name = "/tmp/" + file.getName() + "2";
			System.out.print("Exporting model to \"" + new_file_name + "\" ... ");
			(new COBRAWriter()).write(reaction_network, new_file_name);
			System.out.println("finished\n");

			MyReactionNetworkStatistics mrns = new MyReactionNetworkStatistics();

//			System.out.println(mrns.getReactionInformation(reaction_network));
//			System.out.println(mrns.getSpeciesInformation(reaction_network));

			System.out.println(mrns.getReactionStatistics(reaction_network));	
			System.out.println(mrns.getSpeciesStatistics(reaction_network));
			System.out.println(mrns.getMiscStatistics(reaction_network));
			System.out.println("Rank computed by CobraToolbox: " + cobra_stoichiometric_matrix_ranks[i] + "\n");
		}
		
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("-----------------------------------------------------------------------\n");
		
		for (int i = 0; i < files.length; i++){
			File file = files[i];
			String new_file_name = "/tmp/" + file.getName() + "2";
			
			if (i > 0)
				System.out.println("-----------------------------------------------------------------------\n");
			
			System.out.print("Importing model from \"" + new_file_name + "\" ... ");
			ReactionNetwork reaction_network = (new COBRAParser()).parse(file);
			System.out.println("finished\n");
			
			System.out.print("Exporting model to \"" + new_file_name + "\" ... ");
			(new COBRAWriter()).write(reaction_network, new_file_name);
			System.out.println("finished\n");

			MyReactionNetworkStatistics mrns = new MyReactionNetworkStatistics();

//			System.out.println(mrns.getReactionInformation(reaction_network));
//			System.out.println(mrns.getSpeciesInformation(reaction_network));

			System.out.println(mrns.getReactionStatistics(reaction_network));	
			System.out.println(mrns.getSpeciesStatistics(reaction_network));
			System.out.println(mrns.getMiscStatistics(reaction_network));
			System.out.println("Rank computed by CobraToolbox: " + cobra_stoichiometric_matrix_ranks[i] + "\n");
		}
		
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("-----------------------------------------------------------------------\n");

		for (int i = 0; i < files.length; i++){
			File file = files[i];
			String new_file_name = "/tmp/" + file.getName() + "2";
			
			if (i > 0)
				System.out.println("-----------------------------------------------------------------------\n");
			
			System.out.print("Importing model from \"" + new_file_name + "\" ... ");
			ReactionNetwork reaction_network = (new COBRAParser()).parse(file);
			System.out.println("finished\n");
			
			System.out.print("Exporting model to \"" + new_file_name + "\" ... ");
			(new COBRAWriter()).write(reaction_network, new_file_name);
			System.out.println("finished\n");

			MyReactionNetworkStatistics mrns = new MyReactionNetworkStatistics();

//			System.out.println(mrns.getReactionInformation(reaction_network));
//			System.out.println(mrns.getSpeciesInformation(reaction_network));

			System.out.println(mrns.getReactionStatistics(reaction_network));	
			System.out.println(mrns.getSpeciesStatistics(reaction_network));
			System.out.println(mrns.getMiscStatistics(reaction_network));
			System.out.println("Rank computed by CobraToolbox: " + cobra_stoichiometric_matrix_ranks[i] + "\n");
		}
	}
	
	public ReactionNetwork parse(File file) throws Exception{
		return this.parse(file.getAbsolutePath());
	}
	
	/**
	 * Parses the Cobra SBML file.
	 * 
	 * @param file_name The given Cobra SBML file.
	 * 
	 * @return The corresponding reaction network. 
	 * @throws Exception
	 */
	public ReactionNetwork parse(String file_name) throws Exception{
		this.regexp = new MyRegexp();
		
		SBMLReader sr = new SBMLReader();
		SBMLDocument sd = sr.readSBML(file_name);
		
		this.model = sd.getModel();
		
		ReactionNetwork ret = new ReactionNetwork();		
		for (int i = 0; i < this.model.getNumReactions(); i++){
			org.sbml.jsbml.Reaction sbml_reaction = this.model.getReaction(i);
			
			crnt.Reaction forward_reaction = this.reformatReaction(sbml_reaction);
			forward_reaction.setNotes(sbml_reaction.getNotesString());
			if (sbml_reaction.isReversible()){
				crnt.Reaction backward_reaction = new crnt.Reaction(
						forward_reaction.getId() + "_back",
						forward_reaction.getName() + " (back)",
						forward_reaction.getProduct(),
						forward_reaction.getSubstrate(),
						forward_reaction);
				backward_reaction.setNotes(forward_reaction.getNotes());
				
				forward_reaction.setInverse(backward_reaction);
				ret.addReaction(forward_reaction);
				ret.addReaction(backward_reaction);
			} else
				ret.addReaction(forward_reaction);
		}
		
		return ret;
	}
	
	/**
	 * Reformats a Cobra SBML reaction to a CRNT reaction.
	 * 
	 * @param sbml_reaction The given Cobra SBML reaction.
	 * 
	 * @return The corresponding CRNT reaction.
	 * @throws Exception
	 */
	public crnt.Reaction reformatReaction(org.sbml.jsbml.Reaction sbml_reaction) throws Exception{
		Complex substrate = this.getComplex(sbml_reaction, true);
		Complex product = this.getComplex(sbml_reaction, false);
		
		return new crnt.Reaction(
				this.reformatReactionId(sbml_reaction.getId()),
				this.reformatReactionName(sbml_reaction.getName()),
				substrate,
				product);
	}
	
	/**
	 * Returns a CRNT complex given a Cobra SBML reaction.
	 * 
	 * @param sbml_reaction The given Cobra SBML reaction.
	 * @param is_substrate If substrate or product is to be returned.
	 * 
	 * @return The corresponding complex.
	 * @throws Exception
	 */
	public crnt.Complex getComplex(org.sbml.jsbml.Reaction sbml_reaction, boolean is_substrate) throws Exception{
		int n;
		if (is_substrate)
			n = sbml_reaction.getNumReactants();
		else
			n = sbml_reaction.getNumProducts();
		
		Complex ret = new Complex();
		
		for (int i = 0; i < n; i++){
			SpeciesReference species_reference;
			if (is_substrate)
				species_reference = sbml_reaction.getReactant(i);
			else
				species_reference = sbml_reaction.getProduct(i);
			
			if (!this.isBoundary(species_reference.getSpeciesInstance().getId())){
				String compartment = species_reference.getSpeciesInstance().getCompartment();
				if (compartment.length() == 0)
					compartment = "default";
				
				crnt.Species species = new crnt.Species(
						this.reformatSpeciesId(species_reference.getSpeciesInstance().getId()),
						this.reformatSpeciesName(species_reference.getSpeciesInstance().getName()),
						compartment);
				
				ret.getNode().add(species, species_reference.getStoichiometry());
			}
		}
		
		return ret;
	}
	
	/**
	 * This method checks if the corresponding metabolite is a "boundary metabolite".
	 * Boundary metabolites are ignored in the Cobra framework. See convertSBMLToCobra.m.
	 * 
	 * @param str Species id from SBML model.
	 * 
	 * @return True if it is a boundary species, else false.
	 */
	public boolean isBoundary(String str){
		// Ignore boundary metabolites
	    if (!this.model.getSpecies(str).isBoundaryCondition()){
	    	// Check for the Palsson lab _b$ boundary condition indicator
	        if (!str.matches(".*_b$"))
	        	return false;
	    }
	    return true;
	}
	
	/**
	 * This method returns the reaction id. Some parts of the SBML id is removed and/or replaced.
 	 * See also convertSBMLToCobra.m.
	 * 
	 * @param str Reaction id from SBML model.
	 * 
	 * @return The new Reaction id.
	 */
	public String reformatReactionId(String str){
	    str = (new String(str)).replaceAll("^R_","");
	    str = this.cleanUpFormatting(str);
		
		return str;
	}
	
	/**
	 * This method returns the reaction name. Some parts of the SBML id is removed and/or replaced.
 	 * See also convertSBMLToCobra.m.
	 * 
	 * @param str Reaction name from SBML model.
	 * 
	 * @return The new Reaction name.
	 */	
	public String reformatReactionName(String str){
	    str = (new String(str)).replaceAll("^R_","");
	    str = str.replaceAll("_+"," ");
		
		return str;
	}
	
	/**
	 * This method returns the species id. Some parts of the SBML id is removed and/or replaced.
	 * Also the compartments are analyzed. This method does not fully implement the corresponding
	 * code from convertSBMLToCobra.m.
	 * 
	 * @param str Species id from SBML model.
	 * 
	 * @return The new species id.
	 */
	public String reformatSpeciesId(String str){
		    // Parse metabolite id's
		    // Get rid of the M_ in the beginning of metabolite id's
		    String metID = (new String(str)).replaceAll("^M_","");
		    metID = metID.replaceAll("^_","");
		    
		    // Find compartment id
		    String[] tmpCell = new String[0];
	        for (int j = 0; j < this.model.getNumCompartments(); j++){
	        	tmpCell = regexp.getTokens(metID, "_(" + this.model.getCompartment(j).getId() + ")$");
	        	//tmpCell = COBRAParser.tokens(metID,"_" + this.model.getCompartment(j).getId() + "$", new String[]{this.model.getCompartment(j).getId()});
	            if (tmpCell.length > 0)
	            	break;
	        }
		    
		    String metTmp;
		    if (tmpCell.length == 0)
		    	tmpCell = regexp.getTokens(metID, "_(.)$");
		    	//tmpCell = COBRAParser.tokens(metID, "_.$", new String[]{"."});
		    if (tmpCell.length > 0){
		        String compID = tmpCell[0];
		    	metTmp = metID.replaceAll("_" + compID + "$","") + "[" + compID + "]";
		    } else
		        metTmp = metID;
		    
		    
		    // Clean up met ID
		    return this.cleanUpFormatting(metTmp);		
	}
	
	/**
	 *  This method returns the species name. Some parts of the SBML name is removed and/or replaced.
	 * This method does not fully implement the corresponding code from convertSBMLToCobra.m.
	 * 
	 * @param str Species name from SBML model.
	 * 
	 * @return The new species name.
	 */
	public String reformatSpeciesName(String str){
	    // Parse metabolite names
	    // Clean up some of the weird stuff in the sbml files
	    String metNamesTmp = new String(str).replaceAll("^M_","");
	    metNamesTmp = this.cleanUpFormatting(metNamesTmp);
	    metNamesTmp = metNamesTmp.replaceAll("^_","");
//	%     metNamesTmp = strrep(metNamesTmp,'_','-');
	    metNamesTmp = metNamesTmp.replaceAll("-+","-");
	    metNamesTmp = metNamesTmp.replaceAll("-$","");
	    
	    return metNamesTmp;
	}
	
	/**
	 * See convertSBMLToCobra.m from CobraToolbox.
	 * 
	 * @param str Input string.
	 * 
	 * @return Resulting string.
	 */
	public String cleanUpFormatting(String str){
		str = str.replaceAll("-DASH-"	,	"-");
		str = str.replaceAll("_DASH_"	,	"-");
		str = str.replaceAll("_FSLASH_"	,	"/");
		str = str.replaceAll("_BSLASH_"	,	"\\");
		str = str.replaceAll("_LPAREN_"	,	"(");
		str = str.replaceAll("_LSQBKT_"	,	"[");
		str = str.replaceAll("_RSQBKT_"	,	"]");
		str = str.replaceAll("_RPAREN_"	,	")");
		str = str.replaceAll("_COMMA_"	,	",");
		str = str.replaceAll("_PERIOD_"	,	".");
		str = str.replaceAll("_APOS_"	,	"''");
		str = str.replaceAll("_e_$"		,	"(e)");
		str = str.replaceAll("_e$"		,	"(e)");
		str = str.replaceAll("&amp;"	,	"&");
		str = str.replaceAll("&lt;"		,	"<");
		str = str.replaceAll("&gt;"		,	">");
		str = str.replaceAll("&quot;"	,	"\"");
		
		return str;
	}
	
//	/**
//	 * Finds and reports the matchings of a regular expression with respect to a given string.
//	 * 
//	 * @param str The given string.
//	 * @param regex The regular expression.
//	 * 
//	 * @return A list of strings representing the list of matchings.
//	 */
//	public static ArrayList<String> tokens(String str, String regex){
//		Pattern p = Pattern.compile(regex);
//		Matcher m = p.matcher(str);
//		
//		ArrayList<String> ret = new ArrayList<String>();
//		while (m.find()) 
//		   ret.add(str.substring(m.start(),m.end()));
//		
//		return ret;
//	}
//	
//	/**
//	 * Finds and reports the tokens from a regular expression with respect to a given string.
//	 * 
//	 * @param str The given string.
//	 * @param regex The regular expression.
//	 * @param tokens The tokens, should be substrings of the regular expression. 
//	 * 
//	 * @return Array of strings representing the tokens.
//	 */
//	public static String[] tokens(String str, String regex, String[] tokens){
//		ArrayList<String> matches = COBRAParser.tokens(str, regex);
//		
//		ArrayList<String> result = new ArrayList<String>();
//		for (int i = 0; i < matches.size(); i++)
//			for (int j = 0; j < tokens.length; j++){
//				ArrayList<String> list = COBRAParser.tokens(matches.get(i), tokens[j]);
//				if (list.size() > 0)
//					result.addAll(list);
//			}
//		
//		String[] ret = new String[result.size()];
//		for (int i = 0; i < result.size(); i++)
//			ret[i] = result.get(i);
//		
//		return ret;
//	}
}
