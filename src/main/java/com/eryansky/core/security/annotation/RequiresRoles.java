/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package com.eryansky.core.security.annotation;


import com.eryansky.core.security._enum.Logical;

import java.lang.annotation.*;

import static com.eryansky.core.security._enum.Logical.*;

/**
 * 需要的角色
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-06-11 20:06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRoles {

    String[] value() default {};

    public Logical logical() default AND;
}