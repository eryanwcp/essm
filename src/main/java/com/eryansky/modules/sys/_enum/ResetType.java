package com.eryansky.modules.sys._enum;

/**
 * 序列号重置类型
 */
public enum ResetType {

    Day("D", "每天重置"),
    Month("M", "月度重置"),
    Quarter("Q", "季度重置"),
    Year("Y", "年度重置");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    ResetType(String value, String description) {
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

    public static ResetType getByValue(String value) {
        if (null == value)
            return null;
        for (ResetType _enum : ResetType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static ResetType getByDescription(String description) {
        if (null == description)
            return null;
        for (ResetType _enum : ResetType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}
