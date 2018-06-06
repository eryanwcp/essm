/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.common.orm.persistence.CrudDao;
import com.eryansky.modules.sys.mapper.DictionaryItem;

import java.util.List;


/**
 *日志DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2015-9-26
 */
@MyBatisDao
public interface DictionaryItemDao extends CrudDao<DictionaryItem> {

    /**
     * 查找第一级所有数据
     * @return
     */
    DictionaryItem getByCode(DictionaryItem dictionaryItem);

    /**
     * 查找数据
     * @param dictionaryItem 字典ID #{dictionary.id} #{dictionary.code}
     * @return
     */
    List<DictionaryItem> findByDictionary(DictionaryItem dictionaryItem);

    /**
     * 查找顶级数据
     * @param dictionaryItem
     * @return
     */
    List<DictionaryItem> findParentsByDictionary(DictionaryItem dictionaryItem);
    /**
     * 查找排序最大值
     * @return
     */
    Integer getMaxSort(DictionaryItem dictionaryItem);

    /**
     * 根据查询条件查询
     * @param dictionaryItem
     * @return
     */
    DictionaryItem getByDictionaryItem(DictionaryItem dictionaryItem);

    /**
     * 根据编码查找子级数据
     * @param dictionaryItem
     * @return
     */
    List<DictionaryItem> findChilds(DictionaryItem dictionaryItem);
}
