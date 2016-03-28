/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.tag;


import com.eryansky.core.security.SecurityUtils;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * 权限操作标签
 * User: 尔演&Eryan eryanwcp@gmail.com
 * Date: 13-11-16 下午9:43
 */
@SuppressWarnings("serial")
public class OperatiuonTag extends TagSupport {

    public static String LINKBUTTON = "linkbutton";
    public static String IMAGEBUTTON = "imagebutton";
    public static String MENUITEM = "menuItem";
    /**
     * 按钮显示的操作名称
     */
    private String name = "";
    /**
     * 按钮对应的js函数名称
     */
    private String method = "";
    /**
     * 按钮对应的资源编码访问权限字符串
     */
    private String permission = "";
    /**
     * 要生成按钮的类型，默认为easyui的按钮类型 "linkbutton" 、"imagebutton"、 "menuItem"
     */
    private String type = LINKBUTTON;
    /**
     * easyui按钮对应的图标class
     */
    private String iconCls = "";
    /**
     * 简洁模式
     */
    private boolean plain = false;
    /**
     * 生成图片按钮时的图片链接
     */
    private String imgSrc = "";


    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
        if (!"".equals(this.permission)) {
            if (SecurityUtils.isPermitted(this.permission)) {
                try {
                    if (LINKBUTTON.equals(type)) {
                        pageContext.getOut().print(createEasyuiButton());
                    } else if (IMAGEBUTTON.equals(type)) {
                        pageContext.getOut().print(createImageButton());
                    }else if (MENUITEM.equals(type)) {
                        pageContext.getOut().print(createMenuItem());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return SKIP_BODY;
    }


    /**
     * 生成EasyUI的linkbutton
     * @return
     */
    private String createEasyuiButton() {
        StringBuffer buffer = new StringBuffer("<a class='easyui-linkbutton' ");
        if (!"".equals(this.method)) {
            buffer.append(" href='javascript:" + this.method + "' ");
        }
        if (!"".equals(this.iconCls)) {
            buffer.append(" iconCls='" + this.iconCls + "' ");
        }
        buffer.append(" plain='" + this.plain + "' ");
        buffer.append(" >");
        if (!"".equals(this.name)) {
            buffer.append(this.name);
        }
        buffer.append("</a>");
        return buffer.toString();
    }

    /**
     * 生成图片按钮
     * @return
     */
    private String createImageButton() {
        if (!"".equals(this.imgSrc)) {
            StringBuffer buffer = new StringBuffer("<img style=\"cursor: pointer;\"  ");
            if (!"".equals(this.method)) {
                buffer.append(" onclick=\"" + this.method + "\" ");
            }
            buffer.append(" src=\"" + this.imgSrc + "\" ");
            if (!"".equals(this.name)) {
                buffer.append(" title=\"" + this.name + "\" ");
            }
            buffer.append(" /> ");
            return buffer.toString();
        } else {
            return "";
        }

    }

    /**
     * EasyUI右键菜单项
     * <div onclick="showDialog();" iconCls="icon-add">新增</div>
     * @return
     */
    private String createMenuItem() {
        StringBuffer buffer = new StringBuffer("<div ");
        if (!"".equals(this.method)) {
            buffer.append(" onclick='" + this.method + "' ");
        }
        if (!"".equals(this.iconCls)) {
            buffer.append(" iconCls='" + this.iconCls + "' ");
        }
        buffer.append(" >");
        if (!"".equals(this.name)) {
            buffer.append(this.name);
        }
        buffer.append(" </div> ");
        return buffer.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public boolean isPlain() {
        return plain;
    }

    public void setPlain(boolean plain) {
        this.plain = plain;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }
}