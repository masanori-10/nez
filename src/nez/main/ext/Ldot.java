package nez.main.ext;

import nez.generator.GeneratorLoader;

public class Ldot {
	static {
		GeneratorLoader.regist("dot", nez.generator.DFAGenerator.class);
	}
}
