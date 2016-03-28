package com.eryansky.fastweixin.company.message.resp;

import com.eryansky.fastweixin.message.RespType;
import com.eryansky.fastweixin.message.util.MessageBuilder;

/**
 *  微信企业号被动响应文本消息
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYTextRespMsg extends QYBaseRespMsg {

    private StringBuilder contentBuilder;

    public QYTextRespMsg() {
        contentBuilder = new StringBuilder();
    }

    public QYTextRespMsg(String content){
        setContentBuilder(content);
    }

    public StringBuilder getContentBuilder() {
        return contentBuilder;
    }

    public void setContentBuilder(String content) {
        this.contentBuilder = new StringBuilder(content);
    }

    public QYTextRespMsg add(String text){
        contentBuilder.append(text);
        return this;
    }

    public QYTextRespMsg addln(){
        return add("\n");
    }

    public QYTextRespMsg addln(String text){
        contentBuilder.append(text);
        return addln();
    }

    public QYTextRespMsg addLink(String text, String url){
        contentBuilder.append("<a href=\"").append(url).append("\">").append(text).append("</a>");
        return this;
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("Content", contentBuilder.toString().trim());
        mb.addData("MsgType", RespType.TEXT);
        return mb.toString();
    }

}
