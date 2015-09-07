package dfamaker;

import java.util.ArrayList;

public class DebugPrinter {
	public void printStateList(ArrayList<State> stateList, int currentPredicateDepth) {
		System.out.println();
		System.out.println("==========" + currentPredicateDepth + "==========");
		for (State state : stateList) {
			System.out.println(state.getStateNumber());
			System.out.println(state.getPredicateDepth());
			System.out.println(state.isEOF());
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
			case NOT:
				System.out.println(((NotTransition) transition).getNextNotState().getStateNumber());
				break;
			case AND:
				System.out.println(((AndTransition) transition).getNextAndState().getStateNumber());
			default:
			}
		}
	}
}
