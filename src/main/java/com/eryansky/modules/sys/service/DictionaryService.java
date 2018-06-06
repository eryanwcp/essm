/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.Page;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.orm.mybatis.service.CrudService;
import com.eryansky.modules.sys.dao.DictionaryDao;
import com.eryansky.modules.sys.mapper.Dictionary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据字典管理
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-09-27 
 */
@Service
@Transactional(readOnly = true)
public class DictionaryService extends CrudService<DictionaryDao,Dictionary> {

    @Transactional(readOnly = false)
    @Override
    public void save(Dictionary entity) {
        super.save(entity);
    }

    @Transactional(readOnly = false)
    @Override
    public void delete(Dictionary entity) {
        super.delete(entity);
    }

    @Transactional(readOnly = false)
    public void deleteByIds(List<String> ids) {
        if(Collections3.isNotEmpty(ids)){
            for(String id:ids){
                delete(new Dictionary(id));
            }
        }
    }

    @Override
    public Page<Dictionary> findPage(Page<Dictionary> page, Dictionary entity) {
        entity.setEntityPage(page);
        page.setResult(dao.findList(entity));
        return page;
    }

    /**
     * 根据编码查找
     * @param code
     * @return
     */
    public Dictionary getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        code = StringUtils.strip(code);// 去除两边空格
        Dictionary dictionary = new Dictionary();
        dictionary.setCode(code);
        return dao.getByCode(dictionary);
    }


    /**
     * 检查分组下是否有数据
     * @param groupId
     * @param name
     * @return
     */
    public Dictionary getByGroupCode_Name(String groupId,String name) {
        Dictionary group = new Dictionary();
        group.setId(groupId);
        Dictionary dictionary = new Dictionary();
        dictionary.setName(name);
        dictionary.setGroup(group);
        return dao.getBy(dictionary);
    }

    /**
     * 查找第一级所属有数据
     * @return
     */
    public List<Dictionary> findParents(){
        return dao.findParents(new Dictionary());
    }


    /**
     * 根据编码查找子级数据
     * @param groupId
     * @return
     */
    public List<Dictionary> findChilds(String groupId){
        Dictionary dictionary = new Dictionary(groupId);
        return dao.findChilds(dictionary);
    }


    /**
     * 查找排序最大值
     * @return
     */
    public Integer getMaxSort() {
        Integer max = dao.getMaxSort(new Dictionary());
        if (max == null) {
            max = 0;
        }
        return max;
    }
}
