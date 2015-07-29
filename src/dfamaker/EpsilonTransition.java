package dfamaker;

import dfamaker.Enum.SymbolCase;

public class EpsilonTransition extends Transition {
	private boolean forMarge;

	public EpsilonTransition() {
		super();
		this.forMarge = false;
		super.setSymbolCase(SymbolCase.EPSILON);
	}

	public EpsilonTransition(State nextState) {
		this();
		super.setNextState(nextState);
	}

	public void setForMarge(boolean forMarge) {
		this.forMarge = forMarge;
	}

	public boolean forMarge() {
		return this.forMarge;
	}
}
