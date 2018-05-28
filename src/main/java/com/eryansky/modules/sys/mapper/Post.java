/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;


import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 岗位表 entity
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2018-05-08
 */
@JsonFilter(" ")
public class Post extends DataEntity<Post> {

    /**
     * 岗位名称
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 备注
     */
    private String remark;
    /**
     * 机构ID
     */
    private String organId;

    /**
     * 附属机构
     */
    private Set<String> organIds;

    private String query;


    public Post() {
        this.organIds = Sets.newHashSet();
    }

    public Post(String id) {
        super(id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSort() {
        return this.sort;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOrganId() {
        return this.organId;
    }

    public Set<String> getOrganIds() {
        return organIds;
    }

    public void setOrganIds(Set<String> organIds) {
        this.organIds = organIds;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
