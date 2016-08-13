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

package math.field;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * 
 * @author jostie
 * 
 * Class which implements the mathematical object rational number.
 *
 */
public class MyRationalNumber {
	private BigInteger numerator;	// the numerator of the rational number
	private BigInteger denominator;	// the denominator of the rational number
	
	/**
	 * Main class for testing purposes.
	 * 
	 * @param args Some arguments which are ignored.
	 */
	public static void main(String[] args) throws Exception{
		double value = 1.333333;//1.20206309961144;//1.33333;//0.38461538461538;
		System.out.println(value);
		
		MyRationalNumber q = new MyRationalNumber(value);
		System.out.println(q.toString());
		
		BigInteger[] cf = (new MyRationalNumber(1)).chain(value, -3);
		System.out.println("chain computation finished");
		System.out.println("length of chain is " + cf.length);		
		
		MyRationalNumber a = new MyRationalNumber(3,4);
		MyRationalNumber b = new MyRationalNumber(5,6);
		System.out.println(a.clone().mul(b).toString());
		System.out.println(a.toString());
		
		System.out.println((new MyRationalNumber(1)).numer(cf, cf.length - 1));
		System.out.println("numerator computation finished");
		System.out.println((new MyRationalNumber(1)).denom(cf, cf.length - 1));
		System.out.println("denominator computation finished");

		System.out.println((new MyRationalNumber(1)).gcd(6, 3));
		System.out.println((new MyRationalNumber(1)).gcd(3, 6));
		
		System.out.println((new MyRationalNumber(0.02)).toString());
		System.out.println((new MyRationalNumber(50)).toString());
	}
	
	/**
	 * Empty constructor.
	 */
	public MyRationalNumber(){
	}
	
	/**
	 * Constructor which creates a rational number from a double.
	 *  
	 * @param value The double which is to be formated to a rational number.
	 */
	public MyRationalNumber(double value) throws Exception{
		BigInteger[] cf = (new MyRationalNumber()).chain(Math.abs(value), -3);
		
		this.numerator = (new MyRationalNumber()).numer(cf, cf.length - 1);
		if (value < 0)
			this.numerator = this.numerator.negate();
		this.denominator = (new MyRationalNumber()).denom(cf, cf.length - 1);
	}
	
	/**
	 * Constructor which creates a rational number from an longeger.
	 * 
	 * @param value The longeger.
	 */
	public MyRationalNumber(BigInteger value){
		this.numerator = value;
		this.denominator = BigInteger.ONE;
	}
	
	public MyRationalNumber(long value){
		this.numerator = new BigInteger(value + "");
		this.denominator = BigInteger.ONE;
	}
	
	public MyRationalNumber(String string){
		String[] cells = string.split("/");
		this.numerator = new BigInteger(cells[0].replaceAll(" ", ""));
		this.denominator = new BigInteger(cells[1].replaceAll(" ", ""));
	}
	
	/**
	 * Constructor which creates a rational number from numerator and denominator.
	 * 
	 * @param numerator The numerator of the rational number.
	 * @param denominator The denominator of the rational number.
	 */
	public MyRationalNumber(BigInteger numerator, BigInteger denominator){
		this.numerator = numerator;
		this.denominator = denominator;
	}
	
	public MyRationalNumber(long numerator, long denominator){
		this.numerator = new BigInteger(numerator + "");
		this.denominator = new BigInteger(denominator + "");
	}
	
	/**
	 * Compares this with another rational number.
	 * 
	 * @param ratio The other rational number.
	 * @return True if rational numbers are equal, false otherwise.
	 */
	public boolean equals(MyRationalNumber ratio){
		this.cancel();
		ratio.cancel();
		
		if (this.numerator.equals(ratio.getNumerator()) && this.denominator.equals(ratio.getDenominator()))
			return true;
		
		return false;
	}
	
	/**
	 * Gets the numerator.
	 * 
	 * @return The numerator of the rational number.
	 */
	public BigInteger getNumerator(){
		return this.numerator;
	}
	
	/**
	 * Gets the denominator.
	 * 
	 * @return The denominator of the rational number.
	 */
	public BigInteger getDenominator(){
		return this.denominator;
	}
	
