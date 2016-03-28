package com.eryansky.fastweixin.company.message;

import com.eryansky.fastweixin.api.entity.Article;

/**
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYMpArticle extends Article {

    public QYMpArticle(String thumbMediaId, String author, String title, String contentSourceUrl, String content, String digest, Integer showConverPic) {
        super(thumbMediaId, author, title, contentSourceUrl, content, digest, showConverPic);
    }

}
