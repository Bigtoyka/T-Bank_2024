package org.tbank.hw2;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        City city = new City();
        File file = new File("src/main/resources/city.json");
        city.toXML(file);
    }
}
