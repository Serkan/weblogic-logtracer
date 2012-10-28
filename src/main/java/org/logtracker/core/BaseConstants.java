package org.logtracker.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class BaseConstants {

    public static final String DOMAIN_HOME = "DOMAIN_HOME";

    public static BaseConstants instance = null;

    private Properties props;

    private BaseConstants() {
        InputStream propsFile = getClass().getResourceAsStream(
                "tracer.properties");
        props = new Properties();
        try {
            props.load(propsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BaseConstants getInstance() {
        if (instance == null) {
            instance = new BaseConstants();
        }
        return instance;
    }

    public String get(String key) {
        return (String) props.get(key);
    }

}
