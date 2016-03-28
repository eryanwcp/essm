package com.eryansky.fastweixin.api.entity;

/**
 * 模版参数
 */
public class TemplateParam extends BaseModel {

    /**
     * 值
     */
    private String value;
    /**
     * 颜色
     */
    private String color;

    public String getValue() {
        return value;
    }

    public TemplateParam setValue(String value) {
        this.value = value;
        return this;
    }

    public String getColor() {
        return color;
    }

    public TemplateParam setColor(String color) {
        this.color = color;
        return this;
    }
}
