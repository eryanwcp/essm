package com.eryansky.modules.mail._enum;

/**
 * 邮件发送类型
 */
public enum ReceiveType {
    /**
     * 收件人(0)
     */
    TO(0, "收件人"),
    /**
     * 抄送(1)
     */
    CC(1, "抄送"),
    /**
     * 密送(2)
     */
    BCC(2, "密送");

    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    ReceiveType(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public Integer getValue() {
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

    public static ReceiveType getReceiveType(Integer value) {
        if (null == value)
            return null;
        for (ReceiveType _enum : ReceiveType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static ReceiveType getReceiveType(String description) {
        if (null == description)
            return null;
        for (ReceiveType _enum : ReceiveType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}