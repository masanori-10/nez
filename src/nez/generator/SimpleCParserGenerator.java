package nez.generator;

import nez.lang.And;
import nez.lang.AnyChar;
import nez.lang.Block;
import nez.lang.ByteChar;
import nez.lang.ByteMap;
import nez.lang.Capture;
import nez.lang.CharMultiByte;
import nez.lang.Choice;
import nez.lang.DefIndent;
import nez.lang.DefSymbol;
import nez.lang.ExistsSymbol;
import nez.lang.Expression;
import nez.lang.Grammar;
import nez.lang.IsIndent;
import nez.lang.IsSymbol;
import nez.lang.Link;
import nez.lang.LocalTable;
import nez.lang.New;
import nez.lang.NonTerminal;
import nez.lang.Not;
import nez.lang.Option;
import nez.lang.Production;
import nez.lang.Repetition;
import nez.lang.Repetition1;
import nez.lang.Replace;
import nez.lang.Sequence;
import nez.lang.Tagging;

public class SimpleCParserGenerator extends ParserGenerator {

	@Override
	public void makeHeader(Grammar grammar) {
		this.file
				.write("// This file is generated by nez/src/nez/generator/SimpleCParserGenerator.java");
		this.file.writeNewLine();
		this.file.writeIndent("#include <stdio.h>");
		for (Production r : grammar.getProductionList()) {
			if (!r.getLocalName().startsWith("\"")) {
				this.file.writeIndent("int p" + r.getLocalName()
						+ "(char* source, size_t* position);");
			}
		}
		this.file.writeNewLine();
	}

	@Override
	public void makeFooter(Grammar grammar) {
		this.file.writeNewLine();
		this.file.writeIndent("int main(int argc, char* const argv[])");
		this.openBlock();

		this.file.writeIndent("size_t length = 0;");
		this.file.writeIndent("FILE *fp = fopen(filename, \"rb\");");
		this.file.writeIndent("char *source;");
		this.file.writeIndent("if (!fp)");
		this.openBlock();
		this.file
				.writeIndent("fprintf(stderr,\"fopen error: cannot open file\");");
		this.file.writeIndent("return NULL;");
		this.closeBlock();
		this.file.writeIndent("fseek(fp, 0, SEEK_END);");
		this.file.writeIndent("length = (size_t)ftell(fp);");
		this.file.writeIndent("fseek(fp, 0, SEEK_SET);");
		this.file.writeIndent("source = (char *)malloc(length + 1);");
		this.file.writeIndent("if (length != fread(source, 1, len, fp))");
		this.openBlock();
		this.file
				.writeIndent("fprintf(stderr,\"fread error: cannot read file collectly\");");
		this.closeBlock();
		this.file.writeIndent("source[length] = '\\0';");
		this.file.writeIndent("fclose(fp);");
		this.file.writeIndent("");

		this.file.writeIndent("size_t *position;");
		this.file.writeIndent("*position = 0;");
		this.file.writeIndent("if(pFile(source, position))");
		this.openBlock();
		this.file.writeIndent("fprintf(stderr,\"parse error\");");
		this.closeBlock();
		this.file.writeIndent("else if()");
		this.openBlock();
		this.file.writeIndent("fprintf(stderr,\"unconsume\");");
		this.closeBlock();
		this.file.writeIndent("else");
		this.openBlock();
		this.file.writeIndent("fprintf(stderr,\"parse success\");");
		this.closeBlock();
		this.file.writeIndent("return 0;");
		this.closeBlock();
	}

	@Override
	public String getDesc() {
		return "a readable Nez parser generator for C";
	}

	@Override
	public void visitProduction(Production r) {
		this.file.writeIndent("int p" + r.getLocalName()
				+ "(char* source, size_t* position)");
		this.openBlock();
		visitExpression(r);
		this.file.writeIndent("return 1;");
		this.closeBlock();
	}

