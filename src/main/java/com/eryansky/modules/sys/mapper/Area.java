/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.mapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.eryansky.core.orm.mybatis.entity.TreeEntity;
import org.hibernate.validator.constraints.Length;

/**
 * 区域Entity
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-12
 */
public class Area extends TreeEntity<Area> {

    /**
     * 简称
     */
    private String shortName;
    private String code;    // 区域编码
    private String type;    // 区域类型（1：国家；2：省份、直辖市；3：地市；4：区县）
    /**
     * 备注
     */
    private String remark;

    public Area() {
        super();
        this.sort = 30;
    }

    public Area(String id) {
        super(id);
    }

    @JsonIgnore
    public Area getParent() {
        return parent;
    }

    public void setParent(Area parent) {
        this.parent = parent;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Length(min = 0, max = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Length(min = 1, max = 1)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getParentId() {
        return parent != null && parent.getId() != null ? parent.getId() : "0";
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}