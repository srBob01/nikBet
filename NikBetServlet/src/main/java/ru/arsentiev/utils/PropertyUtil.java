package ru.arsentiev.utils;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.util.Properties;

@UtilityClass
public class PropertyUtil {
    private static final Properties PROPERTIES = new Properties();
    private static final Properties PROPERTIES_TEST = new Properties();

    static {
        loadProperties();
        loadPropertiesTest();
    }

    private static void loadProperties() {
        try (var inputStream = PropertyUtil.class.getClassLoader()
                .getResourceAsStream("application.properties");) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }


    private static void loadPropertiesTest() {
        try (var inputStream = PropertyUtil.class.getClassLoader()
                .getResourceAsStream("test.properties")) {
            PROPERTIES_TEST.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTest(String key) {
        return PROPERTIES_TEST.getProperty(key);
    }
}
