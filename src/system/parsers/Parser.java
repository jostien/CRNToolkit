package system.parsers;

public abstract class Parser {
	public Parser(){
	}
	
	public abstract Object parse(String file) throws Exception;
	
	public boolean testIfNumber(String s){
		try{
			new Integer(s);
			return true;
		} catch (Exception e){
			return false;
		}
	}
}
