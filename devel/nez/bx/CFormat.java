package nez.bx;

import java.io.IOException;

import nez.ParserGenerator;
import nez.ast.Source;
import nez.ast.Tree;
import nez.lang.Grammar;
import nez.main.Command;
import nez.parser.Parser;
import nez.tool.ast.TreeWriter;

public class CFormat extends Command {
	@Override
	public void exec() throws IOException {
		strategy.Optimization = false;
		Grammar grammar = this.newGrammar();
		checkInputSource();
		Parser parser = newParser();
		TreeWriter tw = this.getTreeWriter("ast xml json");
		while (hasInputSource()) {
			Source input = nextInputSource();
			Tree<?> node = parser.parse(input);
			if (node == null) {
				parser.showErrors();
				continue;
			}
			if (this.outputDirectory != null) {
				tw.init(getOutputFileName(input, tw.getFileExtension()));
			}
			ParserGenerator pg = new ParserGenerator();
			grammar = pg.loadGrammar("format.nez");
			Parser formatParser = this.strategy.newParser(grammar);
			input = nextInputSource();
			Tree<?> formatNode = formatParser.parse(input);
			if (formatNode == null) {
				formatParser.showErrors();
				continue;
			}
			FormatterBuilder builder = new FormatterBuilder();
			builder.visit(formatNode);
			Formatter formatter = new Formatter(builder.getContext());
			String source = formatter.format(node);
			System.out.println(source);
		}
	}
}
