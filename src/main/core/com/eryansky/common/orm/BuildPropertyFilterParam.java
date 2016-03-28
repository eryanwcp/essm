/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.common.orm;

import java.util.Map;

/**
 * 
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2012-4-17 下午8:02:17
 */
public class BuildPropertyFilterParam {
    private String hql;
    private Map<String, Object> param;


    public BuildPropertyFilterParam(String hql, Map<String, Object> param) {
        this.hql = hql;
        this.param = param;
    }

    public String getHql() {
        return hql;
    }

    public void setHql(String hql) {
        this.hql = hql;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
