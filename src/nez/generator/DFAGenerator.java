package nez.generator;

import java.io.IOException;
import java.util.ArrayList;

import dfamaker.AndTransition;
import dfamaker.CodingErrorException;
import dfamaker.DFAReshaper;
import dfamaker.Enum.SymbolCase;
import dfamaker.EpsilonTransition;
import dfamaker.NotTransition;
import dfamaker.Printer;
import dfamaker.State;
import dfamaker.Transition;
import nez.lang.And;
import nez.lang.AnyChar;
import nez.lang.Block;
import nez.lang.ByteChar;
import nez.lang.ByteMap;
import nez.lang.Capture;
import nez.lang.CharMultiByte;
import nez.lang.Choice;
import nez.lang.DefIndent;
import nez.lang.DefSymbol;
import nez.lang.ExistsSymbol;
import nez.lang.Expression;
import nez.lang.Grammar;
import nez.lang.IsIndent;
import nez.lang.IsSymbol;
import nez.lang.Link;
import nez.lang.LocalTable;
import nez.lang.New;
import nez.lang.NonTerminal;
import nez.lang.Not;
import nez.lang.Option;
import nez.lang.Production;
import nez.lang.Repetition;
import nez.lang.Repetition1;
import nez.lang.Replace;
import nez.lang.Sequence;
import nez.lang.Tagging;

public class DFAGenerator extends ParserGenerator {
	private State currentState;
	private ArrayList<State> stateList;
	private int predicateDepth;
	private int maxPredicateDepth;
	DFAReshaper dfaReshaper;
	Printer printer;

	public DFAGenerator() {
		this.currentState = new State();
		this.stateList = new ArrayList<State>();
		this.stateList.add(this.currentState);
		this.predicateDepth = 0;
		this.maxPredicateDepth = 0;
		this.dfaReshaper = new DFAReshaper();
		this.printer = new Printer();
	}

	@Override
	public void makeHeader(Grammar grammar) {
	}

