package com.eryansky.fastweixin.company.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYTextMsg extends QYBaseMsg {

    @JSONField(name = "text")
    private Text text;

    public QYTextMsg() {
        this.setMsgType("text");
    }

    public QYTextMsg(String content) {
        this.text = new Text(content);
    }

    public Text getText() {
        return text;
    }

    public QYTextMsg setText(Text text) {
        this.text = text;
        return this;
    }

    public QYTextMsg setConetnt(String content){
        this.text = new Text(content);
        return this;
    }

    public static class Text{
        @JSONField(name = "content")
        private String content;

        public Text(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public Text setContent(String content) {
            this.content = content;
            return this;
        }
    }
}
