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

import java.io.*;

public class MyProgram{
	private boolean cmd_as_string;
	
	private FileInformation input_file_information;
	private FileInformation output_file_information;
	
	private String program_name;
	private String program_path;
	private String[] parameter_names;
	private String[] parameter_values;
	
	public MyProgram(){
		this.cmd_as_string = true;
		
		this.input_file_information = new FileInformation();
		this.output_file_information = new FileInformation();
		
		this.parameter_names = new String[0];
		this.parameter_values = new String[0];
		
		this.makeId();
	}
	
	public boolean isCmdString(){
		return this.cmd_as_string;
	}
	
	public boolean isCmdStringArray(){
		return !this.cmd_as_string;
	}
	
	public void setCmdToString(){
		this.cmd_as_string = true;
	}
	
	public void setCmdToStringArray(){
		this.cmd_as_string = false;
	}
	
	public void setInputPath(String input_path){
		this.input_file_information.setPath(this.correctPath(input_path));
	}
	
	public void setOutputPath(String output_path){
		this.output_file_information.setPath(this.correctPath(output_path));
	}
	
	public void setProgramPath(String program_path){
		this.program_path = this.correctPath(program_path);
	}
	
	public void setProgramName(String program_name){
		this.program_name = program_name;
	}
	
	public void setParameterNames(String[] parameter_names){
		this.parameter_names = parameter_names;
	}
	
	public void setParameterValues(String[] parameter_values){
		this.parameter_values = parameter_values;
	}
	
	public String getInputPath(){
		return this.input_file_information.getPath();
	}
	
	public String getOutputPath(){
		return this.output_file_information.getPath();
	}
	
	public String getProgramPath(){
		return this.program_path;
	}
	
	public String getProgramName(){
		return this.program_name;
	}
	
	public String[] getParameterNames(){
		return this.parameter_names;
	}
	
	public String[] getParameterValues(){
		return this.parameter_values;
	}
	
	public String getId(){
		return this.input_file_information.getId();
	}
	
	public String getIdPrefix(){
		return this.input_file_information.getIdPrefix();
	}
	
	public void setIdPrefix(String id_prefix){
		this.input_file_information.setIdPrefix(id_prefix);
		this.output_file_information.setIdPrefix(id_prefix);
	}
	
	public String getInputFile(){
		return this.input_file_information.getFileName();
	}
	
	public String getOutputFile(){
		return this.output_file_information.getFileName();
	}
	
	public Object getCmd() throws Exception{
		return "";
	}
	
	public void writeInputFile(String data) throws Exception{		
	}
	
	public Object parseOutputFile() throws Exception{
		return new Object();
	}
	
	public void deleteFiles(){
		(new File(this.getInputFile())).delete();
		(new File(this.getOutputFile())).delete();
	}
	
	public void makeId(){
		try {
			String possible_id = Long.toHexString(System.currentTimeMillis());
			this.input_file_information.setId(possible_id + "_input");
			this.output_file_information.setId(possible_id + "_output");
			
//			while ((new File(this.getInputFile())).exists()){
//				Thread.sleep(1);
//				possible_id = Long.toHexString(System.currentTimeMillis());
//				this.input_file_information.setId(possible_id + "_input");
//				this.output_file_information.setId(possible_id + "_output");
//			}
		} catch(Exception e){
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private String correctPath(String path){
		if (path.length() > 0)
			if (path.charAt(path.length() - 1) != '/')
				return path + "/";
		return path;
	}
	
	public String getCmdString() throws Exception{
		return "";
	}
	
	public FileInformation getInputFileInformation(){
		return this.input_file_information;
	}
	
	public void setInputFileInformation(FileInformation input_file_information){
		this.input_file_information = input_file_information;
	}
	
	public FileInformation getOutputFileInformation(){
		return this.output_file_information;
	}
	
	public void setOutputFileInformation(FileInformation output_file_information){
		this.output_file_information = output_file_information;
	}
}
