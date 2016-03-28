package com.eryansky.fastweixin.message;

import com.eryansky.fastweixin.message.util.MessageBuilder;

public class Article {

    private String title;
    private String description;
    private String picUrl;
    private String url;

    public Article() {

    }

    public Article(String title) {
        this.title = title;
    }

    public Article(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public Article(String title, String description, String picUrl, String url) {
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public Article setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Article setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public Article setPicUrl(String picUrl) {
        this.picUrl = picUrl;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Article setUrl(String url) {
        this.url = url;
        return this;
    }

    public String toXml() {
        MessageBuilder mb = new MessageBuilder();
        mb.addData("Title", title);
        mb.addData("Description", description);
        mb.addData("PicUrl", picUrl);
        mb.addData("Url", url);
        mb.surroundWith("item");
        return mb.toString();
    }

}
