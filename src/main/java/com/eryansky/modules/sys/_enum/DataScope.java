package com.eryansky.modules.sys._enum;

/**
 * 数据范围
 */
public enum DataScope {

    ALL("1", "所有数据"),
    COMPANY_AND_CHILD ("2", "所在公司及以下数据"),
    COMPANY("3", "所在公司数据"),
    OFFICE_AND_CHILD("4", "所在部门及以下数据"),
    OFFICE("5", "所在部门数据"),
    SELF("8", "仅本人数据"),
    CUSTOM("9", "按明细设置");

    /**
     * 值 Integer型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    DataScope(String value, String description) {
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

    public static DataScope getDataScopeByValue(String value) {
        if (null == value)
            return null;
        for (DataScope _enum : DataScope.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static DataScope getDataScopeByDescription(String description) {
        if (null == description)
            return null;
        for (DataScope _enum : DataScope.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}
