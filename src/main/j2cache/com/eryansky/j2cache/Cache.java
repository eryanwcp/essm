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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Cache Data Operation Interface
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public interface Cache {
	Queue<String> queue = new LinkedBlockingQueue<String>();

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
	 * 队列 放入
	 */
	default void push(String... values) {
		for(String value:values){
			queue.add(value);
		}
	}
	/**
	 * 队列 获取
	 */
	default String pop(){return queue.poll();}

	/**
	 * 队列 清空
	 */
	default void clearQueue(){queue.clear();}

}
