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

import math.field.MyDouble;
import math.field.MyEntry;
import math.field.MyInteger;
import math.graph.MyConnectedComponents;
import math.graph.MyEdge;
import math.graph.MyGraph;
import math.graph.MyNode;
import math.graph.MyStronglyConnectedComponents;
import math.linalg.MyMatrix;
import math.linalg.MySimpleMatrix;
import math.set.MyEquivalenceClass;
import math.set.MyMultiset;
import math.set.MyPartition;
import math.set.MySet;
import system.parsers.simple.SimpleParser;

/**
 * 
 * @author neigenfind
 *
 * The class ReactionNetwork.
 * This class implements the mathematical structure reaction network.
 */
public class ReactionNetwork extends MyGraph<MyMultiset<Species>>{
	private String name;
	private MySet<Species> S;							// the set of species
	private MyPartition<Complex> linkage_classes;			// the set of linkage classes
	private MyPartition<Complex> strong_linkage_classes;	// the set of strong linkage classes
	private MyMatrix<Species, Complex> Y;
	private MyMatrix<Complex, Reaction> Ia;
	private MyMatrix<Species, Reaction> N;
	private MyMatrix<Complex, Complex> A;
	private MyMatrix<Complex, MyEquivalenceClass<Complex>> L;
	private MySet<String> compartments;
	private MySimpleMatrix<Species, Reaction> simple_N;
	
	// each species can occur in many complexes as well as in many reactions, therefore, multiset counts how often a complex
	// is used as substrate or product so that removal of a reaction decreases multiplicities of corresponding substrate and
	// product complexes
	private HashMap<String, MyMultiset<Complex>> species_complex_map;	// lookup table for complexes containing species;
	
	public static void main(String[] args) throws Exception{
		ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_4.7");
		
		Reaction reaction = null;
		
		reaction = (new SimpleParser()).parseString("R05 1 A + 1 B = 2 C");
		ReactionNetwork rn1 = new ReactionNetwork();
		rn1.addReaction(reaction);
		
		reaction = (new SimpleParser()).parseString("R09 1 D = 2 E");
		ReactionNetwork rn2 = new ReactionNetwork();
		rn2.addReaction(reaction);
		
		MySet<MyEquivalenceClass<Complex>> ecs1 = rn1.getLinkageClasses();
		MySet<MyEquivalenceClass<Complex>> ecs2 = rn2.getLinkageClasses();
		
		Iterator<MyEquivalenceClass<Complex>> iterator = reaction_network.getLinkageClasses().iterator();
		while (iterator.hasNext()){
			MyEquivalenceClass<Complex> ec = iterator.next();
			ReactionNetwork con = reaction_network.getShortestPathBetweenComplexSets(ecs1.toArrayList().get(0), ecs2.toArrayList().get(0), ec, true);
			if (con != null)
				System.out.println(ec.toString() + " is superset of " + ecs1.toArrayList().get(0).toString() + " and/or " + ecs2.toArrayList().get(0).toString() + ": " + con.toString());
			else
				System.out.println(ec.toString() + " is NOT superset of " + ecs1.toArrayList().get(0).toString() + " and/or " + ecs2.toArrayList().get(0).toString() + ": null");
		}
		System.out.println();
		
		reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_2.9");
		System.out.println(reaction_network.toString());
		
		ReactionNetwork[] lcs = reaction_network.getLinkageClassesAsReactionNetworks();
		for (int i = 0; i < lcs.length; i++)
			System.out.println(lcs[i].toString());
		System.out.println();

		lcs = reaction_network.getStrongLinkageClassesAsReactionNetworks();
		for (int i = 0; i < lcs.length; i++)
			System.out.println(lcs[i].toString());
		System.out.println();
	}
	
	/**
	 * The constructor.
	 */
	public ReactionNetwork() throws Exception{
		super();
		
		this.S = new MySet<Species>();
		this.linkage_classes = new MyPartition(new MyConnectedComponents(this));
		this.strong_linkage_classes = new MyPartition(new MyStronglyConnectedComponents(this));
		this.compartments = new MySet<String>();
		
		this.species_complex_map = new HashMap<String, MyMultiset<Complex>>();
	}
	
