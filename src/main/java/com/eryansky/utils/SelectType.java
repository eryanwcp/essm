/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.TreeNode;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * combobox、combotree功能类型 全部、请选择  枚举类型.
 * <br>全部("all") 请选择("select")
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-01-28 上午10:48:23
 * 
 */
public enum SelectType {
	/** 全部("all") */
	all("all", "全部"),
	/** 请选择("select") */
	select("select", "请选择...");

	/**
	 * 值 String型
	 */
	private final String value;
	/**
	 * 描述 String型
	 */
	private final String description;

	SelectType(String value, String description) {
		this.value = value;
		this.description = description;
	}

	/**
	 * 获取值
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
     * 获取描述信息
     * @return description
     */
	public String getDescription() {
		return description;
	}

	public static SelectType getByValue(String value) {
		if (null == value)
			return null;
		for (SelectType _enum : SelectType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static SelectType getDescription(String description) {
		if (null == description)
			return null;
		for (SelectType _enum : SelectType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

    /**
     * 快速构造一个树形节点
     * @param selectType
     * @return
     */
    public static TreeNode treeNode(String selectType){
        TreeNode selectTreeNode = null;
        //为combobox添加  "---全部---"、"---请选择---"
        if(StringUtils.isNotBlank(selectType)){
            SelectType s = SelectType.getByValue(selectType);
            String title = selectType;
            if(s != null){
                title = s.getDescription();
            }
            selectTreeNode = new TreeNode("", title);
        }
        return selectTreeNode;
    }

    /**
     * 快速构造一个选项节点
     * @param selectType
     * @return
     */
    public static Combobox combobox(String selectType){
        Combobox selectCombobox = null;
        //为combobox添加  "---全部---"、"---请选择---"
        if(StringUtils.isNotBlank(selectType)){
            SelectType s = SelectType.getByValue(selectType);
            String title = selectType;
            if(s != null){
                title = s.getDescription();
            }
            selectCombobox = new Combobox("", title);
        }
        return selectCombobox;
    }

}