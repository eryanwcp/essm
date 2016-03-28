/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import java.io.Serializable;

/**
 * 权限（角色）
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15 
 */
public class PermissonRole implements Serializable {
    /**
     * 编码
     */
    private String code;

    public PermissonRole(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public PermissonRole setCode(String code) {
        this.code = code;
        return this;
    }
}