	public void addReactions(MySet<Reaction> reactions){
		Iterator<Reaction> reaction_iterator = reactions.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			this.addReaction(reaction);
		}
	}
	
	/**
	 * Adds a reaction to the reaction network.
	 * 
	 * @param reaction The new reaction.
	 */
	public boolean addReaction(Reaction reaction) /*throws Exception*/{
		if (!this.addEdge(reaction))
			return false;
		
		reaction = (Reaction)this.getEdge(reaction);
	
		this.getSpecies().addAll(reaction.getSubstrate().getSpecies());	// add all species of the substrate complex to the set of species
		this.getSpecies().addAll(reaction.getProduct().getSpecies());	// add all species of the substrate complex to the set of species

		// add compartments of substrates
		Iterator<Species> species_iterator = reaction.getSubstrate().getObject().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			this.compartments.add(species.getCompartment());
		}
		
		// add compartments of products
		species_iterator = reaction.getProduct().getObject().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			this.compartments.add(species.getCompartment());
		}
		
		//#################################################
		//# everything with respect to species of complex #
		//#################################################
		
		Complex substrate = reaction.getSubstrate();
		species_iterator = substrate.getObject().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			MyMultiset<Complex> complexes = new MyMultiset<Complex>();
			if (this.species_complex_map.containsKey(species.toString()))
				complexes = this.species_complex_map.get(species.toString());
			complexes.add(substrate);
			this.species_complex_map.put(species.toString(), complexes);
		}
		
		Complex product = reaction.getProduct();
		species_iterator = product.getObject().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			MyMultiset<Complex> complexes = new MyMultiset<Complex>();
			if (this.species_complex_map.containsKey(species.toString()))
				complexes = this.species_complex_map.get(species.toString());
			complexes.add(product);
			this.species_complex_map.put(species.toString(), complexes);
		}
		
		return true;
	}
	
	/**
	 * Removes reaction from reaction network. (Not heavily tested yet)
	 * 
	 * @param reaction The reaction to remove.
	 */
	public void removeReaction(Reaction reaction){
		this.removeEdge(reaction);
		
		Complex substrate = reaction.getSubstrate();
		Complex product = reaction.getProduct();
		
		// remove species complex references
		this.removeSpeciesComplexReferences(substrate);
		this.removeSpeciesComplexReferences(product);
	}
	
	/**
	 * The reference of a species to the complexes it occurs in is removed.
	 * 
	 * @param complex The complex whose species references are to be removed.
	 */
	private void removeSpeciesComplexReferences(Complex complex){
		Iterator<Species> species_iterator = complex.getObject().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			
			//System.out.println("trying to remove " + species.toString());
			if (this.species_complex_map.containsKey(species.toString())){
				MyMultiset<Complex> complexes = this.species_complex_map.get(species.toString());
				//System.out.println(complexes.toString());
				complexes.remove(complex);
				//System.out.println(complexes.toString());
			
				// if there is no complex associated with this species, then remove reference and species
				if (complexes.size() == 0){
					this.species_complex_map.remove(species.toString());
					this.getSpecies().remove(species);
					//System.out.println(species.toString() + " removed");
				}
			}
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
		return (MySet)this.getEdges();
	}
	
	/**
	 * Get the set of strong linkage classes.
	 * 
	 * @return The set of strong linkage classes.
	 */
	public MyPartition<Complex> getStrongLinkageClasses() throws Exception{
		if (this.strong_linkage_classes.size() == 0)
			this.makeStrongLinkageClasses();
		
		return this.strong_linkage_classes;
	}
	
	/**
	 * Returns the array of reaction networks. Each network consists of the set of consuming
	 * reactions belonging to the elements of the corresponding strong linkage class. Thus,
	 * the resulting network may have terminal nodes.  
	 * 
	 * @return Array of reaction networks representing set of strong linkage classes.
	 * @throws Exception
	 */
	public ReactionNetwork[] getStrongLinkageClassesAsReactionNetworks() throws Exception{
		if (this.strong_linkage_classes.size() == 0)
			this.makeStrongLinkageClasses();
		
		int c = 0;
		ReactionNetwork[] ret = new ReactionNetwork[this.getStrongLinkageClasses().size()];
		Iterator<MyEquivalenceClass<Complex>> iterator = this.getStrongLinkageClasses().iterator();
		while (iterator.hasNext()){
			MyEquivalenceClass<Complex> ec = iterator.next();
			ReactionNetwork rn = new ReactionNetwork();
			Iterator<Complex> complex_iterator = ec.iterator();
			while (complex_iterator.hasNext()){
				Complex complex = complex_iterator.next();
				MySet<Reaction> reactions = (MySet)this.getEdgesOut(complex);
				Iterator<Reaction> reaction_iterator = reactions.iterator();
				while (reaction_iterator.hasNext()){
					rn.addReaction(reaction_iterator.next());
				}
			}
			ret[c] = rn;
			c++;
		}
		
		return ret;
	}
	
	/**
	 * Get the set of linkage classes.
	 * 
	 * @return The set of linkage classes.
	 */
	public MyPartition<Complex> getLinkageClasses() throws Exception{
		if (this.linkage_classes.size() == 0)
			this.makeLinkageClasses();
		
		return this.linkage_classes;
	}
	
	/**
	 * Returns the array of reaction networks. Each network consists of the set of consuming
	 * reactions belonging to the elements of the corresponding linkage class.
	 *  
	 * @return Array of reaction networks representing set of linkage classes.
	 * @throws Exception
	 */
	public ReactionNetwork[] getLinkageClassesAsReactionNetworks() throws Exception{
		if (this.linkage_classes.size() == 0)
			this.makeLinkageClasses();
		
		int c = 0;
		ReactionNetwork[] ret = new ReactionNetwork[this.getLinkageClasses().size()];
		Iterator<MyEquivalenceClass<Complex>> iterator = this.getLinkageClasses().iterator();
		while (iterator.hasNext()){
			MyEquivalenceClass<Complex> ec = iterator.next();
			ReactionNetwork rn = new ReactionNetwork();
			Iterator<Complex> complex_iterator = ec.iterator();
			while (complex_iterator.hasNext()){
				Complex complex = complex_iterator.next();
				MySet<Reaction> reactions = (MySet)this.getEdgesOut(complex);
				Iterator<Reaction> reaction_iterator = reactions.iterator();
				while (reaction_iterator.hasNext()){
					rn.addReaction(reaction_iterator.next());
				}
			}
			ret[c] = rn;
			c++;
		}
		
		return ret;
	}
	
	/**
	 * Returns the subnetwork corresponding to the reactions adjacent to the given complexes.
	 * (Not clear why I cannot use the corresponding method from MyGraph, compiler complains)
	 * 
	 * @param complexes Set of complexes.
	 * @return The resulting subnetwork.
	 */
	public ReactionNetwork getComplexesAsReactionNetwork(MySet<Complex> complexes) throws Exception{
		ReactionNetwork ret = new ReactionNetwork();
		
		Iterator<Complex> iterator = this.getComplexes().iterator();
		while (iterator.hasNext()){
			Complex complex = iterator.next();
			MySet<Reaction> reactions = (MySet)this.getEdges(complex);
			Iterator<Reaction> iter = reactions.iterator();
			while (iter.hasNext()){
				Reaction reaction = iter.next();
				if (complexes.contains(reaction.getSubstrate()) && complexes.contains(reaction.getProduct())){
					ret.addReaction(reaction);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Makes a human readable string.
	 */
	public String toString(){
		String ret = "";
		
		Iterator<Reaction> iterator = this.getReactions().iterator();
		while (iterator.hasNext()){
			Reaction reaction = iterator.next();
			ret = ret + reaction.toString() + "\n";
		}
		
		return ret;
	}
	
	public void makeStrongLinkageClasses() throws Exception{
		MyPartition<MyNode<MyMultiset<Species>>> partition_of_nodes = new MyPartition(new MyStronglyConnectedComponents(this));
		Iterator<MyNode<MyMultiset<Species>>> iterator = (Iterator<MyNode<MyMultiset<Species>>>)this.getNodes().iterator();
		while (iterator.hasNext())
			partition_of_nodes.addElementToEquivalenceClasses(iterator.next());
		
		this.strong_linkage_classes = (MyPartition)partition_of_nodes;
	}
	
	/**
	 * Calculates the linkage classes recursively using method depthFirstSearch.
	 */
	public void makeLinkageClasses() throws Exception{
		MyPartition<MyNode<MyMultiset<Species>>> partition_of_nodes = new MyPartition(new MyConnectedComponents(this));
		Iterator<MyNode<MyMultiset<Species>>> iterator = (Iterator<MyNode<MyMultiset<Species>>>)this.getNodes().iterator();
		while (iterator.hasNext())
			partition_of_nodes.addElementToEquivalenceClasses(iterator.next());
		
		this.linkage_classes = (MyPartition)partition_of_nodes;
	}
	
	/**
	 * Creates Y matrix.
	 */
	public void makeYMatrix(){
		this.Y = new MyMatrix<Species,Complex>();
		
//		int c = 0;
		Iterator<Complex> complex_iterator = this.getComplexes().iterator();
		while (complex_iterator.hasNext()){
			Complex complex = complex_iterator.next();
			
//			System.out.println(c + " of " + this.getComplexes().size() + ": " + complex.toString());
//			c++;
			
			Iterator<Species> species_iterator = this.getSpecies().iterator();
			while (species_iterator.hasNext()){
				Species species = species_iterator.next();
				
				MyEntry<Double,Species,Complex> entry;
				if (complex.getObject().contains(species))
					entry = new MyDouble<Species,Complex>(complex.getObject().getNumberOfOccurences(species), species, complex);
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
		this.Ia = new MyMatrix<Complex,Reaction>();
		
//		int c = 0;
		Iterator<Reaction> reaction_iterator = this.getReactions().iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			
//			System.out.println(c + " of " + this.getReactions().size() + ": " + reaction.toString());
//			c++;
			
			Complex substrate = reaction.getSubstrate();
			Complex product = reaction.getProduct();
			
			Iterator<Complex> complex_iterator = this.getComplexes().iterator();
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
		this.simple_N = new MySimpleMatrix<Species,Reaction>(this.getSpecies(),this.getReactions());
		
		ArrayList<Species> species_array = this.getSpecies().toArrayList();
		ArrayList<Reaction> reaction_array = this.getReactions().toArrayList();
		for (int j = 0; j < reaction_array.size(); j++){
			Reaction reaction = reaction_array.get(j);
			Complex substrate = reaction.getSubstrate();
			Complex product = reaction.getProduct();
			MyMultiset<Species> difference = product.getObject().sub(substrate.getObject());
			
//			System.out.println(j + " of " + this.getReactions().size() + ": " + reaction.toString());

			for (int i = 0; i < species_array.size(); i++){
				Species species = species_array.get(i);
				
				Double entry;
				if (difference.contains(species))
					entry = difference.getNumberOfOccurences(species);
				else
					entry = new Double(0);
				
				this.simple_N.setSimpleEntry(i, j, entry);
			}
		}
	}
	
	/**
	 * Creates N matrix. Fast version.
	 */
	public void makeNMatrix(){
		this.N = new MyMatrix<Species,Reaction>();
		
//		int c = 0;
		Iterator<Reaction> reaction_iterator = this.getReactions().iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			Complex substrate = reaction.getSubstrate();
			Complex product = reaction.getProduct();
			MyMultiset<Species> difference = product.getObject().sub(substrate.getObject());
			
//			System.out.println(c + " of " + this.getReactions().size() + ": " + reaction.toString());
//			c++;
			
			Iterator<Species> species_iterator = this.getSpecies().iterator();
			while (species_iterator.hasNext()){
				Species species = species_iterator.next();
				
				MyDouble<Species,Reaction> entry;
				if (difference.contains(species))
					this.N.add(new MyDouble(difference.getNumberOfOccurences(species), species, reaction));
				else
					this.N.add(new MyDouble(new Double(0), species, reaction));
			}
		}
	}
	
	public void makeLMatrix() throws Exception{
		if (this.linkage_classes.size() == 0)
			this.makeLinkageClasses();
		
		this.L = new MyMatrix<Complex, MyEquivalenceClass<Complex>>();
		
		Iterator<MyEquivalenceClass<Complex>> ec_iterator = this.linkage_classes.iterator();
		while (ec_iterator.hasNext()){
			MyEquivalenceClass<Complex> ec = ec_iterator.next();
			
			ArrayList<Complex> complex_array = this.getComplexes().toArrayList();
			for (int i = 0; i < complex_array.size(); i++){
				Complex complex = complex_array.get(i);
				
				MyInteger<Complex,MyEquivalenceClass<Complex>> entry;
				if (ec.contains(complex))
					entry = new MyInteger<Complex,MyEquivalenceClass<Complex>>(new Integer(1), complex, (MyEquivalenceClass<Complex>)ec);
				else
					entry = new MyInteger<Complex,MyEquivalenceClass<Complex>>(new Integer(0), complex, (MyEquivalenceClass<Complex>)ec);

				this.L.add(entry);
			}
		}
	}

	public void makeAMatrix() throws Exception{
		this.A = new MyMatrix<Complex,Complex>();
	
		MyMatrix<Complex,Reaction> Ia = this.getIaMatrix();
		for (int j = 0; j < Ia.getWidth(); j++){
			for (int i = 0; i < Ia.getHeight(); i++){
				for (int k = i + 1; k < Ia.getHeight(); k++){
					Complex complex1 = Ia.getEntry(i, j).getFirstDimension();
					Complex complex2 = Ia.getEntry(k, j).getFirstDimension();
					
					MyInteger<Complex,Complex> entry;
					if (Ia.getEntry(i, j).isLess(0) &&	Ia.getEntry(k, j).isGreater(0)){
						entry = new MyInteger<Complex,Complex>(new Integer(1), complex1, complex2);
						this.A.add(entry);
					}
					else if (Ia.getEntry(i, j).isGreater(0) &&	Ia.getEntry(k, j).isLess(0)){
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
		return (MySet)this.getNodes();
	}
	
	public MySet<Species> getSpecies(){
		return this.S;
	}
	
	public MySimpleMatrix<Species,Reaction> getSimpleNMatrix() throws Exception{
		// very faster computation of stoichiometric matrix
		if (this.simple_N == null)
			this.makeSimpleNMatrix();
		
		return this.simple_N;
	}
	
	/**
	 * Returns the stoichiometric matrix.
	 * 
	 * @return The stoichiometric matrix.
	 */
	public MyMatrix<Species,Reaction> getNMatrix() throws Exception{
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
	
	public MyMatrix<Species, Complex> getYMatrix(){
		if (this.Y == null)
			this.makeYMatrix();
		
		return this.Y;
	}

	public MyMatrix<Complex, Reaction> getIaMatrix(){
		if (this.Ia == null)
			this.makeIaMatrix();
		
		return this.Ia;
	}
	
	public MyMatrix<Complex, Complex> getAMatrix() throws Exception{
		if (this.A == null)
			this.makeAMatrix();
		
		return this.A;
	}
	
	public MyMatrix<Complex, MyEquivalenceClass<Complex>> getLMatrix() throws Exception{
		if (this.L == null)
			this.makeLMatrix();
		
		return this.L;
	}
	
	public String getOctavePsi(){
		String ret = "psi = [";
		
		Iterator<Complex> complex_iterator = this.getComplexes().iterator();
		while (complex_iterator.hasNext()){
			Complex complex = complex_iterator.next();
			if (complex.getObject().numberOfDistinctElements() > 0)
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
		
		return this.getComplexes().size() - this.linkage_classes.size() - this.getNMatrix().getRankUsingOctave("/tmp/");
	}
	
	public boolean hasACR() throws Exception{
		boolean ret = false;
		
		if (this.getDeficiency()==1){
			ArrayList<Complex> array = this.getComplexes().toArrayList();
			for (int i = 0; i < array.size(); i++){
				for (int j = i + 1; j < array.size(); j++){
					Complex A = array.get(i);
					Complex B = array.get(j);
					if (this.isTerminal(A)==false && this.isTerminal(B)==false){
						if (A.getObject().difference(B.getObject()).numberOfDistinctElements() == 1 ||
							B.getObject().difference(A.getObject()).numberOfDistinctElements() == 1)
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
		MyPartition<Complex> slcs = this.getStrongLinkageClasses();
		Iterator<MyEquivalenceClass<Complex>> iterator = this.getLinkageClasses().iterator();
		while (iterator.hasNext())
			if (!slcs.contains(iterator.next()))
				return false;
		return true;
	}
	
	public Species getSpeciesById(String id){
		Iterator<Species> species_iterator = this.getSpecies().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			if (species.getId().equals(id))
				return species;
		}
		return null;
	}
	
	public Species getSpeciesByName(String name){
		Iterator<Species> species_iterator = this.getSpecies().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			if (species.getName().equals(name))
				return species;
		}
		return null;
	}
	
	public Reaction getReactionById(String id){
		Iterator<Reaction> reaction_iterator = this.getReactions().iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			if (reaction.getId().equals(id))
				return reaction;
		}
		return null;
	}
	
	public Reaction getReactionByName(String name){
		Iterator<Reaction> reaction_iterator = this.getReactions().iterator();
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
		MyEquivalenceClass<Complex> linkage_class = this.getLinkageClasses().getEquivalenceClassByElement(c1);
		// get the set of reactions attached to the complexes
		MySet<Reaction> reactions = (MySet)this.getEdges(linkage_class);
		
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
		String[] ret = new String[this.getSpecies().size()];
		
		int i = 0;
		Iterator<Species> species_iterator = this.getSpecies().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			
			ret[i] = species.getId();
			i++;
		}
		
		return ret;
	}
	
	public String[] getSpeciesNames(){
		String[] ret = new String[this.getSpecies().size()];
		
		int i = 0;
		Iterator<Species> species_iterator = this.getSpecies().iterator();
		while (species_iterator.hasNext()){
			Species species = species_iterator.next();
			
			ret[i] = species.getName();
			i++;
		}
		
		return ret;
	}
	
	public String[] getReactionIds(){
		String[] ret = new String[this.getReactions().size()];
		
		int i = 0;
		Iterator<Reaction> reaction_iterator = this.getReactions().iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			
			ret[i] = reaction.getId();
			i++;
		}
		
		return ret;
	}
	
	public String[] getReactionNames(){
		String[] ret = new String[this.getReactions().size()];
		
		int i = 0;
		Iterator<Reaction> reaction_iterator = this.getReactions().iterator();
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
	public boolean isTerminal(MyEquivalenceClass<Complex> strong_linkage_class){
		Iterator<Complex> iterator = strong_linkage_class.iterator();		// get the iterator of all complexes of this strong linkage class
		while (iterator.hasNext()){											// loop over all complexes of this strong linkage class
			Complex complex = iterator.next();
			MySet<Complex> neighbours = (MySet)this.getNodeNeighboursForward(complex);	// get all neighbouring complexes of the current complex in the direction of the edges adjacent to the current complex
			
			Iterator<Complex> neighbour_iterator = neighbours.iterator();	// get the iterator of the neighbouring complexes
			while (neighbour_iterator.hasNext()){							// loop over all neighbouring complexes
				Complex neighbour = neighbour_iterator.next();
				if (!strong_linkage_class.contains(neighbour))				// if there exists at least one neighbouring complex which is not element of this strong linkage class, then this strong linkage class cannot be terminal
					return false;											// therefore, return false
			}
		}
		
		return true; // if all complexes which can be reached from inside this strong linkage class are elements of this strong linkage class, then this strong linkage class must be terminal
	}
	
	public ReactionNetwork getShortestPathBetweenComplexSets(MySet<Complex> subset1, MySet<Complex> subset2, MySet<Complex> superset, boolean directed) throws Exception{
		if (!superset.containsAll(subset1) || !superset.containsAll(subset2))
			return null;
		
		Complex node1 = subset1.head();
		Complex node2 = subset2.head();
		
		Complex source = (new Complex()).addSpecies(new Species("source"));
		Complex sink = (new Complex()).addSpecies(new Species("sink"));
		
		Reaction reaction1 = new Reaction("source_reaction", source, node1);
		Reaction reaction2 = new Reaction("sink_reaction", node2, sink);
		
		this.addReaction(reaction1);
		this.addReaction(reaction2);
		
		//System.out.println("creating: " + reaction1.toString());
		//System.out.println("creating: " + reaction2.toString());
		
		ReactionNetwork ret = new ReactionNetwork();
		MySet<Reaction> route = (MySet)this.dijkstra(source, sink, directed);

		this.removeReaction(reaction1);
		this.removeReaction(reaction2);
		
		if (route == null)
			return ret;
		
		Iterator<Reaction> iterator = route.iterator();
		while (iterator.hasNext()){
			Reaction reaction = iterator.next();
			if (!(reaction.equals(reaction1) || reaction.equals(reaction2)) &&							// we do not want helper reactions
				!(subset1.contains(reaction.getSource()) && subset1.contains(reaction.getSink())) &&	// we do not want reactions from first set of reactions
				!(subset2.contains(reaction.getSource()) && subset2.contains(reaction.getSink())))		// we do not want reactions from second set of reactions
				ret.addReaction(reaction);
		}
		
		return ret;
	}
}