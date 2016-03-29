package com.eryansky.fastweixin.company.message;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-03-15
 */
public class QYFileMsg extends QYBaseMsg {

    @JSONField(name = "file")
    private File file;

    public File getFile() {
        return file;
    }

    public QYFileMsg setFile(File file) {
        this.file = file;
        return this;
    }

    public class File{
        @JSONField(name = "media_id")
        private String mediaId;

        public String getMediaId() {
            return mediaId;
        }

        public File setMediaId(String mediaId) {
            this.mediaId = mediaId;
            return this;
        }
    }
}