	/**
	 * Gets a copy of the rational number.
	 * 
	 * @return The copy of the rational number.
	 */
	public MyRationalNumber clone(){
		return new MyRationalNumber(new BigInteger(this.getNumerator().toString()), new BigInteger(this.getDenominator().toString()));
	}
	
	
	public boolean isZero(){
		return this.numerator.equals(BigInteger.ZERO);
	}
	
	public boolean isOne(){		
		return this.numerator.equals(this.denominator);
	}
	
	/**
	 * Adds another rational number to this.
	 * 
	 * @param summand The other rational number
	 * @return The sum.
	 */
	public MyRationalNumber add(MyRationalNumber summand){
		// expansion
		BigInteger gcd = this.gcd(this.denominator, summand.getDenominator());
		
		//System.out.prlongln(this.toString());
		//System.out.prlongln(this.denominator);
		//System.out.prlongln(summand.getDenominator());
		//System.out.prlongln(gcd);
		//System.out.prlongln();
		
		BigInteger mul1 = this.denominator.divide(gcd);
		BigInteger mul2 = summand.getDenominator().divide(gcd);
		
		this.numerator = this.numerator.multiply(mul2).add(summand.getNumerator().multiply(mul1));
		this.denominator = this.denominator.multiply(mul2);

		// canceling
		this.cancel();
		
		return this;
	}
	
	/**
	 * Negates this.
	 * 
	 * @return The negation.
	 */
	public MyRationalNumber neg(){
		this.numerator = this.numerator.negate();
		
		return this;
	}
	
	/**
	 * Multiplies this with another rational number.
	 * 
	 * @param ratio The other rational number.
	 * @return The product.
	 */
	public MyRationalNumber mul(MyRationalNumber ratio){
		if (this.isZero())
			return this;
		
		this.numerator = this.numerator.multiply(ratio.getNumerator());
		this.denominator = this.denominator.multiply(ratio.getDenominator());
		
		this.cancel();
		
		return this;
	}
	
	/**
	 * Divides this with another rational number.
	 * 
	 * @param ratio The other rational number.
	 * @return The Quotient
	 */
	public MyRationalNumber div(MyRationalNumber ratio) throws Exception{
		this.mul(ratio.clone().inv());
		
		return this;
	}
	
	/**
	 * Inverts this.
	 * 
	 * @return The inversion.
	 */
	public MyRationalNumber inv() throws Exception{
		if (this.isZero())
			throw new Exception("inversion of zero not defined");
		
		
		BigInteger svd = this.numerator;
		this.numerator = this.denominator;
		this.denominator = svd;
		
		return this;
	}
	
	/**
	 * Cancels numerator and denominator out.
	 * 
	 * @return This with smallest numerator and denominator.
	 */
	public MyRationalNumber cancel(){
		if (!this.numerator.equals(BigInteger.ZERO) && !this.denominator.equals(BigInteger.ZERO)){
			BigInteger gcd = this.gcd(this.numerator, this.denominator);
			this.numerator = this.numerator.divide(gcd);
			this.denominator = this.denominator.divide(gcd);
		}
		
		if (this.numerator.signum() == -1 && this.denominator.signum() == -1){
			this.numerator = this.numerator.negate();
			this.denominator = this.denominator.negate();
		}
		
		return this;
	}
	
	/**
	 * Gets this as double.
	 * 
	 * @return This as double.
	 */
	public double getRatio(){
		return this.numerator.doubleValue()/this.denominator.doubleValue();
	}
	
	/**
	 * Gets a string representing this.
	 */
	public String toString(){
		if (this.numerator.equals(BigInteger.ZERO))
			return "0";
		
		if (this.numerator.equals(this.denominator))
			return "1";
		
		if (this.denominator.equals(BigInteger.ONE))
			return this.numerator + "";
		
		if (this.denominator.equals(BigInteger.ONE.negate()))
			return this.numerator.multiply(BigInteger.ONE.negate()) + "";
		
		return this.numerator + " / " + this.denominator;
	}
	
	/**
	 * Computes numerator of rational number recursively from chain representation.
	 * 
	 * @param cf Chain representation.
	 * @param n Position in chain.
	 * @return Result of current recursion.
	 */
	private BigInteger numer(BigInteger[] cf, int n){
		if (n == -1)
			return BigInteger.ONE;
		if (n == -2)
			return BigInteger.ZERO;
		
		//System.out.println(n + "\t" + (n-1) + "\t" + (n-2));
		
		return cf[n].multiply(this.numer(cf, n-1)).add(this.numer(cf, n-2));
	}
	
