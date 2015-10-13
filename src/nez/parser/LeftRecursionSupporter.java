package nez.parser;

import java.util.HashMap;

import nez.ast.Tree;
import nez.lang.Expression;
import nez.lang.Production;

abstract class AbstractMapInstruction extends Instruction {
	protected static HashMap<Production, HashMap<Integer, Tree<?>>> LRSmap;

	AbstractMapInstruction(byte opcode, Expression e, Instruction next) {
		super(opcode, e, next);
	}

	@Override
	protected void encodeImpl(ByteCoder c) {
		// TODO encode HashMap
	}
}

class IMAccess extends AbstractMapInstruction {
	IMAccess(Expression e, Instruction next) {
		super(InstructionSet.MAccess, e, next);
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		return null;
	}
}
