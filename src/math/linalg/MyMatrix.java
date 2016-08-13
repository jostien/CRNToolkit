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

package math.linalg;

import system.parsers.simple.SimpleParser;
import system.process.octave.MyOctaveKernel;
import system.process.octave.MyOctaveRank;
import system.process.octave.MyOctaveRref;

import java.util.logging.Level;
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

import ch.javasoft.polco.adapter.*;
import crnt.Complex;
import crnt.Reaction;
import crnt.ReactionNetwork;
import crnt.Species;
import math.field.MyDouble;
import math.field.MyEntry;
import math.set.MySet;

public class MyMatrix<FirstD,SecondD> extends MySet<MyEntry<?,FirstD,SecondD>>{
	private static final long serialVersionUID = 1L;
	
	private MySet<FirstD> first_dimension_set;
	private MySet<SecondD> second_dimension_set;
	private MyEntry<?,FirstD,SecondD>[][] matrix;
	
	public static void main(String[] args) throws Exception{
		ReactionNetwork reaction_network = (new SimpleParser()).parse(System.getProperty("user.dir") + "/examples/simple/Feinberg1995a_example_4.7");
		
		MyMatrix<Species,Reaction> N = reaction_network.getNMatrix();
		System.out.println(N.toString());
		System.out.println(N.toOctaveString());
		
		System.out.println(N.getRankUsingOctave("/tmp"));
	}
	
	// -------------------------------------------------------------------------------------------
	//                          Methods necessary for data management
	// -------------------------------------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public MyMatrix(){
		super();

