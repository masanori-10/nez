package dfamaker;

import dfamaker.Enum.SymbolCase;

public class AndTransition extends Transition {
	private State nextAndState;
	private int predicateDepth;

	public AndTransition() {
		super.setSymbolCase(SymbolCase.AND);
	}

	public AndTransition(State nextState) {
		this();
		super.setNextState(nextState);
	}

	public void setNextAndState(State nextAndState) {
		this.nextAndState = nextAndState;
	}

	public void setPredicateDepth(int predicateDepth) {
		this.predicateDepth = predicateDepth;
	}

	public State getNextAndState() {
		return this.nextAndState;
	}

	public int getPredicateDepth() {
		return this.predicateDepth;
	}
}
