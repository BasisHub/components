package com.basiscomponents.util;

import java.util.Map.Entry;

/**
 * {@inheritDoc}
 */
public class KeyValuePair<K, V> implements Entry<K, V> {

	private K key;
	private V value;

	public KeyValuePair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public K getKey() {

		return this.key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V getValue() {
		return this.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V setValue(V value) {
		this.value = value;
		return this.value;
	}

}
