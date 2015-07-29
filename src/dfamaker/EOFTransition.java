package dfamaker;

import dfamaker.Enum.SymbolCase;

public class EOFTransition extends Transition {
	public EOFTransition() {
		super();
		super.setNextState(new AcceptState());
		super.setSymbolCase(SymbolCase.EOF);
	}
}