	/**
	 * Computes denominator of rational number recursively from chain representation.
	 * 
	 * @param cf Chain representation.
	 * @param n Position in chain.
	 * @return Result of current recursion.
	 */
	private BigInteger denom(BigInteger[] cf, int n){
		if (n == -1)
			return BigInteger.ZERO;
		if (n == -2)
			return BigInteger.ONE;
		
		return cf[n].multiply(this.denom(cf, n-1)).add(this.denom(cf, n-2));
	}
	
	/**
	 * Creates a chain representation of a given double. 
	 * 
	 * @param value The value that is to be transformed to a rational number.
	 * @param eps Epsilon when chain creation is aborted.
	 * @return Chain representation of given double as longeger array. 
	 */
	private BigInteger[] chain(double value, int eps) throws Exception{
		ArrayList<Integer> list = new ArrayList<Integer>();
	
		Double x = new Double(value);
		if (x > 1){
			list.add(x.intValue());
			x = x - x.intValue();
		} else
			list.add(0);
		
		while (x > Math.pow(10, eps)){
			x = 1/x;
			list.add(x.intValue());
			x = x - x.intValue();
		}
		
		if (list.size() > 100)
			throw new Exception("chain for computing fraction to long: " + list.size());
		
		BigInteger[] ret = new BigInteger[list.size()];
		for (int i = 0; i < list.size(); i++)
			ret[i] = new BigInteger(list.get(i) + "");
		
		return ret;
	}
	
	/**
	 * Computes greatest common divisor of two longegers recursively.
	 * 
	 * @param a First longeger.
	 * @param b Second longeger.
	 * @return Result of current recursion.
	 */
	public BigInteger gcd(BigInteger a, BigInteger b){
		return a.gcd(b);
		
//		// modern euclidean algorithm
//		// recursive version
//		
//		if (b == 0)
//			return a;
//		return gcd(b, a % b);
	}
	
	public BigInteger gcd(long a_, long b_){
		BigInteger a = new BigInteger(a_ + "");
		BigInteger b = new BigInteger(b_ + "");
		
		return a.gcd(b);
	}
	
	/**
	 * Gets a human readable string of a matrix of MyRationalNumbers.
	 * 
	 * @param matrix The matrix which is to be transformed to a string.
	 * @return The string of the matrix.
	 */
	public static String toString(MyRationalNumber[][] matrix) {
		String ret = "";
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[0].length; j++){
				ret = ret + matrix[i][j].toString() + "\t";
				//System.out.prlong("(" + matrix[i][j].getNumerator() + "," + matrix[i][j].getDenominator() + ")\t");
			}
			ret = ret + "\n";
			//System.out.prlongln();
		}
		return ret;
	}
	
	/**
	 * Creates a matrix of MyRationalNumbers from a matrix of doubles.
	 * 
	 * @param matrix The matrix which is to be transformed.
	 * @return Matrix of MyRationalNumbers.
	 */
	public static MyRationalNumber[][] toMyRationalNumberMatrix(double [][] matrix) throws Exception{
		MyRationalNumber[][] ret = new MyRationalNumber[matrix.length][matrix[0].length];

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				ret[i][j] = new MyRationalNumber(matrix[i][j]);
			}
		}

		return ret;
	}
	
	/**
	 * Creates a matrix of doubles from a matrix of MyRationalNumbers.
	 * 
	 * @param matrix The matrix which is to be transformed.
	 * @return Matrix of doubles.
	 */
	public static double[][] toDoubleMatrix(MyRationalNumber[][] matrix){
		double[][] ret = new double[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[0].length; j++)
				ret[i][j] = matrix[i][j].getRatio();
		
		return ret;
	}
	
	/**
	 * Creates string of matrix which can directly put longo octave.
	 * 
	 * @param matrix The matrix which is to be put longo octave.
	 * @return The string of the matrix.
	 */
	public static String toOctaveString(MyRationalNumber[][] matrix) throws Exception{
		StringBuffer buffer = new StringBuffer("A = [\n");
		
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[0].length; j++)
				buffer = buffer.append(matrix[i][j].getRatio()).append(",");
					
			buffer.setLength(buffer.length() - 1);
			buffer = buffer.append(";\n");
		}
	
		return buffer.append("]").toString();
	}
}
