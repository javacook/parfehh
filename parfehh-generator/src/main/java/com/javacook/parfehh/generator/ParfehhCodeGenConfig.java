package com.javacook.parfehh.generator;

import com.jiowa.codegen.config.JiowaCodeGenConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by vollmer on 11.11.16.
 */
public class ParfehhCodeGenConfig extends JiowaCodeGenConfig {

    public ParfehhCodeGenConfig(File file) throws IOException {
        addPropertiesFromFile(file);
    }

    public ParfehhCodeGenConfig(String... resources) {
        super(resources);
    }

    public ParfehhCodeGenConfig(File file, String... resourcesForDefaults) throws IOException {
        this(resourcesForDefaults);
        addPropertiesFromFile(file);
    }

    public ParfehhCodeGenConfig(Properties properties) throws IOException {
        PROPERTIES = properties;
    }

    protected void addPropertiesFromFile(File file) throws IOException {
        PROPERTIES.load(new FileReader(file));
    }

}
