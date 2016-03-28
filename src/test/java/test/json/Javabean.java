/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.json;

import com.fasterxml.jackson.annotation.JsonFilter;

import java.util.Date;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-04-29 19:35
 */
@JsonFilter(value = "1")
public class Javabean {
    private String name;
    private Integer code;
    private Date birthday;

    public Javabean() {
    }

    public Javabean(String name, Integer code, Date birthday) {
        this.name = name;
        this.code = code;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
