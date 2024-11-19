package org.tbank.hw3;

import java.util.Iterator;
import java.util.List;

public class CustomLinkedList<T> implements CustomList<T> {
    Node first;
    Node last;
    int size = 0;

    @Override
    public T get(int index) {
        return getNode(index).value;
    }

    @Override
    public boolean add(T o) {
        if (size == 0) {
            Node newnode = new Node(null, o, null);
            first = newnode;
            last = newnode;
        } else {
            Node secondlast = last;
            last = new Node(secondlast, o, null);
            secondlast.next = last;
        }
        size++;
        return true;
    }

    @Override
    public boolean remove(T o) {
        Node node = first;
        for (int i = 0; i < size; i++) {
            if (node.value.equals(o)) {
                return removeAt(i);
            }
            node = node.next;
        }
        return false;
    }

    @Override
    public boolean removeAt(int index) {
        Node node = getNode(index);
        Node nodeNext = node.next;
        Node nodePrev = node.previous;
        if (nodeNext != null) {
            nodeNext.previous = nodePrev;
        } else {
            last = nodePrev;
        }
        if (nodePrev != null) {
            nodePrev.next = nodeNext;
        } else {
            first = nodeNext;
        }
        size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        first = null;
        last = null;
        size = 0;
    }

    @Override
    public boolean contains(T o) {
        Node node = first;
        for (int i = 0; i < size; i++) {
            if (node.value.equals(o)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    @Override
    public boolean addAll(List<? extends T> c) {
        boolean result = false;
        for (T o : c) {
            add(o);
            result = true;
        }
        return result;
    }

    private class Node {
        private Node previous;
        private T value;
        private Node next;

        public Node(Node previous, T value, Node next) {
            this.previous = previous;
            this.value = value;
            this.next = next;
        }
    }

    public Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node node = first;
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private Node node = first;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public T next() {
                T o = node.value;
                node = node.next;
                return o;
            }
        };
    }
}
