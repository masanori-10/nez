package dfamaker;

import java.util.ArrayList;

import dfamaker.Enum.SymbolCase;

public class PredicateMarger {
	private ArrayList<State> stateList;
	private ArrayList<State> checkedStates;

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
						this.checkedStates = new ArrayList<State>();
						int depthA = serchDepth(((PredicateTransition) currentTransition).getPredicateNextState());
						this.checkedStates = new ArrayList<State>();
						int depthB = serchDepth(currentTransition.getNextState());
						if (depthA != -1 && depthB != -1) {
							for (int i = 0; i < depthA - depthB; i++) {
								this.addAnyTransitionAndState(currentTransition.getNextState());
							}
						}
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
							addNewState(currentTransition, currentState, predicateState);
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

	private void addNewState(Transition currentTransition, State currentState, State predicateState) {
		State newState = new State();
		this.stateList.add(newState);
		if (currentTransition.getNextState().isEOF()) {
			newState.setEOF();
		}
		newState.setCoStateNumber(currentTransition.getNextState().getCoStateNumber(),
				((PredicateTransition) currentTransition).getPredicateNextState().getCoStateNumber());
		newState.setNextTransitions(predicateState.getNextTransitions());
		newState.addAllNextTransitions(currentTransition.getNextState().getNextTransitions());
		Transition newTransition = new EpsilonTransition(newState);
		currentState.addNextTransition(newTransition);
	}

	private int serchDepth(State currentState) {
		this.checkedStates.add(currentState);
		if (currentState.getNextTransitions().isEmpty()) {
			return 0;
		}
		int maxDepth = 0;
		for (Transition currentTransition : currentState.getNextTransitions()) {
			if (this.checkedStates.contains(currentTransition.getNextState())) {
				return -1;
			} else {
				int currentDepth = serchDepth(currentTransition.getNextState());
				if (currentDepth == -1) {
					return -1;
				}
				if (maxDepth < currentDepth) {
					maxDepth = currentDepth;
				}
			}
		}
		return maxDepth + 1;
	}

	private void addAnyTransitionAndState(State currentState) {
		if (currentState.getNextTransitions().isEmpty()) {
			State newState = new State();
			this.stateList.add(newState);
			newState.setPredicateDepth(currentState.getPredicateDepth());
			currentState.setPredicateDepth(0);
			Transition newTransition = new Transition();
			newTransition.setSymbolCase(SymbolCase.ANY);
			newTransition.setNextState(newState);
			currentState.addNextTransition(newTransition);
		} else {
			for (Transition currentTransition : currentState.getNextTransitions()) {
				this.addAnyTransitionAndState(currentTransition.getNextState());
			}
		}
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}
}
