package com.eryansky.fastweixin.api.enums;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2016-03-15
 */
public enum MediaType {

    /**
     * 图片
     */
    IMAGE("image"),

    /**
     * 语音
     */
    VOICE("voice"),

    /**
     * 视频
     */
    VIDEO("video"),

    /**
     * 缩略图
     */
    THUMB("thumb"),

    /**
     * 图文消息
     */
    NEWS("news");

    String value;

    MediaType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
