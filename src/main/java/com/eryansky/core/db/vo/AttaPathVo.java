/**
 *  Copyright (c) 2013 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.vo;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * 附件路径
 * @author 尔演&Eryan eryanwcp@gmail.com
 */
public class AttaPathVo {
    /**
     * 附件映射字段
     */
    private List<String> lstAttaFieldName = Lists.newArrayList();

    /**
     * 绝对路径映射字段
     */
    private List<String> lstAbsolutePath = Lists.newArrayList();

    /**
     * 映射列名和相对前置路径
     */
    private Map<String, String> prefixPaths;

    public List<String> getLstAbsolutePath() {
        return lstAbsolutePath;
    }

    public void setLstAbsolutePath(List<String> lstAbsolutePath) {
        this.lstAbsolutePath = lstAbsolutePath;
    }

    public List<String> getLstAttaFieldName() {
        return lstAttaFieldName;
    }

    public void setLstAttaFieldName(List<String> lstAttaFieldName) {
        this.lstAttaFieldName = lstAttaFieldName;
    }

    public Map<String, String> getPrefixPaths() {
        return prefixPaths;
    }

    public void setPrefixPaths(Map<String, String> prefixPaths) {
        this.prefixPaths = prefixPaths;
    }

}
