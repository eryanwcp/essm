package com.eryansky.codegen.convert;

/**
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-7-15 下午1:38:50 
 * @version 1.0
 */
public class TableConvertor implements Convertor {

	@Override
	public String convert(String tableName) {
		String[] arrs = tableName.split("_");
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String s : arrs) {
			i++;
			if(i>2){
				sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1).toLowerCase());
			}
		}
		return sb.toString();
	}

}
