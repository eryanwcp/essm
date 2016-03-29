package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 图文群发每日数据
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class ArticleSummary extends BaseDataCube {

    private String  msgid;
    private String  title;
    @JSONField(name = "int_page_read_user")
    private Integer intPageReadUser;
    @JSONField(name = "int_page_read_count")
    private Integer intPageReadCount;
    @JSONField(name = "ori_page_read_user")
    private Integer oriPageReadUser;
    @JSONField(name = "ori_page_read_count")
    private Integer oriPageReadCount;
    @JSONField(name = "share_user")
    private Integer shareUser;
    @JSONField(name = "shareCount")
    private Integer share_count;
    @JSONField(name = "add_to_fav_user")
    private Integer addToFavUser;
    @JSONField(name = "add_to_fav_count")
    private Integer addToFavCount;

    public String getMsgid() {
        return msgid;
    }

    public ArticleSummary setMsgid(String msgid) {
        this.msgid = msgid;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ArticleSummary setTitle(String title) {
        this.title = title;
        return this;
    }

    public Integer getIntPageReadUser() {
        return intPageReadUser;
    }

    public ArticleSummary setIntPageReadUser(Integer intPageReadUser) {
        this.intPageReadUser = intPageReadUser;
        return this;
    }

    public Integer getIntPageReadCount() {
        return intPageReadCount;
    }

    public ArticleSummary setIntPageReadCount(Integer intPageReadCount) {
        this.intPageReadCount = intPageReadCount;
        return this;
    }

    public Integer getOriPageReadUser() {
        return oriPageReadUser;
    }

    public ArticleSummary setOriPageReadUser(Integer oriPageReadUser) {
        this.oriPageReadUser = oriPageReadUser;
        return this;
    }

    public Integer getOriPageReadCount() {
        return oriPageReadCount;
    }

    public ArticleSummary setOriPageReadCount(Integer oriPageReadCount) {
        this.oriPageReadCount = oriPageReadCount;
        return this;
    }

    public Integer getShareUser() {
        return shareUser;
    }

    public ArticleSummary setShareUser(Integer shareUser) {
        this.shareUser = shareUser;
        return this;
    }

    public Integer getShare_count() {
        return share_count;
    }

    public ArticleSummary setShare_count(Integer share_count) {
        this.share_count = share_count;
        return this;
    }

    public Integer getAddToFavUser() {
        return addToFavUser;
    }

    public ArticleSummary setAddToFavUser(Integer addToFavUser) {
        this.addToFavUser = addToFavUser;
        return this;
    }

    public Integer getAddToFavCount() {
        return addToFavCount;
    }

    public ArticleSummary setAddToFavCount(Integer addToFavCount) {
        this.addToFavCount = addToFavCount;
        return this;
    }
}
