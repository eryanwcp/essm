package com.eryansky.codegen.vo;

public class Column {

	public static final int DEFAULT_LENGTH = 255;
	public static final int DEFAULT_PRECISION = 19;
	public static final int DEFAULT_SCALE = 2;

	/**
	 * ******* JDBC *************
	 */
	private String columnName = null;// 字段名
	private String jdbcType = null;// 字段类型
	private int length;// 长度
	private boolean primaryKey;// 是否主键
	private boolean nullable = true;// 是否允许空
	private boolean unique = false;// 是否允许重复
	private String defaultValue = null;// 默认值
	private int digits;// 精度
	private String comment = null;// 备注
	private Boolean autoIncrement = false;// 是否自增长

	/**
	 * ******* Java *************
	 */
	private String javaType = null;// java type
	private String fieldName = null;// entity field名称
	private String setMethod = null;// set 方法名
	private String getMethod = null;// get 方法名

	/**
	 * ******* View *************
	 */
	private String displayLabel = "";
	private String validate = null;
	private int min;
	private int max;
	private boolean used_query = false;
	private boolean used_list = true;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public void setJdbcType(String jdbcType) {
		this.jdbcType = jdbcType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getDigits() {
		return digits;
	}

	public void setDigits(int digits) {
		this.digits = digits;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSetMethod() {
		return setMethod;
	}

	public void setSetMethod(String setMethod) {
		this.setMethod = setMethod;
	}

	public String getGetMethod() {
		return getMethod;
	}

	public void setGetMethod(String getMethod) {
		this.getMethod = getMethod;
	}

	public String getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public boolean isUsed_query() {
		return used_query;
	}

	public void setUsed_query(boolean usedQuery) {
		used_query = usedQuery;
	}

	public boolean isUsed_list() {
		return used_list;
	}

	public void setUsed_list(boolean usedList) {
		used_list = usedList;
	}

	public Boolean getAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(Boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	@Override
	public String toString() {
		return "Column [columnName=" + columnName + ", comment=" + comment
				+ ", defaultValue=" + defaultValue + ", digits=" + digits
				+ ", displayLabel=" + displayLabel + ", fieldName=" + fieldName
				+ ", getMethod=" + getMethod + ", javaType=" + javaType
				+ ", jdbcType=" + jdbcType + ", length=" + length + ", max="
				+ max + ", min=" + min + ", nullable=" + nullable
				+ ", setMethod=" + setMethod + ", unique=" + unique
				+ ", used_list=" + used_list + ", used_query=" + used_query
				+ ", validate=" + validate + "]";
	}

}
