package com.eryansky.utils;

/**
 * 
 * 是否 公用枚举类
 * 
 */
public enum YesOrNo {
	/** 是(1) */
	YES(1, "是"),
	/** 否(0) */
	NO(0, "否");

	/**
	 * 值 Integer型
	 */
	private final Integer value;
	/**
	 * 描述 String型
	 */
	private final String description;

	YesOrNo(Integer value, String description) {
		this.value = value;
		this.description = description;
	}

	/**
	 * 获取值
	 * @return value
	 */
	public Integer getValue() {
		return value;
	}

	/**
     * 获取描述信息
     * @return description
     */
	public String getDescription() {
		return description;
	}

	public static YesOrNo getYesOrNo(Integer value) {
		if (null == value)
			return null;
		for (YesOrNo _enum : YesOrNo.values()) {
			if (value.equals(_enum.getValue()))
				return _enum;
		}
		return null;
	}
	
	public static YesOrNo getYesOrNo(String description) {
		if (null == description)
			return null;
		for (YesOrNo _enum : YesOrNo.values()) {
			if (description.equals(_enum.getDescription()))
				return _enum;
		}
		return null;
	}

}