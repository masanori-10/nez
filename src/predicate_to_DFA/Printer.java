package predicate_to_DFA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import predicate_to_DFA.Enum.SymbolCase;

public class Printer {
	// private int stateNumber = 0;
	// private int transitionNumber = 0;

	public void printDOTFile(ArrayList<State> stateList) throws IOException {
		File file = new File("../DFAMaker/dot/DFA.dot");
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		System.out.println("digraph DFA {");
		pw.println("digraph DFA {");
		for (int i = 0; i < stateList.size(); i++) {
			State state = stateList.get(i);
			// if (!(state.getStateNumber() == -1)) {
			// this.stateNumber++;
			// }
			for (int j = 0; j < state.getNextTransitions().size(); j++) {
				System.out.print("	");
				pw.print("	");
				Transition transition = state.getNextTransitions().get(j);
				if (transition.getSymbolCase() == SymbolCase.EOF) {
					System.out.println("q" + state.getStateNumber() + " [peripheries = 2];");
					pw.println("q" + state.getStateNumber() + " [peripheries = 2];");
				} else {
					System.out.print("q" + state.getStateNumber() + " -> ");
					pw.print("q" + state.getStateNumber() + " -> ");
					// for dubug
					// System.out.print(state.getCoStateNumber());
					// System.out.print(state.getPredicateNumber() + "--");
					System.out.print("q" + transition.getNextState().getStateNumber());
					pw.print("q" + transition.getNextState().getStateNumber());
					// for debug
					// System.out.print(transition.getNextState().getCoStateNumber());
					// System.out.println(transition.getNextState()
					// .getPredicateNumber());

					System.out.print(" [label = \"");
					pw.print(" [label = \"");
					if (transition.getSymbolCase() == SymbolCase.SYMBOL) {
						System.out.print(transition.getSymbol());
						pw.print(transition.getSymbol());
					} else if (transition.getSymbolCase() == SymbolCase.OTHER) {
						String omittedSymbols = null;
						int lengthCounter = 0;
						for (String symbol : transition.getOmittedSymbols().get()) {
							if (omittedSymbols == null) {
								System.out.print("_");
								pw.print("_");
								omittedSymbols = symbol;
							} else {
								if (lengthCounter == 2) {
									System.out.print("_");
									pw.print("_");
									lengthCounter = 0;
								}
								System.out.print("_");
								pw.print("_");
								omittedSymbols += ",";
								omittedSymbols += symbol;
							}
							lengthCounter++;
						}
						System.out.print("\\n");
						pw.print("\\n");
						System.out.print(omittedSymbols);
						pw.print(omittedSymbols);
					} else {
						System.out.print(transition.getSymbolCase());
						pw.print(transition.getSymbolCase());
					}
					System.out.println("\"];");
					pw.println("\"];");
				}
			}
		}
		System.out.println("}");
		pw.println("}");
		// for print style
		// System.out.println("Number of States is " + this.stateNumber + ".");
		// System.out.println("Number of Transitions is " +
		// this.transitionNumber
		// + ".");
		pw.close();
	}
}
