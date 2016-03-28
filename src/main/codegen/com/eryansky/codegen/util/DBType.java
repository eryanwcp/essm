package com.eryansky.codegen.util;

/**
 * 
 * 数据库类型 枚举类型.
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-5-22 下午10:48:23
 * 
 */
public enum DBType {
	/** DB2(0) */
	db2(0, "DB2"),
	/** SQL Server(1) */
	mssql(1, "SQL Server"),
    /** SQL Server(1) */
    MySQL(2, "MySQL");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	DBType(Integer value, String description) {
		this.value = value;
		this.description = description;
	}

	/**
	 * 获取值
	 * @return value
	 */
	public Integer getValue() {
		return value;
	}

	/**
     * 获取描述信息
     * @return description
     */
	public String getDescription() {
		return description;
	}

	public static DBType getDBType(Integer value) {
		if (null == value)
			return null;
		for (DBType _enum : DBType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static DBType getDBType(String description) {
		if (null == description)
			return null;
		for (DBType _enum : DBType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}