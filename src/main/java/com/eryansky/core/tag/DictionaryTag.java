/**
 *  Copyright (c) 2012-2018 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.tag;

import com.eryansky.utils.AppConstants;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;


/**
 * 数据字典标签
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-3-29 下午8:02:39
 */
@SuppressWarnings("serial")
public class DictionaryTag extends TagSupport {


    public static final String TYPE_COMBOBOX = "combobox";
    public static final String TYPE_COMBOTREE = "combotree";

    /**
     * 元素id
     */
    private String id = "";
    /**
     * 元素name
     */
    private String name = "";
    /**
     * 宽度
     */
    private Integer width;
    /**
     * 高度
     */
    private Integer height;
    /**
     * 字典类别编码
     */
    private String code;
    /**
     * 要生成字典的类型，默认下拉列表："combobox" 、下拉树："combotree"
     */
    private String type = TYPE_COMBOBOX;
    /**
     * 选择类型  "select" 、"all"
     */
    private String selectType;

    /**
     * 是否必选
     */
    private boolean required = false;
    /**
     * 为空提示信息
     */
    private String missingMessage;
    /**
     * 校验类型
     */
    private String validType;
    /**
     * 是否多选 默认值:false
     */
    private boolean multiple = false;
    /**
     * 是否可编辑 默认值：true
     */
    private boolean editable = true;
    /**
     * 默认值
     */
    private String value = "";



    @Override
    public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().print(createTagHtml());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return SKIP_BODY;
    }


    /**
     * 下拉框
     * @return
     */
    private String createTagHtml() {
        String contextPath = pageContext.getServletContext().getContextPath(); //上下文路径
        String method = null;
        StringBuffer buffer = new StringBuffer("<input ");
        if (TYPE_COMBOBOX.equals(type)) {
            buffer.append(" class=\"easyui-combobox\"");
            method = TYPE_COMBOBOX;
        } else if (TYPE_COMBOTREE.equals(type)) {
            buffer.append(" class=\"easyui-combotree\"");
            method = TYPE_COMBOTREE;
        }
        if (!"".equals(this.id)) {
            buffer.append(" id=\"" + this.id + "\" ");
        }

        if (this.name != null && !"".equals(this.name)) {
            buffer.append(" name=\"" + this.name + "\" ");
        }

        buffer.append(" data-options=\"url:'").append(contextPath).append(AppConstants.getAdminPath()).append("/sys/dictionary/")
                .append(method)
                .append("?dictionaryCode=")
                .append(this.code);
        if(this.selectType !=null && !"".equals(this.selectType)){
            buffer.append("&selectType=").append(this.selectType);
        }
        buffer.append("'");
        if(this.required){
            buffer.append(",required:").append(required);
        }
        if(StringUtils.isNotBlank(this.missingMessage)){
            buffer.append(",missingMessage:'").append(missingMessage).append("'");
        }

        if(this.multiple){
            buffer.append(",multiple:").append(multiple);
        }
        buffer.append(",editable:").append(editable);
        if(this.width != null){
            buffer.append(",width:'").append(this.width).append("'");
        }
        if(this.height != null){
            buffer.append(",height:'").append(this.height).append("'");
        }
        if(StringUtils.isNotBlank(this.value)){
            buffer.append(",value:'").append(this.value).append("'");
        }

        if(!"".equals(validType)){
            buffer.append(",validType:").append(this.validType);
        }
        buffer.append(",valueField:'value',textField:'text'")
                .append("\"");

        buffer.append(" />");
        return buffer.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getWidth() {
        return width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getMissingMessage() {
        return missingMessage;
    }

    public void setMissingMessage(String missingMessage) {
        this.missingMessage = missingMessage;
    }

    public String getValidType() {
        return validType;
    }

    public void setValidType(String validType) {
        this.validType = validType;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}