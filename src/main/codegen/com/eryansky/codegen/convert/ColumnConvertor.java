package com.eryansky.codegen.convert;

public class ColumnConvertor implements Convertor {

	@Override
	public String convert(String columnName) {
		if (columnName == null || "".equals(columnName))
			return null;
		columnName = columnName.toLowerCase();
		String[] arrs = columnName.split("_");
		if (arrs == null || arrs.length == 0)
			return null;
		StringBuilder sb = new StringBuilder();
		int len = arrs.length;
		sb.append(arrs[0]);
		if (len > 1) {
			for (int i = 1; i < len; i++) {
				String s = arrs[i];
				sb.append(Character.toUpperCase(s.charAt(0)));
				sb.append(s.substring(1));
			}
		}
		return sb.toString();
	}

}
