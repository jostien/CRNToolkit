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

package system.process;

public class FileInformation {
	private String id;
	private String id_prefix;
	private String path;
	
	public FileInformation(){
	}
	
	public FileInformation(String path, String id_prefix, String id){
		this.path = path;
		this.id = id;
		this.id_prefix = id_prefix;
	}
	
	public String getId(){
		return this.id;
	}
	
	public String getIdPrefix(){
		return this.id_prefix;
	}
	
	public String getPath(){
		return this.path;
	}
	
	public String getFileName(){
		return this.path + this.id_prefix + this.id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public void setIdPrefix(String id_prefix){
		this.id_prefix = id_prefix;
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public FileInformation copy(){
		return new FileInformation(this.path, this.id_prefix, this.id);
	}
}
