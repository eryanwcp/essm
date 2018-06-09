/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.core.orm.mybatis.entity.DataEntity;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.modules.sys._enum.VersionLogType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.eryansky.modules.sys._enum.YesOrNo;

import java.util.Calendar;
import java.util.Date;

/**
 * 系统更新日志
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-01-09
 */
public class VersionLog extends DataEntity<VersionLog> {

    public static final String FOLDER_VERSIONLOG = "VersionLog";

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
     * APP标识
     */
    private String app;
    /**
     * 更新类型 ${@link VersionLogType}
     */
    private String versionLogType;
    /**
     * 是否发布
     */
    private String isPub;
    /**
     * 是否提示
     */
    private String isTip;
    /**
     * 发布时间
     */
    private Date pubTime;
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
        this.pubTime = Calendar.getInstance().getTime();
    }

    @Override
    public void preUpdate() {
        this.pubTime = Calendar.getInstance().getTime();
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

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getVersionLogType() {
        return versionLogType;
    }

    public void setVersionLogType(String versionLogType) {
        this.versionLogType = versionLogType;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    public Date getPubTime() {
        return pubTime;
    }

    public void setPubTime(Date updateTime) {
        this.pubTime = updateTime;
    }


    public String getIsPub() {
        return isPub;
    }

    public String getIsPubView() {
        YesOrNo s = YesOrNo.getByValue(isPub);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
    }

    public void setIsPub(String isPub) {
        this.isPub = isPub;
    }

    public String getIsTip() {
        return isTip;
    }

    public void setIsTip(String isTip) {
        this.isTip = isTip;
    }

    public String getIsTipView() {
        YesOrNo s = YesOrNo.getByValue(isTip);
        String str = "";
        if (s != null) {
            str = s.getDescription();
        }
        return str;
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
        VersionLogType ss = VersionLogType.getByValue(versionLogType);
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
