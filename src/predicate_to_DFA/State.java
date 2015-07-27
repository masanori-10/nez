package predicate_to_DFA;

import java.util.ArrayList;

public class State {
	private ArrayList<Transition> nextTransitions;
	private int stateNumber;
	private ArrayList<Integer> coStateNumber;
	static private int stateCounter;
	private boolean isEOP;
	private boolean isEOS;

	static {
		stateCounter = 0;
	}

	public State() {
		this.nextTransitions = new ArrayList<Transition>();
		this.coStateNumber = new ArrayList<Integer>();
		this.stateNumber = stateCounter;
		this.coStateNumber.add(stateCounter);
		stateCounter++;
		this.isEOP = false;
		this.isEOS = false;
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

	public void setCoStateNumber(ArrayList<Integer> coStateNumberA,
			ArrayList<Integer> coStateNumberB) {
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

	public void setAccept() {
		this.stateNumber = -1;
	}

	public void setEOP() {
		this.isEOP = true;
	}

	public void setStateNumber(int stateNumber) {
		this.stateNumber = stateNumber;
	}

	public boolean isEOP() {
		return this.isEOP;
	}

	public void setEOS() {
		this.isEOS = true;
	}

	public boolean isEOS() {
		return this.isEOS;
	}
}

class StateLabel {
	private State state;
	private int positionLabel;
	private int depthLabel;

	public StateLabel(State state, int positionLabel, int depthLabel) {
		this.state = state;
		this.positionLabel = positionLabel;
		this.depthLabel = depthLabel;
	}

	public State getState() {
		return this.state;
	}

	public int getPositionLabel() {
		return this.positionLabel;
	}

	public int getDepthLabel() {
		return this.depthLabel;
	}
}

class AcceptState extends State {
	public AcceptState() {
		super();
		super.setAccept();
	}
}