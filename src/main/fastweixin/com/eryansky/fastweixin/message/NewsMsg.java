package com.eryansky.fastweixin.message;

import com.eryansky.fastweixin.message.util.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class NewsMsg extends BaseMsg {

    private static final int WX_MAX_SIZE = 10;
    private              int maxSize     = WX_MAX_SIZE;
    private List<Article> articles;

    public NewsMsg() {
        this.articles = new ArrayList<Article>(maxSize);
    }

    public NewsMsg(int maxSize) {
        setMaxSize(maxSize);
        this.articles = new ArrayList<Article>(maxSize);
    }

    public NewsMsg(List<Article> articles) {
        setArticles(articles);
    }

    public int getMaxSize() {
        return maxSize;
    }

    public NewsMsg setMaxSize(int maxSize) {
        if (maxSize < WX_MAX_SIZE && maxSize >= 1) {
            this.maxSize = maxSize;
        }
        if (articles != null && articles.size() > this.maxSize) {
            articles = articles.subList(0, this.maxSize);
        }
        return this;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public NewsMsg setArticles(List<Article> articles) {
        if (articles.size() > this.maxSize) {
            this.articles = articles.subList(0, this.maxSize);
        } else {
            this.articles = articles;
        }
        return this;
    }

    public NewsMsg add(String title) {
        return add(title, null, null, null);
    }

    public NewsMsg add(String title, String url) {
        return add(title, null, null, url);
    }

    public NewsMsg add(String title, String picUrl, String url) {
        return add(new Article(title, null, picUrl, url));
    }

    public NewsMsg add(String title, String description, String picUrl, String url) {
        return add(new Article(title, description, picUrl, url));
    }

    public NewsMsg add(Article article) {
        if (this.articles.size() < maxSize) {
            this.articles.add(article);
        }
        return this;
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("MsgType", RespType.NEWS);
        mb.addTag("ArticleCount", String.valueOf(articles.size()));
        mb.append("<Articles>\n");
        for (Article article : articles) {
            mb.append(article.toXml());
        }
        mb.append("</Articles>\n");
        mb.surroundWith("xml");
        return mb.toString();
    }

}