		this.matrix = null;
		this.first_dimension_set = null;
		this.second_dimension_set = null;
	}
	
	public MySet<FirstD> getFirstDimensionSet(){
		return this.first_dimension_set;
	}
	
	public MySet<SecondD> getSecondDimensionSet(){
		return this.second_dimension_set;
	}
	
	public MyEntry<?,FirstD,SecondD> getEntry(int i, int j){
		this.check();
		
		return this.matrix[i][j];
	}
	
	// this is not complete yet
	public void setEntry(int i, int j, MyEntry<?,FirstD,SecondD> entry){
		this.check();
		
		this.matrix[i][j] = entry;
	}
	
	public FirstD getFirstDimension(int i){
		this.check();
		
		return this.getFirstDimensionSet().toArrayList().get(i);
	}
	
	public SecondD getSecondDimension(int j){
		this.check();
		
		return this.getSecondDimensionSet().toArrayList().get(j);
	}
	
	/**
	 * Makes the matrix from the set of entries.
	 * 
	 * @return A set of column vectors.
	 */
	private void makeMatrix(){
		this.first_dimension_set = new MySet<FirstD>();
		this.second_dimension_set = new MySet<SecondD>();
		
		Iterator<MyEntry<?,FirstD,SecondD>> entry_iterator = this.iterator();
		while (entry_iterator.hasNext()){
			MyEntry<?,FirstD,SecondD> entry = entry_iterator.next();
			this.first_dimension_set.add(entry.getFirstDimension());
			this.second_dimension_set.add(entry.getSecondDimension());
		}
		
		// for the column vectors, accessed by the second dimension
		HashMap<SecondD,MyVector<FirstD,SecondD>> hm = new HashMap<SecondD,MyVector<FirstD,SecondD>>();
		ArrayList<MyEntry<?,FirstD,SecondD>> list = this.toArrayList();	// loop over all entries
		for (int i = 0; i < list.size(); i++){
			// get the second dimension of the current entry
			SecondD key = ((MyEntry<?,FirstD,SecondD>)list.get(i)).getSecondDimension();
			// if this object is not already contained in the hash add it as key to the hash plus a new vector 
			if (!hm.containsKey(key)){
				MyVector<FirstD,SecondD> vector = new MyVector<FirstD,SecondD>();
				hm.put(key, vector);
			}
		}
		
		// put each of the entries into the vector corresponding to the second dimension
		for (int i = 0; i < list.size(); i++){
			SecondD key = list.get(i).getSecondDimension();
			MyVector<FirstD,SecondD> vector = hm.get(key);
			MyEntry<?,FirstD,SecondD> entry = list.get(i);
			// the entries inside the vector are sorted by the first dimension
			vector.add(entry.clone().setComparatorToFirstDimension());
		}
		
		// create a set of vectors from hash, vectors are sorted by second dimension
		MySet<MyVector<FirstD,SecondD>> vectors = new MySet<MyVector<FirstD,SecondD>>();
		Iterator<SecondD> iterator = hm.keySet().iterator();
		while (iterator.hasNext()){
			SecondD key = iterator.next();
			MyVector<FirstD,SecondD> vector = hm.get(key);
			vectors.add(vector.setComparatorToSecondDimension());
		}
		
		// create a matrix of entries
		ArrayList<MyVector<FirstD,SecondD>> vector_list = vectors.toArrayList();
		int height = vector_list.get(0).size();
		int width = vector_list.size();
		this.matrix = new MyEntry[height][width];
		
		for (int j = 0; j < width; j++){
			MyVector<FirstD,SecondD> vector = vector_list.get(j);
			ArrayList<MyEntry<?,FirstD,SecondD>> entry_list = vector.toArrayList();
			for (int i = 0; i < height; i++){
				this.matrix[i][j] = entry_list.get(i);
			}
		}
	}
	
	private void check(){
		if (this.matrix == null && this.size() > 0)
			this.makeMatrix();
	}
	
	/**
	 * Creates human readable string.
	 * 
	 * @param Human readable string.
	 */
	public String toString(){
		this.check();
		
		String ret = "";
		for (int j = 0; j < this.getWidth(); j++){
			ret = ret + this.getEntry(0, j).getSecondDimension().toString() + " ";
		}
		ret = ret + "\n";
		
		for (int i = 0; i < this.getHeight(); i++){
			for (int j = 0; j < this.getWidth(); j++){
				ret = ret + this.getEntry(i, j).getEntry().toString() + " ";
			}
			ret = ret + this.getEntry(i, 0).getFirstDimension().toString() + "\n";
		}
		
		return ret;
	}

	public MyMatrix<FirstD,FirstD> diag() throws Exception{
		if (this.getWidth() > 1)
			throw new Exception("not a column vector: height = " + this.getHeight() + ", width = " + this.getWidth());
		
		MyMatrix<FirstD,FirstD> ret = new MyMatrix<FirstD,FirstD>();
		for (int i = 0; i < this.getHeight(); i++){
			for (int j = 0; j < this.getHeight(); j++){
				if (i == j){
					ret.add(new MyDouble((Double)this.getEntry(i, 0).getEntry(), this.getFirstDimension(i), this.getFirstDimension(j)));
				} else {
					ret.add(new MyDouble(0.0, this.getFirstDimension(i), this.getFirstDimension(j)));	
				}
			}
		}
		
		return ret;
	}
	
	public MyMatrix<FirstD,SecondD> getColumnAsMatrix(int j){
		this.check();
		
		MyMatrix<FirstD,SecondD> ret = new MyMatrix<FirstD,SecondD>();
		for (int i = 0; i < this.getHeight(); i++)
			ret.add(this.getEntry(i, j).clone().setComparatorToFirstDimension());
		
		return ret;
	}
	
	/**
	 * Gets a column.
	 * 
	 * @param j Index of column of interest.
	 * 
	 * @return The column.
	 */
	public MyVector<FirstD,SecondD> getColumn(int j){
		this.check();
		
		MyVector<FirstD,SecondD> ret = new MyVector<FirstD,SecondD>();
		for (int i = 0; i < this.getHeight(); i++)
			ret.add(this.getEntry(i, j).clone().setComparatorToFirstDimension());
		
		return ret;
	}
	
	/**
	 * Gets a column.
	 * 
	 * @param secondD The object defining the column of interest.
	 * 
	 * @return The column.
	 */
	public MyVector<FirstD,SecondD> getColumn(SecondD secondD){
		MyVector<FirstD,SecondD> ret = new MyVector<FirstD,SecondD>();
		Iterator<MyEntry<?,FirstD,SecondD>> entry_iterator = this.iterator();
		while (entry_iterator.hasNext()){
			MyEntry<?,FirstD,SecondD> entry = entry_iterator.next();
			
			if (entry.getSecondDimension().equals(secondD))
				ret.add(entry.clone().setComparatorToFirstDimension());
		}
		
		return ret;
	}
	
	/**
	 * Sets a column.
	 * 
	 * @param column Column to set.
	 * @param The column.
	 */
	public void setColumn(MyVector<FirstD,SecondD> column){
		ArrayList<MyEntry<?,FirstD,SecondD>> list = column.toArrayList();
		
		// bring the entries into the correct order
		MyVector<FirstD,SecondD> vector = new MyVector<FirstD,SecondD>();
		for (int i = 0; i < list.size(); i++)
			vector.add(list.get(i).clone().setComparatorToFirstDimension());
		
		// remove old column
		this.removeColumn(column.getSecondDimension());
		
		// add new column
		Iterator<MyEntry<?,FirstD,SecondD>> entry_iterator = column.iterator();
		while (entry_iterator.hasNext())
			this.add(entry_iterator.next());
	}
	
	/**
	 * Removes a column.
	 * 
	 * @param secondD The object defining the column which is to be removed.
	 *
	 */
	public void removeColumn(SecondD secondD){
		ArrayList<MyEntry<?,FirstD,SecondD>> entry_array = this.toArrayList();
		for (int i = 0; i < entry_array.size(); i++){
			MyEntry<?,FirstD,SecondD> entry = entry_array.get(i);
			
			if (entry.getSecondDimension().equals(secondD))
				this.remove(entry);
		}
		
		this.matrix = null;
	}
	
	public MyMatrix<FirstD,SecondD> getRowAsMatrix(int i){
		this.check();
		
		MyMatrix<FirstD,SecondD> ret = new MyMatrix<FirstD,SecondD>();
		for (int j = 0; j < this.getWidth(); j++)
			ret.add(this.getEntry(i, j).clone().setComparatorToSecondDimension());
		
		return ret;
	}
	
	/**
	 * Gets a row.
	 * 
	 * @param i Index of row of interest.
	 * 
	 * @return The row.
	 */
	public MyVector<FirstD,SecondD> getRow(int i){
		this.check();
		
		MyVector<FirstD,SecondD> ret = new MyVector<FirstD,SecondD>();
		for (int j = 0; j < this.getWidth(); j++)
			ret.add(this.getEntry(i, j).clone().setComparatorToSecondDimension());
		
		return ret;		
	}

	/**
	 * Gets a row.
	 * 
	 * @param firstD The object defining the row of interest.
	 * 
	 * @return The row.
	 */
	public MyVector<FirstD,SecondD> getRow(FirstD firstD){
		MyVector<FirstD,SecondD> ret = new MyVector<FirstD,SecondD>();
		Iterator<MyEntry<?,FirstD,SecondD>> entry_iterator = this.iterator();
		while (entry_iterator.hasNext()){
			MyEntry<?,FirstD,SecondD> entry = entry_iterator.next();
			
			if (entry.getFirstDimension().equals(firstD))
			ret.add(entry.clone().setComparatorToSecondDimension());
		}
		
		return ret;
	}
	
	/**
	 * Sets a row.
	 * 
	 * @param row Row to set.
	 * @param The row.
	 */
	public void setRow(MyVector<FirstD,SecondD> row){
		ArrayList<MyEntry<?,FirstD,SecondD>> list = row.toArrayList();
		
		// bring the entries into the correct order
		MyVector<FirstD,SecondD> vector = new MyVector<FirstD,SecondD>();
		for (int i = 0; i < list.size(); i++)
			vector.add(list.get(i).clone().setComparatorToSecondDimension());
		
		// remove old row
		this.removeRow(row.getFirstDimension());
		
		// add new row
		Iterator<MyEntry<?,FirstD,SecondD>> entry_iterator = row.iterator();
		while (entry_iterator.hasNext())
			this.add(entry_iterator.next());
	}
	
	/**
	 * Removes a row.
	 * 
	 * @param firstD The object defining the row which is to be removed.
	 *
	 */
	public void removeRow(FirstD firstD){
		ArrayList<MyEntry<?,FirstD,SecondD>> entry_array = this.toArrayList();
		for (int i = 0; i < entry_array.size(); i++){
			MyEntry<?,FirstD,SecondD> entry = entry_array.get(i);
			
			if (entry.getFirstDimension().equals(firstD))
				this.remove(entry);
		}
		
		this.matrix = null;
	}
	
	/**
	 * Gets the index of a specific first dimension object.
	 * 
	 * @param fd The object whose index is needed.
	 * 
	 * @return The index.
	 */
	public int getFirstDimensionIndex(FirstD fd){
		this.check();
		
		return this.getFirstDimensionSet().getIndex(fd);
	}

	/**
	 * Gets the index of a specific second dimension object.
	 * 
	 * @param sd The object whose index is needed.
	 * 
	 * @return The index.
	 */	
	public int getSecondDimensionIndex(SecondD sd){
		this.check();
		
		return this.getSecondDimensionSet().getIndex(sd);
	}
	
	public int getHeight(){
		this.check();
		
		return this.matrix.length;
	}
	
	public int getWidth(){
		this.check();
		
		return this.matrix[0].length;
	}
	
	public MyMatrix<FirstD,SecondD> getSubMatrix(int index_i, int index_j, int height, int width){
		MyMatrix<FirstD,SecondD> ret = new MyMatrix<FirstD,SecondD>();
		
		for (int i = index_i; i < index_i + height; i++){
			for (int j = index_j; j < index_j + width; j++){
				this.getEntry(i, j).setComparator(0);
				ret.add(this.getEntry(i, j));
			}
		}
		
		return ret;
	}
	
	// -------------------------------------------------------------------------------------------
	//                                   Mathematical methods
	// -------------------------------------------------------------------------------------------
	
	public MyMatrix<SecondD,FirstD> transpose(){
		MyMatrix<SecondD,FirstD> ret = new MyMatrix<SecondD,FirstD>();
		
		Iterator<MyEntry<?,FirstD,SecondD>> iterator = this.iterator();
		while (iterator.hasNext()){
			MyEntry<?,FirstD,SecondD> entry = iterator.next();
			ret.add(entry.transpose().setComparatorToFirstDimension());
		}
		
		return ret;
	}
	
	public Object invokeMethod(Object o1, Object[] os, String method) throws Exception{
		Method found = null;
		
		Class[] classes = new Class[os.length];
		for (int i = 0; i < os.length; i++)
			classes[i] = os[i].getClass();
		
		found = o1.getClass().getMethod(method, classes);
		
		if (found != null){
			return (Object)found.invoke(o1, os);
		}
		
		return null;
	}
	
	/**
	 * Multiplies two MyMatrix objects.
	 * 
	 * @param A Second matrix.
	 * 
	 * @return The resulting matrix.
	 */
	public MyMatrix<FirstD, ?> mul(MyMatrix<SecondD, ?> A) throws Exception{
		this.check();
		
		MyMatrix<FirstD, ?> ret = new MyMatrix();
		for (int k = 0; k < A.getWidth(); k++){
			for (int i = 0; i < this.getHeight(); i++){
				MyDouble sum = new MyDouble(new Double(0),this.getEntry(i, 0).getFirstDimension(),A.getEntry(0, k).getSecondDimension());
				for (int j = 0; j < this.getWidth(); j++){
					Object o1 = this.getEntry(i, j);
					Object o2 = A.getEntry(j, k);
				
					Object result = this.invokeMethod(o1, new Object[]{o2}, "mul");
					sum = (MyDouble)this.invokeMethod(sum, new Object[]{result}, "add");
				}
				ret.add(sum);
			}
		}
	
		return ret;
	}
	
	/**
	 * Rounds the entries in a MyMatrix object.
	 * 
	 * @return The resulting matrix.
	 */
	public MyMatrix<FirstD, SecondD> round() throws Exception{
		// very ugly solved, the same holds for method mul, see the comment in MyEntry class. 
		MyMatrix<FirstD,SecondD> ret = new MyMatrix();
		
		for (int i = 0; i < this.getHeight(); i++){
			for (int j = 0; j < this.getWidth(); j++){
				MyEntry entry = (MyEntry<?,FirstD,SecondD>)this.invokeMethod(this.getEntry(i, j), new Object[]{},"round");
				ret.add(entry);
			}
		}
		
		return ret;
	}

	/**
	 * Computes the extreme rays of this MyMatrix object using polco.
	 * 
	 * @return The extreme rays of this matrix.
	 * @throws Exception
	 */
	public MyMatrix<SecondD,?> getExtremeRays() throws Exception{
		this.check();
		
		MyMatrix<SecondD, SecondD> I = this.getUnityMatrix();
		
		double[][] eq = this.toDoubleMatrix();	//the coefficients of the system equalities
		double[][] iq = I.toDoubleMatrix();		//the coefficients of the system inequalities
		Options opts = new Options();
		opts.setLoglevel(Level.FINE);
		opts.setLogFile(new File("/tmp/polco.log"));
		PolcoAdapter polco = new PolcoAdapter(opts);
		double[][] rays = polco.getDoubleRays(eq, iq);

		MyMatrix<SecondD,?> ret = new MyMatrix();
		
		ArrayList<SecondD> secondD_array = this.getSecondDimensionSet().toArrayList();
		for (int j = 0; j < rays[0].length; j++){
			for (int i = 0; i < rays.length; i++){
				MyDouble entry = new MyDouble<SecondD,String>(rays[i][j],secondD_array.get(j), "er" + i);
				ret.add(entry);
			}
		}
		
		return ret;
	}
	
	public MyMatrix<SecondD,SecondD> getUnityMatrix(){
		MyMatrix<SecondD,SecondD> ret = new MyMatrix<SecondD,SecondD>();
		ArrayList<SecondD> secondD_array = this.getSecondDimensionSet().toArrayList();
		for (int i = 0; i < secondD_array.size(); i++){
			for (int j = 0; j < secondD_array.size(); j++){
				MyEntry<?,FirstD,SecondD> entry = this.head();
				if (secondD_array.get(i).equals(secondD_array.get(j))){
					MyEntry<?,SecondD,SecondD> new_entry = new MyEntry(entry.getOne().getEntry(), secondD_array.get(i), secondD_array.get(j));
					ret.add(new_entry);
				} else {
					MyEntry<?,SecondD,SecondD> new_entry = new MyEntry(entry.getZero().getEntry(), secondD_array.get(i), secondD_array.get(j));
					ret.add(new_entry);
				}
			}
		}
		
		return ret;
	}
	
	public double[][] toDoubleMatrix() throws Exception{
		this.check();
		
		if (this.size() == 0)
			throw new Exception("No entries in matrix.");
		
		double[][] ret = new double[this.getHeight()][this.getWidth()];
		for (int i = 0; i < this.getHeight(); i++){
			for (int j = 0; j < this.getWidth(); j++){
				Object entry = this.getEntry(i, j).getEntry();
				if (entry instanceof Integer)
					ret[i][j] = ((Integer)this.getEntry(i, j).getEntry()).doubleValue();
				else if (entry instanceof Double)
					ret[i][j] = ((Double)this.getEntry(i, j).getEntry()).doubleValue();
			}
		}
		
		return ret;
	}
	
	// -------------------------------------------------------------------------------------------
	//                             Methods using octave functions
	// -------------------------------------------------------------------------------------------

	public String toOctaveString() throws Exception{
		this.check();
		
		if (this.size() == 0)
			throw new Exception("No entries in matrix.");
		
		StringBuffer buffer = new StringBuffer("A = [\n");
		
		for (int i = 0; i < this.getHeight(); i++){
			for (int j = 0; j < this.getWidth(); j++)
				buffer = buffer.append(this.getEntry(i, j).getEntry()).append(",");
					
			buffer.setLength(buffer.length() - 1);
			buffer = buffer.append(";\n");
		}
	
		return buffer.append("]").toString();
	}
	
	/**
	 * Computes the rank using octave. Octave
	 * must be installed for this method to work.
	 * 
	 * @param path Path to the directory for writing files, e.g., /tmp/.
	 * 
	 * @return The rank of the given matrix.
	 * @throws Exception
	 */
	public int getRankUsingOctave(String path) throws Exception{
		MyOctaveRank rank = new MyOctaveRank();
		rank.setProgramName("octave");
		rank.setProgramPath("");
		rank.setInputPath(path);
		rank.setOutputPath(rank.getInputPath());
		rank.makeInputFile(this.toOctaveString());
		rank.run();
		return rank.parseOutputFile();
	}
	
	/**
	 * Computes the kernel of a matrix using octave.
	 * Octave must be installed for this method to work.
	 * 
	 * @param path Path to the directory for writing files, e.g., /tmp/.
	 * 
	 * @return A MyMatrix object consisting of vectors whose span represents the kernel.
	 * @throws Exception
	 */
	public MyMatrix<SecondD,String> getKernelUsingOctave(String path) throws Exception{
		MyOctaveKernel kernel = new MyOctaveKernel();
		kernel.setProgramName("octave");
		kernel.setProgramPath("");
		kernel.setInputPath(path);
		kernel.setOutputPath(kernel.getInputPath());
		kernel.makeInputFile(this.toOctaveString());
		kernel.run();
		double[][] matrix = kernel.parseOutputFile();
		
		this.check();
		MySet<SecondD> second_dimension = this.getSecondDimensionSet();
		ArrayList<SecondD> sd_list = second_dimension.toArrayList();
		MyMatrix<SecondD,String> ret = new MyMatrix<SecondD,String>();
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				MyDouble<SecondD,String> entry = new MyDouble<SecondD,String>(matrix[i][j], this.getSecondDimension(i), "kernel " + j);
				ret.add(entry);
			}
		}
		
		return ret;
	}	

	/**
	 * Computes the row reduced echelon form of a matrix using octave.
	 * Octave must be installed for this method to work.
	 * 
	 * @param path Path to the directory for writing files, e.g., /tmp/.
	 * 
	 * @return A MyMatrix object representing the row reduced row echelon form.
	 * @throws Exception
	 */
	public MyMatrix<FirstD,SecondD> getRowReducedEchelonFormUsingOctave(String path) throws Exception{
		MyOctaveRref rref = new MyOctaveRref();
		rref.setProgramName("octave");
		rref.setProgramPath("");
		rref.setInputPath(path);
		rref.setOutputPath(rref.getInputPath());
		rref.makeInputFile(this.toOctaveString(), -15);
		rref.run();
		double[][] matrix = rref.parseOutputFile();
		
		this.check();
		MyMatrix<FirstD, SecondD> ret = new MyMatrix<FirstD,SecondD>();
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				MyDouble<FirstD,SecondD> entry = new MyDouble<FirstD,SecondD>(matrix[i][j], this.getFirstDimension(i), this.getSecondDimension(j));
				ret.add(entry);
			}
		}

		return ret;
	}
	
	// -------------------------------------------------------------------------------------------
	//                                 Additional Methods
	// -------------------------------------------------------------------------------------------
	
	public int getLeadingOne(int i, int tolerance){
		boolean zeros = true;
		int j;
		for(j = 0; j < this.getWidth(); j++){
			if (Math.abs((Double)this.getEntry(i, j).getEntry()) < Math.pow(10, tolerance))
				zeros = zeros&&true;
			else
				zeros = zeros&&false;
			if (!zeros)
				break;
		}
		
		if (j < this.getWidth())
			if (Math.abs(1 - (Double)this.getEntry(i, j).getEntry()) < Math.pow(10, tolerance))
				return j;
		
		return -1;
	}
	
	public boolean isZeroRow(int i, int tolerance){
		double sum = 0;
		for (int j = 0; j < this.getWidth(); j++){
			sum = sum + Math.abs((Double)this.getEntry(i, j).getEntry());
		}
		
		if (sum < Math.pow(10, tolerance))
			return true;
		return false;
	}
}
