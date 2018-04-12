package com.eryansky.modules.sys._enum;

/**
 * 区域类型
 */
public enum AreaType {

    COUNTRY("1", "国家"),
    PROVINCE("2", "省份、直辖市"),
    CITY("3", "地市"),
    AREA("4", "区县"),
    STREET("5", "乡镇"),
    VILLAGE("6", "村庄"),
    OTHER("9", "其它");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    AreaType(String value, String description) {
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

    public static AreaType getByValue(String value) {
        if (null == value)
            return null;
        for (AreaType _enum : AreaType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static AreaType getByDescription(String description) {
        if (null == description)
            return null;
        for (AreaType _enum : AreaType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}
