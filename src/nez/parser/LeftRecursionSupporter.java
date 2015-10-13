package nez.parser;

import java.util.HashMap;

import nez.ast.Tree;
import nez.lang.Expression;
import nez.lang.Production;

public final class LeftRecursionSupporter {
	private static HashMap<Production, HashMap<Integer, Tree<?>>> LRSmap;
}

abstract class AbstractMapInstruction extends Instruction {
	AbstractMapInstruction(byte opcode, Expression e, Instruction next) {
		super(opcode, e, next);
	}

	@Override
	protected void encodeImpl(ByteCoder c) {
		// No argument
	}
}

class IMLookup extends AbstractMapInstruction {
	IMLookup(Expression e, Instruction next) {
		super(InstructionSet.MLookup, e, next);
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		return null;
	}
}
