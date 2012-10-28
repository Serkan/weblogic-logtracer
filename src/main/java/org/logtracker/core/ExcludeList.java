package org.logtracker.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public final class ExcludeList {

    public static ExcludeList instance = null;

    private String excludes;

    private ExcludeList() {
        InputStream excludeStream = getClass().getResourceAsStream(
                "excludelist");
        try {
            excludes = IOUtils.toString(excludeStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExcludeList getInstance() {
        if (instance == null) {
            instance = new ExcludeList();
        }
        return instance;
    }

    public boolean exist(String name) {
        return excludes.contains(name);
    }

}
