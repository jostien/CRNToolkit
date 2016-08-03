package miscellaneous;

public class MyPair<E,F>{
	private E first_element;
	private F second_element;
	
	public MyPair(E first_element, F second_element){
		this.first_element = first_element;
		this.second_element = second_element;
	}
	
	public E getFirstElement(){
		return this.first_element;
	}
	
	public F getSecondElement(){
		return this.second_element;
	}
}
