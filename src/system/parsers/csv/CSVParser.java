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

package system.parsers.csv;

import system.parsers.*;

import java.io.*;
import java.util.*;

public class CSVParser extends Parser{
	private String file_name;			// name of input file
	private String file_id;				// internal id of file 
	
	private ArrayList<String> data;		// lines from CSV file
	private int n;						// number of lines
	private int m;						// number of columns
	
	public CSVParser(){
	}

	public String[][] parse(File file) throws Exception{
		return this.parse(file.getAbsolutePath());
	}
	
	public String[][] parse(String file_name) throws Exception{
		// reads CSV file and fill global variables
		
		this.file_name = file_name; // which file
		this.file_id = "";			// no file file id
		
		File f = new File(file_name);
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		
		this.data = this.parseLines(br); // parse it
		
		br.close();
		fr.close();
		
		this.guessDelimiter(this.data);
		this.getDimensions();
		//if (this.delimiter != "\t" || this.quotation != "")
		//	throw new Exception("It seems that CSV file uses \"" + this.delimiter + "\" and \"" + this.quotation + "\" to delimit and quote cells or even is not a CSV file");
		
		return this.getArray();
	}
	
	public String[][] parse(ArrayList<String> data) throws Exception{
        this.data = data;
		this.guessDelimiter(this.data);
		
		return this.getArray();
	}
	
    // -------------------------------------------------------------------------------------
	//                                  get methods
	// -------------------------------------------------------------------------------------
	
	/**
	 * Returns the lines of input file.
	 * 
	 * @return ArrayList containing the lines of the input file.
	 */
	public ArrayList<String> getData(){
		return this.data;
	}

	/**
	 * Returns CSV file id.
	 * 
	 * @return CSV file id
	 */
	public String getFileId(){
		return this.file_id;
	}
	
	/**
	 * Returns CSV file name
	 * 
	 * @return CSV file name
	 */
	public String getFileName(){
		return this.file_name;
	}

	/**
	 * Returns input from CSV file in ArrayList.
	 * 
	 * @return input from CSV file
	 */
	public ArrayList<String> getLines(){
		return data;
	}

	/**
	 * Parses lines from CSV file and fills corresponding global variables.
	 * 
	 * @param br BufferedReader for accessing CSV file
	 * @return ArrayList containing lines from input file
	 * @throws Exception
	 */
	private ArrayList<String> parseLines(BufferedReader br) throws Exception{
		// parses lines from CSV file
		
		ArrayList<String> data = new ArrayList<String>();
		String line;
		while ((line = br.readLine()) != null)
			data.add(line);
		
		return data;
	}

	public int getNumberOfRows(){
		return this.n;
	}
	
	public int getNumberOfColumns(){
		return this.m;
	}
	
	private void getDimensions(){
		this.n = this.data.size();
		this.m = this.delimit(this.getFirstLine()).length;
	}
	
	public String[] getLine(int i){
		return this.delimit((String)this.data.get(i));
	}
	
	//--------------------------------------------------------------------------------------
	//                                  set methods
	//--------------------------------------------------------------------------------------
	
	/**
	 * Sets the file id so that different processes do not work on the same files.
	 * 
	 * @param file_id the file id
	 */
	public void setFileId(String file_id){
		this.file_id = file_id;
	}

	/**
	 * Sets the file name.
	 * 
	 * @param file_name the file name
	 */
	public void setFileName(String file_name){
		this.file_name = file_name;
	}
	
	/**
	 * Sets new input data.
	 * 
	 * @param data the input data
	 */
	public void setData(ArrayList<String> data){
		this.data = data;
	}
	
	//--------------------------------------------------------------------------------------
	//                                  set methods
	//--------------------------------------------------------------------------------------
	
	private String[] delimiters = {"\t",",",";",":"," "};
	private String[] quotations = {"", "\"", "'"};
	
	private String delimiter;
	private String quotation;
	
