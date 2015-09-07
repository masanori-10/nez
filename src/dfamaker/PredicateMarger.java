package dfamaker;

import java.util.ArrayList;

public class PredicateMarger {
	private ArrayList<State> stateList;

	public void margePredicate(ArrayList<State> stateList, int predicateDepth) {
		this.stateList = stateList;
		for (int stateNumber = 0; stateNumber < this.stateList.size(); stateNumber++) {
			State currentState = this.stateList.get(stateNumber);
			int transitionNumber = 0;
			while (transitionNumber < currentState.getNextTransitions().size()) {
				Transition currentTransition = currentState.getNextTransitions().get(transitionNumber);
				if (currentTransition instanceof AndTransition) {
					State andState = ((AndTransition) currentTransition).getNextAndState();
					if (((AndTransition) currentTransition).getPredicateDepth() == predicateDepth) {
						boolean predefined = false;
						ArrayList<Integer> coStateNumbers = new ArrayList<Integer>();
						coStateNumbers.addAll(currentTransition.getNextState().getCoStateNumber());
						coStateNumbers.addAll(((AndTransition) currentTransition).getNextAndState().getCoStateNumber());
						for (State checkState : stateList) {
							predefined = checkState.getCoStateNumber().containsAll(coStateNumbers);
							predefined = predefined && coStateNumbers.containsAll(checkState.getCoStateNumber());
							if (predefined) {
								Transition newTransition = new EpsilonTransition(checkState);
								currentState.addNextTransition(newTransition);
								break;
							}
						}
						if (!(predefined)) {
							addNewState(currentTransition, currentState, andState);
						}
						currentState.getNextTransitions().remove(transitionNumber);
					} else {
						transitionNumber++;
					}
				} else {
					transitionNumber++;
				}
			}
		}
	}

	private void addNewState(Transition currentTransition, State currentState, State andState) {
		State newState = new State();
		this.stateList.add(newState);
		if (currentTransition.getNextState().isEOF()) {
			newState.setEOF();
		}
		newState.setCoStateNumber(currentTransition.getNextState().getCoStateNumber(),
				((AndTransition) currentTransition).getNextAndState().getCoStateNumber());
		newState.setNextTransitions(andState.getNextTransitions());
		newState.addAllNextTransitions(currentTransition.getNextState().getNextTransitions());
		Transition newTransition = new EpsilonTransition(newState);
		currentState.addNextTransition(newTransition);
	}
}
