package dfamaker;

import java.util.ArrayList;

public class EpsilonEraser {

	public void eraseEpsilon(ArrayList<State> stateList) {
		for (int stateNumber = 0; stateNumber < stateList.size(); stateNumber++) {
			State currentState = stateList.get(stateNumber);
			int transitionNumber = 0;
			while (transitionNumber < currentState.getNextTransitions().size()) {
				Transition currentTransition = currentState.getNextTransitions().get(transitionNumber);
				if (currentTransition instanceof EpsilonTransition) {
					if (currentTransition.getNextState().isEOF()) {
						currentState.setEOF();
					}
					currentState.getNextTransitions().remove(transitionNumber);
					currentState.getNextTransitions().addAll(currentTransition.getNextState().getNextTransitions());
				} else {
					transitionNumber++;
				}
			}
		}
	}
}
