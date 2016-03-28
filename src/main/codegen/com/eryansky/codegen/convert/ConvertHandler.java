package com.eryansky.codegen.convert;

import com.eryansky.codegen.util.JavaType;
import com.eryansky.codegen.vo.Column;
import com.eryansky.codegen.vo.Table;

/**
 * 执行JDBC 向 JAVA类型的转换。包括：
 * 1、表名转Entity名
 * 2、列名转Field名
 * 3、Jdbc Type转 Java Type
 * 4、entity get,set 方法转换
 * 5、entity instance名
 *
 */
public class ConvertHandler {
	private Convertor tableConvertor = null;
	private Convertor columnConvertor = null;

	public ConvertHandler(){
		
	}
	/**
	 * @param tableConvertor
	 *            如果使用默认转换规则，可传 null
	 * @param columnConvertor
	 *            如果使用默认转换规则，可传 null
	 */
	public ConvertHandler(Convertor tableConvertor, Convertor columnConvertor) {
		this.tableConvertor = tableConvertor;
		this.columnConvertor = columnConvertor;
	}

	public void convert(Table table) throws Exception {
		validate(table);
		/************** 生成类名 *******************/
		if (table.getEntityName() == null) {
			String entityName = tableConvertor.convert(table.getTableName());
			table.setEntityName(entityName);
		}
		/************** 数据库类型转JAVA类型 *******************/
		for (Column column : table.getColumns()) {
			// JDBC类型转换java类型
			column.setJavaType(JavaType.getJavaType(column.getJdbcType()));
			// column转换变量
			column.setFieldName(columnConvertor.convert(column.getColumnName()));
			// get set方法
			String methodName = column.getFieldName().substring(0, 1).toUpperCase() + column.getFieldName().substring(1);
			column.setSetMethod("set" + methodName);
			if (column.getJavaType().equalsIgnoreCase("boolean"))
				column.setGetMethod("is" + methodName);
			else
				column.setGetMethod("get" + methodName);
		}
	}

	public void validate(Table table) throws Exception {
		if (table == null || table.getTableName() == null || "".equals(table.getTableName()))
			throw new Exception(ConvertHandler.class.getName() + ": 参数  Table 不能为null.");
		if (table.getColumns() == null || table.getColumns().size() == 0)
			throw new Exception(ConvertHandler.class.getName() + ":  表：[ " + table.getTableName() + " ] ,不包含任何列信息...");
		if (tableConvertor == null)
			tableConvertor = new TableConvertor();
		if (columnConvertor == null)
			columnConvertor = new ColumnConvertor();
	}

}
