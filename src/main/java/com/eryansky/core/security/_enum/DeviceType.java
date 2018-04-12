package com.eryansky.core.security._enum;

/**
 * 设备类型
 */
public enum DeviceType {

    UNKNOWN("0", "UNKNOWN"),
    iPhone("1", "iPhone"),
    iPad("2", "iPad"),
    Android("3", "Android"),
    WinPhone("4", "WinPhone"),
    PC("5", "PC");

    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    DeviceType(String value, String description) {
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

    public static DeviceType getByValue(String value) {
        if (null == value)
            return null;
        for (DeviceType _enum : DeviceType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static DeviceType getByDescription(String description) {
        if (null == description)
            return null;
        for (DeviceType _enum : DeviceType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }

}