package org.tbank.examples;

public class OutOfMemoryExample {
    public static void outOfMem() {
        int[] largeArray = new int[Integer.MAX_VALUE];
    }
}
