package com.eryansky.fastweixin.company.message;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYMpNewsMsg extends QYBaseMsg {

    private static final Integer MAX_ARTICLE_COUNT = 10;

    @JSONField(name = "mpnews")
    private Map<String, Object> news;

    public QYMpNewsMsg() {
        news = new HashMap<String, Object>();
    }

    public Map<String, Object> getNews() {
        return news;
    }

    public QYMpNewsMsg setNews(Map<String, Object> news) {
        this.news = news;
        return this;
    }

    public QYMpNewsMsg setMpArticles(List<QYMpArticle> articles){
        if(articles.size() > MAX_ARTICLE_COUNT){
            articles = articles.subList(0, MAX_ARTICLE_COUNT);
        }
        news.put("articles", articles);
        return this;
    }
}


