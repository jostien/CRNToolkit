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

import java.util.*;
import java.util.regex.*;

/**
 * 
 * This class is supposed to re-implement the matlab function regexp, at least in parts.
 * This is necessary for parsing SBML files produced by the CobraToolbox.
 * 
 * What method getTokens does is the following:
 *    (i)    get all matches of regular expression;
 *    (ii)   get all top level tokens in regular expression;
 *    (iii)  get all prefixes of regular expression whose suffix is a top level token;
 *    (iv)   for each prefix of regular expression and each match do the following:
 *           (1) get all matches of prefix plus rest of original regular expression in
 *               match, such that only head is reported ("^prefix(?=rest)" represents
 *               original regular expression);
 *           (2)  match corresponding token to the resulting matches forcing token to the
 *                end of line ("token$"), resulting match is match of token
 *    (viii) E.g. for one token:
 *           string+regex -> match, match+"^head(?=rest)" -> match2 guaranteeing the match
 *           of token to be at the end of the line, match2+"token$" -> match3 represents the
 *           match of the token.
 *           
 * Not clear if this really re-implements regexp of matlab or octave, but seems to work for
 * in test runs.
 * 
 * @author jostie
 *
 */
public class MyRegexp {
	
	/**
	 * Main method for testing.
	 * 
	 * @param args No arguments are used.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String string = "askdfjC6H12O6tritratrullallaF7G8H19blubb\ndfshjC1O2frackF1H1b";
//		String string = "askdfhCasdfhlsfj";
//		String string = "The quick red fox jumped over the lazy brown dog";
		String string = "glygn2[c]";
//		String regex = "j(((H|C|O)(\\d+))+).*(((F|G|H)(\\d+))+)b";
//		String regex = "(.*)C((a|s|d)+)";
//		String regex = ".*?(?=lazy)";
//		String regex = "(The quick red fox jumped over the )(?=lazy)";
//		String regex = "(?<=lazy).*";
//		String regex = "(?<=lazy)( brown dog)";
		String regex = "([a-zA-Z\\-_]+)\\d?\\[.\\]";
		
		MyRegexp regexp = new MyRegexp();
		String[] matches = regexp.matches(string, regex);
		for (int i = 0; i < matches.length; i++)
			System.out.println(matches[i]);

		System.out.println();
		String[] result = regexp.getTokens(string, regex);
		for (int i = 0; i < result.length; i++)
			System.out.println(result[i]);
	}
	
	/**
	 * Simple method that computes the position of the closing bracket in a given regular expression,
	 * starting at the position in the string indicated by offset.
	 * 
	 * @param string The given string.
	 * @param offset The position in the string from where on to search for the closing bracket.
	 *  
	 * @return The position of the closing bracket in the string.
	 */
	public int getClosingBracket(String string, int offset){
		int c = 0;
		for (int i = 0; i < string.length() - offset; i++){
			if (string.charAt(offset + i) == '(')
				c++;
			else if (string.charAt(offset + i) == ')')
				c--;
			if (c == 0)
				return offset + i + 1;
		}
		return -1;
	}
	
	/**
	 * Simple method which looks for an opening bracket by testing the chars of the string correspondingly.
	 * 
	 * @param string The given string.
	 * @param offset The current position.
	 * 
	 * @return The position of the opening bracket in the string.
	 */
	public int getOpeningBracket(String string, int offset){
		for (int i = 0; i < string.length() - offset; i++)
			if (string.charAt(offset + i) == '(')
				return offset + i;

		return -1;
	}
	
	/**
	 * Returns the substring between opening and closing bracket.
	 * 
	 * @param string The given string.
	 * @param start Position of opening bracket.
	 * 
	 * @return Position of closing bracket.
	 */
	public String getBracketContent(String string, int start){
		int end = this.getClosingBracket(string, start);

		return string.substring(start + 1, end - 1);
	}

	/**
	 * Gets the content of all top level brackets. This might seem sufficient since matlab also only re-
	 * ports the tokens in the top level brackets: "[...] Only the highest level parentheses are used. [...]"
	 * (http://www.mathworks.de/de/help/matlab/matlab_prog/tokens-in-regular-expressions.html).
	 * 
	 * @param string The string to analyze.
	 * 
	 * @return Array of String that contains the content of the top level brackets. 
	 */
	public String[] getBracketContents(String string){
		ArrayList<String> contents = new ArrayList<String>();
		
		int start = 0;
		while ((start = this.getOpeningBracket(string, start)) != -1){
			String content = this.getBracketContent(string, start);
			start = this.getClosingBracket(string, start);
			
			contents.add(content);
		}
		
		return contents.toArray(new String[contents.size()]);
	}
	
