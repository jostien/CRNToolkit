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

package crnt;

import miscellaneous.*;

import java.util.*;

/**
 * 
 * @author neigenfind
 *
 * The class ReactionNetwork.
 * This class implements the mathematical structure reaction network.
 */
public class ReactionNetwork {
	private String name;
	private MySet<Species> S;	// the set of species
	private MySet<Complex> C;	// the set of complexes
	private MySet<Reaction> R;	// the set of reactions
	private Partition<Complex> linkage_classes;	// the set of linkage classes
	private MyMatrix<Double, Species, Complex> Y;
	private MyMatrix<Integer, Complex, Reaction> Ia;
	private MyMatrix<Double, Species, Reaction> N;
	private MyMatrix<Integer, Complex, Complex> A;
	private MyMatrix<Integer, Complex, LinkageClass> L;
	private MySet<String> compartments;
	
	/**
	 * The constructor.
	 */
	public ReactionNetwork() throws Exception{
		this.S = new MySet<Species>();
		this.C = new MySet<Complex>();
		this.R = new MySet<Reaction>();
		this.linkage_classes = new Partition<Complex>();
		this.compartments = new MySet<String>();
	}
	
	/**
	 * Adds a reaction to the reaction network.
	 * 
	 * @param reaction The new reaction.
	 */
	public void addReaction(Reaction reaction){
		Reaction reaction_clone = reaction.clone();
		
		this.R.add(reaction_clone);					// add the reaction to the reaction set

		this.C.add(reaction_clone.getSubstrate());	// add the substrate complex to the set of complexes
		this.C.add(reaction_clone.getProduct());	// add the product complex to the set of complexes

		this.S.addAll(reaction_clone.getSubstrate().getSpecies());	// add all species of the substrate complex to the set of species
		this.S.addAll(reaction_clone.getProduct().getSpecies());	// add all species of the substrate complex to the set of species

		// add compartments of substrates
		Iterator<Species> species_iterator = reaction_clone.getSubstrate().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			this.compartments.add(species.getCompartment());
		}
		
