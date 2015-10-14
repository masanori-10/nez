package nez.parser;

import java.util.HashMap;

import nez.ast.Tree;
import nez.lang.Expression;
import nez.lang.Production;
import nez.lang.expr.NonTerminal;

abstract class AbstractMapInstruction extends Instruction {
	protected static HashMap<NonTerminal, HashMap<Long, Tree<?>>> growing = new HashMap<NonTerminal, HashMap<Long, Tree<?>>>();
	protected static NonTerminal ruleOrig;
	protected static long posOrig;

	AbstractMapInstruction(byte opcode, Expression e, Instruction next) {
		super(opcode, e, next);
	}

	@Override
	protected void encodeImpl(ByteCoder c) {
		// TODO encode argument
	}
}

class IMAccess extends AbstractMapInstruction {
	private NonTerminal rule;

	IMAccess(NonTerminal rule, Instruction next) {
		super(InstructionSet.MAccess, null, next);
		growing.put(rule, new HashMap<Long, Tree<?>>());
		this.rule = rule;
	}

	@Override
	public Instruction exec(RuntimeContext sc) throws TerminationException {
		long pos = sc.getPosition();
		if(this.rule == ruleOrig && growing.get(rule).containsKey(pos)){
			growing.get(rule).get(pos);
			return this.next;
		}else if(this.rule == ruleOrig && pos == posOrig){
			growing.get(rule).put(pos, null);
			while(true){
				Tree<?> result =;
				Tree<?> seed = growing.get(rule).get(pos);
				if(result == null || (seed != null && )){
					growing.get(rule).remove(pos);
					seed;
					return this.next;
				}
				growing.get(rule).put(pos, result);
			}
		}else{
			ruleOrig = this.rule;
			posOrig = pos;
			return this.next;
		}
	}
}
