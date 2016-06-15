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

import java.util.Iterator;

import crnt.*;

import singular.*;
import system.parsers.simple.SimpleParser;
import system.parsers.sbml.SBMLWriter;
import system.parsers.cna.CNAParser;

import miscellaneous.*;

public class Main_Jost {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("HALLO WELT");
		System.out.println();
		
		MySet<String> A = new MySet<String>();
		A.addElement("HALLO WELT").addElement("Foo fighters").addElement("Der Fisch sagt blubb");
		System.out.println("A = " + A.toString());
		
		MySet<String> B = new MySet<String>();
		B.addElement("HALLO WELT").addElement("Hallo Welt");
		System.out.println("B = " + B.toString());
		System.out.println();
		
		System.out.println("A union B = " + (A.union(B)).toString());
		System.out.println("A intersection B = " + (A.intersection(B)).toString());
		System.out.println("A difference B = " + (A.difference(B)).toString());
		System.out.println();
		
		MyMultiset<String> M = new MyMultiset<String>();
		M.addElement("HALLO WELT").addElement("Hallo Welt").addElement("Hallo Welt");
		M.add("Hallo Welt");
		System.out.println(M.toString());
		MyMultiset<String> M2 = M.clone();
		System.out.println(M2.toString());
		System.out.println();
			
		Reaction reaction = new Reaction("R1", (new Complex()).addSpecies(new Species("A")).addSpecies(new Species("A")), (new Complex()).addSpecies(new Species("B")));
		System.out.println(reaction.toString());
		
		reaction = (new SimpleParser()).parseString("R2             1 F = 1 A | # 0 1");
		System.out.println(reaction.toString());
		
		ReactionNetwork reaction_network = (new CNAParser()).parse(System.getProperty("user.dir") + "/examples/CNA/synechocystis.cna");
		System.out.println(reaction_network.getReactions().size());
		//System.out.println(reaction_network.getNMatrix().toString());
		System.out.println(reaction_network.toString());
		
		SBMLWriter sbmlw = new SBMLWriter();
		sbmlw.write(reaction_network, "/home/jostie/test.xml");
		
		reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_4.7");
//		ReactionNetwork reaction_network = ImportReactionNetwork.parseSBML(System.getProperty("user.dir") + "/examples/SBML/model_compartments.xml");
		System.out.println(reaction_network.getReactions().size());
		System.out.println(reaction_network.toString());
		
		System.out.println(System.getProperty("user.dir"));
		
//		MyMatrix matrix = new MyMatrix(new double[][]{{1,2,3},{1,2,3},{1,2,3}});
//		
		reaction_network.makeLinkageClasses();
		MySet<LinkageClass> linkage_classes = reaction_network.getLinkageClasses().getLinkageClasses();
		System.out.println(linkage_classes.size());
		System.out.println();
		
		Iterator<LinkageClass> iterator_linkage_classes = linkage_classes.iterator();
		int i = 0;
		while (iterator_linkage_classes.hasNext()){
			System.out.println("------------------------");
			System.out.println("linkage class # " + (i+1));
			LinkageClass lc = iterator_linkage_classes.next();
			System.out.println(lc.toString());
			System.out.println();	
			
			lc.makeStrongLinkageClasses();
			MySet<StrongLinkageClass> strong_linkage_classes = lc.getSetOfStrongLinkageClasses();
			System.out.println("# of strong linkage classes: " + strong_linkage_classes.size());
			Iterator<StrongLinkageClass> iterator_slc = strong_linkage_classes.iterator();
			while (iterator_slc.hasNext()){
				StrongLinkageClass strong_linkage_class = iterator_slc.next();
				System.out.println(strong_linkage_class.isTerminal() + "\t" + strong_linkage_class.toString());
			}
			System.out.println();
			
			i++;
		}
		
		reaction_network.makeYMatrix();
		System.out.println("Y:\n" + reaction_network.getYMatrix().toString());
		
		reaction_network.makeIaMatrix();
		System.out.println("Ia:\n" + reaction_network.getIaMatrix().toString());
		
		//------------------------------------------------------------------------------
		MyMatrix<Integer,Complex,Reaction> X = reaction_network.getIaMatrix();
		
		System.out.println(X.toString() + "\n");
		System.out.println(X.transpose().toString() + "\n");
		System.out.println(X.getRankUsingOctave("/tmp/") + "\n");
		System.out.println(X.getKernelUsingOctave("/tmp/") + "\n");
		System.out.println(X.getRowReducedEchelonFormUsingOctave("/tmp/") + "\n");
		
		System.out.println(X.mul(X.transpose()).toString());
		//------------------------------------------------------------------------------
		
