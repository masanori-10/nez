package nez.bx;

import java.io.IOException;

import nez.lang.Grammar;
import nez.main.Command;

public class CGenerate extends Command {
	@Override
	public void exec() throws IOException {
		/* Setting requird options */
		strategy.Optimization = false;
		Grammar grammar = this.newGrammar();
		FormatGenerator gen = new FormatGenerator(outputDirectory, grammarFile);
		gen.generate(grammar);
	}
}
