package com.zengshi.butterfly.core;

/**
 * 对象key/value化
 *
 */
public interface IKVable<K,V> {

	public K getKey();
	public V getValue();
}
