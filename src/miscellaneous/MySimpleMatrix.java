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

package miscellaneous;

import java.util.ArrayList;

public class MySimpleMatrix<FirstD,SecondD> {
	private MySet<FirstD> first_dimension;
	private MySet<SecondD> second_dimension;

	private double[][] matrix;
	
	public MySimpleMatrix(){
	}
	
	public MySimpleMatrix(MySet<FirstD> first_dimension, MySet<SecondD> second_dimension){
		this.first_dimension = first_dimension;
		this.second_dimension = second_dimension;
		this.matrix = new double[first_dimension.size()][second_dimension.size()];
	}
	
	public void setSimpleEntry(int i, int j, double value){
		this.matrix[i][j] = value;
	}
	
	public double getSimpleEntry(int i, int j){
		return this.matrix[i][j];
	}
	
	public double[][] toDoubleMatrix() throws Exception{
		return this.matrix;
	}
	
	public MySet<FirstD> getFirstDimensionSet(){
		return this.first_dimension;
	}
	
	public MySet<SecondD> getSecondDimensionSet(){
		return this.second_dimension;
	}
	
	public void setFirstDimensionSet(MySet<FirstD> first_dimension){
		this.first_dimension = first_dimension;
	}
	
	public void setSecondDimensionSet(MySet<SecondD> second_dimension){
		this.second_dimension = second_dimension;
	}
	
	public int getHeight(){
		return this.first_dimension.size();
	}
	
	public int getWidth(){
		return this.second_dimension.size();
	}
	
	public int getFirstDimensionIndex(FirstD fd){
		return this.first_dimension.getIndex(fd);
	}
	
	public int getSecondDimensionIndex(SecondD sd){
		return this.second_dimension.getIndex(sd);
	}
	
	public FirstD getFirstDimension(int index){
		return this.first_dimension.toArrayList().get(index);
	}
	
	public SecondD getSecondDimension(int index){
		return this.second_dimension.toArrayList().get(index);
	}
	
	public String toOctaveString() throws Exception{
		StringBuffer buffer = new StringBuffer("A = [\n");
		
		for (int i = 0; i < this.getHeight(); i++){
			for (int j = 0; j < this.getWidth(); j++)
				buffer = buffer.append(this.getSimpleEntry(i, j)).append(",");
					
			buffer.setLength(buffer.length() - 1);
			buffer = buffer.append(";\n");
		}
	
		return buffer.append("]").toString();
	}
}
