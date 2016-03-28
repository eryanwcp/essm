package com.eryansky.fastweixin.message;

import com.eryansky.fastweixin.message.util.MessageBuilder;

public class VideoMsg extends BaseMsg {

    private String mediaId;
    private String title;
    private String description;

    public VideoMsg(String mediaId) {
        this.mediaId = mediaId;
    }

    public VideoMsg(String mediaId, String title, String description) {
        this.mediaId = mediaId;
        this.title = title;
        this.description = description;
    }

    public String getMediaId() {
        return mediaId;
    }

    public VideoMsg setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public VideoMsg setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VideoMsg setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("MsgType", RespType.VIDEO);
        mb.append("<Video>\n");
        mb.addData("MediaId", mediaId);
        mb.addData("Title", title);
        mb.addData("Description", description);
        mb.append("</Video>\n");
        mb.surroundWith("xml");
        return mb.toString();
    }

}
