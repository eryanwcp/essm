package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class Matchrule extends BaseModel {

    @JSONField(name = "group_id")
    private String groupId;

    private String sex;

    private String country;

    private String province;

    private String city;

    @JSONField(name = "client_platform_type")
    private String clientPlatformType;

    public String getGroupId() {
        return groupId;
    }

    public Matchrule setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public Matchrule setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public Matchrule setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getProvince() {
        return province;
    }

    public Matchrule setProvince(String province) {
        this.province = province;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Matchrule setCity(String city) {
        this.city = city;
        return this;
    }

    public String getClientPlatformType() {
        return clientPlatformType;
    }

    public Matchrule setClientPlatformType(String clientPlatformType) {
        this.clientPlatformType = clientPlatformType;
        return this;
    }
}
