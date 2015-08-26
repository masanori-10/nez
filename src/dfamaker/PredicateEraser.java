package dfamaker;

import java.util.ArrayList;

public class PredicateEraser {
	private ArrayList<State> stateList;
	private int predicateDepth;

	public void erasePredicate(ArrayList<State> stateList, int predicateDepth) {
		this.stateList = stateList;
		this.predicateDepth = predicateDepth;
		boolean isCompleted = false;
		while (!(isCompleted)) {
			isCompleted = this.eraseState();
			isCompleted = isCompleted && this.eraseTransition();
		}
	}

	private boolean eraseState() {
		int stateNumber = 0;
		boolean isCompleted = true;
		while (stateNumber < this.stateList.size()) {
			State currentState = this.stateList.get(stateNumber);
			if ((currentState.getPredicateDepth() == this.predicateDepth && this.predicateDepth != 0)
					|| (currentState.getPredicateDepth() == 0 && currentState.getNextTransitions().isEmpty()
							&& !(currentState.isEOF()))) {
				this.stateList.remove(stateNumber);
				isCompleted = false;
			} else {
				stateNumber++;
			}
		}
		return isCompleted;
	}

	private boolean eraseTransition() {
		int stateNumber = 0;
		boolean isCompleted = true;
		while (stateNumber < this.stateList.size()) {
			int transitionNumber = 0;
			State currentState = this.stateList.get(stateNumber);
			while (transitionNumber < currentState.getNextTransitions().size()) {
				State nextState = currentState.getNextTransitions().get(transitionNumber).getNextState();
				if ((nextState.getPredicateDepth() == predicateDepth && predicateDepth != 0)
						|| (nextState.getPredicateDepth() == 0 && !(nextState.isEOF())
								&& nextState.getNextTransitions().isEmpty())) {
					currentState.getNextTransitions().remove(transitionNumber);
					isCompleted = false;
				} else {
					transitionNumber++;
				}
			}
			stateNumber++;
		}
		return isCompleted;
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}
}