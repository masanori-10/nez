package nez.main.ext;

import nez.generator.GeneratorLoader;

public class Ldot {
	static {
		GeneratorLoader.regist("dot", nez.generator.SimpleCParserGenerator.class);
		// File Extension
		GeneratorLoader.regist(".dot", nez.generator.CParserGenerator.class);
	}
}
