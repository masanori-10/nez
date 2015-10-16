package nez.parser;

import java.util.HashMap;

import nez.ast.Tree;
import nez.lang.Expression;
import nez.lang.expr.NonTerminal;

abstract class AbstractMapInstruction extends Instruction {
	protected static HashMap<NonTerminal, HashMap<Long, MapEntry>> growing = new HashMap<NonTerminal, HashMap<Long, MapEntry>>();
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

class IMLookup extends AbstractMapInstruction {
	private NonTerminal rule;
	private IMMemo sync;

	IMLookup(NonTerminal rule, Instruction next, IMMemo sync) {
		super(InstructionSet.MLookup, null, next);
		if (!growing.containsKey(rule)) {
			growing.put(rule, new HashMap<Long, MapEntry>());
		}
		this.rule = rule;
		this.sync = sync;
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		long pos = sc.getPosition();
		this.sync.pos = pos;
		if (!growing.get(this.rule).containsKey(pos)) {
			growing.get(this.rule).put(pos, new MapEntry(false, pos));
			return this.next;
		} else {
			sc.setPosition(growing.get(this.rule).get(pos).getPos());
			if (growing.get(this.rule).get(pos).getAnsType()) {
				growing.get(this.rule).get(pos).setLRDetected(true);
				return sc.fail();
			} else {
				return;
			}
		}
	}
}

class IMMemo extends AbstractMapInstruction {
	private NonTerminal rule;
	protected long pos;

	IMMemo(NonTerminal rule, Instruction next) {
		super(InstructionSet.MLookup, null, next);
		this.rule = rule;
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		growing.get(this.rule).get(this.pos).setAst(ast);
		growing.get(this.rule).get(this.pos).setPos(sc.getPosition());
		if (growing.get(this.rule).get(this.pos).getAnsType()) {
			if (growing.get(this.rule).get(this.pos).getLRDetected() && this.pos != sc.getPosition()) {
				return;
			}
		}
		return this.next;
	}
}

class MapEntry {
	private boolean ansType; // true -> LR, false -> AST
	private boolean lrDetected;
	private Tree<?> ast = null;
	private long pos;

	public MapEntry(boolean lrDetected, long pos) {
		this.ansType = true;
		this.lrDetected = lrDetected;
		this.pos = pos;
	}

	public MapEntry(Tree<?> ast, long pos) {
		this.ansType = false;
		this.ast = ast;
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

	public Tree<?> getAst() {
		return this.ast;
	}

	public void setAst(Tree<?> ast) {
		this.ansType = false;
		this.ast = ast;
	}

	public long getPos() {
		return this.pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}
}
