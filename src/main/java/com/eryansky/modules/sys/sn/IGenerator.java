package com.eryansky.modules.sys.sn;

import java.util.Map;

public interface IGenerator {
	/**
	 * 获取子序列类型
	 * @return
	 */
	public String getGeneratorType();
	/**
	 * 生成子序列串
	 * @param formatStr
	 * @return
	 */
	public String generate(String formatStr, Map paraMap);
}