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

import java.util.*;
import miscellaneous.*;

public class Reaction extends MyEdge<MyMultiset<Species>>{
	private String id;
	private String name;
	private Reaction inverse;
	private String notes;
	
	public Reaction(String id, Complex substrate, Complex product) throws Exception{
		super(substrate, product);
		
		this.id = id;
		this.name = "";
		this.inverse = null;
		this.notes = null;
		
		this.checkId();
	}
	
	public Reaction(String id, String name, Complex substrate, Complex product) throws Exception{
		super(substrate, product);
		
		this.id = id;
		this.name = name;
		this.inverse = null;
		this.notes = null;
		
		this.checkId();
	}
	
	public Reaction(String id, String name, Complex substrate, Complex product, Reaction inverse) throws Exception{
		super(substrate, product);
		
		this.id = id;
		this.name = name;

		if (inverse != null)
			if (!inverse.getSubstrate().equals(this.getProduct()) || !inverse.getProduct().equals(this.getSubstrate()))
				throw new Exception("Given reaction is not inverse of this Reaction.");
		
		this.inverse = inverse;
		this.notes = null;
		
		this.checkId();
	}
	
	public Reaction(String id, String name, Complex substrate, Complex product, Reaction inverse, String notes) throws Exception{
		super(substrate, product);
		
		this.id = id;
		this.name = name;

		if (inverse != null)
			if (!inverse.getSubstrate().equals(this.getProduct()) || !inverse.getProduct().equals(this.getSubstrate()))
				throw new Exception("Given reaction is not inverse of this Reaction.");
		
		this.inverse = inverse;
		this.notes = notes;
		
		this.checkId();
	}

	public void setId(String id) throws Exception{
		this.id = id;
		
		this.checkId();
	}
	
