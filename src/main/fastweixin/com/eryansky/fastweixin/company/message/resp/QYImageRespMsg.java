package com.eryansky.fastweixin.company.message.resp;

import com.eryansky.fastweixin.message.RespType;
import com.eryansky.fastweixin.message.util.MessageBuilder;

/**
 *  微信企业号被动响应图片消息
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class QYImageRespMsg extends QYBaseRespMsg {

    private String mediaId;

    public QYImageRespMsg() {
    }

    public QYImageRespMsg(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public String toXml(){
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("MsgType", RespType.IMAGE);
        mb.append("<Image>\n");
        mb.addData("MediaId", mediaId);
        mb.append("</Image>\n");
        mb.surroundWith("xml");
        return mb.toString();
    }
}
