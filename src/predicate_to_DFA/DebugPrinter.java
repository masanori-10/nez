package predicate_to_DFA;

import java.util.ArrayList;

public class DebugPrinter {
	public void printStateList(ArrayList<State> stateList) {
		for (State state : stateList) {
			System.out.println(state.getStateNumber());
			this.printTransitionList(state.getNextTransitions());
			System.out.println("");
		}
	}

	private void printTransitionList(ArrayList<Transition> transitionList) {
		for (Transition transition : transitionList) {
			System.out.println(transition.getNextState().getStateNumber());
			System.out.println(transition.getSymbolCase());
			switch (transition.getSymbolCase()) {
			case SYMBOL:
				System.out.println(transition.getSymbol());
				break;
			case OTHER:
				System.out.println(transition.getOmittedSymbols().get());
				break;
			case PREDICATE:
				System.out.println(((PredicateTransition) transition).getPredicateNextState().getStateNumber());
			default:
			}
		}
	}
}