		// add compartments of products
		species_iterator = reaction_clone.getProduct().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			this.compartments.add(species.getCompartment());
		}
	}

	public MySet<String> getCompartments(){
		return this.compartments;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	/**
	 * Get the set of reactions.
	 * 
	 * @return The set of reactions.
	 */
	public MySet<Reaction> getReactions(){
		return this.R;
	}
	
	/**
	 * Get the set of linkage classes.
	 * 
	 * @return The set of linkage classes.
	 */
	public Partition<Complex> getLinkageClasses() throws Exception{
		if (this.linkage_classes.size() == 0)
			this.makeLinkageClasses();
		
		return this.linkage_classes;
	}
	
	/**
	 * Makes a human readable string.
	 */
	public String toString(){
		String ret = "";
		
		Iterator<Reaction> iterator = this.R.iterator();
		while (iterator.hasNext()){
			Reaction reaction = iterator.next();
			ret = ret + reaction.toString() + "\n";
		}
		
		return ret;
	}
	
	public void makeStrongLinkageClasses() throws Exception{
		Iterator<EquivalenceClass<Complex>> iterator = this.linkage_classes.iterator();
		while (iterator.hasNext())
			((LinkageClass)iterator.next()).makeStrongLinkageClasses();
	}
	
	/**
	 * Calculates the linkage classes recursively using method depthFirstSearch.
	 */
	public void makeLinkageClasses() throws Exception{
		MySet<Complex> rest_of_complexes = this.C.clone();		// get a copy of all complexes of the network
		MySet<Reaction> rest_of_reactions = this.R.clone();		// get a copy of all reactions of the network
		this.linkage_classes = new Partition<Complex>();		// this empty set will later on consist of the linkage classes
		
		while (rest_of_complexes.size() > 0){					// as long as there exist complexes which are not element of a linkage class
			Complex head_of_rest = rest_of_complexes.head();	// get the first of these complexes which is the seed of a new linkage class
			rest_of_complexes.remove(head_of_rest);				// remove this complex from this set
			
			LinkageClass lc = new LinkageClass(this.R);			// create a new empty linkage class
			this.depthFirstSearch(head_of_rest, lc, rest_of_complexes, rest_of_reactions);	// find all complexes that can be reached from this complex
			this.linkage_classes.addEquivalenceClass(lc);		// add the resulting linkage class to the set of the linkage classes
		}
		
		this.makeStrongLinkageClasses();
	}
	
	/**
	 * Walks recursively along the edges of the hypergraph of the given metabolic network.
	 * 
	 * @param complex The current complex which is to be analyzed.
	 * @param lc The current set of complexes which are members of the current linkage class.
	 * @param rest The set of complexes which were not yet touched
	 */
	public void depthFirstSearch(Complex complex, LinkageClass lc, MySet<Complex> rest_of_complexes, MySet<Reaction> rest_of_reactions){
		lc.add(complex);
		
		MySet<Complex> neighbours = Reaction.getComplexNeighboursForwardBackward(complex, rest_of_complexes, rest_of_reactions);	// get all the neighbours of this reaction
		rest_of_complexes.removeAll(neighbours);	// remove these complexes from the set of untouched complexes
		
		while (neighbours.size() > 0){						// as long as there is a neighbour which was not yet touched
			Complex head_of_neighbours = neighbours.head();	// get the first neighbour
			neighbours.remove(head_of_neighbours);			// remove it from the neighbours list
		
			depthFirstSearch(head_of_neighbours, lc, rest_of_complexes, rest_of_reactions);	// walk along the edges of the hypergraph
		}
	}
	
	/**
	 * Creates Y matrix.
	 */
	public void makeYMatrix(){
		this.Y = new MyMatrix<Double,Species,Complex>();
		
//		int c = 0;
		Iterator<Complex> complex_iterator = this.C.iterator();
		while (complex_iterator.hasNext()){
			Complex complex = complex_iterator.next();
			
//			System.out.println(c + " of " + this.C.size() + ": " + complex.toString());
//			c++;
			
			Iterator<Species> species_iterator = this.S.iterator();
			while (species_iterator.hasNext()){
				Species species = species_iterator.next();
				
				MyEntry<Double,Species,Complex> entry;
				if (complex.contains(species))
					entry = new MyDouble<Species,Complex>(complex.getNumberOfOccurences(species), species, complex);
				else
					entry = new MyDouble<Species,Complex>(new Double(0), species, complex);
				
				this.Y.add(entry);
			}
		}
	}
	
	/**
	 * Creates Ia matrix.
	 */
	public void makeIaMatrix(){
		this.Ia = new MyMatrix<Integer,Complex,Reaction>();
		
//		int c = 0;
		Iterator<Reaction> reaction_iterator = this.R.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			
//			System.out.println(c + " of " + this.R.size() + ": " + reaction.toString());
//			c++;
			
			Complex substrate = reaction.getSubstrate();
			Complex product = reaction.getProduct();
			
			Iterator<Complex> complex_iterator = this.C.iterator();
			while (complex_iterator.hasNext()){
				Complex complex = complex_iterator.next();
				
				MyInteger<Complex,Reaction> entry;
				if (complex.equals(substrate))
					entry = new MyInteger<Complex,Reaction>(new Integer(-1), complex, reaction);
				else if (complex.equals(product))
					entry = new MyInteger<Complex,Reaction>(new Integer(1), complex, reaction);
				else
					entry = new MyInteger<Complex,Reaction>(new Integer(0), complex, reaction);
				
				this.Ia.add(entry);
			}
		}
	}

	/**
	 * Creates N matrix. Very fast version.
	 */
	public void makeSimpleNMatrix(){
		this.N = new MyMatrix<Double,Species,Reaction>(this.S,this.R);
		
		ArrayList<Species> species_array = this.S.toArrayList();
		ArrayList<Reaction> reaction_array = this.R.toArrayList();
		for (int j = 0; j < reaction_array.size(); j++){
			Reaction reaction = reaction_array.get(j);
			Complex substrate = reaction.getSubstrate();
			Complex product = reaction.getProduct();
			MyMultiset<Species> difference = product.sub(substrate);
			
//			System.out.println(j + " of " + this.R.size() + ": " + reaction.toString());

			for (int i = 0; i < species_array.size(); i++){
				Species species = species_array.get(i);
				
				Double entry;
				if (difference.contains(species))
					entry = difference.getNumberOfOccurences(species);
				else
					entry = new Double(0);
				
				this.N.setSimpleEntry(i, j, entry);
			}
		}
	}
	
	/**
	 * Creates N matrix. Fast version.
	 */
	public void makeNMatrix(){
		this.N = new MyMatrix<Double,Species,Reaction>();
		
//		int c = 0;
		Iterator<Reaction> reaction_iterator = this.R.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			Complex substrate = reaction.getSubstrate();
			Complex product = reaction.getProduct();
			MyMultiset<Species> difference = product.sub(substrate);
			
//			System.out.println(c + " of " + this.R.size() + ": " + reaction.toString());
//			c++;
			
			Iterator<Species> species_iterator = this.S.iterator();
			while (species_iterator.hasNext()){
				Species species = species_iterator.next();
				
				MyEntry<Double,Species,Reaction> entry;
				if (difference.contains(species))
					entry = new MyDouble<Species,Reaction>(difference.getNumberOfOccurences(species), species, reaction);
				else
					entry = new MyDouble<Species,Reaction>(new Double(0), species, reaction);
				
				this.N.add(entry);
			}
		}
	}
	
	public void makeLMatrix() throws Exception{
		if (this.linkage_classes.size() == 0)
			this.makeLinkageClasses();
		
		this.L = new MyMatrix<Integer, Complex, LinkageClass>();
		
		Iterator<EquivalenceClass<Complex>> ec_iterator = this.linkage_classes.iterator();
		while (ec_iterator.hasNext()){
			EquivalenceClass<Complex> ec = ec_iterator.next();
			
			ArrayList<Complex> complex_array = this.C.toArrayList();
			for (int i = 0; i < complex_array.size(); i++){
				Complex complex = complex_array.get(i);
				
				MyInteger<Complex,LinkageClass> entry;
				if (ec.contains(complex))
					entry = new MyInteger<Complex,LinkageClass>(new Integer(1), complex, (LinkageClass)ec);
				else
					entry = new MyInteger<Complex,LinkageClass>(new Integer(0), complex, (LinkageClass)ec);

				this.L.add(entry);
			}
		}
	}

	public void makeAMatrix() throws Exception{
		this.A = new MyMatrix<Integer,Complex,Complex>();
	
		MyMatrix<Integer,Complex,Reaction> Ia = this.getIaMatrix();
		for (int j = 0; j < Ia.getWidth(); j++){
			for (int i = 0; i < Ia.getHeight(); i++){
				for (int k = i + 1; k < Ia.getHeight(); k++){
					Complex complex1 = Ia.getEntry(i, j).getFirstDimension();
					Complex complex2 = Ia.getEntry(k, j).getFirstDimension();
					
					MyInteger<Complex,Complex> entry;
					if (Ia.getEntry(i, j).getEntry() < 0 &&	Ia.getEntry(k, j).getEntry() > 0){
						entry = new MyInteger<Complex,Complex>(new Integer(1), complex1, complex2);
						this.A.add(entry);
					}
					else if (Ia.getEntry(i, j).getEntry() > 0 &&	Ia.getEntry(k, j).getEntry() < 0){
						entry = new MyInteger<Complex,Complex>(new Integer(1), complex2, complex1);	
						this.A.add(entry);
					}
				}
			}
		}
		

		for (int i = 0; i < Ia.getHeight(); i++){
			for (int j = 0; j < Ia.getHeight(); j++){
				Complex complex1 = Ia.getFirstDimension(i);
				Complex complex2 = Ia.getFirstDimension(j);
					
				MyInteger<Complex,Complex> entry = new MyInteger<Complex,Complex>(new Integer(0), complex1, complex2);
					
				if (!this.A.contains(entry))
					this.A.add(entry);
			}
		}
	}

	/**
	 * Returns the set of complexes.
	 * 
	 * @return The set of complexes.
	 */
	public MySet<Complex> getComplexes(){
		return this.C;
	}
	
	public MySet<Species> getSpecies(){
		return this.S;
	}
	
	public MyMatrix<Double,Species,Reaction> getSimpleNMatrix() throws Exception{
		// very faster computation of stoichiometric matrix
		if (this.N == null)
			this.makeSimpleNMatrix();
		
		return this.N;
	}
	
	/**
	 * Returns the stoichiometric matrix.
	 * 
	 * @return The stoichiometric matrix.
	 */
	public MyMatrix<Double,Species,Reaction> getNMatrix() throws Exception{
		// faster computation of stoichiometric matrix
		if (this.N == null)
			this.makeNMatrix();
		
		return this.N;
		
//		// slow computation of stoichiometric matrix
//		if (this.Y == null)
//			this.makeYMatrix();
//		if (this.Ia == null)
//			this.makeIaMatrix();
//		
//		// not nice solved, see comments in MyMatrix (mul and round), MyInteger, MyDouble and MyEntry.
//		return (MyMatrix<Integer, Species, Reaction>)(this.Y.mul(this.Ia)).round();
	}
	
	public MyMatrix<Double, Species, Complex> getYMatrix(){
		if (this.Y == null)
			this.makeYMatrix();
		
		return this.Y;
	}

	public MyMatrix<Integer, Complex, Reaction> getIaMatrix(){
		if (this.Ia == null)
			this.makeIaMatrix();
		
		return this.Ia;
	}
	
	public MyMatrix<Integer, Complex, Complex> getAMatrix() throws Exception{
		if (this.A == null)
			this.makeAMatrix();
		
		return this.A;
	}
	
	public MyMatrix<Integer, Complex, LinkageClass> getLMatrix() throws Exception{
		if (this.L == null)
			this.makeLMatrix();
		
		return this.L;
	}
	
	public String getOctavePsi(){
		String ret = "psi = [";
		
		Iterator<Complex> complex_iterator = this.C.iterator();
		while (complex_iterator.hasNext()){
			Complex complex = complex_iterator.next();
			if (complex.numberOfDistinctElements() > 0)
				ret = ret + complex.getPsi() + ", ";
		}
		ret = ret.substring(0, ret.length() - 2) + "]'";
		
		return ret;
	}
	
	
	/**
	 * Computes the deficiency.
	 * 
	 * @return An integer representing the deficiency of the reaction network.
	 */
	public int getDeficiency() throws Exception{
		if (this.linkage_classes.size() == 0)
			this.makeLinkageClasses();
		
		return this.C.size() - this.linkage_classes.size() - this.getNMatrix().getRankUsingOctave("/tmp/");
	}
	
	public boolean hasACR() throws Exception{
		boolean ret = false;
		
		if (this.getDeficiency()==1){
			ArrayList<Complex> array = this.C.toArrayList();
			for (int i = 0; i < array.size(); i++){
				for (int j = i + 1; j < array.size(); j++){
					Complex A = array.get(i);
					Complex B = array.get(j);
					if (this.isTerminal(A)==false && this.isTerminal(B)==false){
						if (A.difference(B).numberOfDistinctElements() == 1 ||
							B.difference(A).numberOfDistinctElements() == 1)
							return true;
					}
				}
			}
		}
		return ret;
	}
	
	public boolean isTerminal(Complex complex) throws Exception{
		boolean result = false;
		LinkageClass lc = (LinkageClass)this.getLinkageClasses().getEquivalenceClassByElement(complex);						//get linkage class to which complex belongs
		StrongLinkageClass slc = (StrongLinkageClass)lc.getStrongLinkageClasses().getEquivalenceClassByElement(complex); 	//get strong linkage class to which complex belongs
		result = slc.isTerminal();
		return result;
	}
		
	public boolean isWeaklyReversible() throws Exception{
		boolean result = true;
		Iterator<EquivalenceClass<Complex>> iter = this.getLinkageClasses().getEquivalenceClasses().iterator();
		while (iter.hasNext()){
			LinkageClass current_lc = (LinkageClass)iter.next();
			if (current_lc.getStrongLinkageClasses().size()>1){
				result = false;  //if one linkage class contains more than one strong linkage class
								 //the network is not weakly reversible
			}
		}
		return result; 
	}
	
	public Species getSpeciesById(String id){
		Iterator<Species> species_iterator = this.S.iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			if (species.getId().equals(id))
				return species;
		}
		return null;
	}
	
	public Species getSpeciesByName(String name){
		Iterator<Species> species_iterator = this.S.iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			if (species.getName().equals(name))
				return species;
		}
		return null;
	}
	
	public Reaction getReactionById(String id){
		Iterator<Reaction> reaction_iterator = this.R.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			if (reaction.getId().equals(id))
				return reaction;
		}
		return null;
	}
	
	public Reaction getReactionByName(String name){
		Iterator<Reaction> reaction_iterator = this.R.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			if (reaction.getName().equals(name))
				return reaction;
		}
		return null;
	}

	public Reaction getReactionBetweenComplexes(Complex c1, Complex c2){
		Iterator<Reaction> iter = this.R.iterator();
		Reaction result = null;
		while (iter.hasNext()){
			Reaction currentReaction = iter.next();
			if (currentReaction.getSubstrate().equals(c1) && currentReaction.getProduct().equals(c2)){
				if (result == null){
					result = currentReaction;
				}
				else{
					System.out.println("Warning : reaction appears twice");
				}					
			}
		}
		return result;
	}
	
	public Boolean isDirectlyLinked(Complex c1, Complex c2){
		Boolean result = null;
		if(getReactionBetweenComplexes(c1, c2)!=null || getReactionBetweenComplexes(c2, c1)!=null){
			result = true;			
		}
		else{
			result = false;
		}
		if (c1.equals(c2)){
			result = true;
		}
		return result;
	}
	
	public Boolean isCutPair(Complex c1, Complex c2) throws Exception{
		Boolean result = false;		
		if (isDirectlyLinked(c1, c2)==false){
			result = false;
		}
		else{
			Reaction forward_reaction = getReactionBetweenComplexes(c1, c2);
			Reaction backward_reaction = getReactionBetweenComplexes(c2, c1);
			if (forward_reaction == null){
				forward_reaction = backward_reaction;
			}
			else if (backward_reaction == null){
				backward_reaction = forward_reaction;
			}
			// if only one reactions exists we set the other direction to be the same reaction
		    // reason : simplifies programming the while loop below (SERGIO)
			ReactionNetwork remaining_lc = new ReactionNetwork();
			LinkageClass linkage_class = (LinkageClass)this.getLinkageClasses().getEquivalenceClassByElement(c1);
			remaining_lc.C = linkage_class.toReactionNetwork().C;
			Iterator<Reaction> iter = linkage_class.toReactionNetwork().getReactions().iterator();
			
			while (iter.hasNext()){
				Reaction current_reaction = iter.next();
				if (!current_reaction.equals(forward_reaction) && !current_reaction.equals(backward_reaction)){
					remaining_lc.addReaction(current_reaction);
				}
			}
			if (remaining_lc.getLinkageClasses().size()>1){
				result = true;
			}
			else{
				result = false;
			}
		}	
		return result;
	}
	
	public Boolean isR3() throws Exception{
		Boolean result = true;
		if (this.linkage_classes==null){
			this.makeLinkageClasses();
		}
		Iterator<EquivalenceClass<Complex>> iterator_lc = this.getLinkageClasses().iterator();
		while (iterator_lc.hasNext()){			
			LinkageClass current_lc = (LinkageClass)iterator_lc.next();
			if (current_lc.getStrongLinkageClasses()==null){
				current_lc.makeStrongLinkageClasses();
			}
			Iterator<EquivalenceClass<Complex>> iterator_slc = current_lc.getStrongLinkageClasses().iterator();
			while (iterator_slc.hasNext()){
				StrongLinkageClass current_slc = (StrongLinkageClass)iterator_slc.next();
				if (current_slc.isTerminal()){									
					Iterator<Complex> iteratorComplex = current_slc.iterator();
					while (iteratorComplex.hasNext()){
						Complex c1 = iteratorComplex.next();												
						Iterator<Complex> iteratorSecondComplex = current_slc.iterator();
						while (iteratorSecondComplex.hasNext()){
							Complex c2 = iteratorSecondComplex.next();
							if (!c1.equals(c2)){
								if(this.isDirectlyLinked(c1, c2) || this.isDirectlyLinked(c2, c1)){
									if(!this.isCutPair(c1, c2)){
										result = false;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	
	public Boolean isR2() throws Exception{
		Boolean result = true;
		if (this.linkage_classes==null){
			this.makeLinkageClasses();
		}
		Iterator<EquivalenceClass<Complex>> iterator_lc = this.getLinkageClasses().iterator();
		while (iterator_lc.hasNext()){			
			LinkageClass current_lc = (LinkageClass)iterator_lc.next();
			if (current_lc.getStrongLinkageClasses()==null){
				current_lc.makeStrongLinkageClasses();
			}
			Iterator<EquivalenceClass<Complex>> iterator_slc = current_lc.getStrongLinkageClasses().iterator();
			Integer count = 0;
			while (iterator_slc.hasNext()){				
				StrongLinkageClass current_slc = (StrongLinkageClass)iterator_slc.next();					
				if (current_slc.isTerminal()){
					count = count +1 ;
				}
			}	
			if (count>1){
				result = false;
			}
		}		
		
		return result;
	}
	
	public String[] getSpeciesIds(){
		String[] ret = new String[this.S.size()];
		
		int i = 0;
		Iterator<Species> species_iterator = this.S.iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			
			ret[i] = species.getId();
			i++;
		}
		
		return ret;
	}
	
	public String[] getSpeciesNames(){
		String[] ret = new String[this.S.size()];
		
		int i = 0;
		Iterator<Species> species_iterator = this.S.iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			
			ret[i] = species.getName();
			i++;
		}
		
		return ret;
	}
	
	public String[] getReactionIds(){
		String[] ret = new String[this.R.size()];
		
		int i = 0;
		Iterator<Reaction> reaction_iterator = this.R.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			
			ret[i] = reaction.getId();
			i++;
		}
		
		return ret;
	}
	
	public String[] getReactionNames(){
		String[] ret = new String[this.R.size()];
		
		int i = 0;
		Iterator<Reaction> reaction_iterator = this.R.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			
			ret[i] = reaction.getName();
			i++;
		}
		
		return ret;
	}
}