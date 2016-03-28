package com.eryansky.modules.disk.entity._enum;

/**
 * 文件操作类型
 */
public enum FileOperate {

	DOWNLOAD(0, "下载"),
	COLLECT(1, "收藏"),
	SHARE(2, "分享"),
	DELETE(3,"删除");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	FileOperate(Integer value, String description) {
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

	public static FileOperate getFileOperate(Integer value) {
		if (null == value)
			return null;
		for (FileOperate _enum : FileOperate.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
}