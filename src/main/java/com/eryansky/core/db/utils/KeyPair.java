/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.core.db.utils;

import java.util.Map;

/**
 * Author: 温春平 wencp@jx.tobacco.gov.cn
 * Date: 2014-03-17 17:11
 */
public class KeyPair<K, V> implements Map.Entry<K, V> {

    private K key;
    private V value;

    public KeyPair() {
	}
    
    public KeyPair(K key) {
        this.key = key;
    }

    public KeyPair(K key, V value) {
        this.key = key;
        this.value = value;
    }


	public K getKey() {
        return this.key;
    }

    public K setKey(K key) {
        this.key = key;
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public V setValue(V value) {
        this.value = value;
        return this.value;
    }

}
