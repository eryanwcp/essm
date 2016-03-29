package com.eryansky.fastweixin.company.message.resp;

import com.eryansky.fastweixin.message.RespType;
import com.eryansky.fastweixin.message.util.MessageBuilder;

/**
 *  微信企业号被动响应语音消息
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYVoiceRespMsg extends QYBaseRespMsg {

    private String mediaId;

    public QYVoiceRespMsg() {
    }

    public QYVoiceRespMsg(String mediaId) {
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
        mb.addData("MsgType", RespType.VOICE);
        mb.append("<Voice>\n");
        mb.addData("MediaId", mediaId);
        mb.append("</Voice>\n");
        mb.surroundWith("xml");
        return mb.toString();
    }
}
