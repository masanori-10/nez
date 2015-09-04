package nez.main;

import nez.SourceContext;
import nez.ast.AbstractTreeWriter;
import nez.ast.CommonTree;
import nez.lang.Grammar;
import nez.util.ConsoleUtils;

public class LCparse extends Command {
	@Override
	public String getDesc() {
		return "an AST parser";
	}

	@Override
	public void exec(CommandContext config) {
		Grammar g = config.getGrammar();
		while (config.hasInputSource()) {
			SourceContext source = config.nextInputSource();
			CommonTree node = g.parseCommonTree(source);
			if (node == null) {
				ConsoleUtils.println(source.getSyntaxErrorMessage());
				continue;
			}
			if (source.hasUnconsumed()) {
				ConsoleUtils.println(source.getUnconsumedMessage());
			}
			source = null;
			record(g.getProfiler(), node);
			g.logProfiler();
			AbstractTreeWriter w = new AbstractTreeWriter(config.getNezOption(), config.getOutputFileName(source, "ast"));
			w.writeTree(node);
			w.writeNewLine();
			w.close();
		}
	}

	private void record(NezProfier prof, CommonTree node) {
		if (prof != null) {
			System.gc();
			prof.setCount("O.Size", node.countSubNodes());
			long t1 = System.nanoTime();
			CommonTree t = node.dup();
			long t2 = System.nanoTime();
			NezProfier.recordLatencyMS(prof, "O.Overhead", t1, t2);
		}
	}

}