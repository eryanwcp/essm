package com.eryansky.fastweixin.company.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYVoiceMsg extends QYBaseMsg {

    @JSONField(name = "voice")
    private Voice voice;

    public Voice getVoice() {
        return voice;
    }

    public QYVoiceMsg setVoice(Voice voice) {
        this.voice = voice;
        return this;
    }

    public class Voice{
        @JSONField(name = "media_id")
        private String mediaId;

        public String getMediaId() {
            return mediaId;
        }

        public Voice setMediaId(String mediaId) {
            this.mediaId = mediaId;
            return this;
        }
    }
}
