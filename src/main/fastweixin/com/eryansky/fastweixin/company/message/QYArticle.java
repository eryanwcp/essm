package com.eryansky.fastweixin.company.message;

import com.eryansky.fastweixin.message.Article;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYArticle extends Article {

    private String picurl;

    public QYArticle(String title, String description, String picUrl, String url) {
        super(title, description, null, url);
        this.picurl = picUrl;
    }

    public String getPicurl() {
        return picurl;
    }

    public QYArticle setPicurl(String picurl) {
        this.picurl = picurl;
        return this;
    }
}
