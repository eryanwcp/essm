/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.db.utils;

import java.util.Map;

/**
 * Author: 尔演&Eryan eryanwcp@gmail.com
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
