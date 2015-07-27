package predicate_to_DFA;

import java.io.IOException;

public class Main {
	public static void main(String[] args) {

		String[] input = new String[1];
		input[0] = "input";
		Reader reader = new Reader();
		Lexer lexer = new Lexer();
		PEGReshaper pegReshaper = new PEGReshaper();
		DFAMaker dfaMaker = new DFAMaker();
		DFAReshaper dfaReshaper = new DFAReshaper();
		Printer printer = new Printer();

		try {

			reader.read(input);
			lexer.lexe(reader.getInputLine());
			pegReshaper.reshapePEG(lexer.getToken());
			dfaMaker.makeDFA(pegReshaper.getTokenList());
			dfaReshaper.reshapeDEA(dfaMaker.getStateList(), dfaMaker.getMaxPredicateDepth());
			printer.printDOTFile(dfaReshaper.getStateList());

		} catch (IOException e) {
			System.out.println(e + "(Input is invalid.)");
		} catch (ComandLineArgumentException e) {
			System.out.println(e);
		} catch (FileReadException e) {
			System.out.println(e);
		} catch (NonDefinedTokenException e) {
			System.out.println(e);
		} catch (SyntaxErrorException e) {
			System.out.println(e);
		} catch (CodingErrorException e) {
			System.out.println(e);
		}
	}
}