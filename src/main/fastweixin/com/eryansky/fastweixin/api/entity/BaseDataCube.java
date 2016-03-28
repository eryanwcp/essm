package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class BaseDataCube extends BaseModel {

    @JSONField(name = "ref_date", format = "yyyy-MM-dd")
    private Date refDate;

    public Date getRefDate() {
        return refDate;
    }

    public BaseDataCube setRefDate(Date refDate) {
        this.refDate = refDate;
        return this;
    }
}