		System.out.println("N:\n" + reaction_network.getNMatrix().toString());
		System.out.println("E:\n" + reaction_network.getNMatrix().getExtremeRays().toString());

		//System.out.println("extreme rays:\n" + reaction_network.getNMatrix().getExtremeRays().toString());
		
		System.out.println("Octave/Matlab-string of N:\n" + reaction_network.getNMatrix().toOctaveString());
		System.out.println();
		
		System.out.println("psi:\n" + reaction_network.getOctavePsi());
		System.out.println();
		
		// compute deficiency
		System.out.println("deficiency of reaction network: " + reaction_network.getDeficiency());
		System.out.println("# of complexes: " + reaction_network.getComplexes().size());
		System.out.println("# of linkage classes: " + reaction_network.getLinkageClasses().getLinkageClasses().size());		
		System.out.println("rank of stoichiometric matrix: " + reaction_network.getNMatrix().getRankUsingOctave("/tmp/"));
		System.out.println();
		
		System.out.println("has ACR: " + reaction_network.hasACR());
		
		ChemicalReactionNetwork crn = new ChemicalReactionNetwork();
		MySet<Reaction> reactions = reaction_network.getReactions();
		Iterator<Reaction> reaction_iterator = reactions.iterator();
		while (reaction_iterator.hasNext())
			crn.addReactionAndRateConstant(reaction_iterator.next(), new RateConstant(1.0));
		
		System.out.println(crn.getIkMatrix().toOctaveString());
		
		System.out.println("N:\n" + crn.getNMatrix().toString());
//		System.out.println(crn.getNMatrix().getKernelSpan().toOctaveString());
//		System.out.println(crn.getNMatrix().toOctaveString());
		
		System.out.println(crn.toReadableODEString());
		System.out.println(crn.toSingularODEString());
		
//		MyMatrix kernel_span = crn.getNMatrix().getKernelSpan();
//		System.out.println(kernel_span.isMultipleRows(0, 1, 0.0000001));
//		
//		MyMatrix blubb = crn.getBlubb();
//		System.out.println(blubb.toOctaveString());
//		System.out.println();
//		
//		int n = 10000;
//		MyMatrix er = null;
//		for (int j = 0; j < n; j++){
//			MyMatrix er_svd = er;
//			er = crn.getBlubb().getExtremeRays();
//			
//			if (er_svd != null && !HelpfullStuff.compareVectors(HelpfullStuff.sortColumnVectors(er_svd), HelpfullStuff.sortColumnVectors(er)))
//				System.out.println("Dependent on the rate constants");
//			
//			
//			if (j%(n/100) == 0)
//				System.out.println(j);
//		}
		
//		double[][] A_ = new double[][]{
//				{-1.0,1.0,-1.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0},
//				{1.0,-1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,-1.0,1.0,1.0},
//				{0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,-1.0,-1.0},
//				{0.0,0.0,1.0,-1.0,-1.0,0.0,0.0,0.0,-1.0,1.0,1.0,0.0,0.0,0.0},
//				{0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,-1.0,-1.0,0.0,0.0,0.0},
//				{0.0,0.0,0.0,0.0,1.0,-1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0},
//				{0.0,0.0,0.0,0.0,0.0,1.0,-1.0,-1.0,0.0,0.0,0.0,0.0,0.0,0.0},
//				{0.0,0.0,0.0,0.0,0.0,-1.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,1.0},
//				{0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,-1.0,1.0,0.0,-1.0,1.0,0.0}
//		};
//		
//		Array2DRowRealMatrix A = new Array2DRowRealMatrix(A_);
//		
//		SingularValueDecomposition svd = new SingularValueDecomposition(A);
//		
//		RealMatrix U = svd.getU();
//		RealMatrix S = svd.getS();
//		RealMatrix V = svd.getV();
//		
//		for (int i = 0; i < U.getRowDimension(); i++){
//			for (int j = 0; j < U.getColumnDimension(); j++){
//				System.out.print(U.getEntry(i, j) + "\t");
//			}
//			System.out.println();
//		}
//		System.out.println();
//		
//		for (int i = 0; i < S.getRowDimension(); i++){
//			for (int j = 0; j < S.getColumnDimension(); j++){
//				System.out.print(S.getEntry(i, j) + "\t");
//			}
//			System.out.println();
//		}
//		System.out.println();
//		
//		for (int i = 0; i < V.getRowDimension(); i++){
//			for (int j = 0; j < V.getColumnDimension(); j++){
//				System.out.print(V.getEntry(i, j) + "\t");
//			}
//			System.out.println();
//		}
//		System.out.println();
	}

}
