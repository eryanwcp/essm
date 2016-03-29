package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class UserShareHour extends BaseDataCube {

    @JSONField(name = "ref_hour")
    private Integer refHour;
    @JSONField(name = "share_scene")
    private Integer shareScene;
    @JSONField(name = "share_count")
    private Integer shareCount;
    @JSONField(name = "share_user")
    private Integer shareUser;

    public Integer getRefHour() {
        return refHour;
    }

    public UserShareHour setRefHour(Integer refHour) {
        this.refHour = refHour;
        return this;
    }

    public Integer getShareScene() {
        return shareScene;
    }

    public UserShareHour setShareScene(Integer shareScene) {
        this.shareScene = shareScene;
        return this;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public UserShareHour setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
        return this;
    }

    public Integer getShareUser() {
        return shareUser;
    }

    public UserShareHour setShareUser(Integer shareUser) {
        this.shareUser = shareUser;
        return this;
    }
}
