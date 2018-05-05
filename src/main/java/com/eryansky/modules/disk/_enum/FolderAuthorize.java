package com.eryansky.modules.disk._enum;

/**
 * 文件夹授权
 */
public enum FolderAuthorize {
	/**
	 * 个人(0)
	 */
	User("0", "我的云盘"),
	/**
	 * 系统(1)
	 */
	SysTem("1", "系统云盘");

	/**
	 * 值 Integer型
	 */
	private final String value;
	/**
	 * 描述 String型
	 */
	private final String description;

	FolderAuthorize(String value, String description) {
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

	public static FolderAuthorize getByValue(String value) {
		if (null == value)
			return null;
		for (FolderAuthorize _enum : FolderAuthorize.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}

	public static FolderAuthorize getByDescription(String description) {
		if (null == description)
			return null;
		for (FolderAuthorize _enum : FolderAuthorize.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}
}