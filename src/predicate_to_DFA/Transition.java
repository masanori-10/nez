package predicate_to_DFA;

import java.util.ArrayList;

import predicate_to_DFA.Enum.SymbolCase;

public class Transition {
	private State nextState;
	private String symbol;
	private SymbolCase symbolCase;
	private SymbolSet omittedSymbols;

	public void setNextState(State nextState) {
		this.nextState = nextState;
	}

	public void setSymbolAndCase(String symbol, SymbolCase symbolCase) {
		this.symbol = symbol;
		this.symbolCase = symbolCase;
	}

	public void setSymbolCase(SymbolCase symbolCase) {
		this.symbolCase = symbolCase;
	}

	public void setOmittedSymbols(SymbolSet omittedSymbols) {
		this.omittedSymbols = omittedSymbols;
	}

	public void addOmittedSymbol(String omittedSymbol) {
		this.omittedSymbols.add(omittedSymbol);
	}

	public void addAllOmittedSymbols(SymbolSet omittedSymbols) {
		this.omittedSymbols.addAll(omittedSymbols.get());
	}

	public State getNextState() {
		return this.nextState;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public SymbolCase getSymbolCase() {
		return this.symbolCase;
	}

	public SymbolSet getOmittedSymbols() {
		return this.omittedSymbols;
	}
}

class SymbolSet {
	private ArrayList<String> symbolSet;

	public SymbolSet() {
		this.symbolSet = new ArrayList<String>();
	}

	public void set(ArrayList<String> symbolSet) {
		this.symbolSet = symbolSet;
	}

	public void add(String symbol) {
		this.symbolSet.add(symbol);
	}

	public void addAll(ArrayList<String> symbolSet) {
		this.symbolSet.addAll(symbolSet);
	}

	public ArrayList<String> get() {
		return this.symbolSet;
	}

	public boolean isEmpty() {
		return this.symbolSet.isEmpty();
	}

	public int size() {
		return this.symbolSet.size();
	}

	public void organize() {
		for (int symbolNumberA = 0; symbolNumberA < this.size(); symbolNumberA++) {
			String symbolA = this.symbolSet.get(symbolNumberA);
			int symbolNumberB = symbolNumberA + 1;
			while (symbolNumberB < this.size()) {
				String symbolB = this.symbolSet.get(symbolNumberB);
				if (symbolA.equals(symbolB)) {
					this.symbolSet.remove(symbolNumberB);
				} else {
					symbolNumberB++;
				}
			}
		}
	}
}