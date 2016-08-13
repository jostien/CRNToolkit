/* CRNToolkit, Copyright (c) 2010-2016 Jost Neigenfind  <jostie@gmx.de>
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

import java.math.BigInteger;

import math.field.MyRationalNumber;

/**
 * Class which implements some helpful mathematical tools. 
 * 
 * Code for Gauss-Jordan algorithm taken from
 * 
 * http://ic.ucsc.edu/~ptantalo/math21/Winter07/GaussJordan.java
 * 
 * which seems to work. Adapted to class design.
 * 
 * @author jostie
 * 
 */
public class MyMath {
	
	/**
	 * Main method for testing purposes.
	 * 
	 * @param args Some arguments which are ignored.
	 */
	public static void main (String[] args) throws Exception{
		MyMath my_math = new MyMath();

		double[][] matrix_1 = {
				{1, 2, -1, -4},
				{2, 3, -1, -11},
				{-2, 0, -3, 22}
		};

		System.out.println("before\n" + MyRationalNumber.toString(MyRationalNumber.toMyRationalNumberMatrix(matrix_1)) + "\n");
		System.out.println("after\n" + MyRationalNumber.toString(my_math.rref(matrix_1)) + "\n");
		System.out.println("Rank of matrix: " + my_math.rank(matrix_1) + "\n");
		System.out.println("kernel\n" + MyRationalNumber.toString(my_math.kernel(matrix_1)) + "\n");
		System.out.println("---------------------\n");

		double matrix_2 [][] = {
				{2, 0, -1, 0, 0},
				{1, 0, 0, -1, 0},
				{3, 0, 0, -2, -1},
				{0, 1, 0, 0, -2},
				{0, 1, -1, 0, 0}
		};

		System.out.println("before\n" + MyRationalNumber.toString(MyRationalNumber.toMyRationalNumberMatrix(matrix_2)) + "\n");
		System.out.println("after\n" + MyRationalNumber.toString(my_math.rref(matrix_2)) + "\n");
		System.out.println("Rank of matrix: " + my_math.rank(matrix_2) + "\n");
		System.out.println("kernel\n" + MyRationalNumber.toString(my_math.kernel(matrix_2)) + "\n");
		System.out.println("---------------------\n");

		double matrix_3 [][] = {
				{1, 2, 3, 4, 3, 1},
				{2, 4, 6, 2, 6, 2},
				{3, 6, 18, 9, 9, -6},
				{4, 8, 12, 10, 12, 4},
				{5, 10, 24, 11, 15, -4}
		};

		System.out.println("before\n" + MyRationalNumber.toString(MyRationalNumber.toMyRationalNumberMatrix(matrix_3)) + "\n");
		System.out.println("after\n" + MyRationalNumber.toString(my_math.rref(matrix_3)) + "\n");
		System.out.println("Rank of matrix: " + my_math.rank(matrix_3) + "\n");
		System.out.println("kernel\n" + MyRationalNumber.toString(my_math.kernel(matrix_3)) + "\n");
		System.out.println("---------------------\n");

		double matrix_4 [][] = {
				{0, 0},
				{1, 1},
				{-1, 0},
				{0, 1},
				{0, 0},
				{0, 0},
				{0, 0},
				{0, 0},
				{1, 1}
		};

		System.out.println("before\n" + MyRationalNumber.toString(MyRationalNumber.toMyRationalNumberMatrix(matrix_4)) + "\n");
		System.out.println("after\n" + MyRationalNumber.toString(my_math.rref(matrix_4)) + "\n");
		System.out.println("Rank of matrix: " + my_math.rank(matrix_4) + "\n");
		System.out.println("kernel\n" + MyRationalNumber.toString(my_math.kernel(matrix_4)) + "\n");
		System.out.println("---------------------\n");
	}	

	/**
	 * Swaps row i with row k.
	 * 
	 * @param matrix The matrix whose rows are to be swapped.
	 * @param i First row.
	 * @param k Second row.
	 * @param j Starting index in row.
	 */
	private void swap(MyRationalNumber[][] matrix, int i, int k, int j){
		int m = matrix[0].length;

		MyRationalNumber temp;
		for(int l = j; l < m; l++){
			temp = matrix[i][l];
			matrix[i][l] = matrix[k][l];
			matrix[k][l] = temp;
		}
	}

	/**
	 * Divide row i by matrix[i][j].
	 * 
	 * @param matrix The matrix whose row is to be divided.
	 * @param i Row which is to be divided.
	 * @param j Starting index in row.
	 */
	private void divide(MyRationalNumber[][] matrix, int i, int j) throws Exception{
		int m = matrix[0].length;

		for(int l = j + 1; l < m; l++)
			matrix[i][l] = matrix[i][l].clone().div(matrix[i][j]);

		matrix[i][j] = new MyRationalNumber(1);
	}
	
