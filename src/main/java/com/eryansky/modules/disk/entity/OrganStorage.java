/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 部门云盘存储空间配置
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-24
 */
@Entity
@Table(name = "T_DISK_ORGAN_STORAGE")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class OrganStorage extends BaseEntity<OrganStorage> {

    /**
     * 部门ID
     */
    private String organId;
    /**
     * 最大限制大小 单位：M {@link com.eryansky.utils.AppConstants}
     */
    private Integer limitSize ;

    public OrganStorage() {
//        limitSize = AppConstants.getDiskOrganLimitSize();
    }

    @Column(length = 36)
    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }
}
