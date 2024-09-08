package main.java.org.tbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class City {
    private String slug;
    private Map coords;

    public void toXML(File file) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            logger.info("Starting to read JSON file.");
            File fileout = new File("src/main/java/org/tbank/output.xml");
            City json = objectMapper.readValue(file, City.class);
            logger.info("Successfully read JSON file", json);
            logger.debug("Coordinates: {}", json.getCoords());
            logger.info("Starting to write XML file.");
            XmlMapper xmlMapper = new XmlMapper();
            if (fileout.exists()) {
                logger.warn("File already exists");
            }
            xmlMapper.writeValue(fileout, json);
            logger.info("Successfully wrote XML file.");
        } catch (Exception e) {
            logger.error("Error in JSON file", e);
            throw new RuntimeException(e);
        }
    }
}
