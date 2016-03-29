package com.eryansky.fastweixin.company.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYImageMsg extends QYBaseMsg{

    @JSONField(name = "image")
    private Image image;

    public QYImageMsg() { this.setMsgType("image"); }

    public Image getImage() {
        return image;
    }

    public QYImageMsg setImage(Image image) {
        this.image = image;
        return this;
    }

    public QYImageMsg setMediaId(String mediaId) {
        this.image = new Image(mediaId);
        return this;
    }

    public class Image{
        @JSONField(name = "media_id")
        private String mediaId;

        public Image(String mediaId) {this.mediaId = mediaId;}

        public String getMediaId() {
            return mediaId;
        }

        public Image setMediaId(String mediaId) {
            this.mediaId = mediaId;
            return this;
        }
    }
}
