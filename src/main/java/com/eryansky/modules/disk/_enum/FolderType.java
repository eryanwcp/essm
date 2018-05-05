package com.eryansky.modules.disk._enum;

/**
 * 文件夹类型
 */
public enum FolderType {

	NORMAL("0", "正常"),
	HIDE("1", "隐藏");

	/**
	 * 值 String型
	 */
	private final String value;
	/**
	 * 描述 String型
	 */
	private final String description;

	FolderType(String value, String description) {
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


	public static FolderType getByValue(String value) {
		if (null == value)
			return null;
		for (FolderType _enum : FolderType.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}

	public static FolderType getByDescription(String description) {
		if (null == description)
			return null;
		for (FolderType _enum : FolderType.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}
}