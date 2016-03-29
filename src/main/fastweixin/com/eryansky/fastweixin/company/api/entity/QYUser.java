package com.eryansky.fastweixin.company.api.entity;

import com.eryansky.fastweixin.api.entity.BaseModel;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYUser extends BaseModel {

    public final class Gender{
        public static final String MAN = "1";
        public static final String WOMAN = "2";
    }

    @JSONField(name = "userid")
    private String userId;// 用户ID。必须唯一。可用随机数或UUID代替此值
    private String name;// 用户姓名
    private Integer[] department;// 部门
    private String position;// 职务
    private String mobile;// 手机
    private String gender;// 性别
    private String email;// 邮箱
    private String weixinid;// 微信号。不是微信名称
    private String avatar;
    private Integer status;// 关注状态: 1=已关注，2=已冻结，4=未关注
    private Map<String, Object> extattr;

    public QYUser() {
    }

    public QYUser(String userId, String name, Integer[] department, String position, String mobile, String gender, String email, String weixinid, Map<String, Object> extattr) {
        this.userId = userId;
        this.name = name;
        this.department = department;
        this.position = position;
        this.mobile = mobile;
        this.gender = gender;
        this.email = email;
        this.weixinid = weixinid;
        this.extattr = extattr;
    }

    public QYUser(String userId, String name, Integer[] department, String position, String mobile, String gender, String email, String weixinid, String avatar, Integer status, Map<String, Object> extattr) {
        this.userId = userId;
        this.name = name;
        this.position = position;
        this.mobile = mobile;
        this.gender = gender;
        this.email = email;
        this.weixinid = weixinid;
        this.avatar = avatar;
        this.status = status;
        this.extattr = extattr;
    }

    public String getUserId() {
        return userId;
    }

    public QYUser setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public QYUser setName(String name) {
        this.name = name;
        return this;
    }

    public Integer[] getDepartment() {
        return department;
    }

    public QYUser setDepartment(Integer[] department) {
        if(department.length != 0) {
            this.department = department;
        }
        return this;
    }

    public String getPosition() {
        return position;
    }

    public QYUser setPosition(String position) {
        this.position = position;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public QYUser setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public QYUser setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public QYUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getWeixinid() {
        return weixinid;
    }

    public QYUser setWeixinid(String weixinid) {
        this.weixinid = weixinid;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public QYUser setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public QYUser setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Map<String, Object> getExtattr() {
        return extattr;
    }

    public QYUser setExtattr(Map<String, Object> extattr) {
        this.extattr = extattr;
        return this;
    }
}
