package com.jklis.database.utilities;

import java.io.IOException;
import java.util.Properties;

public class Utilities {

    public static String getProperty(String property) {
        Properties prop = new Properties();
        try {
            prop.load(Utilities.class
                    .getClassLoader()
                    .getResourceAsStream("application.properties"));
            return prop.getProperty(property);
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static boolean propertyExists(String propertyName) {
        String propertyValue = getProperty(propertyName);
        return propertyValue != null && !propertyValue.trim().isEmpty();
    }

    public static boolean propertyDoesNotExist(String propertyName) {
        return !propertyExists(propertyName);
    }

}
