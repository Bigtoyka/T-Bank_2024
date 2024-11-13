package org.tbank.hw3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList();
        list.add(101);
        list.add(102);
        CustomList<Integer> customLinkedList = new CustomLinkedList();
        customLinkedList.add(1);
        //сделал через sout для наглядности, так же есть тесты
        System.out.println("get: " + customLinkedList.get(0));
        System.out.println("remove: " + customLinkedList.remove(1));
        System.out.println("contains: " + customLinkedList.contains(1));
        customLinkedList.addAll(list);
        for (int i = 0; i < customLinkedList.size(); i++) {
            System.out.println(customLinkedList.get(i));
        }
        //Задание 2
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        CustomLinkedList<Integer> customLinkedList2 = stream.reduce(
                new CustomLinkedList<>(),
                (newlist, element) -> {
                    newlist.add(element);
                    return newlist;
                },
                (list1, list2) -> {
                    list1.addAll((List<? extends Integer>) list2);
                    return list1;
                });
        for (int i : customLinkedList2) {
            System.out.print(i);
        }
    }
}