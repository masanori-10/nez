package predicate_to_DFA;

public class Enum {

	public enum Token {
		CHOICE("/"), ANY("."), PREDICATE("!"), OPTIONAL("?"), REPETITION("*"), OPEN(
				"("), CLOSE(")"), EPSILON("E"), OTHER;
		private String name;

		public String getName() {
			return this.name();
		}

		private Token() {
			this.name = "";
		}

		private Token(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}

		public static Token getEnum(String str) {
			Token[] enumArray = Token.values();
			for (Token enumStr : enumArray) {
				if (str.equals(enumStr.name.toString())) {
					return enumStr;
				}
			}
			return OTHER;
		}
	}

	public enum SymbolCase {
		ANY, EOF, EPSILON, OTHER, PREDICATE, SYMBOL;
	}
}