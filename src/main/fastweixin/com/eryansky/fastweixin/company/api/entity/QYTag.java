package com.eryansky.fastweixin.company.api.entity;

import com.eryansky.fastweixin.api.entity.BaseModel;

/**
 *  企业通讯录-标签
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYTag extends BaseModel {

    private String tagname;
    private Integer tagid;

    public QYTag() {
    }

    public QYTag(String tagname) {
        this.tagname = tagname;
    }

    public QYTag(String tagname, Integer tagid) {
        this.tagname = tagname;
        this.tagid = tagid;
    }

    public String getTagname() {
        return tagname;
    }

    public QYTag setTagname(String tagname) {
        this.tagname = tagname;
        return this;
    }

    public Integer getTagid() {
        return tagid;
    }

    public QYTag setTagid(Integer tagid) {
        this.tagid = tagid;
        return this;
    }
}