	public void checkId() throws Exception{
		if (this.id == null || this.id.length() == 0)
			throw new Exception("No valid reaction identifier.");
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setSubstrate(Complex substrate){
		this.setSource(substrate);
	}
	
	public void setProduct(Complex product){
		this.setSink(product);
	}
	
	public Complex getSubstrate(){
		return (Complex)this.getSource();
	}
	
	public Complex getProduct(){
		return (Complex)this.getSink();
	}
	
	public Reaction getInverse(){
		return this.inverse;
	}
	
	public void setInverse(Reaction inverse){
		this.inverse = inverse;
	}
	
	public String getNotes(){
		return this.notes;
	}
	
	public void setNotes(String notes){
		this.notes = notes;
	}
	
	public boolean containsSpecies(Species species){
		if (((Complex)this.getSource()).getObject().contains(species))
			return true;
		if (((Complex)this.getSink()).getObject().contains(species))
			return true;
		return false;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String toString(){
		return this.id + " := " + ((Complex)this.getSource()).toString() + " -> " + ((Complex)this.getSink()).toString();
	}
	
	public String toLaTeXString() throws Exception{
		return "R_{" + this.id + "}" + " := " + ((Complex)this.getSource()).getObject().toLaTeXString() + " \\rightarrow " + ((Complex)this.getSink()).getObject().toLaTeXString();
	}
	
	public int compareTo(Reaction reaction){
		return this.toString().compareTo(((Reaction)reaction).toString());
	}
	
	public boolean equals(Reaction o){
		return this.toString().equals(((Reaction)o).toString());
	}
	
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	public MySet<Reaction> getNeighbours(MySet<Reaction> reactions){
		MySet<Reaction> ret = new MySet<Reaction>();
		
		Iterator<Reaction> iterator = reactions.iterator();
		while (iterator.hasNext()){
			Reaction reaction_from_set = iterator.next();
			if (this.getProduct().equals(reaction_from_set.getSubstrate()))
				ret.add(reaction_from_set);
			if (this.getSubstrate().equals(reaction_from_set.getProduct()))
				ret.add(reaction_from_set);
			if (this.getProduct().equals(reaction_from_set.getProduct()))
				ret.add(reaction_from_set);
			if (this.getSubstrate().equals(reaction_from_set.getSubstrate()))
				ret.add(reaction_from_set);
		}
		
		return ret;
	}
	
	public MySet<Reaction> getDirectionalNeighbours(MySet<Reaction> reactions){
		MySet<Reaction> ret = new MySet<Reaction>();
		
		Iterator<Reaction> iterator = reactions.iterator();
		while (iterator.hasNext()){
			Reaction reaction_from_set = iterator.next();
			if (this.getProduct().equals(reaction_from_set.getSubstrate()))
				ret.add(reaction_from_set);
		}
		
		return ret;
	}

	public static MySet<Reaction> getReactionsConsumingComplex(Complex complex, MySet<Reaction> reactions){
		MySet<Reaction> ret = new MySet<Reaction>();
		
		Iterator<Reaction> iterator = reactions.iterator();
		while (iterator.hasNext()){
			Reaction reaction = iterator.next();
			if (reaction.getSubstrate().equals(complex))
				ret.add(reaction);
		}
		
		return ret;
	}
	
	public static MySet<Reaction> getReactionsConsumingComplexes(MySet<Complex> complexes, MySet<Reaction> reactions){
		MySet<Reaction> ret = new MySet<Reaction>();
		
		Iterator<Complex> iterator = complexes.iterator();
		while (iterator.hasNext()){
			Complex complex = iterator.next();
			ret = ret.union(Reaction.getReactionsConsumingComplex(complex, reactions));
		}
		
		return ret;
	}
	
	public static MySet<Reaction> getReactionsConsumingOrProducingComplex(Complex complex, MySet<Reaction> reactions){
		MySet<Reaction> ret = new MySet<Reaction>();
		
		Iterator<Reaction> iterator = reactions.iterator();
		while (iterator.hasNext()){
			Reaction reaction = iterator.next();
			if (reaction.getSubstrate().equals(complex) || reaction.getProduct().equals(complex))
				ret.add(reaction);
		}
		
		return ret;
	}
	
	public static MySet<Reaction> getReactionsConsumingOrProducingComplexes(MySet<Complex> complexes, MySet<Reaction> reactions){
		MySet<Reaction> ret = new MySet<Reaction>();
		
		Iterator<Complex> iterator = complexes.iterator();
		while (iterator.hasNext()){
			Complex complex = iterator.next();
			ret = ret.union(Reaction.getReactionsConsumingOrProducingComplex(complex, reactions));
		}
		
		return ret;
	}
	
	public static MySet<Complex> getComplexNeighboursForward(Complex complex, MySet<Reaction> reactions){
		MySet<Complex> ret = new MySet<Complex>();
		
		MySet<Reaction> reactions_consuming_complex = Reaction.getReactionsConsumingComplex(complex, reactions);
		Iterator<Reaction> iterator = reactions_consuming_complex.iterator();
		while (iterator.hasNext()){
			Reaction reaction = iterator.next();
			ret.add(reaction.getProduct());
		}
		
		return ret;
	}
	
	public static MySet<Complex> getComplexNeighboursForward(Complex complex, MySet<Complex> rest, MySet<Reaction> reactions){
		MySet<Complex> ret = new MySet<Complex>();
		
		MySet<Reaction> reactions_consuming_complex = Reaction.getReactionsConsumingComplex(complex, reactions);
		Iterator<Reaction> iterator = reactions_consuming_complex.iterator();
		while (iterator.hasNext()){
			Reaction reaction = iterator.next();
			if (rest.contains(reaction.getProduct()))
				ret.add(reaction.getProduct());
		}
		
		return ret;
	}
	
	public static MySet<Complex> getComplexNeighboursForwardBackward(Complex complex, MySet<Complex> rest, MySet<Reaction> reactions){
		MySet<Complex> ret = new MySet<Complex>();
		
		MySet<Reaction> reactions_consuming_or_producing_complex = Reaction.getReactionsConsumingOrProducingComplex(complex, reactions);
		reactions.removeAll(reactions_consuming_or_producing_complex);

		Iterator<Reaction> iterator = reactions_consuming_or_producing_complex.iterator();
		while (iterator.hasNext()){
			Reaction reaction = iterator.next();
			if (rest.contains(reaction.getProduct()))
				ret.add(reaction.getProduct());
			if (rest.contains(reaction.getSubstrate()))
				ret.add(reaction.getSubstrate());
		}
		
		return ret;
	}	
	
	public static MySet<Complex> getComplexes(MySet<Reaction> reactions){
		MySet<Complex> ret = new MySet<Complex>();
		
		Iterator<Reaction> iterator = reactions.iterator();
		while (iterator.hasNext()){
			Reaction reaction_from_set = iterator.next();
			ret.add(reaction_from_set.getSubstrate());
			ret.add(reaction_from_set.getProduct());
		}
		
		return ret;
	}
	
	public static MySet<Species> getSpecies(MySet<Reaction> reactions){
		MySet<Species> ret = new MySet<Species>();
		
		Iterator<Reaction> iterator = reactions.iterator();
		while (iterator.hasNext()){
			Reaction reaction_from_set = iterator.next();
			MySet<Species> substrate_species = reaction_from_set.getSubstrate().getSpecies();
			MySet<Species> product_species = reaction_from_set.getProduct().getSpecies();
			
			ret.addAll(substrate_species);
			ret.addAll(product_species);
		}
		
		return ret;
	}

	public static MySet<Complex> getSubstrateComplexes(MySet<Reaction> reactions){
		MySet<Complex> ret = new MySet<Complex>();
		
		Iterator<Reaction> reaction_iterator = reactions.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			ret.add(reaction.getSubstrate());
		}
		
		return ret;
	}
	
	public boolean isExportReaction(){
		return this.isSubstrateZeroComplex() || this.isProductZeroComplex();
	}
	
	public boolean isSubstrateZeroComplex(){
		return this.getSubstrate().equals(new Complex());
	}
	
	public boolean isProductZeroComplex(){
		return this.getProduct().equals(new Complex());
	}
	
	public boolean isReversible(){
		return this.inverse != null;
	}
	
	public Reaction clone(){
		try {
			return new Reaction(this.getId(), this.getName(), this.getSubstrate().clone(), this.getProduct().clone(), this.getInverse(), this.getNotes());
		} catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
		
		return null;
	}
	
	public static String ID_PREFIX = "R_";
}
