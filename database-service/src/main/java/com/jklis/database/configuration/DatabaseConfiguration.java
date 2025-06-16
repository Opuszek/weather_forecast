package com.jklis.database.configuration;

import com.jklis.database.utilities.Utilities;
import static com.jklis.database.utilities.Utilities.getProperty;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    
    public static void validateConfiguration() {
        List<String> missingParameters = Arrays.stream(new String[]{"user", "password", "url"})
                .filter(Utilities::propertyDoesNotExist)
                .collect(Collectors.toList());
        if (!missingParameters.isEmpty()) {
              throw new InvalidParameterException(
                      String.format("Parameter(s) %s is/are missing.",  
                              String.join(",", missingParameters)));
        }        
    }
    
    

}
