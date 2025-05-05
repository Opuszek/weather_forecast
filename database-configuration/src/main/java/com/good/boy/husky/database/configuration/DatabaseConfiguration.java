package com.good.boy.husky.database.configuration;

import java.io.IOException;
import java.util.Properties;

public class DatabaseConfiguration {
    
    public static String getUser() {
        return getProperty("user");
    }
    
    public static String getPassword() {
        return getProperty("password");
    }
    
    public static String getUrl() {
        return getProperty("url");
    }

    private static String getProperty(String property) {
        Properties prop = new Properties();
        try {
            prop.load(DatabaseConfiguration.class
                    .getClassLoader()
                    .getResourceAsStream("database.properties"));
            return prop.getProperty(property);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

}
