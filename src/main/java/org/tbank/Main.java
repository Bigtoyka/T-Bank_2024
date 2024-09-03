package main.java.org.tbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        City city = new City();
        File file = new File("src/main/java/org/tbank/city.json");
        city.toXML(file);
    }
}
