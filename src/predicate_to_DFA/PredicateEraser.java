package predicate_to_DFA;

import java.util.ArrayList;

public class PredicateEraser {
	private ArrayList<State> stateList;

	public void erasePredicate(ArrayList<State> stateList) {
		this.stateList = stateList;
		boolean isCompleted = false;
		while (!(isCompleted)) {
			isCompleted = true;
			{
				int stateNumber = 0;
				while (stateNumber < this.stateList.size()) {
					State currentState = this.stateList.get(stateNumber);
					if (currentState.isEOP()
							|| (!(currentState.isEOS()) && currentState
									.getNextTransitions().isEmpty())) {
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
					while (transitionNumber < currentState.getNextTransitions()
							.size()) {
						State nextState = currentState.getNextTransitions()
								.get(transitionNumber).getNextState();
						if (nextState.isEOP()
								|| (!(nextState.isEOS())
										&& !(nextState instanceof AcceptState) && nextState
										.getNextTransitions().isEmpty())) {
							currentState.getNextTransitions().remove(
									transitionNumber);
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
