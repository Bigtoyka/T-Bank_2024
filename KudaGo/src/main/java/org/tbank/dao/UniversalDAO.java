package org.tbank.dao;

import java.util.Collection;

public interface UniversalDAO<K,V> {

    public void put(K key, V value);

    public V get(K key);

    public void remove(K key);

    public Collection<V> getAll();

    public void update(K key, V value);
}