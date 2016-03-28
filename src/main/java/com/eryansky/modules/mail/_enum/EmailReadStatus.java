package com.eryansky.modules.mail._enum;

/**
 * 邮件是否读取
 */
public enum EmailReadStatus {
    /**
     * 未读(0)
     */
    unreaded(0, "未读"),
    /**
     * 已读(2)
     */
    readed(2, "已读"),
    /**
     * 删除(1)
     */
    Deteled(1, "删除");

    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    EmailReadStatus(Integer value, String description) {
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

    public static EmailReadStatus getEmailReadStatus(Integer value) {
        if (null == value)
            return null;
        for (EmailReadStatus _enum : EmailReadStatus.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static EmailReadStatus getEmailReadStatus(String description) {
        if (null == description)
            return null;
        for (EmailReadStatus _enum : EmailReadStatus.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}