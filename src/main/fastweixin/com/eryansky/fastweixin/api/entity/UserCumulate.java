package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 累计用户数据
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class UserCumulate extends BaseDataCube {

    @JSONField(name = "cumulate_user")
    private Integer cumulateUser;

    public Integer getCumulateUser() {
        return cumulateUser;
    }

    public UserCumulate setCumulateUser(Integer cumulateUser) {
        this.cumulateUser = cumulateUser;
        return this;
    }
}
