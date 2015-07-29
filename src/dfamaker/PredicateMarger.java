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
				if (currentTransition instanceof PredicateTransition) {
					State predicateState = ((PredicateTransition) currentTransition).getPredicateNextState();
					if (((PredicateTransition) currentTransition).getPreicateDepth() == predicateDepth) {
						this.searchEOSAndSetEOP(
								((PredicateTransition) currentTransition).getPredicateTransitionNumber());
						boolean predefined = false;
						ArrayList<Integer> coStateNumbers = new ArrayList<Integer>();
						coStateNumbers.addAll(currentTransition.getNextState().getCoStateNumber());
						coStateNumbers.addAll(
								((PredicateTransition) currentTransition).getPredicateNextState().getCoStateNumber());
						for (State checkState : stateList) {
							predefined = checkState.getCoStateNumber().containsAll(coStateNumbers);
							predefined = predefined && coStateNumbers.containsAll(checkState.getCoStateNumber());
							if (predefined) {
								currentState.addAllNextTransitions(checkState.getNextTransitions());
								break;
							}
						}
						if (!(predefined)) {
							State newState = new State();
							this.stateList.add(newState);
							newState.setCoStateNumber(currentTransition.getNextState().getCoStateNumber(),
									((PredicateTransition) currentTransition).getPredicateNextState()
											.getCoStateNumber());
							newState.setNextTransitions(predicateState.getNextTransitions());
							newState.addAllNextTransitions(currentTransition.getNextState().getNextTransitions());
							Transition newTransition = new EpsilonTransition(newState);
							currentState.addNextTransition(newTransition);
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

	private void searchEOSAndSetEOP(int predicateNumber) {
		for (State state : this.stateList) {
			if (state.getPredicateNumber() == predicateNumber) {
				state.setEOP();
			}
		}
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}
}
