/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.tag;


import com.eryansky.core.security.SecurityUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * 判断是否不是管理员
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-25
 */
@SuppressWarnings("serial")
public class LacksAdminTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        if (!SecurityUtils.isCurrentUserAdmin()) {
            return TagSupport.EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
    }

}