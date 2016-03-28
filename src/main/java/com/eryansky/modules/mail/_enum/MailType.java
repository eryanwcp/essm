package com.eryansky.modules.mail._enum;

/**
 * 邮件类型
 */
public enum MailType {
    /**
     * 站内邮件(0)
     */
    System(0, "站内邮件"),
    /**
     * 邮件(1)
     */
    Mail(1, "邮件");

    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    MailType(Integer value, String description) {
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

    public static MailType getMailType(Integer value) {
        if (null == value)
            return null;
        for (MailType _enum : MailType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static MailType getMailType(String description) {
        if (null == description)
            return null;
        for (MailType _enum : MailType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}