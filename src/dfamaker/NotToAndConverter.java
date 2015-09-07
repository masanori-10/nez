package dfamaker;

import java.util.ArrayList;

import dfamaker.Enum.SymbolCase;

public class NotToAndConverter {
	private ArrayList<State> stateList;
	private int predicateDepth;
	private ArrayList<State> convertedStates;
	private Marger marger;
	private PredicateEraser predicateEraser;

	public NotToAndConverter() {
		this.marger = new Marger();
		this.predicateEraser = new PredicateEraser();
	}

	public void convertNotToAnd(ArrayList<State> stateList, int predicateDepth) throws CodingErrorException {
		this.stateList = stateList;
		this.predicateDepth = predicateDepth;
		this.convertedStates = new ArrayList<State>();
		for (int stateNumber = 0; stateNumber < this.stateList.size(); stateNumber++) {
			State currentState = this.stateList.get(stateNumber);
			int transitionNumber = 0;
			while (transitionNumber < currentState.getNextTransitions().size()) {
				Transition currentTransition = currentState.getNextTransitions().get(transitionNumber);
				if (currentTransition instanceof NotTransition) {
					if (((NotTransition) currentTransition).getPredicateDepth() == this.predicateDepth) {
						AndTransition newAndTransition = new AndTransition(currentTransition.getNextState());
						newAndTransition.setNextAndState(((NotTransition) currentTransition).getNextNotState());
						newAndTransition.setPredicateDepth(this.predicateDepth);
						if (!(this.convertedStates.contains(newAndTransition.getNextAndState()))) {
							this.convertedStates.add(newAndTransition.getNextAndState());
							State newState = new State();
							this.stateList.add(newState);
							this.makeAnyTransition(newAndTransition.getNextAndState(), newState);
							this.makeAnyTransition(newState, newState);
							this.marger.margeTransition(this.getForMargeStateList(newAndTransition.getNextAndState()));
							this.predicateEraser.eraseNot(this.marger.getStateList(), this.predicateDepth);
							this.setNewPredicateDepth(this.predicateEraser.getStateList());
							this.addStateList(this.predicateEraser.getStateList());
						}
						currentState.getNextTransitions().remove(transitionNumber);
						currentState.addNextTransition(newAndTransition);
					} else {
						transitionNumber++;
					}
				} else {
					transitionNumber++;
				}
			}
		}
	}

	private void makeAnyTransition(State from, State to) {
		Transition newAnyTransition = new Transition();
		newAnyTransition.setSymbolCase(SymbolCase.ANY);
		newAnyTransition.setNextState(to);
		from.addNextTransition(newAnyTransition);
	}

	private ArrayList<State> getForMargeStateList(State andState) {
		ArrayList<State> forMargeStateList = new ArrayList<State>();
		forMargeStateList.add(andState);
		for (Transition currentTransition : andState.getNextTransitions()) {
			State currentState = currentTransition.getNextState();
			if (!(forMargeStateList.contains(currentState))) {
				forMargeStateList.addAll(this.getForMargeStateList(currentState));
			}
		}
		return forMargeStateList;
	}

	private void setNewPredicateDepth(ArrayList<State> stateList) {
		for (State currentState : stateList) {
			if (currentState.getNextTransitions().get(0).getSymbolCase() == SymbolCase.ANY
					&& currentState.getNextTransitions().get(0).getNextState() == currentState) {
				currentState.setPredicateDepth(this.predicateDepth);
			}
		}
	}

	private void addStateList(ArrayList<State> stateList) {
		for (State currentState : stateList) {
			if (!(this.stateList.contains(currentState))) {
				this.stateList.add(currentState);
			}
		}
	}
}
