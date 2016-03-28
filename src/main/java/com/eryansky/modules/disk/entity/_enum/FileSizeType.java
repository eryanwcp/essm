package com.eryansky.modules.disk.entity._enum;

public enum FileSizeType {
	MIN(0, "10M以下"),
	MIDDEN(1, "10M~100M"), 
	MAX(2, "100M以上");

	private final Integer value;
	private final String description;

	FileSizeType(Integer value, String description) {
		this.value = value;
		this.description = description;
	}

	public Integer getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}
}
