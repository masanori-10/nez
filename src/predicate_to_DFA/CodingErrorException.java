package predicate_to_DFA;

class ComandLineArgumentException extends Exception {
	public ComandLineArgumentException() {
		super("Comand line argument error.");
	}
}

class FileReadException extends Exception {
	public FileReadException() {
		super("File read error.");
	}
}

class NonDefinedTokenException extends Exception {
	public NonDefinedTokenException() {
		super("Input token is not defined.");
	}
}

public class CodingErrorException extends Exception {
	public CodingErrorException(){
		super("Coding error.");
	}
}

class SyntaxErrorException extends Exception {
	public SyntaxErrorException(){
		super("Syntax error.");
	}
}
