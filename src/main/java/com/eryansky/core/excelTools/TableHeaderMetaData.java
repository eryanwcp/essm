/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.excelTools;

import java.util.LinkedList;
import java.util.List;

/**
 * 表头对象，用于创建excel表头，支持多层表头
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-31 20:36
 */
public class TableHeaderMetaData {
	/**
	 * 表头对象集合
	 */
	private LinkedList<TableColumn> columns;

	/**
	 * 复合表头叶子层表头对象集合
	 */
	private LinkedList<TableColumn> leafs;

	private String common;

	/**
	 * 复合表头最大深度
	 */
	public int maxlevel = 0;

	public TableHeaderMetaData() {
		columns = new LinkedList<TableColumn>();
		leafs = new LinkedList<TableColumn>();
	}

	/**
	 * 添加列表头
	 * @param col 列表头对象
	 */
	public void addColumn(TableColumn col) {
		setLevel(col, 1);
		columns.add(col);
		addLeafColumn(col);
	}

	/**
	 * 刷新表头
	 */
	public void refresh() {
		maxlevel = 1;
		for (TableColumn col : columns) {
			if (col.isVisible()) {
				col.level = 1;
				int level = refreshChildren(col);
				if (level > maxlevel)
					maxlevel = level;
			}
		}
	}

	/**
	 * 刷新子表头并返回子表头深度
	 * @param parent 父表头对象
	 * @return 深度
	 */
	private int refreshChildren(TableColumn parent) {
		if (parent.children.size() != 0) {
			int max = parent.level;
			for (TableColumn col : parent.children) {
				if (col.isVisible()) {
					col.parent = parent;
					col.level = parent.level + 1;
					int level = refreshChildren(col);
					if (level > max)
						max = level;
				}
			}
			return max;
		} else {
			return parent.level;
		}
	}

	/**
	 * 设置表头对象深度
	 * @param col 表头对象
	 * @param level 深度
	 */
	private void setLevel(TableColumn col, int level) {
		col.level = level;
		if (col.isVisible() && level > maxlevel)
			maxlevel = level;
	}

	/**
	 * 添加叶子列表头
	 * @param col 列表头对象
	 */
	private void addLeafColumn(TableColumn col) {
		if (col.parent != null)
			setLevel(col, col.parent.level + 1);
		if (col.isComplex()) {
			for (TableColumn c : col.getChildren()) {
				addLeafColumn(c);
			}
		} else {
			leafs.add(col);
		}
	}

	/**
	 * 获取列表头集合
	 * @return
	 */
	public List<TableColumn> getColumns() {
		return leafs;
	}

	/**
	 * 获取原始列表头集合（不包含隐藏列）
	 * @return
	 */
	public List<TableColumn> getOriginColumns() {
		LinkedList<TableColumn> ret = new LinkedList<TableColumn>();
		for (TableColumn c : columns) {
			if (c.isVisible())
				ret.add(c);
		}
		return ret;
	}

	/**
	 * 根据序号获取表头对象
	 * @param index 序号
	 * @return
	 */
	public TableColumn getColumnAt(int index) {
		return leafs.get(index);
	}

	/**
	 * 获取列数
	 * @return
	 */
	public int getColumnCount() {
		return leafs.size();
	}

	public String getCommon() {
		return common;
	}

	public void setCommon(String common) {
		this.common = common;
	}
}