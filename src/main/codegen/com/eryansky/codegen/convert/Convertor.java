package com.eryansky.codegen.convert;

/**
 * 1、Table Name 转换为 Entity Name
 * 2、Column Name 转换为 Field
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-7-15 下午1:38:40 
 * @version 1.0
 */
public interface Convertor {
	/**
	 * jdbc类型转换为java类型
	 * 
	 * @param jdbcType
	 *            TableName,ColumnName
	 * @return Entity Name,Entity Name Field
	 */
	public String convert(String jdbcType);
}
