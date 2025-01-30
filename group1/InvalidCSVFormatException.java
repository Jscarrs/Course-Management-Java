package group1;

/**
*
* Custom exception for Invalid CSV
* 
*/

public class InvalidCSVFormatException extends Exception {
	public InvalidCSVFormatException(String m) {
		super(m);
	}
}