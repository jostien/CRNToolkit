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

package system.process;

import miscellaneous.*;

import java.io.*;

public class MyProcess extends MyProgram{
	private MyRegexp regexp;
	private String regex;
	private String[] status;
	
	public MyProcess(){
		this.regexp = new MyRegexp();
	}
	
	public void run() throws Exception{
		this.run(false);
	}
	
	public void run(boolean verbose) throws Exception{
		Process p;
		if (this.isCmdString()){
			if (verbose)
				System.out.println("Executing '" + this.getCmdString() + "'");
			p = Runtime.getRuntime().exec((String)this.getCmd());
		} else {
			if (verbose)
				System.out.println("Executing '" + this.getCmdString() + "'");
			p = Runtime.getRuntime().exec((String[])this.getCmd());
		}

		BufferedReader bisr = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader besr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		boolean b = true;
		while (b){
			this.printBufferedReader(bisr, verbose);
			this.printBufferedReader(besr, verbose);
			try{
				p.exitValue();
				b = false;
			} catch(Exception e){
			}
			
			Thread.sleep(100);
		}
		this.printBufferedReader(bisr, verbose);
		this.printBufferedReader(besr, verbose);
		bisr.close();
		besr.close();
	}
	
	private void printBufferedReader(BufferedReader br, boolean verbose) throws Exception{
		String line = "";
		while ((line = br.readLine()) != null){
			if (verbose)
				System.out.println(line);
			String[] matches = this.regexp.matches(line, this.regex);
			if (matches != null && matches.length > 0)
				this.status = matches;
		}
	}
	
	public void setRegex(String regex){
		this.regex = regex;
	}
	
	public String[] getStatus(){
		return this.status;
	}
}
