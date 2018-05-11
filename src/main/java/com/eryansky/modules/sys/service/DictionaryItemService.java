/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.DictionaryItemDao;
import com.eryansky.modules.sys.mapper.Dictionary;
import com.eryansky.modules.sys.mapper.DictionaryItem;
import com.eryansky.utils.CacheConstants;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 数据字典项管理
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-27 
 */
@Service
@Transactional(readOnly = true)
public class DictionaryItemService extends CrudService<DictionaryItemDao,DictionaryItem> {

    @Autowired
    private DictionaryService dictionaryService;

    @CacheEvict(value = {CacheConstants.DICTIONARYITEM_BY_DICTIONARYCODE_CACHE,
            CacheConstants.DICTIONARYITEM_CONBOTREE_CACHE,
            CacheConstants.DICTIONARYITEM_CONBOBOX_CACHE}, allEntries = true)
    @Transactional(readOnly = false)
    @Override
    public void save(DictionaryItem entity) {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARYITEM_BY_DICTIONARYCODE_CACHE
                + "," + CacheConstants.DICTIONARYITEM_CONBOTREE_CACHE
                + "," + CacheConstants.DICTIONARYITEM_CONBOBOX_CACHE);
        super.save(entity);
    }

    @CacheEvict(value = {CacheConstants.DICTIONARYITEM_BY_DICTIONARYCODE_CACHE,
            CacheConstants.DICTIONARYITEM_CONBOTREE_CACHE,
            CacheConstants.DICTIONARYITEM_CONBOBOX_CACHE}, allEntries = true)
    @Transactional(readOnly = false)
    @Override
    public void delete(DictionaryItem entity) {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARYITEM_BY_DICTIONARYCODE_CACHE
                + "," + CacheConstants.DICTIONARYITEM_CONBOTREE_CACHE
                + "," + CacheConstants.DICTIONARYITEM_CONBOBOX_CACHE);
        super.delete(entity);
    }


    @CacheEvict(value = {CacheConstants.DICTIONARYITEM_BY_DICTIONARYCODE_CACHE,
            CacheConstants.DICTIONARYITEM_CONBOTREE_CACHE,
            CacheConstants.DICTIONARYITEM_CONBOBOX_CACHE}, allEntries = true)
    @Transactional(readOnly = false)
    public void deleteByIds(List<String> ids) {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARYITEM_BY_DICTIONARYCODE_CACHE
                + "," + CacheConstants.DICTIONARYITEM_CONBOTREE_CACHE
                + "," + CacheConstants.DICTIONARYITEM_CONBOBOX_CACHE);
        if (Collections3.isNotEmpty(ids)) {
            for (String id : ids) {
                dao.delete(new DictionaryItem(id));
            }
        }
    }

    /**
     * 根据编码查找
     * @param dictionaryId
     * @param code
     * @return
     */
    public DictionaryItem getByCode(String dictionaryId,String code) {
        Dictionary dictionary = new Dictionary(dictionaryId);
        DictionaryItem dictionaryItem = new DictionaryItem();
        dictionaryItem.setCode(code);
        dictionaryItem.setDictionary(dictionary);
        return dao.getByCode(dictionaryItem);
    }


    /**
     * 根据数据字典编码dictionaryCode得到List<TreeNode>对象. <br>
     * 当excludeDictionaryItemId不为空的时候根据id排除自身节点.
     *
     * @param excludeDictionaryItemId
     *            需要排除数据字典项ID 下级也会被排除
     * @param isCascade
     *            是否级联加载
     * @return List<TreeNode> 映射关系： TreeNode.text-->Dicitonary.name;TreeNode.id-->Dicitonary.id;
     */
    @SuppressWarnings("unchecked")
    public List<TreeNode> getByDictionaryId(String dictionaryId, String excludeDictionaryItemId, boolean isCascade){
        List<DictionaryItem> list = null;
        List<TreeNode> treeNodes = Lists.newArrayList();
        if (StringUtils.isBlank(dictionaryId)) {
            return treeNodes;
        }
        DictionaryItem dictionaryItem = new DictionaryItem();
        dictionaryItem.setDictionary(new Dictionary(dictionaryId));
        list = dao.findParentsByDictionary(dictionaryItem);
        for (DictionaryItem d : list) {
            TreeNode t = getTreeNode(d, excludeDictionaryItemId, isCascade);
            if (t != null) {
                treeNodes.add(t);
            }

        }
        return treeNodes;
    }

    /**
     * 根据数据字典类型编码dictionaryTypeCode得到List<TreeNode>对象. <br>
     * 当id不为空的时候根据id排除自身节点.
     *
     * @param entity
     *            数据字典对象
     * @param excludeDictionaryItemId
     *            数据字ID
     * @param isCascade
     *            是否级联加载
     * @return
     */
    public TreeNode getTreeNode(DictionaryItem entity, String excludeDictionaryItemId, boolean isCascade){
        TreeNode node = new TreeNode(entity.getId(), entity.getName());
        List<DictionaryItem> subDictionaries = dao.findChilds(entity);
        if (isCascade) {// 递归查询子节点
            List<TreeNode> children = Lists.newArrayList();
            for (DictionaryItem d : subDictionaries) {
                boolean isInclude = true;// 是否包含到节点树
                TreeNode treeNode = null;
                // 排除自身
                if (excludeDictionaryItemId != null) {
                    if (!d.getId().equals(excludeDictionaryItemId)) {
                        treeNode = getTreeNode(d, excludeDictionaryItemId, true);
                    } else {
                        isInclude = false;
                    }
                } else {
                    treeNode = getTreeNode(d, excludeDictionaryItemId, true);
                }
                if (isInclude) {
                    children.add(treeNode);
                    node.setState(TreeNode.STATE_CLOASED);
                } else {
                    node.setState(TreeNode.STATE_OPEN);
                }
            }

            node.setChildren(children);
        }
        return node;
    }




    /**
     * @param dictionaryCode 字典编码
     * @param code 字典项编码
     * @return
     */
    public DictionaryItem getDictionaryItemByDC(
            String dictionaryCode,String code){
        DictionaryItem dictionaryItem = new DictionaryItem();
        dictionaryItem.setCode(code);
        Dictionary dictionary = new Dictionary();
        dictionary.setCode(dictionaryCode);
        dictionaryItem.setDictionary(dictionary);
        return dao.getByDictionaryItem(dictionaryItem);
    }

    /**
     * @param dictionaryCode 字典编码
     * @param value 字典项值
     * @return
     */
    public DictionaryItem getDictionaryItemByDV(
            String dictionaryCode,String value){
        DictionaryItem dictionaryItem = new DictionaryItem();
        dictionaryItem.setValue(value);
        Dictionary dictionary = new Dictionary();
        dictionary.setCode(dictionaryCode);
        dictionaryItem.setDictionary(dictionary);
        return dao.getByDictionaryItem(dictionaryItem);
    }


    /**
     * 查找排序最大值
     *
     * @return
     */
    public Integer getMaxSort() {
        Integer max = dao.getMaxSort(new DictionaryItem());
        if (max == null) {
            max = 0;
        }
        return max;
    }

    /*外部接口*/


    /**
     * 根据数据字典类型编码得到数据字典列表.
     *
     * @param dictionaryCode 字典分类编码
     * @return
     */
    @Cacheable(value = {CacheConstants.DICTIONARYITEM_BY_DICTIONARYCODE_CACHE})
    @SuppressWarnings("unchecked")
    public List<DictionaryItem> findDictionaryItemsByDictionaryCode(
            String dictionaryCode) {
        Assert.notNull(dictionaryCode, "参数[dictionaryCode]为空!");
        DictionaryItem dictionaryItem = new DictionaryItem();
        Dictionary dictionary = new Dictionary();
        dictionary.setCode(dictionaryCode);
        dictionaryItem.setDictionary(dictionary);
        List<DictionaryItem> list = dao.findByDictionary(dictionaryItem);
        return list;
    }

    /**
     * 获取下拉列表
     * @param dictionaryCode 数据字典编码
     * @return
     */
    @Cacheable(value = { CacheConstants.DICTIONARYITEM_CONBOBOX_CACHE})
    public List<Combobox> getByDictionaryCode(String dictionaryCode){
        DictionaryItem dictionaryItem = new DictionaryItem();
        Dictionary dictionary = new Dictionary();
        dictionary.setCode(dictionaryCode);
        dictionaryItem.setDictionary(dictionary);
        List<DictionaryItem> list = findDictionaryItemsByDictionaryCode(dictionaryCode);
        List<Combobox> cList = Lists.newArrayList();
        for (DictionaryItem d : list) {
            Combobox c = new Combobox(d.getValue(), d.getName());
            cList.add(c);
        }
        logger.debug("缓存:{}", CacheConstants.DICTIONARYITEM_CONBOBOX_CACHE +" 参数：dictionaryTypeCode="+ dictionaryCode);
        return cList;

    }

    /**
     * 获取树形节点
     * @param dictionaryCode 数据字典编码
     * @param isCascade 是否级联
     * @return
     */
    @Cacheable(value = { CacheConstants.DICTIONARYITEM_CONBOTREE_CACHE} )
    @SuppressWarnings("unchecked")
    public List<TreeNode> getByDictionaryCode(String dictionaryCode, boolean isCascade){
        logger.debug("缓存:{}", CacheConstants.DICTIONARYITEM_CONBOTREE_CACHE);
        List<DictionaryItem> list = null;
        List<TreeNode> treeNodes = Lists.newArrayList();
        if (StringUtils.isBlank(dictionaryCode)) {
            return treeNodes;
        }
        Dictionary dictionary = dictionaryService.getByCode(dictionaryCode);
        DictionaryItem dictionaryItem = new DictionaryItem();
        dictionaryItem.setDictionary(dictionary);
        list = dao.findParentsByDictionary(dictionaryItem);
        for (DictionaryItem d : list) {
            TreeNode t = getTreeNode(d, isCascade);
            if (t != null) {
                treeNodes.add(t);
            }

        }
        return treeNodes;
    }

    /**
     * 构造TreeNode
     * @param entity
     * @param isCascade 是否级联
     * @return
     */
    public TreeNode getTreeNode(DictionaryItem entity,boolean isCascade){
        TreeNode node = new TreeNode(entity.getCode(), entity.getName());
        List<DictionaryItem> subDictionaries = dao.findChilds(entity);
        if (isCascade) {// 递归查询子节点
            List<TreeNode> children = Lists.newArrayList();
            for (DictionaryItem d : subDictionaries) {
                boolean isInclude = true;// 是否包含到节点树
                TreeNode treeNode = null;
                // 排除自身
                treeNode = getTreeNode(d, true);
                if (isInclude) {
                    children.add(treeNode);
                    node.setState(TreeNode.STATE_CLOASED);
                } else {
                    node.setState(TreeNode.STATE_OPEN);
                }
            }

            node.setChildren(children);
        }
        return node;
    }


}
