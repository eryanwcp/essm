package com.eryansky.fastweixin.api.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public class UploadMaterialResponse extends BaseResponse  {

    @JSONField(name = "media_id")
    private String mediaId;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
