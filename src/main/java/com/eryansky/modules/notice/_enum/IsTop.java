package com.eryansky.modules.notice._enum;

/**
 * 邮件重要性
 */
public enum IsTop {
    /**
     * 不置顶(0)
     */
    No(0, "不置顶"),
    /**
     * 置顶(1)
     */
    Yes(1, "置顶");

    /**
     * 值 Integer型
     */
    private final Integer value;
    /**
     * 描述 String型
     */
    private final String description;

    IsTop(Integer value, String description) {
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

    public static IsTop getIsTop(Integer value) {
        if (null == value)
            return null;
        for (IsTop _enum : IsTop.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static IsTop getIsTop(String description) {
        if (null == description)
            return null;
        for (IsTop _enum : IsTop.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}