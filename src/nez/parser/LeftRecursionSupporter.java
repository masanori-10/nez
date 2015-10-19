package nez.parser;

import java.util.HashMap;

import nez.lang.Expression;

abstract class AbstractMapInstruction extends Instruction {
	protected static HashMap<String, HashMap<Long, MapEntry>> growing = new HashMap<String, HashMap<Long, MapEntry>>();
	// protected static NonTerminal ruleOrig;
	// protected static long posOrig;

	AbstractMapInstruction(byte opcode, Expression e, Instruction next) {
		super(opcode, e, next);
	}

	@Override
	protected void encodeImpl(ByteCoder c) {
		// TODO encode argument
	}
}

class ILRCall extends AbstractMapInstruction {
	ParseFunc f;
	String name;
	public Instruction jump = null;
	private long pos;

	ILRCall(ParseFunc f, String name, Instruction next) {
		super(InstructionSet.LRCall, null, next);
		this.f = f;
		this.name = name;
		if (!growing.containsKey(name)) {
			growing.put(name, new HashMap<Long, MapEntry>());
		}
	}

	ILRCall(ParseFunc f, String name, Instruction jump, Instruction next) {
		super(InstructionSet.LRCall, null, jump);
		this.name = name;
		this.f = f;
		this.jump = next;
	}

	void sync() {
		if (this.jump == null) {
			this.jump = labeling(this.next);
			this.next = labeling(f.compiled);
		}
		this.f = null;
	}

	public final String getNonTerminalName() {
		return this.name;
	}

	@Override
	protected String getOperand() {
		return label(jump);
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		this.pos = sc.getPosition();
		if (!growing.get(this.name).containsKey(this.pos)) {
			growing.get(this.name).put(this.pos, new MapEntry(false, this.pos));
			StackData s = sc.newUnusedStack();
			s.ref = this.jump;
			return this.next;
		} else {
			sc.setPosition(growing.get(this.name).get(this.pos).getPos());
			if (growing.get(this.name).get(this.pos).getAnsType()) {
				growing.get(this.name).get(this.pos).setLRDetected(true);
				return sc.fail();
			} else {
				sc.astMachine.rollTransactionPoint(growing.get(this.name).get(this.pos).getResult());
				StackData s = sc.newUnusedStack();
				s.ref = this.jump;
				return this.next;
			}
		}
	}// TODO add ILRPostCall
}

// class ILRPreCall extends AbstractMapInstruction {
// private NonTerminal rule;
// private ILRPostCall post;
//
// ILRPreCall(NonTerminal rule, Instruction next, ILRPostCall post) {
// super(InstructionSet.LRPreCall, null, next);
// if (!growing.containsKey(rule)) {
// growing.put(rule, new HashMap<Long, MapEntry>());
// }
// this.rule = rule;
// this.post = post;
// }
//
// @Override
// public Instruction exec(RuntimeContext sc) throws TerminationException {
// long pos = sc.getPosition();
// this.post.setPos(pos);
// this.post.setBase(sc.getAstMachine().saveTransactionPoint());
// if (!growing.get(this.rule).containsKey(pos)) {
// growing.get(this.rule).put(pos, new MapEntry(false, pos));
// return this.next;
// } else {
// sc.setPosition(growing.get(this.rule).get(pos).getPos());
// if (growing.get(this.rule).get(pos).getAnsType()) {
// growing.get(this.rule).get(pos).setLRDetected(true);
// return sc.fail();
// } else {
// sc.astMachine.rollTransactionPoint(growing.get(this.rule).get(pos).getResult());
// return this.next;
// }
// }
// }
// }
//
// class ILRPostCall extends AbstractMapInstruction {
// private NonTerminal rule;
// private long pos;
// private Object base;
// private boolean isGrow = false;
// private ICall jump;
//
// ILRPostCall(NonTerminal rule, Instruction next) {
// super(InstructionSet.LRPostCall, null, next);
// this.rule = rule;
// }
//
// public void setJump(ICall jump) {
// this.jump = jump;
// }
//
// public void setPos(long pos) {
// this.pos = pos;
// }
//
// public void setBase(Object base) {
// this.base = base;
// }
//
// @Override
// public Instruction exec(RuntimeContext sc) throws TerminationException {
// if (this.isGrow) {
// if (sc.getPosition() <= growing.get(this.rule).get(this.pos).getPos()) {
// sc.setPosition(growing.get(this.rule).get(this.pos).getPos());
// sc.getAstMachine().rollTransactionPoint(growing.get(this.rule).get(this.pos).getResult());
// return this.next;
// }
// growing.get(this.rule).get(this.pos).setResult(sc.getAstMachine().saveTransactionPoint());
// growing.get(this.rule).get(this.pos).setPos(sc.getPosition());
// sc.setPosition(this.pos);
// sc.getAstMachine().rollTransactionPoint(this.base);
// return this.jump;
// }
// growing.get(this.rule).get(this.pos).setResult(sc.getAstMachine().saveTransactionPoint());
// growing.get(this.rule).get(this.pos).setPos(sc.getPosition());
// if (growing.get(this.rule).get(this.pos).getLRDetected() && this.pos !=
// sc.getPosition()) {
// sc.setPosition(this.pos);
// sc.getAstMachine().rollTransactionPoint(this.base);
// this.isGrow = true;
// return this.jump;
// }
// return this.next;
// }
// }

class MapEntry {
	private boolean ansType; // true -> LR, false -> AST
	private boolean lrDetected;
	private Object result;
	private long pos;

	public MapEntry(boolean lrDetected, long pos) {
		this.ansType = true;
		this.lrDetected = lrDetected;
		this.pos = pos;
	}

	public MapEntry(Object result, long pos) {
		this.ansType = false;
		this.result = result;
		this.pos = pos;
	}

	public boolean getAnsType() {
		return this.ansType;
	}

	public boolean getLRDetected() {
		return this.lrDetected;
	}

	public void setLRDetected(boolean lrDetected) {
		this.ansType = true;
		this.lrDetected = lrDetected;
	}

	public Object getResult() {
		return this.result;
	}

	public void setResult(Object result) {
		this.ansType = false;
		this.result = result;
	}

	public long getPos() {
		return this.pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}
}
