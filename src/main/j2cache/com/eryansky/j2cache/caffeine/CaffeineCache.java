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
package com.eryansky.j2cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.eryansky.j2cache.Level1Cache;
import com.github.benmanes.caffeine.cache.Policy;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine cache
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class CaffeineCache implements Level1Cache {

    private Cache<String, Object> cache;
    private long size ;
    private long expire ;

    public CaffeineCache(Cache<String, Object> cache, long size, long expire) {
        this.cache = cache;
        this.size = size;
        this.expire = expire;
    }

    @Override
    public long ttl() {
        return expire;
    }

    @Override
    public long size() { return size; }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public Map<String, Object> get(Collection<String> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void put(Map<String, Object> elements) {
        cache.putAll(elements);
    }

    @Override
    public Collection<String> keys() {
        return cache.asMap().keySet();
    }

    @Override
    public void evict(String...keys) {
        cache.invalidateAll(Arrays.asList(keys));
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }


    @Override
    public Long ttl(String key) {
        Policy.Expiration<String,Object> p = cache.policy().expireAfterWrite().orElse(null);
        long  total = null == p ? 0:p.getExpiresAfter(TimeUnit.SECONDS);
        Duration d = null == p ? null:p.ageOf(key).orElse(null);
        return null == d ? null:total - d.getSeconds();
    }
}
