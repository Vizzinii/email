package com.example.email.mailmanagement.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    public static Properties loadProperties(String filePath) {
        try (InputStream input = PropertyUtil.class.getClassLoader().getResourceAsStream(filePath)) {
            Properties properties = new Properties();
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find " + filePath);
            }
            properties.load(input);
            return properties;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }
}