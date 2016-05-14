package com.eryansky.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * select2模型对象
 * @author  尔演&Eryan eryanwcp@gmail.com
 * @date  2016-05-08
 */
public class Select2 implements Serializable{

    /**
     * 值域
     */
    private String id;
    /**
     * 文本域
     */
    private String text;
    /**
     * 是否不可用
     */
    private Boolean disabled = false;
    /**
     * 是否锁定
     */
    private Boolean locked = false;
    /**
     * 子节点
     */
    private List<Select2> children;

    public Select2() {
    }

    public Select2(String text) {
        this.text = text;
    }
    public Select2(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public Select2 setId(String id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public Select2 setText(String text) {
        this.text = text;
        return this;
    }


    public Boolean getDisabled() {
        return disabled;
    }

    public Select2 setDisabled(Boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public Boolean getLocked() {
        return locked;
    }

    public Select2 setLocked(Boolean locked) {
        this.locked = locked;
        return this;
    }

    public List<Select2> getChildren() {
        return children;
    }

    public Select2 setChildren(List<Select2> children) {
        this.children = children;
        return this;
    }

    /**
     * 添加子节点.
     *
     * @param child
     *            子节点
     */
    public Select2 addChild(Select2 child) {
        if(this.children == null){
            this.children = new ArrayList<Select2>(1);
        }
        this.children.add(child);
        return this;
    }
}