	@Override
	public void makeFooter(Grammar grammar) {
		this.currentState.setEOF();
		try {
			this.dfaReshaper.reshapeDEA(this.stateList, this.maxPredicateDepth);
			this.printer.printDOTFile(this.dfaReshaper.getStateList());
		} catch (CodingErrorException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	@Override
	public String getDesc() {
		return "a Graphviz dot language for DFA";
	}

	@Override
	public void visitProduction(Production r) {
		this.file.writeIndent("production");
		visitExpression(r);
	}

	@Override
	public void visitEmpty(Expression p) {
		this.file.writeIndent("empty");
	}

	@Override
	public void visitFailure(Expression p) {
		this.file.writeIndent("failure");
	}

	@Override
	public void visitAnyChar(AnyChar p) {
		this.file.writeIndent("anychar");
		Transition newTransition = new Transition();
		State newState = this.makeNewState();
		newTransition.setSymbolCase(SymbolCase.ANY);
		newTransition.setNextState(newState);
		this.currentState.addNextTransition(newTransition);
		this.currentState = newState;
	}

	@Override
	public void visitByteChar(ByteChar p) {
		this.file.writeIndent("bytechar");
		Transition newTransition = new Transition();
		State newState = this.makeNewState();
		String symbol = "" + (char) p.byteChar;
		if (p.byteChar == 34) {
			newTransition.setSymbolAndCase("\\" + symbol, SymbolCase.SYMBOL);
		} else if (p.byteChar == 92) {
			newTransition.setSymbolAndCase("\\" + symbol, SymbolCase.SYMBOL);
		} else {
			newTransition.setSymbolAndCase(symbol, SymbolCase.SYMBOL);
		}
		newTransition.setNextState(newState);
		this.currentState.addNextTransition(newTransition);
		this.currentState = newState;
	}

	@Override
	public void visitByteMap(ByteMap p) {
		this.file.writeIndent("bytemap");
		boolean[] b = p.byteMap;
		State margeState = this.makeNewState();
		for (int start = 0; start < 256; start++) {
			if (b[start]) {
				for (int end = start; end < 256; end++) {
					if (b[end]) {
						State newState = this.makeNewState();
						Transition newEpsilonTransition = new EpsilonTransition(newState);
						this.currentState.addNextTransition(newEpsilonTransition);
						Transition newTransition = new Transition();
						State newState2 = this.makeNewState();
						String symbol = "" + (char) end;
						if (end == 34) {
							newTransition.setSymbolAndCase("\\" + symbol, SymbolCase.SYMBOL);
						} else if (end == 92) {
							newTransition.setSymbolAndCase("\\" + symbol, SymbolCase.SYMBOL);
						} else {
							newTransition.setSymbolAndCase(symbol, SymbolCase.SYMBOL);
						}
						newTransition.setNextState(newState2);
						newState.addNextTransition(newTransition);
						Transition newEpsilonTransition2 = new EpsilonTransition(margeState);
						newState2.addNextTransition(newEpsilonTransition2);
					} else {
						break;
					}
				}
				break;
			}
		}
		this.currentState = margeState;
	}

	@Override
	public void visitOption(Option p) {
		this.file.writeIndent("option");
		Transition newEpsilonTransition = new EpsilonTransition();
		this.currentState.addNextTransition(newEpsilonTransition);
		visitExpression(p.get(0));
		newEpsilonTransition.setNextState(this.currentState);
	}

	@Override
	public void visitRepetition(Repetition p) {
		this.file.writeIndent("repetition");
		Transition newEpsilonTransition = new EpsilonTransition(this.currentState);
		visitExpression(p.get(0));
		this.currentState.addNextTransition(newEpsilonTransition);
		this.currentState = newEpsilonTransition.getNextState();
		this.makeNot(p.get(0));
	}

	@Override
	public void visitRepetition1(Repetition1 p) {
		this.file.writeIndent("repetition1");
		Transition newEpsilonTransition = new EpsilonTransition(this.currentState);
		visitExpression(p.get(0));
		this.currentState.addNextTransition(newEpsilonTransition);
		this.makeNot(p.get(0));
	}

	@Override
	public void visitAnd(And p) {
		this.file.writeIndent("AND");
		this.predicateDepth++;
		int currentPredicateDepth = this.predicateDepth;
		if (this.maxPredicateDepth < this.predicateDepth) {
			this.maxPredicateDepth = this.predicateDepth;
		}
		State newState = this.makeNewState();
		State newAndState = this.makeNewState();
		AndTransition newAndTransition = new AndTransition();
		newAndTransition.setPredicateDepth(this.predicateDepth);
		newAndTransition.setNextAndState(newAndState);
		newAndTransition.setNextState(newState);
		this.currentState.addNextTransition(newAndTransition);
		this.currentState = newAndState;
		visitExpression(p.get(0));
		this.currentState.setPredicateDepth(currentPredicateDepth);
		Transition newAnyTransition = new Transition();
		newAnyTransition.setSymbolCase(SymbolCase.ANY);
		newAnyTransition.setNextState(this.currentState);
		this.currentState.addNextTransition(newAnyTransition);
		this.currentState = newState;
		this.predicateDepth = currentPredicateDepth;
	}

	@Override
	public void visitNot(Not p) {
		this.file.writeIndent("not");
		this.makeNot(p.get(0));
	}

	private void makeNot(Expression e) {
		this.predicateDepth++;
		int currentPredicateDepth = this.predicateDepth;
		if (this.maxPredicateDepth < this.predicateDepth) {
			this.maxPredicateDepth = this.predicateDepth;
		}
		State newState = this.makeNewState();
		State newNotState = this.makeNewState();
		NotTransition newNotTransition = new NotTransition();
		newNotTransition.setPredicateDepth(this.predicateDepth);
		newNotTransition.setNextNotState(newNotState);
		newNotTransition.setNextState(newState);
		this.currentState.addNextTransition(newNotTransition);
		this.currentState = newNotState;
		visitExpression(e);
		this.currentState.setPredicateDepth(currentPredicateDepth);
		this.currentState = newState;
		this.predicateDepth = currentPredicateDepth;
	}

	@Override
	public void visitSequence(Sequence p) {
		this.file.writeIndent("sequence");
		visitExpression(p.get(0));
		visitExpression(p.get(1));
	}

	@Override
	public void visitChoice(Choice p) {
		this.file.writeIndent("choice");
		State forkPointState = this.currentState;
		State margePointState = this.makeNewState();
		for (int i = 0; i < p.size(); i++) {
			State newState = this.makeNewState();
			this.currentState.addNextTransition(new EpsilonTransition(newState));
			this.currentState = newState;
			if (i > 0) {
				for (int j = 0; j < i; j++) {
					this.makeNot(p.get(j));
				}
			}
			visitExpression(p.get(i));
			EpsilonTransition epsilonTransition = new EpsilonTransition(margePointState);
			this.currentState.addNextTransition(epsilonTransition);
			this.currentState = forkPointState;
		}
		this.currentState = margePointState;
	}

	@Override
	public void visitNonTerminal(NonTerminal p) {
		this.file.writeIndent("NonTerminal");
	}

	@Override
	public void visitCharMultiByte(CharMultiByte p) {
		this.file.writeIndent("CHARMULTBYTE");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitLink(Link p) {
		visitExpression(p.get(0));
	}

	@Override
	public void visitNew(New p) {
	}

	@Override
	public void visitCapture(Capture p) {
	}

	@Override
	public void visitTagging(Tagging p) {
	}

	@Override
	public void visitReplace(Replace p) {
		this.file.writeIndent("REPLACE");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitBlock(Block p) {
		this.file.writeIndent("BLOCK");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitDefSymbol(DefSymbol p) {
		this.file.writeIndent("DEFSYMBOL");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitIsSymbol(IsSymbol p) {
		this.file.writeIndent("ISSYMBOL");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitDefIndent(DefIndent p) {
		this.file.writeIndent("DEFINDENT");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitIsIndent(IsIndent p) {
		this.file.writeIndent("ISINDENT");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitExistsSymbol(ExistsSymbol p) {
		this.file.writeIndent("EXISTSSYMBOL");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitLocalTable(LocalTable p) {
		this.file.writeIndent("LOCALTABLE");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	private State makeNewState() {
		State newState = new State();
		this.stateList.add(newState);
		return newState;
	}

	public void openBlock() {
		this.file.write("{");
		this.file.incIndent();
	}

	public void closeBlock() {
		this.file.decIndent();
		this.file.writeIndent("}");
	}

}
