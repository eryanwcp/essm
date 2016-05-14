package com.eryansky.fastweixin.api.response;

import com.eryansky.fastweixin.api.entity.Tag;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2016-05-14
 */
public class GetAllTagsResponse extends BaseResponse {

    private List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}