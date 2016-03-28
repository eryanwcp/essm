package com.eryansky.modules.sys._enum;

/**
 * 是否 枚举类型
 */
public enum YesOrNo {

    YES("1", "是"),
    NO ("0", "否");

    /**
     * 值 Integer型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    YesOrNo(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取描述信息
     * @return description
     */
    public String getDescription() {
        return description;
    }

    public static YesOrNo getYesOrNoByValue(String value) {
        if (null == value)
            return null;
        for (YesOrNo _enum : YesOrNo.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static YesOrNo getYesOrNoByDescription(String description) {
        if (null == description)
            return null;
        for (YesOrNo _enum : YesOrNo.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}
