package dfamaker;

import java.util.ArrayList;

import dfamaker.Enum.SymbolCase;

public class PredicateEraser {
	private ArrayList<State> stateList;

	public void erasePredicate(ArrayList<State> stateList, int predicateDepth) {
		this.stateList = stateList;
		if (predicateDepth > 0) {
			for (int stateNumber = 0; stateNumber < this.stateList.size(); stateNumber++) {
				State currentState = this.stateList.get(stateNumber);
				if (!(currentState.getPredicateNumber().isEmpty())) {
					this.instillPredicate(currentState);
				}
			}
			return;
		}
		boolean isCompleted = false;
		while (!(isCompleted)) {
			isCompleted = true;
			{
				int stateNumber = 0;
				while (stateNumber < this.stateList.size()) {
					State currentState = this.stateList.get(stateNumber);
					if (!(this.checkStateValidity(currentState)) && currentState.getNextTransitions().isEmpty()) {
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
						if (!(this.checkStateValidity(nextState)) && nextState.getNextTransitions().isEmpty()) {
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

	private void instillPredicate(State checkState) {
		int transitionNumber = 0;
		while (transitionNumber < checkState.getNextTransitions().size()) {
			Transition currentTransition = checkState.getNextTransitions().get(transitionNumber);
			State predicateState = currentTransition.getNextState();
			if (!(predicateState.getPredicateNumber().containsAll(checkState.getPredicateNumber()))) {
				State newState = new State();
				this.stateList.add(newState);
				Transition newTransition = new Transition();
				switch (currentTransition.getSymbolCase()) {
				case SYMBOL:
					newTransition.setSymbolAndCase(currentTransition.getSymbol(), SymbolCase.SYMBOL);
					break;
				case OTHER:
					newTransition.setSymbolCase(SymbolCase.OTHER);
					newTransition.setOmittedSymbols(currentTransition.getOmittedSymbols());
					break;
				default:
					newTransition.setSymbolCase(currentTransition.getSymbolCase());
					break;
				}
				newState.setCoStateNumber(predicateState.getCoStateNumber());
				if (predicateState.isEOF()) {
					newState.setEOF();
				}
				newState.setNextTransitions(predicateState.getNextTransitions());
				newState.addAllPredicateNumber(predicateState.getPredicateNumber());
				newState.addAllTargetPredicateNumber(predicateState.getTargetPredicateNumber());
				for (int i = 0; i < checkState.getPredicateNumber().size(); i++) {
					if (!(newState.getPredicateNumber().contains(checkState.getPredicateNumber().get(i)))) {
						newState.addPredicateNumber(checkState.getPredicateNumber().get(i));
						newState.addTargetPredicateNumber(checkState.getTargetPredicateNumber().get(i));
					}
				}
				newTransition.setNextState(newState);
				checkState.addNextTransition(newTransition);
				checkState.getNextTransitions().remove(transitionNumber);
				this.instillPredicate(newState);
			} else {
				transitionNumber++;
			}
		}
	}

	private boolean checkStateValidity(State checkState) {
		if (checkState.getPredicateNumber().isEmpty()) {
			return true;
		} else {
			for (int i = 0; i < checkState.getPredicateNumber().size(); i++) {
				int targetPredicateNumber = checkState.getTargetPredicateNumber().get(i);
				if (targetPredicateNumber == 0) {
					if (this.checkPredicateValidity(i, checkState.getPredicateNumber(),
							checkState.getTargetPredicateNumber())) {
						return false;
					}
				}
			}
			return true;
		}
	}

	private boolean checkPredicateValidity(int position, ArrayList<Integer> predicateNumbers,
			ArrayList<Integer> targetPredicateNumbers) {
		int predicateNumber = predicateNumbers.get(position);
		if (targetPredicateNumbers.contains(predicateNumber)) {
			for (int i = 0; i < targetPredicateNumbers.size(); i++) {
				int targetPredicateNumber = targetPredicateNumbers.get(i);
				if (targetPredicateNumber == predicateNumber) {
					if (this.checkPredicateValidity(i, predicateNumbers, targetPredicateNumbers)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}
}
