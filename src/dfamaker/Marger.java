package dfamaker;

import java.util.ArrayList;

import dfamaker.Enum.SymbolCase;

public class Marger {
	private ArrayList<State> stateList;
	private State currentState;

	public void margeTransition(ArrayList<State> stateList) throws CodingErrorException {
		this.stateList = stateList;
		for (int stateNumber = 0; stateNumber < this.stateList.size(); stateNumber++) {
			this.currentState = this.stateList.get(stateNumber);
			if (this.currentState.getNextTransitions().size() > 1) {
				this.splitAnyTransitions();
				this.margeSameTransitions();
				this.margeSameSymbolTransitions();
			}
		}
	}

	private void splitAnyTransitions() throws CodingErrorException {
		SymbolSet requiredSymbols = new SymbolSet();
		for (int transitionNumber = 0; transitionNumber < this.currentState.getNextTransitions()
				.size(); transitionNumber++) {
			Transition currentTransition = this.currentState.getNextTransitions().get(transitionNumber);
			if (currentTransition.getSymbolCase() == SymbolCase.SYMBOL) {
				requiredSymbols.add(currentTransition.getSymbol());
			} else if (currentTransition.getSymbolCase() == SymbolCase.OTHER) {
				requiredSymbols.addAll(currentTransition.getOmittedSymbols().get());
			}
		}
		requiredSymbols.organize();
		if (!(requiredSymbols.isEmpty())) {
			ArrayList<Transition> anyOrOtherTransitions = new ArrayList<Transition>();
			int transitionNumber = 0;
			while (transitionNumber < this.currentState.getNextTransitions().size()) {
				Transition currentTransition = this.currentState.getNextTransitions().get(transitionNumber);
				switch (currentTransition.getSymbolCase()) {
				case OTHER:
					anyOrOtherTransitions.add(currentTransition);
					this.currentState.getNextTransitions().remove(transitionNumber);
					break;
				case ANY:
					anyOrOtherTransitions.add(currentTransition);
					this.currentState.getNextTransitions().remove(transitionNumber);
					break;
				default:
					transitionNumber++;
				}
			}
			if (!(anyOrOtherTransitions.isEmpty())) {
				this.addNewTransitions(anyOrOtherTransitions, requiredSymbols);
			}
		}
	}

	private void addNewTransitions(ArrayList<Transition> anyOrOtherTransitions, SymbolSet requiredSymbols)
			throws CodingErrorException {
		int transitionNumber = 0;
		while (transitionNumber < anyOrOtherTransitions.size()) {
			Transition currentAnyOrOtherTransition = anyOrOtherTransitions.get(transitionNumber);
			SymbolSet symbolToMake = new SymbolSet();
			symbolToMake.addAll(requiredSymbols.get());
			switch (currentAnyOrOtherTransition.getSymbolCase()) {
			case OTHER:
				symbolToMake.get().clear();
				for (String symbolA : requiredSymbols.get()) {
					boolean contain = false;
					for (String symbolB : currentAnyOrOtherTransition.getOmittedSymbols().get()) {
						if (symbolB.equals(symbolA)) {
							contain = true;
							break;
						}
					}
					if (!(contain)) {
						symbolToMake.add(symbolA);
					}
				}
			case ANY:
				for (String symbol : symbolToMake.get()) {
					Transition newSymbolTransition = new Transition();
					newSymbolTransition.setNextState(currentAnyOrOtherTransition.getNextState());
					newSymbolTransition.setSymbolAndCase(symbol, SymbolCase.SYMBOL);
					this.currentState.addNextTransition(newSymbolTransition);
				}
				Transition newOtherTransition = new Transition();
				newOtherTransition.setNextState(currentAnyOrOtherTransition.getNextState());
				newOtherTransition.setSymbolCase(SymbolCase.OTHER);
				newOtherTransition.setOmittedSymbols(requiredSymbols);
				this.currentState.addNextTransition(newOtherTransition);
				break;
			default:
				throw new CodingErrorException();
			}
			transitionNumber++;
		}
	}

