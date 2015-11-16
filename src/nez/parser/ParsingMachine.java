package nez.parser;

import nez.io.SourceContext;

public class ParsingMachine {

	public boolean run(Instruction code, SourceContext sc) {
		boolean result = false;
		long t = System.nanoTime();
		try {
			while (true) {
				code = code.exec(sc);
			}
		} catch (TerminationException e) {
			result = e.status;
		}
		long t2 = System.nanoTime();
		System.out.println("ParsingTime:" + (t2 - t));
		return result;
	}

}
