package com.eryansky.modules.disk._enum;


import com.eryansky.common.utils.StringUtils;

/**
 * 文件类型
 */
public enum FileType {
    /**
     * 文档
     */
    Document("Document", "文档"),
    /**
     * 图片
     */
    Image("Image", "图片"),
    /**
     * 媒体
     */
    Music("Music", "音乐"),
    /**
     * 媒体
     */
    Video("Video", "视频"),
    /**
     * 软件
     */
    Soft("Soft", "软件"),
    /**
     * 其它
     */
    Other("Other", "其它");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    FileType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取描述信息
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public static FileType getByValue(String value) {
        if (null == value)
            return null;
        for (FileType _enum : FileType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static FileType getByDescription(String description) {
        if (null == description)
            return null;
        for (FileType _enum : FileType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }

    /**
     * 根据文件后缀名返回文件分类
     * @param fileSuffix
     * @return
     */
    public static FileType getFileTypeByFileSuffix(String fileSuffix) {
        FileType fileType = FileType.Other;
        if (StringUtils.isNotBlank(fileSuffix)){
            //TODO
        }
        return fileType;
    }
}