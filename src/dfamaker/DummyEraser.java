package dfamaker;

import java.util.ArrayList;

public class DummyEraser {
	private ArrayList<State> stateList;
	private ArrayList<State> implementedStates;

	public void eraseDummy(ArrayList<State> stateList) {
		this.stateList = stateList;
		this.implementedStates = new ArrayList<State>();
		int stateNumber = 0;
		while (stateNumber < this.stateList.size()) {
			if (this.stateList.get(stateNumber).getStateNumber() == 0) {
				State startState = this.stateList.get(stateNumber);
				this.implementedStates.add(startState);
				this.checkNextTransition(startState);
				break;
			}
			stateNumber++;
		}
		stateNumber = 0;
		while (stateNumber < this.stateList.size()) {
			if (!(this.implemented(this.stateList.get(stateNumber)))) {
				this.stateList.remove(stateNumber);
			} else {
				stateNumber++;
			}
		}
	}

	private void checkNextTransition(State startState) {
		for (int transitionNumber = 0; transitionNumber < startState.getNextTransitions().size(); transitionNumber++) {
			State implementedState = startState.getNextTransitions().get(transitionNumber).getNextState();
			if (!(this.implemented(implementedState))) {
				this.implementedStates.add(implementedState);
				this.checkNextTransition(implementedState);
			}
			if (startState.getNextTransitions().get(transitionNumber) instanceof NotTransition) {
				implementedState = ((NotTransition) startState.getNextTransitions().get(transitionNumber))
						.getNextNotState();
				if (!(this.implemented(implementedState))) {
					this.implementedStates.add(implementedState);
					this.checkNextTransition(implementedState);
				}
			}
			if (startState.getNextTransitions().get(transitionNumber) instanceof AndTransition) {
				implementedState = ((AndTransition) startState.getNextTransitions().get(transitionNumber))
						.getNextAndState();
				if (!(this.implemented(implementedState))) {
					this.implementedStates.add(implementedState);
					this.checkNextTransition(implementedState);
				}
			}
		}
	}

	private boolean implemented(State state) {
		for (int implementedStateNumber = 0; implementedStateNumber < this.implementedStates
				.size(); implementedStateNumber++) {
			if (this.implementedStates.get(implementedStateNumber) == state) {
				return true;
			}
		}
		return false;
	}
}
