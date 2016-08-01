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
public class ReactionNetwork{
	private String name;
	private MySet<Species> S;							// the set of species
	private MySet<Complex> C;							// the set of complexes
	private MySet<Reaction> R;							// the set of reactions
	private Partition<Complex> linkage_classes;			// the set of linkage classes
	private Partition<Complex> strong_linkage_classes;	// the set of strong linkage classes
	private MyMatrix<Double, Species, Complex> Y;
	private MyMatrix<Integer, Complex, Reaction> Ia;
	private MyMatrix<Double, Species, Reaction> N;
	private MyMatrix<Integer, Complex, Complex> A;
	private MyMatrix<Integer, Complex, EquivalenceClass<Complex>> L;
	private MySet<String> compartments;
	
	private HashMap<String, MySet<Reaction>> substrate_reaction_map;// lookup table for reactions consuming complex
	private HashMap<String, MySet<Reaction>> product_reaction_map;	// lookup table for reactions producing complex
	
	private HashMap<String, MySet<Complex>> directed_neighbours;	// lookup table for directed neighbours of complex
	private HashMap<String, MySet<Complex>> undirected_neighbours;	// lookup table for undirected neighbours of complex
	
	/**
	 * The constructor.
	 */
	public ReactionNetwork() throws Exception{
		this.S = new MySet<Species>();
		this.C = new MySet<Complex>();
		this.R = new MySet<Reaction>();
		this.linkage_classes = new Partition<Complex>(new Linked(this));
		this.strong_linkage_classes = new Partition<Complex>(new StronglyLinked(this));
		this.compartments = new MySet<String>();
		
		this.substrate_reaction_map = new HashMap<String, MySet<Reaction>>();
		this.product_reaction_map = new HashMap<String, MySet<Reaction>>();
		
		this.directed_neighbours = new HashMap<String, MySet<Complex>>();
		this.undirected_neighbours = new HashMap<String, MySet<Complex>>();
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
		
		Reaction reaction_ = this.getReactions().getElement(reaction);	// only work with references from this reaction network

		//###################################################
		//# everything with respect to reaction's substrate #
		//###################################################
		
		// add substrate and reaction to substrate-reaction-map
		Complex substrate = reaction_.getSubstrate();
		MySet<Reaction> reactions_consuming_complex = new MySet<Reaction>();
		if (this.substrate_reaction_map.containsKey(substrate.toString()))
			reactions_consuming_complex = this.substrate_reaction_map.get(substrate.toString());
		reactions_consuming_complex.add(reaction_);
		this.substrate_reaction_map.put(substrate.toString(), reactions_consuming_complex);

		// substrate points to product so add product to substrate's directed neighbours
		MySet<Complex> neighbours = new MySet<Complex>();
		if (this.directed_neighbours.containsKey(substrate.toString()))
			neighbours = this.directed_neighbours.get(substrate.toString());
		neighbours.add(reaction_.getProduct());
		this.directed_neighbours.put(substrate.toString(), neighbours);
		
		// substrate is connected to product so add product to substrate's undirected neighbours
		neighbours = new MySet<Complex>();
		if (this.undirected_neighbours.containsKey(substrate.toString()))
			neighbours = this.undirected_neighbours.get(substrate.toString());
		neighbours.add(reaction_.getProduct());
		this.undirected_neighbours.put(substrate.toString(), neighbours);
		
		//#################################################
		//# everything with respect to reaction's product #
		//#################################################
		
		// add product and reaction to product-reaction-map
		Complex product = reaction_.getProduct();
		MySet<Reaction> reactions_producing_complex = new MySet<Reaction>();
		if (this.product_reaction_map.containsKey(product.toString()))
			reactions_producing_complex = this.product_reaction_map.get(product.toString());
		// only add an reference from this reaction network
		reactions_producing_complex.add(reaction_);
		this.product_reaction_map.put(product.toString(), reactions_producing_complex);
		
		// product is connected to substrate so add substrate to product's undirected neighbours
		neighbours = new MySet<Complex>();
		if (this.undirected_neighbours.containsKey(product.toString()))
			neighbours = this.undirected_neighbours.get(product.toString());
		neighbours.add(reaction_.getSubstrate());
		this.undirected_neighbours.put(product.toString(), neighbours);
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
	 * Get the set of strong linkage classes.
	 * 
	 * @return The set of strong linkage classes.
	 */
	public Partition<Complex> getStrongLinkageClasses() throws Exception{
		if (this.strong_linkage_classes.size() == 0)
			this.makeStrongLinkageClasses();
		
		return this.strong_linkage_classes;
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
		this.strong_linkage_classes = new Partition<Complex>(new StronglyLinked(this));

		Iterator<Complex> iterator = this.getComplexes().iterator();
		while (iterator.hasNext())
			this.strong_linkage_classes.addElementToEquivalenceClasses(iterator.next());
	}
	
	/**
	 * Calculates the linkage classes recursively using method depthFirstSearch.
	 */
	public void makeLinkageClasses() throws Exception{
		this.linkage_classes = new Partition<Complex>(new Linked(this));	// this empty set will later on consist of the linkage classes

		Iterator<Complex> iterator = this.getComplexes().iterator();
		while (iterator.hasNext())
			this.linkage_classes.addElementToEquivalenceClasses(iterator.next());
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
		
		this.L = new MyMatrix<Integer, Complex, EquivalenceClass<Complex>>();
		
		Iterator<EquivalenceClass<Complex>> ec_iterator = this.linkage_classes.iterator();
		while (ec_iterator.hasNext()){
			EquivalenceClass<Complex> ec = ec_iterator.next();
			
			ArrayList<Complex> complex_array = this.C.toArrayList();
			for (int i = 0; i < complex_array.size(); i++){
				Complex complex = complex_array.get(i);
				
				MyInteger<Complex,EquivalenceClass<Complex>> entry;
				if (ec.contains(complex))
					entry = new MyInteger<Complex,EquivalenceClass<Complex>>(new Integer(1), complex, (EquivalenceClass<Complex>)ec);
				else
					entry = new MyInteger<Complex,EquivalenceClass<Complex>>(new Integer(0), complex, (EquivalenceClass<Complex>)ec);

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
	
	public MyMatrix<Integer, Complex, EquivalenceClass<Complex>> getLMatrix() throws Exception{
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
		return this.isTerminal(this.getStrongLinkageClasses().getEquivalenceClassByElement(complex));	//get strong linkage class to which complex belongs
	}
		
	public boolean isWeaklyReversible() throws Exception{
		Partition<Complex> slcs = this.getStrongLinkageClasses();
		Iterator<EquivalenceClass<Complex>> iterator = this.getLinkageClasses().iterator();
		while (iterator.hasNext())
			if (!slcs.contains(iterator.next()))
				return false;
		return true;
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

	/**
	 * Gets reaction between two complexes. Does not check, whether there are
	 * multiple reactions between the two neighboring complexes.
	 * 
	 * @param c1 First complex.
	 * @param c2 Second complex.
	 * @return Reaction between two complexes if existent, null otherwise.
	 */
	public Reaction getReactionBetweenComplexes(Complex c1, Complex c2){
		Iterator<Reaction> iter = this.getReactions().iterator();
		while (iter.hasNext()){
			Reaction current_reaction = iter.next();
			if (current_reaction.getSubstrate().equals(c1) && current_reaction.getProduct().equals(c2))
				return current_reaction;
		}
		return null;
	}
	
	/**
	 * Check if two complexes are directly linked.
	 * 
	 * @param c1 First complex.
	 * @param c2 Second complex.
	 * @return True if directly linked, false otherwise.
	 */
	public boolean isDirectlyLinked(Complex c1, Complex c2){
		if (c1.equals(c2))
			return true;
		
		return getReactionBetweenComplexes(c1, c2) != null || getReactionBetweenComplexes(c2, c1) != null;
	}
	
	/**
	 * Check if two complexes are a cut pair.
	 * 
	 * @param c1 First complex.
	 * @param c2 Second complex.
	 * @return True if cut pair, false otherwise.
	 * @throws Exception
	 */
	public boolean isCutPair(Complex c1, Complex c2) throws Exception{
		// if both complexes are not directly linked, they cannot be a cut pair
		if (!isDirectlyLinked(c1, c2))
			return false;

		Reaction forward_reaction = getReactionBetweenComplexes(c1, c2);
		Reaction backward_reaction = getReactionBetweenComplexes(c2, c1);
		// if only one reactions exists we set the other direction to be the same reaction
		// one case must be true, since the complexes are directly linked
		if (forward_reaction == null)
			forward_reaction = backward_reaction;
		if (backward_reaction == null)
			backward_reaction = forward_reaction;
		
		// get the corresponding linkage class, which must contain both complexes because they are directly linked
		EquivalenceClass<Complex> linkage_class = this.getLinkageClasses().getEquivalenceClassByElement(c1);
		// get the set of reactions attached to the complexes
		MySet<Reaction> reactions = this.getReactionsConsumingOrProducingComplexes(linkage_class);

		// fill a new reaction network with the reactions that are not forward_reaction and not backward_reaction
		ReactionNetwork reaction_network = new ReactionNetwork();
		Iterator<Reaction> iterator = reactions.iterator();
		while (iterator.hasNext()){
			Reaction current_reaction = iterator.next();
			if (!current_reaction.equals(forward_reaction) && !current_reaction.equals(backward_reaction))
				reaction_network.addReaction(current_reaction);
		}
		
		// if the number of linkage classes of the new reaction network
		// is greater 1, then two complexes represent a cut pair
		return reaction_network.getLinkageClasses().size() > 1;
	}
	
//	public Boolean isR3() throws Exception{
//		Boolean result = true;
//		if (this.linkage_classes==null){
//			this.makeLinkageClasses();
//		}
//		Iterator<EquivalenceClass<Complex>> iterator_lc = this.getLinkageClasses().iterator();
//		while (iterator_lc.hasNext()){			
//			LinkageClass current_lc = (LinkageClass)iterator_lc.next();
//			if (current_lc.getStrongLinkageClasses()==null){
//				current_lc.makeStrongLinkageClasses();
//			}
//			Iterator<EquivalenceClass<Complex>> iterator_slc = current_lc.getStrongLinkageClasses().iterator();
//			while (iterator_slc.hasNext()){
//				StrongLinkageClass current_slc = (StrongLinkageClass)iterator_slc.next();
//				if (current_slc.isTerminal()){									
//					Iterator<Complex> iteratorComplex = current_slc.iterator();
//					while (iteratorComplex.hasNext()){
//						Complex c1 = iteratorComplex.next();												
//						Iterator<Complex> iteratorSecondComplex = current_slc.iterator();
//						while (iteratorSecondComplex.hasNext()){
//							Complex c2 = iteratorSecondComplex.next();
//							if (!c1.equals(c2)){
//								if(this.isDirectlyLinked(c1, c2) || this.isDirectlyLinked(c2, c1)){
//									if(!this.isCutPair(c1, c2)){
//										result = false;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		return result;
//	}
//	
//	
//	public Boolean isR2() throws Exception{
//		Boolean result = true;
//		if (this.linkage_classes==null){
//			this.makeLinkageClasses();
//		}
//		Iterator<EquivalenceClass<Complex>> iterator_lc = this.getLinkageClasses().iterator();
//		while (iterator_lc.hasNext()){			
//			LinkageClass current_lc = (LinkageClass)iterator_lc.next();
//			if (current_lc.getStrongLinkageClasses()==null){
//				current_lc.makeStrongLinkageClasses();
//			}
//			Iterator<EquivalenceClass<Complex>> iterator_slc = current_lc.getStrongLinkageClasses().iterator();
//			Integer count = 0;
//			while (iterator_slc.hasNext()){				
//				StrongLinkageClass current_slc = (StrongLinkageClass)iterator_slc.next();					
//				if (current_slc.isTerminal()){
//					count = count +1 ;
//				}
//			}	
//			if (count>1){
//				result = false;
//			}
//		}		
//		
//		return result;
//	}
	
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
	
	/**
	 * Computes whether this strong linkage class is terminal.
	 * 
	 * @return True if this strong linkage class is terminal, otherwise false.
	 */
	public boolean isTerminal(EquivalenceClass<Complex> strong_linkage_class){
		Iterator<Complex> iterator = strong_linkage_class.iterator();		// get the iterator of all complexes of this strong linkage class
		while (iterator.hasNext()){											// loop over all complexes of this strong linkage class
			Complex complex = iterator.next();
			MySet<Complex> neighbours = this.getComplexNeighboursForward(complex);	// get all neighbouring complexes of the current complex in the direction of the edges adjacent to the current complex
			
			Iterator<Complex> neighbour_iterator = neighbours.iterator();	// get the iterator of the neighbouring complexes
			while (neighbour_iterator.hasNext()){							// loop over all neighbouring complexes
				Complex neighbour = neighbour_iterator.next();
				if (!strong_linkage_class.contains(neighbour))				// if there exists at least one neighbouring complex which is not element of this strong linkage class, then this strong linkage class cannot be terminal
					return false;											// therefore, return false
			}
		}
		
		return true; // if all complexes which can be reached from inside this strong linkage class are elements of this strong linkage class, then this strong linkage class must be terminal
	}
	
	/**
	 * Returns the set of reactions which consume given complex.
	 * 
	 * @param complex The complex.
	 * @return The set of reactions consuming the complex.
	 */
	public MySet<Reaction> getReactionsConsumingComplex(Complex complex){
		if (this.substrate_reaction_map.containsKey(complex.toString()))
			return this.substrate_reaction_map.get(complex.toString());
		
		return new MySet<Reaction>();
	}
	
	/**
	 * Returns the set of reactions which consume the set of given complexes.
	 * 
	 * @param complexes Set of complexes.
	 * @return The set of reactions consuming the given complexes.
	 */
	public MySet<Reaction> getReactionsConsumingComplexes(MySet<Complex> complexes){
		MySet<Reaction> ret = new MySet<Reaction>();
		
		Iterator<Complex> iterator = complexes.iterator();
		while (iterator.hasNext()){
			Complex complex = iterator.next();
			ret = ret.union(this.substrate_reaction_map.get(complex.toString()));
		}
		
		return ret;
	}
	
	/**
	 * Returns the set of reactions which consume or produce the given complex.
	 * 
	 * @param complex The complex.
	 * @return The set of reactions consuming or producing the given complex.
	 */
	public MySet<Reaction> getReactionsConsumingOrProducingComplex(Complex complex){
		MySet<Reaction> ret = this.substrate_reaction_map.get(complex.toString()).union(this.product_reaction_map.get(complex.toString()));
		if (ret == null)
			return new MySet<Reaction>();
		
		return ret;
	}
	
	/**
	 * Returns the set of reactions which consume or produce the set of given complexes.
	 * 
	 * @param complexes Set of complexes.
	 * @return The set of reactions consuming or producing the given complexes.
	 */
	public MySet<Reaction> getReactionsConsumingOrProducingComplexes(MySet<Complex> complexes){
		MySet<Reaction> ret = new MySet<Reaction>();
		
		Iterator<Complex> iterator = complexes.iterator();
		while (iterator.hasNext()){
			Complex complex = iterator.next();
			ret = ret.union(this.substrate_reaction_map.get(complex.toString()).union(this.product_reaction_map.get(complex.toString())));
		}
		
		return ret;
	}
	
	/**
	 * Returns directed neighbours of given complex.
	 * 
	 * @param complex The complex.
	 * @return Set of directed neighbours of the given complex.
	 */
	public MySet<Complex> getComplexNeighboursForward(Complex complex){
		if (this.directed_neighbours.containsKey(complex.toString()))
			return this.directed_neighbours.get(complex.toString());
		
		return new MySet<Complex>();
	}
	
	/**
	 * Returns undirected neighbours of given complex.
	 * 
	 * @param complex The complex.
	 * @return Set of undirected neighbours of the given complex.
	 */
	public MySet<Complex> getComplexNeighboursForwardBackward(Complex complex){
		if (this.undirected_neighbours.containsKey(complex.toString()))
			return this.undirected_neighbours.get(complex.toString());
		
		return new MySet<Complex>();
	}
}