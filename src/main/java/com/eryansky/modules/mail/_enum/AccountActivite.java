package com.eryansky.modules.mail._enum;

/**
 * 邮件账号是否活动
 */
public enum AccountActivite {
    /**
     * 账号活动(1)
     */
    ACTIVITE(1, "账号活动"),
    /**
     * 账号不活动(0)
     */
    UNACTIVITE(0, "账号不活动");

    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    AccountActivite(Integer value, String description) {
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

    public static AccountActivite getAccountActivite(Integer value) {
        if (null == value)
            return null;
        for (AccountActivite _enum : AccountActivite.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static AccountActivite getAccountActivite(String description) {
        if (null == description)
            return null;
        for (AccountActivite _enum : AccountActivite.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}