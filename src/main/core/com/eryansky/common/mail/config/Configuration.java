/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */

package com.eryansky.common.mail.config;

import java.util.Properties;

/**
 * 存储用户配置对于系统配置信息
 *
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-09-14
 */
public interface Configuration {
	/**
	 * 工厂方法，创建属性集实体
	 * @return 属性集实体类，存储属性键值对
	 */
	public Properties makeProperties();
}
