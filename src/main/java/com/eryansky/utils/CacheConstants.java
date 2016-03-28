/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.utils;

/**
 * 缓存静态变量.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-19 下午6:45:50 
 *
 */
public class CacheConstants {
	
	//Spring Ehcache Annoction
	/**
	 * 用户导航菜单(根据用户权限缓存).
	 */
	public static final String RESOURCE_USER_MENU_TREE_CACHE = "resource_user_menu_Tree_cache";
    /**
     * 用户资源树(根据用户权限缓存).
     */
    public static final String RESOURCE_USER_RESOURCE_TREE_CACHE = "resource_user_resource_Tree_cache";

    /**
     * 某个url对应的是否授权给某个用户.
     */
    public static final String RESOURCE_USER_AUTHORITY_URLS_CACHE = "resource_user_authority_urls_cache";
	
	/**
	 * 角色(无).
	 */
	public static final String ROLE_ALL_CACHE = "role_all_cache";

    /**
     * 数据字典项缓存数据(根据数据字典编码缓存).
     */
    public static final String DICTIONARYITEM_BY_DICTIONARYCODE_CACHE = "dictionaryItem_byDictionaryCode_cache";
	/**
	 * 数据字典项combotree的数据(根据数据字典编码缓存).
	 */
	public static final String DICTIONARYITEM_CONBOTREE_CACHE = "dictionaryItem_combotree_cache";
    /**
     * 数据字典项combobox的数据(根据数据字典编码缓存).
     */
    public static final String DICTIONARYITEM_CONBOBOX_CACHE = "dictionaryItem_combobox_cache";


    public static final String ORGAN_USER_TREE_CACHE = "organ_user_tree_cache";

	
}
