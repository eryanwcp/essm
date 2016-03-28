package com.eryansky.codegen.util;


/**
 * JDBC Types转换Java Types
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-7-15 下午1:37:32 
 * @version 1.0
 */
public enum JavaType {
	STRING("String"), 
	CHAR("String"), 
	VARCHAR("String"),
	LONGVARCHAR("String"),
	TEXT("String"), 
	NUMERIC("java.math.BigDecimal"), 
	DECIMAL("java.math.BigDecimal"), 
	BIT("Boolean"), 
	TINYINT("Integer"), 
	SMALLINT("Short"), 
	INTEGER("Integer"), 
	INT("Integer"),
	BIGINT("Long"), 
	REAL("Float"),
	FLOAT("Double"), 
	DOUBLE("Double"), 
	BINARY("byte[]"),
	VARBINARY("byte[]"), 
	LONGVARBINARY("byte[]"),
	BLOB("byte[]"), 
	CLOB("String"), 
	DATE("String"),
	DATETIME("String"),
	TIME("String"),
	TIMESTAMP("String"),
	LONGTEXT("String"),

    NUMBER("Integer"),
    VARCHAR2("String");

	private String typeName;

	private JavaType(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	public static String getJavaType(String sqlType) {
		JavaType javaType = null;
		if (sqlType == null || "".equals(sqlType))
			return STRING.getTypeName();
		javaType = JavaType.valueOf((sqlType.replaceAll("\\(\\d{1,9}\\)$", "")).toUpperCase());
		if (javaType == null)
			return STRING.getTypeName();
		return javaType.getTypeName();
	}

	public static void main(String[] args) {
		System.out.println("STRING:" + JavaType.getJavaType("STRING"));
		System.out.println("CHAR:" + JavaType.getJavaType("CHAR"));
		System.out.println("DATE:" + JavaType.getJavaType("DATE"));
	}

}
