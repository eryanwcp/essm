package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class UserShare extends BaseDataCube {

    @JSONField(name = "share_scene")
    private Integer shareScene;
    @JSONField(name = "share_count")
    private Integer shareCount;
    @JSONField(name = "share_user")
    private Integer shareUser;

    public Integer getShareScene() {
        return shareScene;
    }

    public UserShare setShareScene(Integer shareScene) {
        this.shareScene = shareScene;
        return this;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public UserShare setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
        return this;
    }

    public Integer getShareUser() {
        return shareUser;
    }

    public UserShare setShareUser(Integer shareUser) {
        this.shareUser = shareUser;
        return this;
    }
}
