package com.javacook.parfehh.generator;

import com.jiowa.codegen.config.JiowaCodeGenConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by vollmer on 11.11.16.
 */
public class IntegraCodeGenConfig extends JiowaCodeGenConfig {

    public IntegraCodeGenConfig(File file) throws IOException {
        addPropertiesFromFile(file);
    }

    public IntegraCodeGenConfig(String... resources) {
        super(resources);
    }

    public IntegraCodeGenConfig(File file, String... resourcesForDefaults) throws IOException {
        this(resourcesForDefaults);
        addPropertiesFromFile(file);
    }

    public IntegraCodeGenConfig(Properties properties) throws IOException {
        PROPERTIES = properties;
    }

    protected void addPropertiesFromFile(File file) throws IOException {
        PROPERTIES.load(new FileReader(file));
    }

}
