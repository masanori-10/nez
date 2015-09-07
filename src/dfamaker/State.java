package dfamaker;

import java.util.ArrayList;

public class State {
	private ArrayList<Transition> nextTransitions;
	private int stateNumber;
	private ArrayList<Integer> coStateNumber;
	static private int stateCounter;
	private int predicateDepth;
	private boolean isEOF;

	static {
		stateCounter = 0;
	}

	public State() {
		this.nextTransitions = new ArrayList<Transition>();
		this.coStateNumber = new ArrayList<Integer>();
		this.stateNumber = stateCounter;
		this.coStateNumber.add(stateCounter);
		stateCounter++;
		this.predicateDepth = 0;
		this.isEOF = false;
	}

	public void addNextTransition(Transition nextTransition) {
		this.nextTransitions.add(nextTransition);
	}

	public void addAllNextTransitions(ArrayList<Transition> nextTransitions) {
		this.nextTransitions.addAll(nextTransitions);
	}

	public void setNextTransition(Transition nextTransition) {
		this.nextTransitions.clear();
		this.nextTransitions.add(nextTransition);
	}

	public void setNextTransitions(ArrayList<Transition> nextTransitions) {
		this.nextTransitions.clear();
		this.nextTransitions.addAll(nextTransitions);
	}

	public void setCoStateNumber(ArrayList<Integer> coStateNumber) {
		this.coStateNumber = coStateNumber;
	}

	public void setCoStateNumber(ArrayList<Integer> coStateNumberA, ArrayList<Integer> coStateNumberB) {
		this.coStateNumber = new ArrayList<Integer>();
		this.coStateNumber.addAll(coStateNumberA);
		this.coStateNumber.addAll(coStateNumberB);
	}

	public ArrayList<Transition> getNextTransitions() {
		return this.nextTransitions;
	}

	public int getStateNumber() {
		return this.stateNumber;
	}

	public ArrayList<Integer> getCoStateNumber() {
		return this.coStateNumber;
	}

	public void setStateNumber(int stateNumber) {
		this.stateNumber = stateNumber;
	}

	public void setPredicateDepth(int predicateDepth) {
		this.predicateDepth = predicateDepth;
	}

	public int getPredicateDepth() {
		return this.predicateDepth;
	}

	public void setEOF() {
		this.isEOF = true;
	}

	public boolean isEOF() {
		return this.isEOF;
	}
}