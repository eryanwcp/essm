package com.eryansky.fastweixin.company.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYVideoMsg extends QYBaseMsg {

    @JSONField(name = "video")
    private Video video;

    public Video getVideo() {
        return video;
    }

    public QYVideoMsg setVideo(Video video) {
        this.video = video;
        return this;
    }

    public class Video{
        @JSONField(name = "media_id")
        private String mediaId;
        @JSONField(name = "title")
        private String title;
        @JSONField(name = "description")
        private String description;

        public String getMediaId() {
            return mediaId;
        }

        public Video setMediaId(String mediaId) {
            this.mediaId = mediaId;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Video setTitle(String title) {
            this.title = title;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Video setDescription(String description) {
            this.description = description;
            return this;
        }
    }
}
