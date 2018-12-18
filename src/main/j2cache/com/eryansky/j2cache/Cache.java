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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Cache Data Operation Interface
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public interface Cache {

	/**
	 * Get an item from the cache, nontransactionally
	 * 
	 * @param key cache key
	 * @return the cached object or null
	 */
	Object get(String key) ;

	/**
	 * 批量获取缓存对象
	 * @param keys cache keys
	 * @return return key-value objects
	 */
	Map<String, Object> get(Collection<String> keys);

	/**
	 * 判断缓存是否存在
	 * @param key cache key
	 * @return true if key exists
	 */
	default boolean exists(String key) {
		return get(key) != null;
	}
	
	/**
	 * Add an item to the cache, nontransactionally, with
	 * failfast semantics
	 *
	 * @param key cache key
	 * @param value cache value
	 */
	void put(String key, Object value);

	/**
	 * 批量插入数据
	 * @param elements objects to be put in cache
	 */
	void put(Map<String, Object> elements);

	/**
	 * Return all keys
	 *
	 * @return 返回键的集合
	 */
	Collection<String> keys() ;
	
	/**
	 * Remove items from the cache
	 *
	 * @param keys Cache key
	 */
	void evict(String...keys);

	/**
	 * Clear the cache
	 */
	void clear();

	/**
	 * 在region里增加一个可选的层级,作为命名空间,使结构更加清晰
	 * 同时满足小型应用,多个J2Cache共享一个redis database的场景
	 * @param namespace
	 * @param region
	 * @return
	 */
	static String getRegionName(String namespace,String region) {
		if (namespace != null && !namespace.trim().isEmpty())
			region = namespace + ":" + region;
		return region;
	}

	/**
	 * 队列 放入
	 */
	default void queuePush(String... values) {}

	/**
	 * 队列 获取
	 */
	default String queuePop(){return null;};

    /**
     * 队列 储存元素长度
     * @return
     */
	default int queueSize(){return 0;};

    /**
     * 队列 储存的全部元素
     * @return
     */
	default Collection<String> queueList(){return Collections.emptyList();};

	/**
	 * 队列 清空
	 */
	default void queueClear(){}

}
