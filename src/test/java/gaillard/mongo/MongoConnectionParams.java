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

    private static final String MONGO_DB_URL = "default.mongodb.uri";
    private static final String MONGO_DB_DB = "default.mongodb.database";
    private static final String MONGO_DB_USER = "default.mongodb.user";
    private static final String MONGO_DB_PASSWORD = "default.mongodb.password";

    private String mongoDBUrl;
    private String mongoDBDB;
    private String mongoDBUser;
    private String mongoDBPassword;

    public MongoConnectionParams(String propertiesFile) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Paths.get(propertiesFile)));
            //properties.load(getClass().getResourceAsStream(propertiesFile));
            init(properties);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public MongoConnectionParams() {
        init(System.getProperties());
    }

    private void init(Properties properties) {
        this.mongoDBUrl = properties.getProperty(MONGO_DB_URL);
        this.mongoDBDB = properties.getProperty(MONGO_DB_DB);
        this.mongoDBUser = properties.getProperty(MONGO_DB_USER);
        this.mongoDBPassword = properties.getProperty(MONGO_DB_PASSWORD);
        System.out.println(String.format(
                "URL: %s; DB: %s, USER: %s; PASS: %s",
                mongoDBUrl, mongoDBDB, mongoDBUser, mongoDBPassword
        ));
    }

    public String getMongoDBUrl() {
        return mongoDBUrl;
    }

    public String getMongoDBDB() {
        return mongoDBDB;
    }

    public String getMongoDBUser() {
        return mongoDBUser;
    }

    public String getMongoDBPassword() {
        return mongoDBPassword;
    }

    public String getConnectionString() {
        final String user = getMongoDBUser();
        final String url;
        if (user != null) {
            if(getMongoDBUser().length()==0 && getMongoDBPassword().length()==0) {
                url = String.format(
                        "mongodb://%s/%s",
                        getMongoDBUrl(),
                        getMongoDBDB()
                );
            } else {
                url = String.format(
                        "mongodb://%s:%s@%s/%s",
                        getMongoDBUser(),
                        getMongoDBPassword(),
                        getMongoDBUrl(),
                        getMongoDBDB()
                );
            }
        } else {
            url = String.format("mongodb://%s/%s",
                    getMongoDBUrl(),
                    getMongoDBDB()
            );
        }
        return url;
    }
}
