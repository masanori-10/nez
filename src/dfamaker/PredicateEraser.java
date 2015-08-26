package dfamaker;

import java.util.ArrayList;

public class PredicateEraser {
	private ArrayList<State> stateList;

	public void erasePredicate(ArrayList<State> stateList, int predicateDepth) {
		this.stateList = stateList;
		boolean isCompleted = false;
		while (!(isCompleted)) {
			isCompleted = true;
			{
				int stateNumber = 0;
				while (stateNumber < this.stateList.size()) {
					State currentState = this.stateList.get(stateNumber);
					if ((currentState.getPredicateDepth() == predicateDepth && predicateDepth != 0)
							|| (currentState.getPredicateDepth() == 0 && currentState.getNextTransitions().isEmpty()
									&& !(currentState.isEOF()))) {
						this.stateList.remove(stateNumber);
						isCompleted = false;
					} else {
						stateNumber++;
					}
				}
			}

			{
				int stateNumber = 0;
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
			}
		}
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}
}