	private void margeSameTransitions() {
		int transitionNumberA = 0;
		while (transitionNumberA < this.currentState.getNextTransitions().size()) {
			Transition currentTransitionA = this.currentState.getNextTransitions().get(transitionNumberA);
			int transitionNumberB = transitionNumberA + 1;
			while (transitionNumberB < this.currentState.getNextTransitions().size()) {
				Transition currentTransitionB = this.currentState.getNextTransitions().get(transitionNumberB);
				if (currentTransitionA.getNextState() == currentTransitionB.getNextState()
						&& currentTransitionA.getSymbolCase() == currentTransitionB.getSymbolCase()) {
					switch (currentTransitionA.getSymbolCase()) {
					case SYMBOL:
						if ((currentTransitionA.getSymbol() == null && currentTransitionB.getSymbol() == null)
								|| currentTransitionA.getSymbol().equals(currentTransitionB.getSymbol())) {
							this.currentState.getNextTransitions().remove(transitionNumberB);
						} else {
							transitionNumberB++;
						}
						break;
					case OTHER:
						if (currentTransitionA.getOmittedSymbols().get()
								.containsAll(currentTransitionB.getOmittedSymbols().get())
								&& currentTransitionB.getOmittedSymbols().get()
										.containsAll(currentTransitionA.getOmittedSymbols().get())) {
							this.currentState.getNextTransitions().remove(transitionNumberB);
						} else {
							transitionNumberB++;
						}
						break;
					default:
						this.currentState.getNextTransitions().remove(transitionNumberB);
					}
				} else {
					transitionNumberB++;
				}
			}
			transitionNumberA++;
		}
	}

	private void margeSameSymbolTransitions() {
		int transitionNumberA = 0;
		while (transitionNumberA < this.currentState.getNextTransitions().size()) {
			Transition currentTransitionA = this.currentState.getNextTransitions().get(transitionNumberA);
			int transitionNumberB = transitionNumberA + 1;
			while (transitionNumberB < this.currentState.getNextTransitions().size()) {
				Transition currentTransitionB = this.currentState.getNextTransitions().get(transitionNumberB);
				if (this.checkEquality(currentTransitionA, currentTransitionB)) {
					boolean predefined = false;
					State margedState = null;
					ArrayList<Integer> costateNumbers = new ArrayList<Integer>();
					costateNumbers.addAll(currentTransitionA.getNextState().getCoStateNumber());
					costateNumbers.addAll(currentTransitionB.getNextState().getCoStateNumber());
					for (State checkState : stateList) {
						predefined = checkState.getCoStateNumber().containsAll(costateNumbers);
						predefined = predefined && costateNumbers.containsAll(checkState.getCoStateNumber());
						if (predefined) {
							margedState = checkState;
							break;
						}
					}
					if (margedState == null) {
						margedState = this.makeNewState(currentTransitionA, currentTransitionB, costateNumbers);
					}
					Transition margedTransition = new Transition();
					margedTransition.setNextState(margedState);
					margedTransition.setSymbolAndCase(currentTransitionA.getSymbol(),
							currentTransitionA.getSymbolCase());
					margedTransition.setOmittedSymbols(currentTransitionA.getOmittedSymbols());
					this.currentState.getNextTransitions().remove(transitionNumberB);
					this.currentState.getNextTransitions().remove(transitionNumberA);
					this.currentState.getNextTransitions().add(margedTransition);
					transitionNumberA = -1;
					break;
				}
				transitionNumberB++;
			}
			transitionNumberA++;
		}
	}

	private boolean checkEquality(Transition transitionA, Transition transitionB) {
		if (transitionA.getSymbolCase() == transitionB.getSymbolCase()) {
			switch (transitionA.getSymbolCase()) {
			case SYMBOL:
				if ((transitionA.getSymbol() == null && transitionB.getSymbol() == null)
						|| transitionA.getSymbol().equals(transitionB.getSymbol())) {
					return true;
				}
				break;
			case OTHER:
				if (transitionA.getOmittedSymbols().get().containsAll(transitionB.getOmittedSymbols().get())
						&& transitionB.getOmittedSymbols().get().containsAll(transitionA.getOmittedSymbols().get())) {
					return true;
				}
				break;
			case PREDICATE:
			case EPSILON:
				break;
			default:
				return true;
			}
		}
		return false;
	}

	private State makeNewState(Transition transitionA, Transition transitionB, ArrayList<Integer> costateNumbers) {
		State newState = new State();
		if (transitionA.getNextState().isEOF() || transitionB.getNextState().isEOF()) {
			newState.setEOF();
		}
		if (transitionA.getNextState().getPredicateDepth() > 0 || transitionB.getNextState().getPredicateDepth() > 0) {
			if (transitionA.getNextState().getPredicateDepth() > transitionB.getNextState().getPredicateDepth()) {
				newState.setPredicateDepth(transitionA.getNextState().getPredicateDepth());
			} else {
				newState.setPredicateDepth(transitionB.getNextState().getPredicateDepth());
			}
		}
		newState.setCoStateNumber(costateNumbers);
		this.stateList.add(newState);
		ArrayList<Transition> newTransitions = new ArrayList<Transition>();
		newTransitions.addAll(transitionA.getNextState().getNextTransitions());
		newTransitions.addAll(transitionB.getNextState().getNextTransitions());
		newState.setNextTransitions(newTransitions);
		return newState;
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}
}
