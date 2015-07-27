package nez.main.ext;

import nez.generator.GeneratorLoader;

public class Ldfadot {
	static {
		GeneratorLoader.regist("dfadot", nez.generator.DFAGenerator.class);
	}
}
