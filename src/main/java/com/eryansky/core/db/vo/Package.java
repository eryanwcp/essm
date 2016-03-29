/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db.vo;

/**
 * 数据包VO 
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class Package {
	
	//XML数据类型
    public static final String TYPE_STRING = "STRING";
    public static final String TYPE_NUMBER = "NUMBER";
    public static final String TYPE_BLOB = "BLOB";
    public static final String TYPE_CLOB = "CLOB";
    public static final String TYPE_DATE = "DATE";
    public static final String TYPE_TIMESTAMP = "TIMESTAMP";

    //XML Attrib
	public static final String ATTRIB_TYPE = "t";
	public static final String ATTRIB_NAME = "n";
	public static final String ATTRIB_ISPK = "ispk"; // 值为: "Y" / "N"
	public static final String ATTRIB_ISNULL = "isnvl"; // 值为: "Y" / "N"
	public static final String ATTRIB_LENGTH = "l"; // 长度
	public static final String ATTRIB_SCALE = "s"; // 精度
	public static final String ATTRIB_ID = "id";	//ID
	public static final String ATTRIB_VALUE = "v";	//值
    
	public static final String IS_ATTAPATH = "isatta"; // 值为: "Y" / "N"
	public static final String IS_REAL_PATH = "isreal"; // 值为: "Y" / "N"
	public static final String ATTA_SUFFIX = "_atta";//虚拟附列名后缀
	
}
