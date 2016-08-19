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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import math.field.MyDouble;
import math.linalg.MyMatrix;
import math.set.MySet;
import singular.*;

import miscellaneous.*;

public class ChemicalReactionNetwork extends ReactionNetwork{
	private HashMap<Reaction, RateConstant> K;
	private MyMatrix<Reaction, Complex> Ik;
	
	public ChemicalReactionNetwork() throws Exception{
		this.K = new HashMap<Reaction, RateConstant>();
	}
	
	public ChemicalReactionNetwork(ReactionNetwork reaction_network) throws Exception{
		this.K = new HashMap<Reaction, RateConstant>();
		
		MySet<Reaction> reactions = reaction_network.getReactions();
		Iterator<Reaction> reaction_iterator = reactions.iterator();
		while (reaction_iterator.hasNext())
			this.addReactionAndRateConstant(reaction_iterator.next(), new RateConstant(1));
	}
	
	public void addReactionAndRateConstant(Reaction reaction, RateConstant rate_constant){
		this.addReaction(reaction);
		this.K.put(reaction, rate_constant);
	}
	
	public RateConstant getRateConstant(Reaction reaction){
		return this.K.get(reaction);
	}
	
	/**
	 * Creates Ik matrix.
	 */
	public void makeIkMatrix(){
		this.Ik = new MyMatrix<Reaction,Complex>();
		
		ArrayList<Reaction> reaction_array = this.getReactions().toArrayList();
		ArrayList<Complex> complex_array = this.getComplexes().toArrayList();
		for (int j = 0; j < reaction_array.size(); j++){
			Reaction reaction = reaction_array.get(j);
			RateConstant rate_constant = this.K.get(reaction);
			
			Complex substrate = reaction.getSubstrate();
			for (int i = 0; i < complex_array.size(); i++){
				Complex complex = complex_array.get(i);
				
				MyDouble<Reaction,Complex> entry;
				if (complex.equals(substrate))
					entry = new MyDouble<Reaction,Complex>(new Double(rate_constant.getRateConstant()),reaction,complex);
				else
					entry = new MyDouble<Reaction,Complex>(new Double(0),reaction,complex);
				
				this.Ik.add(entry);
			}
		}
	}
	
	public void exportToOctave(String filepath, String fileName) throws Exception{
		// fileName without .m
		try{
			BufferedWriter outFile = new BufferedWriter(new FileWriter(filepath + fileName + ".m"));
			outFile.write("function dSdt = " + fileName + "(t,S)");
			outFile.newLine();
			outFile.newLine();
			outFile.write("% ----- input concentrations -----");
			outFile.newLine();
			MySet<Species> species = this.getNMatrix().getFirstDimensionSet().clone();
			int count = 1;
			while(!species.isEmpty()){
				outFile.write(species.head().toString());
				outFile.write(" = S(" + Integer.toString(count) + ");");
				outFile.newLine();
				count +=1;
				species.remove(species.head());
			}
			outFile.newLine();
			outFile.write("% ----- reaction rates -----");
			outFile.newLine();
			Iterator<Reaction> reaction_iterator = this.getReactions().iterator();
			count = 1;
			while (reaction_iterator.hasNext()){
				Reaction currentReaction=reaction_iterator.next();				
				outFile.write("v" + Integer.toString(count) + " = " + Double.toString(this.K.get(currentReaction).getRateConstant()));				
				MySet<Species> currentSubstrate = currentReaction.getSubstrate().getSpecies().clone();								
				while (!currentSubstrate.isEmpty()){
					Double currentOccurence = currentReaction.getSubstrate().getObject().getNumberOfOccurences(currentSubstrate.head());
					while(currentOccurence>0){
						outFile.write(" * ");
						outFile.write(currentSubstrate.head().toString());
						currentOccurence -=1;
					}
					currentSubstrate.remove(currentSubstrate.head());
				}
				outFile.write(";");
				outFile.newLine();
				count +=1;
			}		
			outFile.newLine();
			outFile.write("V = [");			
			for (int i = 1; i < count; i++){
				outFile.newLine();	
				outFile.write("v"+Integer.toString(i));
			}			
			outFile.write("];");
			outFile.newLine();
			outFile.newLine();
			outFile.write("% ----- stoichiometric matrix -----");
			outFile.newLine();
			outFile.write("N = [ ");
			outFile.newLine();
			outFile.write(this.getNMatrix().toString());
			outFile.write("];");
			outFile.newLine();
			outFile.newLine();
			outFile.write("% ----- multiply -----");			
			outFile.newLine();
			outFile.write("dSdt = N * V;");
			outFile.close();			
		}catch (IOException e){
			System.out.println("HELLO WORLD");
		}
	}
	
