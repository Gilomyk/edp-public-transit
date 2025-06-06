package com.example.projectedp.config;

import com.example.projectedp.MainApp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = MainApp.class.getResourceAsStream("config.properties")) {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            // Jeśli plik nie zostanie znaleziony, aplikacja może się nie uruchomić poprawnie.
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