    /**
     * Evaluates the "most probable" delimiter and quotation String depending on the variance of the
     * number of columns in all rows. This proceeding follows the assumption that tables have the
     * same number of columns in every row. The smaller the variance, the bigger the probability.
     * Clearly, the number of columns must be >= 2!.
     * 
     * @param sampleLines the lines of the input file
      */
    private void guessDelimiter(ArrayList<String> sampleLines) throws Exception {

        double min = -1;
        int index_delimiter = 0;
        int index_quotation = 0;
        for (int k = 0; k < this.quotations.length; k++) {
            for (int j = 0; j < this.delimiters.length; j++) {
                ArrayList<Integer> sizes = new ArrayList<Integer>();
                for (int i = 1; i < sampleLines.size(); i++) {

                    String[] cols = delimit((String)sampleLines.get(i),
                                            this.delimiters[j],
                                            this.quotations[k]);
                    if ( cols != null )
                        sizes.add(new Integer(cols.length));
                }

                double mean = 0;
                for (int i = 0; i < sizes.size(); i++)
                    mean = mean + ((Integer)sizes.get(i)).doubleValue();
                mean = mean / sizes.size();

                double var = 0;
                for (int i = 0; i < sizes.size(); i++)
                    var = var + Math.pow(((Integer)sizes.get(i)).doubleValue() - mean,
                                         2);
                var = var / sizes.size();

                if ( ((min == -1) || (var < min)) && (mean >= 2) ) {
                    min = var;
                    index_delimiter = j;
                    index_quotation = k;
                }
            }
        }
        
        if (min != 0)
        	throw new Exception("input data set does not contain clean table in CSV format");
        
        this.delimiter = this.delimiters[index_delimiter];
        this.quotation = this.quotations[index_quotation];
    }

    private String[] delimit(String line){
    	return this.delimit(line, this.delimiter, this.quotation);
    }
    
    /**
     * Delimit the given line using the given delimiter and quotation format.
     * 
     * @param line the line which is to be delimited
     * @param delimiter the given delimiter
     * @param quotation the given quotation
     * @return Array of Strings, which contains the cells of the table
     */
    private String[] delimit(String line, String delimiter, String quotation) {

        if ( line == null )
            return null;
        
        ArrayList<Integer> list = new ArrayList<Integer>();
        delimitNow(line, delimiter, quotation, false, false, list, 0);
        list.add(new Integer(line.length() + 1));
        String[] cols = new String[list.size()];

        int length = 0;
        int a = 0;
        for (int i = 0; i < list.size(); i++) {
            int b = ((Integer) list.get(i)).intValue();
            cols[i] = line.substring(a, b - 1);
  
            for (int j = 1; j < this.quotations.length; j++)
            	if (cols[i].length() > 1 && cols[i].startsWith(this.quotations[j]) && cols[i].endsWith(this.quotations[j])){
            		cols[i] = cols[i].substring(1, cols[i].length() - 1);
            		break;
            	}

            length = length + cols[i].trim().length();
            a = b;
        }
        
        if ( length > 0 )
            return cols;

        return null;
    }
    
    /**
     * Recursive Function to calculate the positions of delimiters and quotations.
     * 
     * @param rest the rest of the line that has to be checked
     * @param delimiter the assumed delimiter
     * @param quotation	the assumed quotation
     * @param inside_of_cell defines if head of string is inside of a cell 
     * @param is_quoted line is quoted
     * @param cuts where to cut the line
     * @param counter the position in the string
     * @return
     */
    private void delimitNow(String rest, String delimiter, String quotation, boolean inside_of_cell, boolean is_quoted, ArrayList<Integer> cuts, int counter) {
        if ( rest.length() > 0 ) {
            counter++;
            if ( !inside_of_cell )
                if ( rest.substring(0, 1).equals(quotation) )
                    delimitNow(rest.substring(1), delimiter, quotation, true, true, cuts, counter);
                else if ( rest.substring(0, 1).equals(delimiter) ) {
                    cuts.add(new Integer(counter));
                    delimitNow(rest.substring(1), delimiter, quotation, false, false, cuts, counter);
                } else
                    delimitNow(rest.substring(1), delimiter, quotation, true, is_quoted, cuts, counter);
            else if ( is_quoted )
                if ( rest.substring(0, 1).equals(quotation) )
                    delimitNow(rest.substring(1), delimiter, quotation, inside_of_cell, false, cuts, counter);
                else
                    delimitNow(rest.substring(1), delimiter, quotation, inside_of_cell, is_quoted, cuts, counter);
            else if ( rest.substring(0, 1).equals(delimiter) ) {
                cuts.add(new Integer(counter));
                delimitNow(rest.substring(1), delimiter, quotation, false, false, cuts, counter);
            } else
                delimitNow(rest.substring(1), delimiter, quotation, inside_of_cell, is_quoted, cuts, counter);
        }
    }
    
    public String getFirstLine(){
    	return (String)this.data.get(0);
    }
	
	public String[] getColum(int j){
		String[] ret = new String[this.getNumberOfRows()];
		for (int i = 0; i < this.getNumberOfRows(); i++){
			String[] row = this.getRow(i);
			ret[i] = row[j];
		}
		
		return ret;
	}
	
	public String[] getRow(int i){
		return this.getLine(i);
	}
	
	public String[][] getArray(){
		String[][] ret = new String[this.n][this.m];

		for (int i = 0; i < this.n; i++){
			String[] line = this.getLine(i);
			
			for (int j = 0; j < this.m; j++)
				ret[i][j] = line[j];
		}
		
		return ret;
	}
}
