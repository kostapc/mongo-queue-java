package gaillard.mongo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 06.03.2017
 * KostaPC
 */
public class MongoConnectionParams {

    private Properties properties;

    public MongoConnectionParams(String propertiesFile) {
        properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream(propertiesFile));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public MongoConnectionParams() {
        properties = System.getProperties();
    }

    public Properties getProperties() {
        return properties;
    }
}
