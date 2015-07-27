package predicate_to_DFA;

import java.util.ArrayList;

import predicate_to_DFA.Enum.Token;

public class PEGReshaper {
	private ArrayList<String> tokenList;
	private Token token;
	private ArrayList<TokenList> nextInsertPredicateList;

	PEGReshaper() {
		this.nextInsertPredicateList = new ArrayList<TokenList>();
	}

	public void reshapePEG(ArrayList<String> tokenList)
			throws SyntaxErrorException {
		this.tokenList = tokenList;
		int position = 0;
		while (position < this.tokenList.size() - 1) {
			this.token = Token.getEnum(this.tokenList.get(position));
			switch (this.token) {
			case CHOICE:
				ArrayList<String> nextInsertPredicate = new ArrayList<String>();
				nextInsertPredicate.add("!");
				nextInsertPredicate.addAll(this.nextInsertPredicateList.get(
						this.nextInsertPredicateList.size() - 1).get());
				nextInsertPredicate.add(")");
				this.nextInsertPredicateList.get(
						this.nextInsertPredicateList.size() - 1).add("/");
				this.tokenList.addAll(position + 1, nextInsertPredicate);
				position += nextInsertPredicate.size();
				break;
			case OPEN:
				this.nextInsertPredicateList.add(new TokenList());
				this.nextInsertPredicateList.get(
						this.nextInsertPredicateList.size() - 1).add("(");
				break;
			case CLOSE:
				if (this.nextInsertPredicateList.size() <= 1) {
					System.out.println("Parentheses is not corresponding.");
					throw new SyntaxErrorException();
				}
				this.nextInsertPredicateList.get(
						this.nextInsertPredicateList.size() - 1).add(")");
				this.nextInsertPredicateList.get(
						this.nextInsertPredicateList.size() - 2).addAll(
						this.nextInsertPredicateList.get(
								this.nextInsertPredicateList.size() - 1).get());
				this.nextInsertPredicateList
						.remove(this.nextInsertPredicateList.size() - 1);
				break;
			default:
				this.nextInsertPredicateList.get(
						this.nextInsertPredicateList.size() - 1).add(
						this.tokenList.get(position));
				break;
			}
			position++;
		}
	}

	public ArrayList<String> getTokenList() {
		return this.tokenList;
	}
}

class TokenList {
	private ArrayList<String> tokenList;

	public TokenList() {
		this.tokenList = new ArrayList<String>();
	}

	public ArrayList<String> get() {
		return this.tokenList;
	}

	public void add(String token) {
		this.tokenList.add(token);
	}

	public void addAll(ArrayList<String> tokenList) {
		this.tokenList.addAll(tokenList);
	}
}