	/**
	 * Returns an array of String containing all substrings of the regular expression ending with a top level token.
	 * 
	 * @param regex The given regular expression.
	 * @return An array of String containing the substrings of the regular expression that end with a top lever token.
	 */
	public String[] getRegexpTokensAsSuffix(String regex){
		ArrayList<String> tokens_as_suffix = new ArrayList<String>();
		
		int start = 0;
		while ((start = this.getOpeningBracket(regex, start)) != -1){
			start = this.getClosingBracket(regex, start);
			
			tokens_as_suffix.add(regex.substring(0, start));
		}
		
		return tokens_as_suffix.toArray(new String[tokens_as_suffix.size()]);
	}

	/**
	 * Computes the token from a given total match.
	 * 
	 * @param total_match The total match of the original regular expression in original string.
	 * @param regex The original regular expression.
	 * @param token The current token.
	 * @param token_is_suffix The prefix of original regular expression where token is suffix.
	 * 
	 * @return The string where token matches with total match.
	 */
	public String getToken(String total_match, String regex, String token, String token_is_suffix){
		// create regular expression such that prefix with token as suffix is forced to the beginning of the line
		// followed by the rest of the original regular expression whereas the rest is not included in the match
		String regex_left = "^" + token_is_suffix + "(?=" + regex.substring(token_is_suffix.length(), regex.length()) + ")";

		//System.out.println("getToken outer " + regex_left);
				
		String[] matches_suffix = this.matches(total_match, regex_left);
		
		for (int i = 0; i < matches_suffix.length; i++){
			//System.out.println(matches_suffix[i]);
			//System.out.println("token " + token);
			String[] matches_token = this.matches(matches_suffix[i], token + "$");
			if (matches_token.length > 0)
				return matches_token[0];
		}
	
		return null;
	}

	/**
	 * Computes an array of String that contains the top level tokens in the given regular expression.
	 * 
	 * @param string The given string of interest.
	 * @param regex The regular expression containing tokens.
	 * 
	 * @return The array of String consisting of the matches represented by the tokens.
	 */
	public String[] getTokens(String string, String regex){
		ArrayList<String> token_match_list = new ArrayList<String>();
		
		String[] tokens_as_suffix  = this.getRegexpTokensAsSuffix(regex);
		String[] tokens_from_regex = this.getBracketContents(regex); 
		
		String[] match_array = this.matches(string, regex);
		for (int i = 0; i < match_array.length; i++){
			String match = match_array[i];

			for (int j = 0; j < tokens_as_suffix.length; j++){
				String token_as_suffix = tokens_as_suffix[j];
				String token = tokens_from_regex[j];
				
				String token_match = this.getToken(match, regex, token, token_as_suffix);
				token_match_list.add(token_match);
			}
		}
		
		return token_match_list.toArray(new String[token_match_list.size()]);
	}

	/**
	 * Finds and reports the matchings of a regular expression with respect to a given string.
	 * 
	 * @param str The given string.
	 * @param regex The regular expression.
	 * 
	 * @return An array of String representing the list of matchings.
	 */
	public String[] matches(String str, String regex){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		
		ArrayList<String> match_list = new ArrayList<String>();
		while (m.find()) 
		   match_list.add(str.substring(m.start(),m.end()));
		
		return match_list.toArray(new String[match_list.size()]);
	}
	
	/**
	 * Some helpful method that returns the length of the string with maximal length of an array of String.
	 * 
	 * @param string_array The given array of String.
	 * 
	 * @return The maximal length of a string in the array of String.
	 */
	public static int max(String[] string_array){
		int ret = -1;
		
		for (int i = 0; i < string_array.length; i++)
			if (ret < string_array[i].length())
				ret = string_array[i].length();
				
		return ret;
	}
	
	/**
	 * Completes a string with " " such that length of string equals a given length.
	 * 
	 * @param string The given string which needs to be completed.
	 * @param length The wanted length of the string.
	 * 
	 * @return The resulting string.
	 */
	public static String complete(String string, int length){
		int n = length - string.length();
		
		if (n > -1)
			return string + MyRegexp.getSpaces(n);
		return string;
	}
	
	/**
	 * Returns a string consisting of a given number of spaces.
	 * 
	 * @param n Number of wanted spaces.
	 * 
	 * @return String of n spaces.
	 */
	public static String getSpaces(int n){
		String ret = "";
		
		for (int i = 0; i < n; i++)
			ret = ret + " ";
		
		return ret;
	}
}
