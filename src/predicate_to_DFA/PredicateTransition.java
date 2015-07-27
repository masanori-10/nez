package predicate_to_DFA;

import predicate_to_DFA.Enum.SymbolCase;

public class PredicateTransition extends Transition {
	private State predicateNextState;
	private int predicateDepth;

	public PredicateTransition() {
		super.setSymbolCase(SymbolCase.PREDICATE);
	}

	public void setPredicateNextState(State predicateNextState) {
		this.predicateNextState = predicateNextState;
	}

	public void setPredicateDepth(int predicateDepth) {
		this.predicateDepth = predicateDepth;
	}

	public State getPredicateNextState() {
		return this.predicateNextState;
	}

	public int getPreicateDepth() {
		return this.predicateDepth;
	}
}