	private Ideal makeIdeal() throws Exception{
		MySet<Reaction> reactions = this.getReactions();
		ArrayList<Monomial> fluxes = new ArrayList<Monomial>();
		
		Iterator<Reaction> reaction_iterator = reactions.iterator();
		while (reaction_iterator.hasNext()){
			Reaction reaction = reaction_iterator.next();
			Complex substrate = reaction.getSubstrate();
			Monomial monomial = substrate.getMonomial();
			
			Variable variable = new Variable(reaction.getName(), 1, true);
			monomial.addVariable(0, variable);
			fluxes.add(monomial);
		}		
		
		MySet<Species> species = this.getSpecies();
		ArrayList<Species> species_array = species.toArrayList();
		MyMatrix<Species,Reaction> N = this.getNMatrix();
		Ideal ret = new Ideal("I");
		for (int i = 0; i < N.getHeight(); i++){
			Polynomial polynomial = new Polynomial(species_array.get(i).getName());
			for (int j = 0; j < N.getWidth(); j++){
				if (!N.getEntry(i, j).isZero()){
					int coeff = -1;
					if (N.getEntry(i, j).getEntry() instanceof Integer)
						coeff = ((Integer)N.getEntry(i, j).getEntry()).intValue();
					if (N.getEntry(i, j).getEntry() instanceof Double)
						coeff = ((Double)N.getEntry(i, j).getEntry()).intValue();
					
					Monomial monomial = fluxes.get(j).clone();
					monomial.setCoefficient(coeff);
					polynomial.addMonomial(monomial);
				}
			}
			ret.addPoylnomial(polynomial);
		}
		
		return ret;
	}
	
	public String toReadableODEString() throws Exception{
		Ideal ret = this.makeIdeal();
		
		return ret.toReadableString();
	}
	
	public String toLaTeXODEString() throws Exception{
		Ideal ret = this.makeIdeal();
		
		return ret.toLaTeXString();
	}
	
	public String toSingularODEString() throws Exception{
		Ideal ret = this.makeIdeal();
		
		return ret.toSingularString();
	}
	
	public MyMatrix<Reaction, Complex> getIkMatrix() throws Exception{
		if (this.Ik == null)
			this.makeIkMatrix();
		
		return this.Ik;
	}
	
	public MyMatrix<Reaction,Complex> getRandomIkMatrix(){
		if (this.Ik == null)
			this.makeIkMatrix();
		
		Random rand = new Random();
		
		MyMatrix<Reaction,Complex> ret = new MyMatrix<Reaction,Complex>();
		for (int i = 0; i < this.Ik.getHeight(); i++){
			for (int j = 0; j < this.Ik.getWidth(); j++){
				MyDouble<Reaction,Complex> entry;
				
				if (!this.Ik.getEntry(i, j).isZero())
					entry = new MyDouble<Reaction,Complex>(1000*rand.nextDouble(),this.Ik.getEntry(i, j).getFirstDimension(),this.Ik.getEntry(i, j).getSecondDimension());
				else
					entry = new MyDouble<Reaction,Complex>(new Double(0),this.Ik.getEntry(i, j).getFirstDimension(),this.Ik.getEntry(i, j).getSecondDimension());

				ret.add(entry);
			}
		}
		
		return ret;
	}
}