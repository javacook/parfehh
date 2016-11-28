package com.javacook.parfehh.util.util;

import java.io.File;
import java.io.IOException;

/**
 * Created by vollmer on 28.11.16.
 */
public class LoadConfigUtil {

    public static final String PROPERTY_KEY_CONFIG_FILE_NAME  = "config";
    public static final String DEFAULT_CONFIG_FILE_NAME       = "config.properties";
    public static final String BASE_CONFIG_RESOURCE_NAME      = "jiowa.codegen.properties";

    /**
     * @param commandLineArguments
     * @throws IOException
     */
    public static JavaCookProperties process(String[] commandLineArguments) throws Exception {
        try {
            JavaCookProperties argProperties   = new JavaCookProperties();
            JavaCookProperties configProperties = new JavaCookProperties();

            // Loading basic config properties from resource jiowa.codegen.properties
            configProperties.loadFromResource(false, BASE_CONFIG_RESOURCE_NAME);

            // Loading (and perhaps overwriting) properties from the argument list "arguments"
            argProperties.loadFromKeyValuePairs(true, commandLineArguments);

            // Check whether there is an property "config=..." in the argument list
            final String configPropertiesFileName = getConfigFileName(argProperties);

            // Adds properties from the config file into the object "configProperties"
            addConfigPropertiesFromFile(configProperties, configPropertiesFileName);

            // Adds and overwrites the final properties with them of the argument list
            configProperties.addProperties(true, argProperties);

            return configProperties;
        }
        catch (IllegalArgumentException | IOException e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }// main


    private static void addConfigPropertiesFromFile(
            JavaCookProperties finalProperties, String configPropertiesFileName) throws IOException {
        // try to load config file at configPropertiesFileName
        final File configPropertiesFile = new File(configPropertiesFileName);
        if (configPropertiesFile.exists()) {
            System.out.println("Loading config properties from file: " + configPropertiesFile.getAbsolutePath());
            finalProperties.loadFromFile(true, configPropertiesFile);
            System.out.println("... config properties successfully read.");
        }
        else {
            System.out.println("A config properties file could not be found at '" + configPropertiesFile.getAbsolutePath() +"'");
            System.out.println("Loading config properties as resource: '" + configPropertiesFileName + "' ...");
            try {
                finalProperties.loadFromResource(true, configPropertiesFileName);
                System.out.println("... config properties successfully read.");
            }
            catch (IllegalArgumentException e) {
                System.out.println("... there is no resource: '" + configPropertiesFileName + "'");
                throw new IllegalArgumentException("Please specify the config file using '" +
                        PROPERTY_KEY_CONFIG_FILE_NAME + "=...' as command line argument.");
            }
        }
    }

    private static String getConfigFileName(JavaCookProperties properties) {
        return properties.getProperty(PROPERTY_KEY_CONFIG_FILE_NAME, DEFAULT_CONFIG_FILE_NAME);
    }

}
