/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.vo;

import com.eryansky.core.security.SessionInfo;

/**
 * Author: 尔演&Eryan eryanwcp@gmail.com
 * Date: 2014-04-02 18:17
 */
public class UserInfoVo extends SessionInfo {

    public UserInfoVo() {
    }

    /**
     * 员工状态
     * @see UserStatus
     */
    private UserStatus userStatus;
    /**
     * 用户头像
     */
    private String userImg;

    /**
     * 用户类型
     */
    private String userType;

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * 用户状态头像
     * @return
     */
    public String getUserStatusIcon() {
        if(userStatus != null){
            return userStatus.getValue();
        }
        return null;
    }

    /**
     * 用户状态描述
     * @return
     */
    public String getUserStatusView() {
        if(userStatus != null){
            return userStatus.getDescription();
        }
        return null;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public enum UserStatus {
        /**
         * 工作中("")
         */
        Working("eu-icon-work_jf", "工作中"),
        /**
         * 出差中("")
         */
        BusinessTrip("eu-icon-trip", "出差中"),
        /**
         * 假期中("")
         */
        Leave("eu-icon-leave", "假期中"),
        /**
         * 会议("")
         */
        Meeting("eu-icon-meeting", "会议中");

        /**
         * 值 String
         */
        private final String value;
        /**
         * 描述 String型
         */
        private final String description;

        UserStatus(String value, String description) {
            this.value = value;
            this.description = description;
        }

        /**
         * 获取值
         *
         * @return value
         */
        public String getValue() {
            return value;
        }

        /**
         * 获取描述信息
         *
         * @return description
         */
        public String getDescription() {
            return description;
        }

        public static UserStatus getUserStatusByValue(String value) {
            if (null == value)
                return null;
            for (UserStatus _enum : UserStatus.values()) {
                if (value.equals(_enum.getValue()))
                    return _enum;
            }
            return null;
        }

        public static UserStatus getUserStatus(String description) {
            if (null == description)
                return null;
            for (UserStatus _enum : UserStatus.values()) {
                if (description.equals(_enum.getDescription()))
                    return _enum;
            }
            return null;
        }

    }

}
