/**
 * Copyright (c) 2012-2018 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.mapper;

import com.eryansky.core.orm.mybatis.entity.DataEntity;

/**
 * 序列号生成器
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-07-14 
 */
public class SystemSerialNumber extends DataEntity<SystemSerialNumber> {
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 模块编码
     */
    private String moduleCode;
    /**
     * 流水号配置模板
     */
    private String configTemplate;
    /**
     * 系列号最大值
     */
    private String maxSerial;
    /**
     * 是否年度重置(1是，0否)
     */
    private String yearClear;
    /**
     * 是否自动增长标识
     */
    private String isAutoIncrement;
    /**
     * 预生成流水号数量
     */
    private String preMaxNum;
    /**
     * 备注
     */
    private String remark;

    public SystemSerialNumber() {
        this.isAutoIncrement = YES;
        this.maxSerial = "0";
        this.preMaxNum = "1";
    }

    public SystemSerialNumber(String id) {
        super(id);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getConfigTemplate() {
        return configTemplate;
    }

    public void setConfigTemplate(String configTemplate) {
        this.configTemplate = configTemplate;
    }

    public String getMaxSerial() {
        return maxSerial;
    }

    public void setMaxSerial(String maxSerial) {
        this.maxSerial = maxSerial;
    }

    public String getIsAutoIncrement() {
        return isAutoIncrement;
    }

    public void setIsAutoIncrement(String isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }

    public String getYearClear(){return yearClear;}

    public void  setYearClear(String yearClear){this.yearClear=yearClear;}

    public String getPreMaxNum() {
        return preMaxNum;
    }

    public void setPreMaxNum(String preMaxNum) {
        this.preMaxNum = preMaxNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
