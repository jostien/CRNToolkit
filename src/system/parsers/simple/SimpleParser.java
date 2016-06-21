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

package system.parsers.simple;

import crnt.*;
import system.parsers.*;

import java.io.*;

public class SimpleParser extends Parser{
	public SimpleParser(){
	}
	
	public ReactionNetwork parse(File file) throws Exception{
		return this.parse(file.getAbsolutePath());
	}
	
	public ReactionNetwork parse(String file_name) throws Exception{
		ReactionNetwork reaction_network = new ReactionNetwork();
		BufferedReader br = new BufferedReader(new FileReader(new File(file_name)));
		String line = "";
		while ((line = br.readLine()) != null)
			if (line.charAt(0) != '#')
				reaction_network.addReaction(this.parseString(line));
		
		br.close();
		return reaction_network;
	}
	
	public Reaction parseString(String line) throws Exception{
		Complex substrate = new Complex();
		Complex product = new Complex();
		
		line = line.replaceAll("\t", " ");
		while (!(line.indexOf("  ") == -1))
			line = line.replaceAll("  ", " ");
		
		line = line.replaceAll("[+]", "");
		while (!(line.indexOf("  ") == -1))
			line = line.replaceAll("  ", " ");

		String[] cells = line.split(" ");
		String name = cells[0];
		boolean p = false;
		for (int i = 1; i < cells.length; i++){
			Integer c = null;
			if (this.testIfNumber(cells[i]))
				c = new Integer(cells[i]);

			p = p || cells[i].equals("=");
			
			if (!cells[i].equals("=") && c != null && !p){
				for (int j = 0; j < c; j++)
					substrate.addSpecies(new Species(cells[i + 1], cells[i + 1], "default"));
				i++;
			}
			
			if (!cells[i].equals("=") && c != null && p){
				for (int j = 0; j < c; j++)
					product.addSpecies(new Species(cells[i + 1], cells[i + 1], "default"));
				i++;
			}
		}
		
		return new Reaction(name, name, substrate, product);
	}
}
