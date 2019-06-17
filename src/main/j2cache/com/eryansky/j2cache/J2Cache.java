/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eryansky.j2cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * J2Cache 的缓存入口
 * @author Winter Lau(javayou@gmail.com)
 */
public class J2Cache {

	private static final Logger logger = LoggerFactory.getLogger(J2Cache.class);

	private final static String CONFIG_FILE = "/j2cache.properties";

	private static J2CacheConfig config;
	private static J2CacheBuilder builder;

	static {
		try {
            config = J2CacheConfig.initFromConfig(CONFIG_FILE);
			builder = J2CacheBuilder.init(config);
		} catch (Exception e) {
			logger.error("Failed to load J2Cache Config File : [{}]. " ,CONFIG_FILE);
		}
	}

	public static void init(J2CacheConfig j2CacheConfig,J2CacheBuilder j2CacheBuilder) {
		config = j2CacheConfig;
		builder = j2CacheBuilder;
		logger.info("Load J2Cache Config File : [{}].",j2CacheConfig.getConfigResource());
	}

	/**
	 * 返回缓存操作接口
	 * @return CacheChannel
	 */
	public static CacheChannel getChannel(){
		return builder == null ? null:builder.getChannel();
	}

	public static J2CacheConfig getConfig() {
		return config;
	}

	/**
     * 关闭 J2Cache
     */
	public static void close() {
	    builder.close();
    }
}
