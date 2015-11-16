package nez.parser;

import java.util.TreeSet;

import nez.Strategy;
import nez.Verbose;
import nez.dfa.AFAConverter;
import nez.dfa.DFA;
import nez.dfa.DOTGenerator;
import nez.dfa.State;
import nez.dfa.Transition;
import nez.lang.Expression;
import nez.lang.Production;
import nez.lang.expr.Cbyte;
import nez.lang.expr.ExpressionCommons;
import nez.lang.expr.NonTerminal;
import nez.util.UList;

public abstract class NezCompiler extends AbstractGenerator {

	public final static NezCompiler newCompiler(Strategy option) {
		return new PackratCompiler(option);
	}

	protected NezCompiler(Strategy option) {
		super(option);
	}

	public NezCode compile(GenerativeGrammar gg) {
		this.setGenerativeGrammar(gg);
		long t = System.nanoTime();
		UList<Instruction> codeList = new UList<Instruction>(new Instruction[64]);
		for (Production p : gg) {
			if (!p.isSymbolTable()) {
				this.encodeProduction(codeList, p, new IRet(p));
			}
		}
		this.layoutCachedInstruction(codeList);
		for (Instruction inst : codeList) {
			if (inst instanceof ICall) {
				((ICall) inst).sync();
			}
			// Verbose.debug("\t" + inst.id + "\t" + inst);
		}
		long t2 = System.nanoTime();

		// add for DFACompiler verbose view
		System.out.println("CompilingTime:" + (t2 - t));
		System.out.println("IDFirst count:" + idfirstCount);
		idfirstCount = 0;
		Verbose.printElapsedTime("CompilingTime", t, t2);
		return new NezCode(gg, codeList, gg.memoPointList);
	}

	private Production encodingProduction;

	protected final Production getEncodingProduction() {
		return this.encodingProduction;
	}

	private UList<Instruction> cachedInstruction;

	protected void addCachedInstruction(Instruction inst) {
		if (this.cachedInstruction == null) {
			this.cachedInstruction = new UList<Instruction>(new Instruction[32]);
		}
		this.cachedInstruction.add(inst);
	}

	private void layoutCachedInstruction(UList<Instruction> codeList) {
		if (this.cachedInstruction != null) {
			for (Instruction inst : this.cachedInstruction) {
				layoutCode(codeList, inst);
			}
		}
	}

	protected void encodeProduction(UList<Instruction> codeList, Production p, Instruction next) {
		ParseFunc f = this.getParseFunc(p);
		// System.out.println("inline: " + f.inlining + " name: " +
		// p.getLocalName());
		encodingProduction = p;
		if (!f.inlining) {
			next = Coverage.encodeExitCoverage(p, next);
		}

		// modify for DFACompiler
		if (strategy.isEnabled("DFA", Strategy.DFA)) {
			if (checkNonExsistNonterminal(p.getExpression())) {
				AFAConverter afaConverter = new AFAConverter();
				afaConverter.build(p);
				DFA dfa = afaConverter.computeDFA();
				if (dfa != null) {
					f.compiled = encodeDFA(dfa, next);
				}
				System.out.println("graph" + graphID++ + ":" + p.getUniqueName());
				DOTGenerator.writeDFA(dfa);
			}
		}
		if (f.compiled == null) {
			f.compiled = encode(f.getExpression(), next, null/* failjump */);
		}

		if (!f.inlining) {
			f.compiled = Coverage.encodeEnterCoverage(p, f.compiled);
		}
		Instruction block = new ILabel(p, f.compiled);
		this.layoutCode(codeList, block);
	}

	public final void layoutCode(UList<Instruction> codeList, Instruction inst) {
		if (inst == null) {
			return;
		}

		// add for DFACompiler
		if (inst instanceof IDFirst) {
			idfirstCount++;
		}

		if (inst.id == -1) {
			inst.id = codeList.size();
			codeList.add(inst);
			layoutCode(codeList, inst.next);
			if (inst.next != null && inst.id + 1 != inst.next.id) {
				Instruction.labeling(inst.next);
			}
			layoutCode(codeList, inst.branch());
			if (inst instanceof IFirst) {
				IFirst match = (IFirst) inst;
				for (int ch = 0; ch < match.jumpTable.length; ch++) {
					layoutCode(codeList, match.jumpTable[ch]);
				}
			}
			// encode(inst.branch2());
		}
	}

	// add for DFACompiler
	int idfirstCount = 0;
	int graphID = 0;
	static Instruction commonFailure = new IFail(null);

	private Instruction encodeDFA(DFA dfa, Instruction next) {
		Instruction states[] = new Instruction[dfa.getS().size() - 1];
		int isByte[] = new int[dfa.getS().size() - 1];
		TreeSet<Transition> transitions = new TreeSet<Transition>();
		for (Transition transition : dfa.getTau()) {
			if (transition.getLabel() != 0 && transition.getSrc() != 1 && transition.getDst() != 1) {
				int srcID = transition.getSrc() == 0 ? transition.getSrc() : transition.getSrc() - 1;
				transitions.add(transition);
				if (isByte[srcID] == 0) {
					isByte[srcID] = transition.getLabel();
				} else {
					isByte[srcID] = -1;
				}
			}
		}
		for (int i = 0; i < dfa.getS().size() - 1; i++) {
			if (isByte[i] == -1) {
				states[i] = new IDFirst(null, commonFailure);
			} else if (isByte[i] == 0) {
				states[i] = commonFailure;
			} else {
				states[i] = new IByte((Cbyte) ExpressionCommons.newCbyte(null, false, isByte[i]), commonFailure);
			}
		}
		for (State state : dfa.getF()) {
			int stateID = state.getID() == 0 ? state.getID() : state.getID() - 1;
			if (isByte[stateID] != 0) {
				states[stateID] = new IAlt(null, next, states[stateID]);
				states[stateID] = new ISucc(null, states[stateID]);
			} else {
				states[stateID] = next;
				states[stateID] = new ISucc(null, states[stateID]);
			}
		}
		for (Transition transition : transitions) {
			int srcID = transition.getSrc() == 0 ? transition.getSrc() : transition.getSrc() - 1;
			int dstID = transition.getDst() == 0 ? transition.getDst() : transition.getDst() - 1;

			if (isByte[srcID] == -1) {
				if (states[srcID] instanceof IDFirst) {
					((IDFirst) states[srcID]).setJumpTable(transition.getLabel(), states[dstID]);
				} else {
					((IDFirst) states[srcID].next.next).setJumpTable(transition.getLabel(), states[dstID]);
				}
			} else if (isByte[srcID] > 0) {
				if (states[srcID] instanceof IByte) {
					states[srcID].next = states[dstID];
				} else {
					states[srcID].next.next.next = states[dstID];
				}
			}
		}
		dfa.tau = transitions;
		return new IAlt(null, commonFailure, states[dfa.getf().getID() == 0 ? dfa.getf().getID() : dfa.getf().getID() - 1]);
	}

	private boolean checkNonExsistNonterminal(Expression e) {
		if (e instanceof NonTerminal) {
			return false;
		}
		for (int i = 0; i < e.size(); i++) {
			if (!checkNonExsistNonterminal(e.get(i))) {
				return false;
			}
		}
		return true;
	}
}
