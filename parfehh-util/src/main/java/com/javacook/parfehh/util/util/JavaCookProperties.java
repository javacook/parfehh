package com.javacook.parfehh.util.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by vollmer on 14.11.16.
 */
public class JavaCookProperties extends Properties {

    public void loadFromResource(boolean overWrite, String resource) throws IOException {
        final InputStream is = ClassLoader.getSystemResourceAsStream(resource);
        if (is == null) {
            throw new IllegalArgumentException("The resource '" + resource + "' does not exist.");
        }
        loadFromInputStream(overWrite, is);
    }

    public void loadFromFile(boolean overWrite, File file) throws IOException {
        if (file == null) throw new IllegalArgumentException("Argument 'file' is null.");
        final InputStream is = new FileInputStream(file);
        loadFromInputStream(overWrite, is);
    }

    public void loadFromInputStream(boolean overWrite, InputStream is) throws IOException {
        Properties props = new Properties();
        props.load(is);
        addProperties(overWrite, props);
    }

    public void addProperties(boolean overWrite, Properties props) {
        for (Object key : props.keySet()) {
            if (overWrite || get(key) == null) {
                put(key, props.get(key));
            }
        }
    }

    public void loadFromKeyValuePairs(boolean overWrite, String... keyValuePairs) {
        if (keyValuePairs == null) return;
        for (String keyValuePair : keyValuePairs) {
            if (keyValuePair == null) continue;
            final String[] keyAndValue = keyValuePair.split("=");
            if (keyAndValue.length != 2) continue;
            final String key = keyAndValue[0];
            final String value = keyAndValue[1];
            if (overWrite || get(key) == null) {
                put(key, value);
            }
        }
    }

}
