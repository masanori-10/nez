package predicate_to_DFA;

import predicate_to_DFA.Enum.SymbolCase;

public class EOFTransition extends Transition {
	public EOFTransition() {
		super();
		super.setNextState(new AcceptState());
		super.setSymbolCase(SymbolCase.EOF);
	}
}
