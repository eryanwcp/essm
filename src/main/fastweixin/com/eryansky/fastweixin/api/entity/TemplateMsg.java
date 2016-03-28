package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Map;

/**
 * 模版消息
 */
public class TemplateMsg extends BaseModel {
    private String touser;
    @JSONField(name = "template_id")
    private String templateId;
    private String url;
    private String topcolor;

    private Map<String, TemplateParam> data;

    public String getTouser() {
        return touser;
    }

    public TemplateMsg setTouser(String touser) {
        this.touser = touser;
        return this;
    }

    public String getTemplateId() {
        return templateId;
    }

    public TemplateMsg setTemplateId(String templateId) {
        this.templateId = templateId;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public TemplateMsg setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTopcolor() {
        return topcolor;
    }

    public TemplateMsg setTopcolor(String topcolor) {
        this.topcolor = topcolor;
        return this;
    }

    public Map<String, TemplateParam> getData() {
        return data;
    }

    public TemplateMsg setData(Map<String, TemplateParam> data) {
        this.data = data;
        return this;
    }
}
