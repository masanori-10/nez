package dfamaker;

import java.util.ArrayList;

import dfamaker.Enum.SymbolCase;

public class PredicateEraser {
	private ArrayList<State> stateList;
	private int predicateDepth;
	private ArrayList<State> trueStates;
	private ArrayList<State> checkedStates;
	private ArrayList<State> falseStates;
	private ArrayList<State> erasedStates;

	public void erasePredicate(ArrayList<State> stateList, int predicateDepth) {
		this.stateList = stateList;
		this.predicateDepth = predicateDepth;
		this.searchEpsilon();
	}

	public void eraseNot(ArrayList<State> stateList, int predicateDepth) {
		this.stateList = stateList;
		this.predicateDepth = predicateDepth;
		this.falseStates = new ArrayList<State>();
		for (State currentState : this.stateList) {
			if (currentState.getPredicateDepth() == this.predicateDepth) {
				this.falseStates.add(currentState);
			}
		}
		this.eraseState();
		this.eraseTransition();
	}

	private void searchEpsilon() {
		int stateNumber = 0;
		this.erasedStates = new ArrayList<State>();
		boolean erased = false;
		while (stateNumber < this.stateList.size()) {
			State currentState = this.stateList.get(stateNumber);
			int transitionNumber = 0;
			while (transitionNumber < currentState.getNextTransitions().size()) {
				Transition currentTransition = currentState.getNextTransitions().get(transitionNumber);
				if (currentTransition instanceof EpsilonTransition
						&& !(this.erasedStates.contains(currentTransition.getNextState()))) {
					this.erasedStates.add(currentTransition.getNextState());
					this.trueStates = new ArrayList<State>();
					this.checkedStates = new ArrayList<State>();
					this.searchCurrentPredicateDepth(currentTransition.getNextState());
					this.distributeState();
					this.eraseFalse();
					if (currentTransition.getNextState().isEOF()) {
						currentState.setEOF();
					}
					currentState.getNextTransitions().remove(transitionNumber);
					currentState.getNextTransitions().addAll(currentTransition.getNextState().getNextTransitions());
					erased = true;
				} else {
					transitionNumber++;
				}
			}
			stateNumber++;
			if (erased) {
				stateNumber = 0;
				erased = false;
			}
		}
	}

	private void searchCurrentPredicateDepth(State currentState) {
		this.checkedStates.add(currentState);
		for (Transition currentTransition : currentState.getNextTransitions()) {
			if (!(this.checkedStates.contains(currentTransition.getNextState()))) {
				this.searchCurrentPredicateDepth(currentTransition.getNextState());
			}
		}
		if (currentState.getPredicateDepth() == this.predicateDepth) {
			this.trueStates.add(currentState);
		}
	}

	private void distributeState() {
		int stateNumber = 0;
		while (stateNumber < this.checkedStates.size()) {
			State currentState = this.checkedStates.get(stateNumber);
			if (!(this.trueStates.contains(currentState))) {
				for (Transition currentTransition : currentState.getNextTransitions()) {
					if (this.trueStates.contains(currentTransition.getNextState())) {
						this.trueStates.add(currentState);
						stateNumber = -1;
						break;
					}
				}
			}
			stateNumber++;
		}
	}

	private void eraseFalse() {
		this.falseStates = new ArrayList<State>();
		for (State currentState : this.checkedStates) {
			if (!(this.trueStates.contains(currentState))) {
				this.falseStates.add(currentState);
			}
			if (this.trueStates.contains(currentState) && !(currentState.getNextTransitions().isEmpty())) {
				if (currentState.getNextTransitions().get(0).getSymbolCase() == SymbolCase.ANY
						&& currentState.getNextTransitions().get(0).getNextState() == currentState) {
					this.falseStates.add(currentState);
				}
			}
		}
		this.eraseState();
		this.eraseTransition();
	}

	private void eraseState() {
		int stateNumber = 0;
		while (stateNumber < this.stateList.size()) {
			State currentState = this.stateList.get(stateNumber);
			if (this.falseStates.contains(currentState)) {
				this.stateList.remove(stateNumber);
			} else {
				stateNumber++;
			}
		}
	}

	private void eraseTransition() {
		int stateNumber = 0;
		while (stateNumber < this.stateList.size()) {
			int transitionNumber = 0;
			State currentState = this.stateList.get(stateNumber);
			while (transitionNumber < currentState.getNextTransitions().size()) {
				State nextState = currentState.getNextTransitions().get(transitionNumber).getNextState();
				if (this.falseStates.contains(nextState)) {
					currentState.getNextTransitions().remove(transitionNumber);
				} else {
					transitionNumber++;
				}
			}
			stateNumber++;
		}
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}
}