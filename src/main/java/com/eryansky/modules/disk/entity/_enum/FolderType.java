package com.eryansky.modules.disk.entity._enum;

/**
 * 文件夹类型
 */
public enum FolderType {

	NORMAL(0, "正常"),
	HIDE(1, "隐藏");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	FolderType(Integer value, String description) {
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
}