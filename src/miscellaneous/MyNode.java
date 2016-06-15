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

public class MyNode<E> extends ArrayList<MyNode<E>>{
	private static final long serialVersionUID = 1L;
	
	private E e;
	private MyNode<E> parent;
	private int position;
	
	public MyNode(E e, MyNode<E> parent){
		this.e = e;
		
		this.parent = parent;
	}
	
	public boolean isRoot(){
		return this.parent == null;
	}
	
	public boolean hasCild(){
		return this.size() > 0;
	}
	
	public int getPosition(){
		return this.position;
	}
	
	public MyNode<E> setPosition(int position){
		this.position = position;
		
		return this;
	}
	
	public E getValue(){
		return this.e;
	}
}
