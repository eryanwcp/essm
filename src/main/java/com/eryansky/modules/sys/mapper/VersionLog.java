/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.orm.mybatis.entity.BaseEntity;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys._enum.VersionLogType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * 系统更新日志
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-01-09
 */
public class VersionLog extends BaseEntity<VersionLog> {

    /**
     * 版本号
     */
    private String versionName;
    /**
     * 版本内部编号
     */
    private String versionCode;
    /**
     * 附件
     */
    private String fileId;
    /**
     * 更新类型 ${@link VersionLogType}
     */
    private Integer versionLogType;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 更新发布人
     */
    private String updateUser;
    /**
     * 变更说明
     */
    private String remark;

    public VersionLog() {
        super();
    }

    public VersionLog(String id) {
        super(id);
    }

    @Override
    public void prePersist() {
        super.prePersist();
        String user = SecurityUtils.getCurrentUserId();
        this.updateUser = user;
        this.updateTime = Calendar.getInstance().getTime();
    }

    @Override
    public void preUpdate() {
        String user = SecurityUtils.getCurrentUserId();
        this.updateUser = user;
        this.updateTime = Calendar.getInstance().getTime();
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public Integer getVersionLogType() {
        return versionLogType;
    }

    public void setVersionLogType(Integer versionLogType) {
        this.versionLogType = versionLogType;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 系统类型描述.
     */
    public String getVersionLogTypeView() {
        VersionLogType ss = VersionLogType.getVersionLogType(versionLogType);
        String str = "";
        if (ss != null) {
            str = ss.getDescription();
        }
        return str;
    }
    @Override
    public String toString() {
        return JsonMapper.getInstance().toJson(this);
    }
}
