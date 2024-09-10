package org.tbank.hw3;

import java.util.Iterator;
import java.util.List;

public interface CustomList<T> extends Iterable<T> {
    T get(int index);

    boolean add(T o);

    boolean remove(T o);

    boolean removeAt(int index);

    int size();

    void clear();

    boolean contains(T o);

    boolean addAll(List<? extends T> c);
}
