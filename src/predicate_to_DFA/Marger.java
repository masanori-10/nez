package predicate_to_DFA;

import java.util.ArrayList;

import predicate_to_DFA.Enum.SymbolCase;

public class Marger {
	private ArrayList<State> stateList;

	public void margeTransition(ArrayList<State> stateList)
			throws CodingErrorException {
		this.stateList = stateList;
		for (int stateNumber = 0; stateNumber < this.stateList.size(); stateNumber++) {
			State currentState = this.stateList.get(stateNumber);
			if (currentState.getNextTransitions().size() > 1) {

				{
					SymbolSet requiredSymbols = new SymbolSet();
					for (int transitionNumber = 0; transitionNumber < currentState
							.getNextTransitions().size(); transitionNumber++) {
						Transition currentTransition = currentState
								.getNextTransitions().get(transitionNumber);
						if (currentTransition.getSymbolCase() == SymbolCase.SYMBOL) {
							requiredSymbols.add(currentTransition.getSymbol());
						} else if (currentTransition.getSymbolCase() == SymbolCase.OTHER) {
							requiredSymbols.addAll(currentTransition
									.getOmittedSymbols().get());
						}
					}
					requiredSymbols.organize();
					if (!(requiredSymbols.isEmpty())) {
						ArrayList<Transition> anyOrOtherTransitions = new ArrayList<Transition>();
						int transitionNumber = 0;
						while (transitionNumber < currentState
								.getNextTransitions().size()) {
							Transition currentTransition = currentState
									.getNextTransitions().get(transitionNumber);
							switch (currentTransition.getSymbolCase()) {
							case OTHER:
								anyOrOtherTransitions.add(currentTransition);
								currentState.getNextTransitions().remove(
										transitionNumber);
								break;
							case ANY:
								anyOrOtherTransitions.add(currentTransition);
								currentState.getNextTransitions().remove(
										transitionNumber);
								break;
							default:
								transitionNumber++;
							}
						}
						if (!(anyOrOtherTransitions.isEmpty())) {
							transitionNumber = 0;
							while (transitionNumber < anyOrOtherTransitions
									.size()) {
								Transition currentAnyOrOtherTransition = anyOrOtherTransitions
										.get(transitionNumber);
								SymbolSet symbolToMake = new SymbolSet();
								symbolToMake.addAll(requiredSymbols.get());
								switch (currentAnyOrOtherTransition
										.getSymbolCase()) {
								case OTHER:
									symbolToMake.get().clear();
									for (String symbolA : requiredSymbols.get()) {
										boolean contain = false;
										for (String symbolB : currentAnyOrOtherTransition
												.getOmittedSymbols().get()) {
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
										newSymbolTransition
												.setNextState(currentAnyOrOtherTransition
														.getNextState());
										newSymbolTransition.setSymbolAndCase(
												symbol, SymbolCase.SYMBOL);
										currentState
												.addNextTransition(newSymbolTransition);
									}
									Transition newOtherTransition = new Transition();
									newOtherTransition
											.setNextState(currentAnyOrOtherTransition
													.getNextState());
									newOtherTransition
											.setSymbolCase(SymbolCase.OTHER);
									newOtherTransition
											.setOmittedSymbols(requiredSymbols);
									currentState
											.addNextTransition(newOtherTransition);
									break;
								default:
									throw new CodingErrorException();
								}
								transitionNumber++;
							}
						}
					}
				}

				{
					int transitionNumberA = 0;
					while (transitionNumberA < currentState
							.getNextTransitions().size()) {
						Transition currentTransitionA = currentState
								.getNextTransitions().get(transitionNumberA);
						int transitionNumberB = transitionNumberA + 1;
						while (transitionNumberB < currentState
								.getNextTransitions().size()) {
							Transition currentTransitionB = currentState
									.getNextTransitions()
									.get(transitionNumberB);
							if (currentTransitionA.getNextState() == currentTransitionB
									.getNextState()
									&& currentTransitionA.getSymbolCase() == currentTransitionB
											.getSymbolCase()) {
								switch (currentTransitionA.getSymbolCase()) {
								case SYMBOL:
									if ((currentTransitionA.getSymbol() == null && currentTransitionB
											.getSymbol() == null)
											|| currentTransitionA.getSymbol()
													.equals(currentTransitionB
															.getSymbol())) {
										currentState.getNextTransitions()
												.remove(transitionNumberB);
									} else {
										transitionNumberB++;
									}
									break;
								case OTHER:
									if (currentTransitionA
											.getOmittedSymbols()
											.get()
											.containsAll(
													currentTransitionB
															.getOmittedSymbols()
															.get())
											&& currentTransitionB
													.getOmittedSymbols()
													.get()
													.containsAll(
															currentTransitionA
																	.getOmittedSymbols()
																	.get())) {
										currentState.getNextTransitions()
												.remove(transitionNumberB);
									} else {
										transitionNumberB++;
									}
									break;
								default:
									currentState.getNextTransitions().remove(
											transitionNumberB);
								}
							} else {
								transitionNumberB++;
							}
						}
						transitionNumberA++;
					}
				}

				{
					int transitionNumberA = 0;
					while (transitionNumberA < currentState
							.getNextTransitions().size()) {
						Transition currentTransitionA = currentState
								.getNextTransitions().get(transitionNumberA);
						int transitionNumberB = transitionNumberA + 1;
						while (transitionNumberB < currentState
								.getNextTransitions().size()) {
							Transition currentTransitionB = currentState
									.getNextTransitions()
									.get(transitionNumberB);
							boolean equalAAndB = false;
							if (currentTransitionA.getSymbolCase() == currentTransitionB
									.getSymbolCase()) {
								switch (currentTransitionA.getSymbolCase()) {
								case SYMBOL:
									if ((currentTransitionA.getSymbol() == null && currentTransitionB
											.getSymbol() == null)
											|| currentTransitionA.getSymbol()
													.equals(currentTransitionB
															.getSymbol())) {
										equalAAndB = true;
									}
									break;
								case OTHER:
									if (currentTransitionA
											.getOmittedSymbols()
											.get()
											.containsAll(
													currentTransitionB
															.getOmittedSymbols()
															.get())
											&& currentTransitionB
													.getOmittedSymbols()
													.get()
													.containsAll(
															currentTransitionA
																	.getOmittedSymbols()
																	.get())) {
										equalAAndB = true;
									}
									break;
								case PREDICATE:
								case EPSILON:
									equalAAndB = false;
									break;
								default:
									equalAAndB = true;
								}
							}
							if (equalAAndB) {
								boolean predefined = false;
								State margedState = null;
								ArrayList<Integer> costateNumbers = new ArrayList<Integer>();
								costateNumbers.addAll(currentTransitionA
										.getNextState().getCoStateNumber());
								costateNumbers.addAll(currentTransitionB
										.getNextState().getCoStateNumber());
								for (State checkState : stateList) {
									predefined = checkState.getCoStateNumber()
											.containsAll(costateNumbers);
									predefined = predefined
											&& costateNumbers
													.containsAll(checkState
															.getCoStateNumber());
									if (predefined) {
										margedState = checkState;
										break;
									}
								}
								if (margedState == null) {
									margedState = new State();
									if (currentTransitionA.getNextState()
											.isEOP()
											|| currentTransitionB
													.getNextState().isEOP()) {
										margedState.setEOP();
									}
									margedState
											.setCoStateNumber(costateNumbers);
									this.stateList.add(margedState);
									ArrayList<Transition> newTransitions = new ArrayList<Transition>();
									newTransitions.addAll(currentTransitionA
											.getNextState()
											.getNextTransitions());
									newTransitions.addAll(currentTransitionB
											.getNextState()
											.getNextTransitions());
									margedState
											.setNextTransitions(newTransitions);
								}
								Transition margedTransition = new Transition();
								margedTransition.setNextState(margedState);
								margedTransition.setSymbolAndCase(
										currentTransitionA.getSymbol(),
										currentTransitionA.getSymbolCase());
								margedTransition
										.setOmittedSymbols(currentTransitionA
												.getOmittedSymbols());
								currentState.getNextTransitions().remove(
										transitionNumberB);
								currentState.getNextTransitions().remove(
										transitionNumberA);
								currentState.getNextTransitions().add(
										margedTransition);
								transitionNumberA = -1;
								break;
							}
							transitionNumberB++;
						}
						transitionNumberA++;
					}
				}

			}
		}
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}
}
