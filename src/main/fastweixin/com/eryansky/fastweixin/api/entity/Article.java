package com.eryansky.fastweixin.api.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 群发图文信息时Article实体
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class Article extends BaseModel {

    public static final class ShowConverPic {
        public static final Integer YES = 1;// 显式封面
        public static final Integer NO  = 0;// 不显式封面
    }

    @JSONField(name = "thumb_media_id")
    private String thumbMediaId;
    private String author;
    private String title;
    @JSONField(name = "content_source_url")
    private String contentSourceUrl;
    private String content;
    private String digest;
    @JSONField(name = "show_cover_pic")
    private Integer showConverPic = ShowConverPic.YES;

    public Article(){

    }
    public Article(String thumbMediaId, String author, String title, String contentSourceUrl, String content, String digest, Integer showConverPic) {
        this.thumbMediaId = thumbMediaId;
        this.author = author;
        this.title = title;
        this.contentSourceUrl = contentSourceUrl;
        this.content = content;
        this.digest = digest;
        this.showConverPic = showConverPic;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public Article setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Article setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContentSourceUrl() {
        return contentSourceUrl;
    }

    public Article setContentSourceUrl(String contentSourceUrl) {
        this.contentSourceUrl = contentSourceUrl;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Article setContent(String content) {
        this.content = content;
        return this;
    }

    public String getDigest() {
        return digest;
    }

    public Article setDigest(String digest) {
        this.digest = digest;
        return this;
    }

    public Integer getShowConverPic() {
        return showConverPic;
    }

    public Article setShowConverPic(Integer showConverPic) {
        this.showConverPic = showConverPic;
        return this;
    }
}