	@Override
	public void visitEmpty(Expression p) {
		this.file.writeIndent("EMPTY");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitFailure(Expression p) {
		this.file.writeIndent("FAILUARE");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitAnyChar(AnyChar p) {
		this.file.writeIndent("if(source[*position] != 0)");
		this.openBlock();
		this.file.writeIndent("position*++;");
		this.file.writeIndent("unconsumed = 0;");
		this.closeBlock();
		this.file.writeIndent("else");
		this.openBlock();
		this.file.writeIndent("unconsumed = 1;");
		this.closeBlock();
	}

	@Override
	public void visitByteChar(ByteChar p) {
		this.file.writeIndent("if(source[*position] == " + p.byteChar + ")");
		this.openBlock();
		this.file.writeIndent("position*++;");
		this.file.writeIndent("unconsumed = 0;");
		this.closeBlock();
		this.file.writeIndent("else");
		this.openBlock();
		this.file.writeIndent("unconsumed = 1;");
		this.closeBlock();
	}

	@Override
	public void visitByteMap(ByteMap p) {
		this.file.writeIndent("BYTEMAP");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitOption(Option p) {
		this.file.writeIndent("OPTION");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitRepetition(Repetition p) {
		this.file.writeIndent("while(1)");
		this.openBlock();
		this.file.writeIndent("size_t startPosition = *position;");
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
			this.file.writeIndent("if(unconsumed)");
			this.openBlock();
			this.file.writeIndent("*position = startPosition;");
			this.file.writeIndent("break;");
			this.closeBlock();
		}
		this.closeBlock();
	}

	@Override
	public void visitRepetition1(Repetition1 p) {
		this.file.writeIndent("REPETITION1");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitAnd(And p) {
		this.file.writeIndent("AND");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitNot(Not p) {
		this.file.writeIndent("NOT");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitSequence(Sequence p) {
		// this.file.writeIndent("SEQUENCE");
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
			// this.file.writeIndent("if(unconsumed)");
			// this.openBlock();
			// this.file.writeIndent("*position = startPosition;");
			// this.file.writeIndent("break;");
			// this.closeBlock();
		}
	}

	@Override
	public void visitChoice(Choice p) {
		this.file.writeIndent("CHOICE");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitNonTerminal(NonTerminal p) {
		this.file
				.writeIndent("if(p" + p.getLocalName() + "(source, position))");
		this.openBlock();
		this.file.writeIndent("return 0;");
		this.closeBlock();
	}

	@Override
	public void visitCharMultiByte(CharMultiByte p) {
		this.file.writeIndent("CHARMULTBYTE");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitLink(Link p) {
		visitExpression(p.get(0));
	}

	@Override
	public void visitNew(New p) {
	}

	@Override
	public void visitCapture(Capture p) {
	}

	@Override
	public void visitTagging(Tagging p) {
	}

	@Override
	public void visitReplace(Replace p) {
		this.file.writeIndent("REPLACE");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitBlock(Block p) {
		this.file.writeIndent("BLOCK");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitDefSymbol(DefSymbol p) {
		this.file.writeIndent("DEFSYMBOL");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitIsSymbol(IsSymbol p) {
		this.file.writeIndent("ISSYMBOL");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitDefIndent(DefIndent p) {
		this.file.writeIndent("DEFINDENT");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitIsIndent(IsIndent p) {
		this.file.writeIndent("ISINDENT");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitExistsSymbol(ExistsSymbol p) {
		this.file.writeIndent("EXISTSSYMBOL");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	@Override
	public void visitLocalTable(LocalTable p) {
		this.file.writeIndent("LOCALTABLE");
		this.file.incIndent();
		for (int i = 0; i < p.size(); i++) {
			visitExpression(p.get(i));
		}
		this.file.decIndent();
	}

	public void openBlock() {
		this.file.write("{");
		this.file.incIndent();
	}

	public void closeBlock() {
		this.file.decIndent();
		this.file.writeIndent("}");
	}
}
