package com.eryansky.fastweixin.message;

import com.eryansky.fastweixin.message.util.MessageBuilder;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class VoiceMsg extends BaseMsg {

    private String mediaId;

    public VoiceMsg(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public VoiceMsg setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("MsgType", RespType.VOICE);
        mb.append("<Voice>\n");
        mb.addData("MediaId", mediaId);
        mb.append("</Voice>\n");
        mb.surroundWith("xml");
        return mb.toString();
    }

}
