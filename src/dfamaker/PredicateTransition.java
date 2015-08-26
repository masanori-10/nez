package dfamaker;

import dfamaker.Enum.SymbolCase;

public class PredicateTransition extends Transition {
	private State predicateNextState;
	private int predicateDepth;

	public PredicateTransition() {
		super.setSymbolCase(SymbolCase.PREDICATE);
	}

	public PredicateTransition(State nextState) {
		this();
		super.setNextState(nextState);
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
