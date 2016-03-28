package com.eryansky.modules.notice._enum;

/**
 * 通知接收范围
 */
public enum NoticeReceiveScope {

    COMPANY_AND_CHILD ("2", "所在单位及以下"),
    COMPANY("3", "所在单位"),
    OFFICE_AND_CHILD("4", "所在部门及以下"),
    OFFICE("5", "所在部门"),
    ALL("10", "所有"),
    CUSTOM("0", "自定义");
    /**
     * 值 String型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    NoticeReceiveScope(String value, String description) {
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

    public static NoticeReceiveScope getNoticeReceiveScopeByValue(String value) {
        if (null == value)
            return null;
        for (NoticeReceiveScope _enum : NoticeReceiveScope.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static NoticeReceiveScope getNoticeReceiveScopeByDescription(String description) {
        if (null == description)
            return null;
        for (NoticeReceiveScope _enum : NoticeReceiveScope.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}