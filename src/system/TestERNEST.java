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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import system.process.*;

public class TestERNEST extends MyProcess{
	private String pathToERNEST;

	public TestERNEST(String pathToERNEST){
		this.setIdPrefix("ERNEST_");
		this.pathToERNEST = pathToERNEST;
		this.setCmdToStringArray();
		
		FileInformation fi = this.getInputFileInformation();
		fi.setId(fi.getId() + ".m");
		this.setInputFileInformation(fi);
	}
	
	public void writeInputFile(String data){
			//String filename, String pathToModel){
		try{
			FileWriter outFile = new FileWriter(this.getInputFile());
			PrintWriter out = new PrintWriter(outFile);
						
			out.println("addpath([matlabroot '/addon/cobra/external/toolboxes/glpkmex:'])");
			out.println("clear model;");
			out.println("run " + data + ";");
			out.println("getCurrentPath = pwd;");
			out.println("cd " + pathToERNEST + ";");
			out.println("result = model_analysis_glpk(model);");
			out.println("cd(getCurrentPath);");
			out.println("fid = fopen('"+ this.getOutputFile() + "', 'wt');");
			out.println("fprintf(fid, '%d', result.d)");
			out.println("fclose(fid);");
			out.println("quit;");			
			out.close();
		} catch (IOException e){
            e.printStackTrace();
		}
	}
	
	public Object getCmd(){
        String[] cmd = new String[6];
        cmd[0] = this.getProgramPath() + this.getProgramName();
        cmd[1] = "-nodesktop";
        cmd[2] = "-nosplash";
        cmd[3] = "-nojvm";
        cmd[4] = "-r";
        cmd[5] = "run " + this.getInputFile();           		
		return cmd;
	}
	
	public String getCmdString() throws Exception{		
		StringBuilder builder = new StringBuilder();
		for(String s : (String[])this.getCmd()) {
		    builder.append(s);
		    builder.append(" ");
		}
		return builder.toString();			
	}
	
	public Integer parseOutputFile() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(new File(this.getOutputFile())));
		String line = "";
		int ret = -1;
		while ((line = br.readLine()) != null){
			if (line.length() > 0 && line.charAt(0) != '#'){			
				ret = new Integer(line);				
			}
		}
		br.close();
		
		return ret;
	}
	
	public void deleteFiles(){
		(new File(this.getInputFile())).delete();
		(new File(this.getOutputFile())).delete();
	}
}
