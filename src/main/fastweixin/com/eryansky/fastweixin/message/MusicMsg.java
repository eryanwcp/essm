package com.eryansky.fastweixin.message;

import com.eryansky.fastweixin.message.util.MessageBuilder;

public class MusicMsg extends BaseMsg {

    private String title;
    private String description;
    private String musicUrl;
    private String hqMusicUrl;
    private String thumbMediaId;

    public MusicMsg(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
    }

    public MusicMsg(String thumbMediaId, String title, String description,
                    String musicUrl, String hqMusicUrl) {
        this.title = title;
        this.description = description;
        this.musicUrl = musicUrl;
        this.hqMusicUrl = hqMusicUrl;
        this.thumbMediaId = thumbMediaId;
    }

    public String getTitle() {
        return title;
    }

    public MusicMsg setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MusicMsg setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public MusicMsg setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
        return this;
    }

    public String getHqMusicUrl() {
        return hqMusicUrl;
    }

    public MusicMsg setHqMusicUrl(String hqMusicUrl) {
        this.hqMusicUrl = hqMusicUrl;
        return this;
    }

    public String getThumbMediaId() {
        return thumbMediaId;
    }

    public MusicMsg setThumbMediaId(String thumbMediaId) {
        this.thumbMediaId = thumbMediaId;
        return this;
    }

    @Override
    public String toXml() {
        MessageBuilder mb = new MessageBuilder(super.toXml());
        mb.addData("MsgType", RespType.MUSIC);
        mb.append("<Music>\n");
        mb.addData("Title", title);
        mb.addData("Description", description);
        mb.addData("MusicUrl", musicUrl);
        mb.addData("HQMusicUrl", hqMusicUrl);
        mb.addData("ThumbMediaId", thumbMediaId);
        mb.append("</Music>\n");
        mb.surroundWith("xml");
        return mb.toString();
    }

}
