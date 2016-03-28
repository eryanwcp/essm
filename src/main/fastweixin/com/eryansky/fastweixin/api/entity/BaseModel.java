package com.eryansky.fastweixin.api.entity;

import com.eryansky.fastweixin.util.JSONUtil;

/**
 * 抽象实体类
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public abstract class BaseModel implements Model {
    @Override
    public String toJsonString() {
        return JSONUtil.toJson(this);
    }
}
