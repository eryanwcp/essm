package com.eryansky.modules.sys.sn;

import java.util.Map;

public class CharacterSequenceGenerator implements IGenerator {
	private static final String type = GeneratorConstants.CHARATER_SEQUENCE;
	//从’A’开始，自动增长
	private char c = 'A'; 
	@Override
	public String getGeneratorType() {
		return type;
	}
	@Override
	public String generate(String formatStr,Map paraMap) {
		return String.valueOf((char)c++);
	}
}