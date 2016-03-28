/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import java.io.Serializable;

/**
 * 权限（菜单/功能）
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15 
 */
public class Permisson implements Serializable {
    /**
     * 编码
     */
    private String code;
    /**
     * URL地址
     */
    private String markUrl;

    public Permisson(String code, String markUrl) {
        this.code = code;
        this.markUrl = markUrl;
    }

    public String getCode() {
        return code;
    }

    public Permisson setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMarkUrl() {
        return markUrl;
    }

    public Permisson setMarkUrl(String markUrl) {
        this.markUrl = markUrl;
        return this;
    }
}
