package predicate_to_DFA;

import java.util.ArrayList;

public class EOSSearcher {
	private ArrayList<State> stateList;

	public void searchEOS(ArrayList<State> stateList) {
		this.stateList = stateList;
		for (int stateNumber = 0; stateNumber < this.stateList.size(); stateNumber++) {
			State currentState = this.stateList.get(stateNumber);
			int transitionNumber = 0;
			while (transitionNumber < currentState.getNextTransitions().size()) {
				Transition currentTransition = currentState
						.getNextTransitions().get(transitionNumber);
				if (currentTransition instanceof PredicateTransition) {
					State predicateState = ((PredicateTransition) currentTransition)
							.getPredicateNextState();
					this.searchEndAndSetEOS(predicateState);
					transitionNumber++;
				} else {
					transitionNumber++;
				}
			}
		}
	}

	private void searchEndAndSetEOS(State state) {
		if (state.getNextTransitions().isEmpty()) {
			state.setEOS();
		} else {
			for (Transition transition : state.getNextTransitions()) {
				searchEndAndSetEOS(transition.getNextState());
			}
		}
	}
}
