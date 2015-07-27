package predicate_to_DFA;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
	private ArrayList<String> token;
	private int lexemeBegin, forward;
	private Pattern pToken, pSpace;
	private Matcher mToken, mSpace;

	public Lexer() {
		this.token = new ArrayList<String>();
		this.pToken = Pattern.compile("^([a-z]|\\(|\\)|\\/|\\.|!|\\?|\\*|E)$");
		this.pSpace = Pattern.compile("^[\\s]$");
	}

	public void lexe(String inputLine) throws NonDefinedTokenException {
		this.lexemeBegin = 0;
		this.forward = 1;
		this.token.clear();

		this.token.add("(");
		while (inputLine.length() > forward) {
			mSpace = pSpace.matcher(inputLine.substring(lexemeBegin, forward));
			mToken = pToken.matcher(inputLine.substring(lexemeBegin, forward));
			if (mSpace.find()) {
				lexemeBegin++;
				forward++;
			} else if (mToken.find()) {
				this.token.add(inputLine.substring(lexemeBegin, forward));
				forward++;
				lexemeBegin = forward - 1;
			} else {
				System.out.println("in Lexer");
				throw new NonDefinedTokenException();
			}
		}
		this.token.add(")");
	}

	public ArrayList<String> getToken() {
		return this.token;
	}

}