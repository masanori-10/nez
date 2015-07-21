package nez.main.ext;

import nez.generator.GeneratorLoader;

public class Lsimplecnez {
	static {
		GeneratorLoader.regist("simplecnez", nez.generator.SimpleCParserGenerator.class);
		// File Extension
		GeneratorLoader.regist(".c", nez.generator.CParserGenerator.class);
	}
}
