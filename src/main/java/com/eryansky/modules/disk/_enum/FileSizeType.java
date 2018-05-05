package com.eryansky.modules.disk._enum;

public enum FileSizeType {
	MIN("0", "10M以下"),
	MIDDEN("1", "10M~100M"),
	MAX("3", "100M以上");

	private final String value;
	private final String description;

	FileSizeType(String value, String description) {
		this.value = value;
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}
}
