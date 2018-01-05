package com.kobie.qaautomation.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
    private static Settings instance = null;
    private Properties props = null;

    private Settings() {
        Properties properties = new Properties();
        InputStream input;

        try {
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            properties.load(input);
        } catch (IOException e) {
        }

        this.props = properties;
    }

    private static synchronized Settings getInstance() {
        if (instance == null) instance = new Settings();
        return instance;
    }

    public static String get(String key) {
        return Settings.getInstance().props.getProperty(key);
    }

    public static void set(String key, String value) {
        Settings.getInstance().props.setProperty(key, value);

        try {
            Settings.getInstance().props.store(new FileOutputStream("config.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