	/**
	 * Substracts an appropriate multiple of row i from every other row.
	 * @param matrix The matrix from whose rows is to be substracted. 
	 * @param i The row which is substracted from the other rows.
	 * @param j Starting index in row.
	 */
	private void eliminate(MyRationalNumber[][] matrix, int i, int j){
		int n = matrix.length;
		int m = matrix[0].length;

		for(int k = 0; k < n; k++){
			if(k != i && matrix[k][j].getRatio() != 0){
				for(int l = j + 1; l < m; l++)
					matrix[k][l] = matrix[k][l].clone().add(matrix[k][j].clone().mul(matrix[i][l]).neg());
				matrix[k][j] = new MyRationalNumber(0);
			}
		}
	}

	/**
	 * Checks if a column is a pivot column.
	 * 
	 * @param matrix The matrix which is to be checked.
	 * @param j The index of the column to check.
	 * @return True if pivot column, false if free variable.
	 */
	private boolean isNonFree(MyRationalNumber[][] matrix, int j){
		int c = 0;
		int one = -1;
		for (int i = 0; i < matrix.length; i++){
			if (matrix[i][j].getRatio() != 0.0 && matrix[i][j].getRatio() != 1.0)
				return false;
			else if (matrix[i][j].getRatio() == 1.0){
				one = i;
				c++;
			}
			
			if (c > 1)
				return false;
		}
		
		return this.checkLeft(matrix, j, one);
	}
	
	private boolean checkLeft(MyRationalNumber[][] matrix, int j, int one){
		if (one == -1)	// in case of a zero column, column cannot represent nonfree variable 
			return false;
		
		for (int k = j - 1; k >= 0; k--)
			if (matrix[one][k].getRatio() != 0.0)
				return false;
		
		return true;
	}

	/**
	 * Performs Gauss-Jordan elimination without pivoting on a given matrix.
	 * 
	 * @param matrix The matrix whose row reduced echelon form is to be computed.
	 * @return The row reduced echelon form of matrix. 
	 */
	public MyRationalNumber[][] rref(MyRationalNumber[][] matrix_) throws Exception{
		MyRationalNumber[][] matrix = this.copy(matrix_);
		
		int n = matrix.length;
		int m = matrix[0].length;

		int i = 0;
		int j = 0;
		while(i < n && j < m){
			//look for a non-zero entry in col j at or below row i
			int k = i;
			while(k < n && matrix[k][j].getRatio() == 0)
				k++;

			// if such an entry is found at row k
			if(k < n){
				//System.out.println(matrix[k][j].getRatio());
				
				//  if k is not i, then swap row i with row k
				if(k != i) {
					this.swap(matrix, i, k, j);
					//System.out.println("swap:\n" + MyRationalNumber.toString(matrix));
				}

				// if ret[i][j] is not 1, then divide row i by ret[i][j]
				if(matrix[i][j].getRatio() != 1){
					this.divide(matrix, i, j);
					//System.out.println("divide:\n" + MyRationalNumber.toString(matrix));
				}

				// eliminate all other non-zero entries from col j by subtracting from each
				// row (other than i) an appropriate multiple of row i
				this.eliminate(matrix, i, j);
				//System.out.println("eliminate (" + i +", " + j + "):\n" + MyRationalNumber.toString(matrix));
				i++;
			}
			j++;
		}

		return matrix;

	}
	
	/**
	 * Performs Gauss-Jordan elimination without pivoting on a given matrix.
	 * 
	 * @param matrix The matrix whose row reduced echelon form is to be computed.
	 * @return The row reduced echelon form of matrix. 
	 */
	public MyRationalNumber[][] rref(double[][] matrix) throws Exception{
		MyRationalNumber[][] matrix_ = MyRationalNumber.toMyRationalNumberMatrix(matrix);

		return this.rref(matrix_);
	}
	
	/**
	 * Computes the rank of a given matrix
	 * 
	 * @param rref Matrix in row reduced echelon form.
	 * @return The rank of the given matrix.
	 */
	private int rank_(MyRationalNumber[][] rref){		
		int ret = 0;
		for (int j = 0; j < rref[0].length; j++)
			if (this.isNonFree(rref, j))
				ret++;
		
		return ret;
	}
	
