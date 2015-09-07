package dfamaker;

import dfamaker.Enum.SymbolCase;

public class NotTransition extends Transition {
	private State nextNotState;
	private int predicateDepth;

	public NotTransition() {
		super.setSymbolCase(SymbolCase.NOT);
	}

	public NotTransition(State nextState) {
		this();
		super.setNextState(nextState);
	}

	public void setNextNotState(State nextNotState) {
		this.nextNotState = nextNotState;
	}

	public void setPredicateDepth(int predicateDepth) {
		this.predicateDepth = predicateDepth;
	}

	public State getNextNotState() {
		return this.nextNotState;
	}

	public int getPredicateDepth() {
		return this.predicateDepth;
	}

}
