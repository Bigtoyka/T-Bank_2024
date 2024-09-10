package org.tbank.hw3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomListTest {
    private CustomLinkedList customLinkedList;

    @BeforeEach
    void setUp() {
        customLinkedList = new CustomLinkedList();
        for (int i = 0; i < 100; i++) {
            customLinkedList.add(i);
        }
    }

    @Test
    void get() {
        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            customLinkedList.get(100); // исключение при > 100
        });
    }

    @Test
    void add() {
        assertEquals(100, customLinkedList.size());
        customLinkedList.add(200);
        assertEquals(101, customLinkedList.size());
    }

    @Test
    void remove() {
        assertEquals(100, customLinkedList.size());
        assertTrue(customLinkedList.remove(5));
        assertEquals(99, customLinkedList.size());
    }

    @Test
    void removeAt() {
        assertEquals(100, customLinkedList.size());
        assertTrue(customLinkedList.removeAt(2));
        assertEquals(99, customLinkedList.size());
    }

    @Test
    void size() {
        assertEquals(100, customLinkedList.size());
    }

    @Test
    void clear() {
        customLinkedList.clear();
        assertEquals(0, customLinkedList.size());
    }

    @Test
    void contains() {
        assertEquals(100, customLinkedList.size());
        customLinkedList.add(200);
        assertTrue(customLinkedList.contains(200));
    }

    @Test
    void addAll() {
        List list = new ArrayList();
        list.add(1000);
        list.add(2000);
        customLinkedList.addAll(list);
        assertEquals(102, customLinkedList.size());
        assertTrue(customLinkedList.contains(1000));
        assertTrue(customLinkedList.contains(2000));
    }
}