	/**
	 * Computes the rank of a given matrix
	 * 
	 * @param rref Matrix in row reduced echelon form.
	 * @return The rank of the given matrix.
	 */
	public int rank(MyRationalNumber[][] matrix) throws Exception{
		MyRationalNumber[][] rref = this.rref(matrix);
		
		return this.rank_(rref);
	}
	
	/**
	 * Computes the rank of a given matrix.
	 * 
	 * @param matrix The matrix whose rank is to be computed.
	 * @return The rank of the given matrix.
	 */
	public int rank(double[][] matrix) throws Exception{		
		MyRationalNumber[][] rref = this.rref(matrix);
		
		return this.rank_(rref);
	}
	
	/**
	 * Computes a basis of the kernel of a given matrix.
	 * 
	 * @param matrix The matrix whose kernel is to be computed.
	 * @return A basis of the kernel of the given matrix. 
	 */
	public MyRationalNumber[][] kernel(MyRationalNumber[][] matrix) throws Exception{
		MyRationalNumber[][] rref = this.rref(matrix);
		int rank = this.rank(rref);
		
		int width = rref[0].length;
		
		if (rank == width){
			MyRationalNumber[][] ret = new MyRationalNumber[width][1];
			for (int j = 0; j < width; j++)
				ret[j][0] = new MyRationalNumber(0);
			return ret;
		}
		
		int krank = width - rank;
		
		MyRationalNumber[][] basis = new MyRationalNumber[width][krank];
		
		boolean[] non_free = new boolean[width]; 
		
		for (int j = 0; j < width; j++)
			non_free[j] = this.isNonFree(rref, j);
		
		//System.out.println(MyRationalNumber.toString(rref));
		//System.out.println(rank);
		int j = 0;
		for (int i = 0; i < rank; i++){
			while (j < width - 1 && rref[i][j].getRatio() == 0)
				j++;
			
			MyRationalNumber[] non_free_entries = this.getNonFreeEntries(rref, i, non_free, krank);
			for (int k = 0; k < krank; k++)
				basis[j][k] = non_free_entries[k].clone();
		}
		
		basis = this.fillNonFreeEntries(basis, non_free, krank);
		
		return basis;
	}
	
	/**
	 * Computes a basis of the kernel of a given matrix.
	 * 
	 * @param matrix The matrix whose kernel is to be computed.
	 * @return A basis of the kernel of the given matrix. 
	 */
	public MyRationalNumber[][] kernel(double[][] matrix) throws Exception{
		MyRationalNumber[][] matrix_ = MyRationalNumber.toMyRationalNumberMatrix(matrix);
		
		return this.kernel(matrix_);
	}
	
	/**
	 * Gets the entries corresponding to non-free variables of a specific row of a matrix in row reduced echelon form.  
	 * 
	 * @param rref The given matrix in row reduced echelon form.
	 * @param i The row whose entries of non-free variables are needed.
	 * @param non_free A boolean vector marking the columns of non-free variables.
	 * @param krank The rank of the kernel.
	 * @return A vector containing the entries of the non-free variables in row i. 
	 */
	private MyRationalNumber[] getNonFreeEntries(MyRationalNumber[][] rref, int i, boolean[] non_free, int krank){
		MyRationalNumber[] ret = new MyRationalNumber[krank];
		int c = 0;
		for (int j = 0; j < non_free.length; j++)
			if (!non_free[j]){
				ret[c] = rref[i][j].clone().neg();
				c++;
			}
		
		return ret;
	}
	
	/**
	 * Fills the entries of the pivot variables of the matrix of the basis of the kernel with the corresponding values. 
	 * 
	 * @param basis The matrix of the basis of the kernel.
	 * @param non_free A boolean vector marking the columns of non-free variables.
	 * @param krank The rank of the kernel.
	 * @return The kernel.
	 */
	private MyRationalNumber[][] fillNonFreeEntries(MyRationalNumber[][] basis, boolean[] non_free, int krank){
		int c = 0;
		for (int j = 0; j < non_free.length; j++){
			if (!non_free[j]){
				for (int k = 0; k < krank; k++){
					if (k == c)
						basis[j][k] = new MyRationalNumber(1);
					else
						basis[j][k] = new MyRationalNumber(0);
				}
				c++;
			}
		}

		
		return basis;
	}

	/**
	 * Finds the column index of two non-zero entries in two different rows. 
	 * 
	 * @param matrix The matrix whose rows are to be checked.
	 * @param i The first row.
	 * @param k The second row.
	 * @return The column index which satisfies the constraint, returns -1 if no such column exists. 
	 */
	private int findIndexOfValidRatio(MyRationalNumber[][] matrix, int i, int k){
		for (int j = 0; j < matrix[0].length; j++){
			if (matrix[i][j].getRatio() != 0 && matrix[k][j].getRatio() != 0){
				return j;
			}
		}
		
		return -1;
	}
	
