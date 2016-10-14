/*
 * Created on 2013-2-4
 * ExcelInfo.java V1.0
 *
 * Copyright Notice =========================================================
 * This file contains proprietary information of Eastcom Technologies Co. Ltd.
 * Copying or reproduction without prior written approval is prohibited.
 * Copyright (c) 2012 =======================================================
 */
package com.eryansky.core.excelTools;

import java.util.List;

/**
 * 导出Excel的信息
 *
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-31 20:36
 * 
 */
@SuppressWarnings("unchecked")
public class ExcelBean {
	/**
	 * excel文件名称
	 */
	private String title;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 数据集
	 */
	private List list;
	/**
	 * 表格头部（如是合并表头，则为父表头）
	 */
	private String[] hearders;
	/**
	 * 合并表头的子表头（非合并表头不需要设置）
	 */
	private String[][] children;
	/**
	 * Map或JavaBean数据集需指定英文字段顺序
	 */
	private String[] fields;
	
	public ExcelBean(){}
	
	public ExcelBean(String title, String creator, List list, String[] hearders) {
		this.title = title;
		this.creator = creator;
		this.list = list;
		this.hearders = hearders;
	}
	
	public ExcelBean(String title, String creator, List list,
					 String[] hearders, String[] fields) {
		this.title = title;
		this.creator = creator;
		this.list = list;
		this.hearders = hearders;
		this.fields = fields;
	}
	
	public ExcelBean(String title, String creator, List list,
					 String[] hearders, String[][] children, String[] fields) {
		this.title = title;
		this.creator = creator;
		this.list = list;
		this.hearders = hearders;
		this.children = children;
		this.fields = fields;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public String[] getHearders() {
		return hearders;
	}
	public void setHearders(String[] hearders) {
		this.hearders = hearders;
	}
	public String[][] getChildren() {
		return children;
	}
	public void setChildren(String[][] children) {
		this.children = children;
	}
	public String[] getFields() {
		return fields;
	}
	public void setFields(String[] fields) {
		this.fields = fields;
	}
}
