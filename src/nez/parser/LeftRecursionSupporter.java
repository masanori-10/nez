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

class ILRPreCall extends AbstractMapInstruction {
	private NonTerminal rule;
	private ILRPostCall post;

	ILRPreCall(NonTerminal rule, Instruction next, ILRPostCall post) {
		super(InstructionSet.LRPreCall, null, next);
		if (!growing.containsKey(rule)) {
			growing.put(rule, new HashMap<Long, MapEntry>());
		}
		this.rule = rule;
		this.post = post;
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		long pos = sc.getPosition();
		this.post.setPos(pos);
		if (!growing.get(this.rule).containsKey(pos)) {
			growing.get(this.rule).put(pos, new MapEntry(false, pos));
			return this.next;
		} else {
			sc.setPosition(growing.get(this.rule).get(pos).getPos());
			if (growing.get(this.rule).get(pos).getAnsType()) {
				growing.get(this.rule).get(pos).setLRDetected(true);
				return sc.fail();
			} else {
				// TODO ans <- m.ans
				return this.next;
			}
		}
	}
}

class ILRPostCall extends AbstractMapInstruction {
	private NonTerminal rule;
	private long pos;
	private boolean isGrow = false;
	private ICall jump;

	ILRPostCall(NonTerminal rule, Instruction next) {
		super(InstructionSet.LRPostCall, null, next);
		this.rule = rule;
	}

	public void setJump(ICall jump) {
		this.jump = jump;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		if (this.isGrow) {
			if (sc.getPosition() <= growing.get(rule).get(pos).getPos()) {
				sc.setPosition(growing.get(rule).get(pos).getPos());
				// TODO set ast
				return this.next;
			}
			// growing.get(this.rule).get(this.pos).setAst(ast); TODO set ast
			growing.get(rule).get(pos).setPos(sc.getPosition());
			sc.setPosition(this.pos);
			return this.jump;
		}
		// growing.get(this.rule).get(this.pos).setAst(ast); TODO set ast
		growing.get(this.rule).get(this.pos).setPos(sc.getPosition());
		if (growing.get(this.rule).get(this.pos).getAnsType()) {
			if (growing.get(this.rule).get(this.pos).getLRDetected() && this.pos != sc.getPosition()) {
				sc.setPosition(this.pos);
				this.isGrow = true;
				return this.jump;
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
