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

package system.process.octave;

import java.io.*;

import system.process.*;

public class MyOctaveRref extends MyProcess{
	public MyOctaveRref(){
		this.setIdPrefix("rref_");
	}
	
	public void makeInputFile(String data, int tolerance) throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(this.getInputFile())));
		bw.write("format long;\n");
		bw.write(data + ";\n");
		bw.write("rref_ = rref(A, 10^(" + tolerance + "));\n");
		bw.write("save_precision(24);\n");
		bw.write("save " + this.getOutputFile() + " rref_;");
		bw.close();
	}
	
	public String getCmd(){
		String ret = this.getProgramPath() + this.getProgramName() + " -qf " + this.getInputFile();
		
		return ret;
	}
	
	public double[][] parseOutputFile() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(new File(this.getOutputFile())));
		String line = "";
		int height = 0;
		int width = 0;
		while ((line = br.readLine()) != null){
			if (line.length() > 0 && line.charAt(0) != '#'){			
				height++;
				width = line.split(" ").length;
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(new File(this.getOutputFile())));
		double[][] matrix = new double[height][width-1];
		int c = 0;
		while ((line = br.readLine()) != null){
			if (line.length() > 0 && line.charAt(0) != '#'){
				//System.out.println(line);
				String[] cells = line.split(" ");
				for (int j = 1; j < width; j++){
					//System.out.println(cells[j]);
					matrix[c][j-1] = (new Double(cells[j])).doubleValue();
				}
				c++;
			}
		}
		br.close();
		
		return matrix;
	}
}
