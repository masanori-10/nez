package dfamaker;

import dfamaker.Enum.SymbolCase;

public class PredicateTransition extends Transition {
	private State predicateNextState;
	private int predicateDepth;
	private int predicateTransitionNumber;
	private static int predicateTransitionCounter;

	static {
		predicateTransitionCounter = 1;
	}

	public PredicateTransition() {
		super.setSymbolCase(SymbolCase.PREDICATE);
		this.predicateTransitionNumber = predicateTransitionCounter;
		predicateTransitionCounter++;
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

	public int getPredicateTransitionNumber() {
		return this.predicateTransitionNumber;
	}
}
