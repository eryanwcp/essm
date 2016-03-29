package com.eryansky.fastweixin.api.entity;

import java.util.List;

/**
 * 图文群发总数据
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class ArticleTotal extends BaseDataCube {

    private String                   msgid;
    private String                   title;
    private List<ArticleTotalDetail> details;

    public String getMsgid() {
        return msgid;
    }

    public ArticleTotal setMsgid(String msgid) {
        this.msgid = msgid;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ArticleTotal setTitle(String title) {
        this.title = title;
        return this;
    }

    public List<ArticleTotalDetail> getDetails() {
        return details;
    }

    public ArticleTotal setDetails(List<ArticleTotalDetail> details) {
        this.details = details;
        return this;
    }
}
