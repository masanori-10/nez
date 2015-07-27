package predicate_to_DFA;

import predicate_to_DFA.Enum.SymbolCase;

public class EpsilonTransition extends Transition {
	public EpsilonTransition() {
		super();
		super.setSymbolCase(SymbolCase.EPSILON);
	}

	public EpsilonTransition(State nextState) {
		this();
		super.setNextState(nextState);
	}
}
