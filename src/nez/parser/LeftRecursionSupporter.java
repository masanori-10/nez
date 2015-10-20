package nez.parser;

import java.util.HashMap;
import java.util.Stack;

import nez.lang.Expression;

abstract class AbstractLRInstraction extends Instruction {
	protected static HashMap<String, HashMap<Long, MapEntry>> growing = new HashMap<String, HashMap<Long, MapEntry>>();

	public AbstractLRInstraction(byte opcode, Expression e, Instruction next) {
		super(opcode, e, next);
	}
}

class ILRCall extends AbstractLRInstraction {
	ParseFunc f;
	String name;
	public Instruction jump = null;

	private long pos;
	private Object astlog;

	ILRCall(ParseFunc f, String name, ILRGrow grow) {
		super(InstructionSet.LRCall, null, grow);
		this.f = f;
		this.name = name;

		if (!growing.containsKey(name)) {
			growing.put(name, new HashMap<Long, MapEntry>());
		}
	}

	ILRCall(ParseFunc f, String name, Instruction jump, ILRGrow grow) {
		super(InstructionSet.LRCall, null, jump);
		this.name = name;
		this.f = f;
		this.jump = grow;

		if (!growing.containsKey(name)) {
			growing.put(name, new HashMap<Long, MapEntry>());
		}
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
	protected void encodeImpl(ByteCoder c) {
		// TODO encode argument
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		this.pos = sc.getPosition();
		this.astlog = sc.getAstMachine().saveTransactionPoint();
		if (growing.get(this.name).containsKey(this.pos)) {
			sc.setPosition(growing.get(this.name).get(this.pos).getPos());
			if (growing.get(this.name).get(this.pos).getAnsType()) {
				growing.get(this.name).get(this.pos).setLRDetected(true);
				return sc.fail();
			}
			sc.getAstMachine().pasteTransactionPoint(growing.get(this.name).get(this.pos).getResult());
			return ((ILRGrow) this.jump).jump;
		}
		growing.get(this.name).put(this.pos, new MapEntry(false, this.pos));
		((ILRGrow) this.jump).pushPos(this.pos);
		((ILRGrow) this.jump).pushAstlog(this.astlog);
		StackData s = sc.newUnusedStack();
		s.ref = this.jump;
		return this.next;
	}
}

class ILRGrow extends AbstractLRInstraction {
	ParseFunc f;
	String name;
	public Instruction jump = null;

	private Stack<Long> pos = new Stack<Long>();
	private Stack<Object> astlog = new Stack<Object>();
	private boolean isGrow = false;

	ILRGrow(ParseFunc f, String name, Instruction next) {
		super(InstructionSet.LRGrow, null, next);
		this.f = f;
		this.name = name;
	}

	ILRGrow(ParseFunc f, String name, Instruction jump, Instruction next) {
		super(InstructionSet.LRGrow, null, jump);
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
	protected void encodeImpl(ByteCoder c) {
		// TODO encode argument
	}

	public void pushPos(long pos) {
		this.pos.push(pos);
	}

	public void pushAstlog(Object astlog) {
		this.astlog.push(astlog);
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		if (this.isGrow) {
			if (sc.getPosition() <= growing.get(this.name).get(this.pos.peek()).getPos()) {
				sc.setPosition(growing.get(this.name).get(this.pos.peek()).getPos());
				sc.getAstMachine().backTransactionPoint(this.astlog.peek());
				sc.getAstMachine().pasteTransactionPoint(growing.get(this.name).get(this.pos.peek()).getResult());
				this.isGrow = false;
				this.pos.pop();
				this.astlog.pop();
				return this.jump;
			}
			growing.get(this.name).get(this.pos.peek()).setPos(sc.getPosition());
			growing.get(this.name).get(this.pos.peek()).setResult(sc.getAstMachine().getNextLog(this.astlog.peek()), sc.getAstMachine().saveTransactionPoint());
			sc.setPosition(this.pos.peek());
			sc.getAstMachine().backTransactionPoint(this.astlog.peek());
			StackData s = sc.newUnusedStack();
			s.ref = this;
			return this.next;
		}
		growing.get(this.name).get(this.pos.peek()).setResult(sc.getAstMachine().getNextLog(this.astlog.peek()), sc.getAstMachine().saveTransactionPoint());
		growing.get(this.name).get(this.pos.peek()).setPos(sc.getPosition());
		if (growing.get(this.name).get(this.pos.peek()).getLRDetected() && this.pos.peek() < sc.getPosition()) {
			sc.setPosition(this.pos.peek());
			sc.getAstMachine().backTransactionPoint(this.astlog.peek());
			StackData s = sc.newUnusedStack();
			s.ref = this;
			this.isGrow = true;
			return this.next;
		}
		this.pos.pop();
		this.astlog.pop();
		return this.jump;
	}
}

class MapEntry {
	private boolean ansType; // true -> LR, false -> AST
	private boolean lrDetected;
	private Object[] result = new Object[2];
	private long pos;

	public MapEntry(boolean lrDetected, long pos) {
		this.ansType = true;
		this.lrDetected = lrDetected;
		this.pos = pos;
	}

	public MapEntry(Object first, Object end, long pos) {
		this.ansType = false;
		this.result[0] = first;
		this.result[1] = end;
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

	public Object[] getResult() {
		return this.result;
	}

	public void setResult(Object first, Object end) {
		this.ansType = false;
		this.result[0] = first;
		this.result[1] = end;
	}

	public long getPos() {
		return this.pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}
}
