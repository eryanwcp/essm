package com.eryansky.fastweixin.company.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.eryansky.fastweixin.api.entity.BaseModel;

/**
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYDepartment extends BaseModel{

    @JSONField(name = "id")
    private Integer id;
    @JSONField(name = "name")
    private String name;
    @JSONField(name = "parentid")
    private Integer parentId;
    @JSONField(name = "order")
    private Integer order;

    public QYDepartment() {
    }

    public QYDepartment(String name, Integer parentId, Integer order) {
        this.name = name;
        this.parentId = (parentId == null || parentId < 1) ? 1 : parentId;
        this.order = order;
    }

    public QYDepartment(Integer id, String name, Integer parentId, Integer order) {
        this.id = id;
        this.name = name;
        this.parentId = (parentId == null || parentId < 1) ? 1 : parentId;
        this.order = order;
    }

    public Integer getId() {
        return id;
    }

    public QYDepartment setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public QYDepartment setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getParentId() {
        return parentId;
    }

    public QYDepartment setParentId(Integer parentId) {
        this.parentId = parentId;
        return this;
    }

    public Integer getOrder() {
        return order;
    }

    public QYDepartment setOrder(Integer order) {
        this.order = order;
        return this;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", order=" + order +
                '}';
    }
}
