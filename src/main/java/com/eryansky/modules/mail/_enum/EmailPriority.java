package com.eryansky.modules.mail._enum;

/**
 * 邮件重要性
 */
public enum EmailPriority {
    /**
     * 一般(0)
     */
    Low(5, "普通"),
    /**
     * 一般(0)
     */
    Normal(3, "重要"),
    /**
     * 重要(1)
     */
    High(1, "很重要"),;

    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    EmailPriority(Integer value, String description) {
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

    public static EmailPriority getEmailPriority(Integer value) {
        if (null == value)
            return null;
        for (EmailPriority _enum : EmailPriority.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return Low;
    }

    public static EmailPriority getEmailPriority(String description) {
        if (null == description)
            return null;
        for (EmailPriority _enum : EmailPriority.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return Low;
    }
}