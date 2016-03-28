/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.core.orm.hibernate.entity.DataEntity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 服务器系统信息
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date: 2013-11-27 下午9:18
 */
//@Entity
//@Table(name = "T_SYS_SERVERINFO")
// jackson标记不生成json对象的属性
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class ServerInfo extends DataEntity<ServerInfo> implements Serializable {
    /**
     * cpu使用率
     */
    private String cpuUsage;
    /**
     * 预设cpu使用率
     */
    private String setCpuUsage;
    /**
     * 当前使用Jvm使用率
     */
    private String jvmUsage;
    /**
     * 预设Jvm使用率
     */
    private String setJvmUsage;
    /**
     * 当前使用Ram使用率
     */
    private String ramUsage;
    /**
     * 预设Ram使用率
     */
    private String setRamUsage;
    /**
     * 预设Ram使用率
     */
    private Date operTime;
    /**
     * 发送的邮件
     */
    private String email;
    /**
     * 时间字符串
     */
    private String stringTime;
    /**
     * 备注
     */
    private String mark;

    public ServerInfo() {
    }

    public String getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(String cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStringTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (operTime != null) {
            stringTime = sdf.format(operTime);
        }
        return stringTime;
    }

    public String getSetJvmUsage() {
        return setJvmUsage;
    }

    public void setSetJvmUsage(String setJvmUsage) {
        this.setJvmUsage = setJvmUsage;
    }

    public String getSetRamUsage() {
        return setRamUsage;
    }

    public void setSetRamUsage(String setRamUsage) {
        this.setRamUsage = setRamUsage;
    }

    public String getSetCpuUsage() {
        return setCpuUsage;
    }

    public void setSetCpuUsage(String setCpuUsage) {
        this.setCpuUsage = setCpuUsage;
    }

    public String getJvmUsage() {
        return jvmUsage;
    }

    public void setJvmUsage(String jvmUsage) {
        this.jvmUsage = jvmUsage;
    }

    public String getRamUsage() {
        return ramUsage;
    }

    public void setRamUsage(String ramUsage) {
        this.ramUsage = ramUsage;
    }

    public void setStringTime(String stringTime) {
        this.stringTime = stringTime;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
