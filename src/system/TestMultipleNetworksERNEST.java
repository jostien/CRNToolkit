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

package system;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import system.parsers.sbml.SBMLParser;
import system.parsers.simple.SimpleParser;
import system.process.MyProcess;

import crnt.Complex;
import crnt.ReactionNetwork;

public class TestMultipleNetworksERNEST {
	private String pathToERNEST;
	private String pathToMatlab;
	private String[] pathsToNetworks = new String[0];
	private String[] pathsToSBML = new String[0];
	private String pathToTempFolder;
	
	public TestMultipleNetworksERNEST(String pathToERNEST, String pathToMatlab, String pathToTempFolder){	
		this.pathToERNEST = pathToERNEST;
		this.pathToMatlab = pathToMatlab;
		this.pathToTempFolder = pathToTempFolder;		
	}
	
	public String[] getPathsToNetworks(){
		return this.pathsToNetworks;
	}
	
	public String[] getPathsToSBML(){
		return this.pathsToSBML;
	}
	
	public void setPathsToSBML(String[] paths){
		this.pathsToSBML = paths;
	}
	
	public void setPathsToNetworks(String[] paths){
		this.pathsToNetworks = paths;
	}
	
	public void executeTest() throws Exception{
		executeTest(false);
	}
	
	public void executeTest(boolean verbose) throws Exception{
		TestERNEST testHandler = new TestERNEST(pathToERNEST);
		testHandler.setProgramPath(pathToMatlab);
		testHandler.setProgramName("matlab");
		ExportToERNEST exporter = new ExportToERNEST();
		try{			
			FileWriter outFile = new FileWriter(this.pathToTempFolder + "resultsOfComparison.csv");
			PrintWriter out = new PrintWriter(outFile);
			out.println("network	d(BOP)	d(ERNEST)");
			for (int i = 0; i < pathsToNetworks.length; i++){
				System.out.println("Comparing network (from file) " +i);
				String pathToCurrentNetwork = pathsToNetworks[i];
				out.print(pathToCurrentNetwork +"	");
				ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + pathToCurrentNetwork);				
				out.print(reaction_network.getDeficiency()+"	");
				String pathToModel = pathToTempFolder + "ERNEST_input" + i + ".m";
				exporter.writeFile(reaction_network, pathToModel);
				testHandler.setInputPath(pathToTempFolder);
				testHandler.setOutputPath(pathToTempFolder);
				testHandler.writeInputFile(pathToModel);				
				testHandler.run(verbose);
				out.println(testHandler.parseOutputFile());
				testHandler.deleteFiles();
				(new File(pathToModel)).delete();
			}
			for (int i = 0; i < pathsToSBML.length; i++){
				System.out.println("Comparing network (from SBML) " +i);
				String pathToCurrentNetwork = pathsToSBML[i];
				out.print(pathToCurrentNetwork +"	");
				ReactionNetwork reaction_network = (new SBMLParser()).parse(System.getProperty("user.dir") + pathToCurrentNetwork);		
				out.print(reaction_network.getDeficiency()+"	");
				String pathToModel = pathToTempFolder + "ERNEST_input" + i + ".m";
				exporter.writeFile(reaction_network, pathToModel);
				testHandler.setInputPath(pathToTempFolder);
				testHandler.setOutputPath(pathToTempFolder);
				testHandler.writeInputFile(pathToModel);				
				testHandler.run(verbose);
				out.println(testHandler.parseOutputFile());
				testHandler.deleteFiles();
				(new File(pathToModel)).delete();				
			}
			out.close();
		} catch (IOException e){
            e.printStackTrace();
		}
	}
}