	/**
	 * Checks if two rows in a matrix are coupled, i.e., if the ratio of the entries of the two rows
	 * in a column is the same for all columns, or both entries are zero. 
	 * 
	 * @param matrix The matrix whose rows are to be checked.
	 * @param i The first row.
	 * @param j The second row.
	 * @return True if the rows are coupled, false otherwise.
	 */
	public boolean isCoupled(MyRationalNumber[][] matrix, int i, int j) throws Exception{
		int valid_index = this.findIndexOfValidRatio(matrix, i, j);
		if (valid_index == -1)
			return false;
		
		MyRationalNumber ratio = matrix[i][valid_index].clone().div(matrix[j][valid_index]);
		for (int k = 0; k < matrix[0].length; k++){
			// if both are zero, no problem and go on, it cannot be two zero columns since a valid index exists
			if (!(matrix[i][k].getRatio() == 0 && matrix[j][k].getRatio() == 0)){
				// if first entry is zero and the other is not, return false ...
				if (matrix[i][k].getRatio() == 0 && matrix[j][k].getRatio() != 0)
					return false;
				// ... and vice versa
				if (matrix[i][k].getRatio() != 0 && matrix[j][k].getRatio() == 0)
					return false;
				
				// now, it is secured that both entries are nonzero, calculate ratio
				MyRationalNumber new_ratio = matrix[i][k].clone().div(matrix[j][k]);
				//if (new_ratio.getNumerator() != 0 || new_ratio.getDenominator() != 0)
				if (!new_ratio.getNumerator().equals(BigInteger.ZERO) || !new_ratio.getDenominator().equals(BigInteger.ZERO))
					// if ratios are different, return false
					if (!ratio.equals(new_ratio))
						return false;
			}	
		}
		
		return true;
	}
	
	/**
	 * Gets the ratio of two coupled columns of a matrix. Coupling must be checked before with isCoupled.
	 * 
	 * @param matrix The matrix which is to be analyzed.
	 * @param i The first row.
	 * @param j The second row.
	 * @return The ratio.
	 */
	public MyRationalNumber getRatio(MyRationalNumber[][] matrix, int i, int j) throws Exception{
		int valid_index = this.findIndexOfValidRatio(matrix, i, j);
		
		return matrix[i][valid_index].clone().div(matrix[j][valid_index]);
	}
	
	/**
	 * Creates string of matrix which can directly put into octave.
	 * 
	 * @param matrix The matrix which is to be put into octave.
	 * @return The string of the matrix.
	 */
	public static String toOctaveString(double[][] matrix){
		StringBuffer buffer = new StringBuffer("A = [\n");
		
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[0].length; j++)
				buffer = buffer.append(matrix[i][j]).append(",");
					
			buffer.setLength(buffer.length() - 1);
			buffer = buffer.append(";\n");
		}
	
		return buffer.append("]").toString();
	}
	
	public static String toOctaveString(MyRationalNumber[][] matrix){
		StringBuffer buffer = new StringBuffer("A = [\n");
		
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[0].length; j++)
				buffer = buffer.append(matrix[i][j].getRatio()).append(",");
					
			buffer.setLength(buffer.length() - 1);
			buffer = buffer.append(";\n");
		}
	
		return buffer.append("]").toString();
	}
	
	public static MyRationalNumber[][] toMyRationalNumber(double[][] matrix) throws Exception{
		MyRationalNumber[][] ret = new MyRationalNumber[matrix.length][matrix[0].length];
	
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				ret[i][j] = new MyRationalNumber(matrix[i][j]);
			}
		}
		
		return ret;
	}
	
	public static MyRationalNumber[][] concatenate(MyRationalNumber[][] A, MyRationalNumber[][] B){
		MyRationalNumber[][] ret = new MyRationalNumber[A.length][A[0].length + B[0].length];
		for (int i = 0; i < A.length; i++){
			for (int j = 0; j < A[i].length; j++){
				ret[i][j] = A[i][j];
			}
		}
		
		for (int i = 0; i < B.length; i++){
			for (int j = 0; j < B[i].length; j++){
				ret[i][A[0].length + j] = B[i][j];
			}
		}
		
		return ret;
	}
	
	public MyRationalNumber[][] copy(MyRationalNumber[][] matrix){
		MyRationalNumber[][] ret = new MyRationalNumber[matrix.length][matrix[0].length];
		
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[i].length; j++){
				ret[i][j] = matrix[i][j];
			}
		}
		
		return ret;
	}
}
