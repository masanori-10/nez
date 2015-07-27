package predicate_to_DFA;

import java.util.ArrayList;

import predicate_to_DFA.Enum.SymbolCase;
import predicate_to_DFA.Enum.Token;

public class DFAMaker {
	private State currentState;
	private ArrayList<State> stateList;
	private ArrayList<StateLabel> openPoints;
	private ArrayList<StateLabel> mergePoints;
	private ArrayList<StateLabel> predicatePoints;
	private boolean optionalFlag;
	private boolean repetitionFlag;
	private boolean predicateFlag;
	private Token token;
	private int predicateDepth;
	private int maxPredicateDepth;

	public DFAMaker() {
		this.currentState = new State();
		this.stateList = new ArrayList<State>();
		this.stateList.add(this.currentState);
		this.openPoints = new ArrayList<StateLabel>();
		this.mergePoints = new ArrayList<StateLabel>();
		this.predicatePoints = new ArrayList<StateLabel>();
		this.optionalFlag = false;
		this.repetitionFlag = false;
		this.predicateFlag = false;
		this.predicateDepth = 0;
		this.maxPredicateDepth = 0;
	}

	public void makeDFA(ArrayList<String> tokenList)
			throws CodingErrorException, SyntaxErrorException {
		int position = 0;
		while (position < tokenList.size()) {
			this.token = Token.getEnum(tokenList.get(position));
			switch (this.token) {
			case CHOICE:
				if (this.openPoints.isEmpty()) {
					System.out.println("Parentheses is not corresponding.");
					throw new SyntaxErrorException();
				}
				this.mergePoints.add(new StateLabel(this.currentState,
						position, this.predicateDepth));
				this.currentState = this.openPoints.get(
						this.openPoints.size() - 1).getState();
				this.predicateDepth = this.openPoints.get(
						this.openPoints.size() - 1).getDepthLabel();
				break;
			case OPTIONAL:
			case REPETITION:
				System.out
						.println("Position of optional or repetition operator(?,*) is invalid.");
				throw new SyntaxErrorException();
			case OPEN:
				this.openPoints.add(new StateLabel(this.currentState, position,
						this.predicateDepth));
				break;
			case CLOSE:
				if (this.openPoints.isEmpty()) {
					System.out.println("Parentheses is not corresponding.");
					throw new SyntaxErrorException();
				}
				int openPoint = this.openPoints.get(this.openPoints.size() - 1)
						.getPositionLabel();
				if (position + 1 < tokenList.size()) {
					if (Token.getEnum(tokenList.get(position + 1)) == Token.OPTIONAL) {
						this.optionalFlag = true;
					} else if (Token.getEnum(tokenList.get(position + 1)) == Token.REPETITION) {
						this.repetitionFlag = true;
					}
				}
				if (!(this.mergePoints.isEmpty())) {
					while (openPoint < this.mergePoints.get(
							this.mergePoints.size() - 1).getPositionLabel()) {
						this.mergePoints
								.get(this.mergePoints.size() - 1)
								.getState()
								.addNextTransition(
										new EpsilonTransition(this.currentState));
						if (this.predicateDepth < this.mergePoints.get(
								this.mergePoints.size() - 1).getDepthLabel()) {
							this.predicateDepth = this.mergePoints.get(
									this.mergePoints.size() - 1)
									.getDepthLabel();
						}
						this.mergePoints.remove(this.mergePoints.size() - 1);
						if (this.mergePoints.isEmpty()) {
							break;
						}
					}
				}
				if (!(this.predicatePoints.isEmpty())) {
					if (this.predicatePoints.get(
							this.predicatePoints.size() - 1).getPositionLabel() + 1 == this.openPoints
							.get(this.openPoints.size() - 1).getPositionLabel()) {
						if (this.repetitionFlag) {
							System.out
									.println("Position of repetition operator(*) is invalid.");
							throw new SyntaxErrorException();
						}
						this.currentState = this.predicatePoints.get(
								this.predicatePoints.size() - 1).getState();
						this.predicateDepth = this.predicatePoints.get(
								this.predicatePoints.size() - 1)
								.getDepthLabel();
						this.predicatePoints
								.remove(this.predicatePoints.size() - 1);
					}
				}
				if (this.optionalFlag) {
					this.openPoints
							.get(this.openPoints.size() - 1)
							.getState()
							.addNextTransition(
									new EpsilonTransition(this.currentState));
					this.optionalFlag = false;
					position++;
				}
				if (this.repetitionFlag) {
					this.currentState.addNextTransition(new EpsilonTransition(
							this.openPoints.get(this.openPoints.size() - 1)
									.getState()));
					this.currentState = this.openPoints.get(
							this.openPoints.size() - 1).getState();
					this.predicateDepth = this.openPoints.get(
							this.openPoints.size() - 1).getDepthLabel();
					this.repetitionFlag = false;
					position++;
				}
				this.openPoints.remove(this.openPoints.size() - 1);
				break;
			case PREDICATE:
				PredicateTransition predicateTransition = new PredicateTransition();
				this.predicateDepth++;
				if (this.maxPredicateDepth < this.predicateDepth) {
					this.maxPredicateDepth = this.predicateDepth;
				}
				predicateTransition.setPredicateDepth(this.predicateDepth);
				State predicateState = new State();
				State nonPredicateState = new State();
				this.stateList.add(predicateState);
				this.stateList.add(nonPredicateState);
				predicateTransition.setPredicateNextState(predicateState);
				predicateTransition.setNextState(nonPredicateState);
				this.currentState.addNextTransition(predicateTransition);
				this.currentState = predicateState;
				switch (Token.getEnum(tokenList.get(position + 1))) {
				case ANY:
				case OTHER:
					this.predicateFlag = true;
				case OPEN:
					this.predicatePoints.add(new StateLabel(nonPredicateState,
							position, this.predicateDepth));
					break;
				default:
					System.out
							.println("Position of predicate operator(!) is invalid.");
					throw new SyntaxErrorException();
				}
				break;
			case ANY:
			case OTHER:
			case EPSILON:
				Transition transition = new Transition();
				State nextState = new State();
				this.stateList.add(nextState);
				if (this.token == Token.ANY) {
					transition.setSymbolCase(SymbolCase.ANY);
				} else if (this.token == Token.EPSILON) {
					transition = new EpsilonTransition(nextState);
				} else {
					transition.setSymbolAndCase(tokenList.get(position),
							SymbolCase.SYMBOL);
				}
				if (Token.getEnum(tokenList.get(position + 1)) == Token.OPTIONAL) {
					this.optionalFlag = true;
					position++;
				} else if (Token.getEnum(tokenList.get(position + 1)) == Token.REPETITION) {
					if (this.predicateFlag) {
						System.out
								.println("Position of repetition operator(*) is invalid.");
						throw new SyntaxErrorException();
					}
					this.repetitionFlag = true;
					position++;
				}
				if (this.repetitionFlag) {
					this.currentState.addNextTransition(new EpsilonTransition(
							nextState));
					transition.setNextState(nextState);
					nextState.addNextTransition(transition);
					this.repetitionFlag = false;
				} else {
					if (this.optionalFlag) {
						this.currentState
								.addNextTransition(new EpsilonTransition(
										nextState));
						this.optionalFlag = false;
					}
					transition.setNextState(nextState);
					this.currentState.addNextTransition(transition);
				}
				if (this.predicateFlag) {
					this.currentState = this.predicatePoints.get(
							this.predicatePoints.size() - 1).getState();
					this.predicatePoints
							.remove(this.predicatePoints.size() - 1);
					this.predicateFlag = false;
				} else {
					this.currentState = nextState;
				}
			}
			position++;
		}
		if (!(this.openPoints.isEmpty())) {
			System.out.println("Parentheses is not corresponding.");
			throw new SyntaxErrorException();
		}
		this.currentState.addNextTransition(new EOFTransition());
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}

	public int getMaxPredicateDepth() {
		return this.maxPredicateDepth;
	}
}