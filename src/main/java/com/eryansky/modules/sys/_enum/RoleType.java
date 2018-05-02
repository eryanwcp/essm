package com.eryansky.modules.sys._enum;

/**
 * 角色权限类型
 */
public enum RoleType {

    USER("user", "普通角色"),
    SECURITY_ROLE("security-role", "管理角色"),
    ASSIGNMENT("assignment", "任务分配");

    /**
     * 值 Integer型
     */
    private final String value;
    /**
     * 描述 String型
     */
    private final String description;

    RoleType(String value, String description) {
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

    public static RoleType getRoleTypeByValue(RoleType value) {
        if (null == value)
            return null;
        for (RoleType _enum : RoleType.values()) {
            if (value.equals(_enum.getValue()))
                return _enum;
        }
        return null;
    }

    public static RoleType getRoleTypeByDescription(String description) {
        if (null == description)
            return null;
        for (RoleType _enum : RoleType.values()) {
            if (description.equals(_enum.getDescription()))
                return _enum;
        }
        return null;
    }
}
