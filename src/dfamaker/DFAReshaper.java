package dfamaker;

import java.util.ArrayList;

public class DFAReshaper {
	private EpsilonEraser epsilonEraser;
	private NotToAndConverter notToAndConverter;
	private PredicateMarger predicateMarger;
	private Marger marger;
	private DummyEraser dummyEraser;
	private PredicateEraser predicateEraser;
	private ArrayList<State> stateList;
	private DebugPrinter debugPrinter;

	public DFAReshaper() {
		epsilonEraser = new EpsilonEraser();
		notToAndConverter = new NotToAndConverter();
		predicateMarger = new PredicateMarger();
		marger = new Marger();
		dummyEraser = new DummyEraser();
		predicateEraser = new PredicateEraser();
		debugPrinter = new DebugPrinter();
	}

	public void reshapeDEA(ArrayList<State> stateList, int maxPredicateDepth) throws CodingErrorException {
		this.stateList = stateList;
		while (maxPredicateDepth >= 0) {
			debugPrinter.printStateList(stateList, maxPredicateDepth);
			epsilonEraser.eraseEpsilon(this.stateList);
			notToAndConverter.convertNotToAnd(this.stateList, maxPredicateDepth);
			predicateMarger.margePredicate(this.stateList, maxPredicateDepth);
			marger.margeTransition(this.stateList);
			dummyEraser.eraseDummy(this.stateList);
			predicateEraser.erasePredicate(this.stateList, maxPredicateDepth);
			maxPredicateDepth--;
		}
		this.renumberingState();
	}

	public ArrayList<State> getStateList() {
		return this.stateList;
	}

	public void renumberingState() {
		int stateNumber = 0;
		while (stateNumber < this.stateList.size()) {
			if (this.stateList.get(stateNumber).getStateNumber() != -1) {
				this.stateList.get(stateNumber).setStateNumber(stateNumber);
				stateNumber++;
			} else {
				this.stateList.remove(stateNumber);
			}
		}
	}
}
