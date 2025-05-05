/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.good.boy.husky.city.finder.utilities;

import com.good.boy.husky.database.configuration.DatabaseConfiguration;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author opuszek
 */
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

}
