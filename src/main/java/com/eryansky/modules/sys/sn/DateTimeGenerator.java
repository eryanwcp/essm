package com.eryansky.modules.sys.sn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DateTimeGenerator implements IGenerator{
	private static final String type = GeneratorConstants.DATE_TIME;
	@Override
	public String getGeneratorType() {
		return type;
	}
	@Override
	public String generate(String formatStr,Map paraMap) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern(formatStr);
		return sdf.format(new Date());
	}
}