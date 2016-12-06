package com.javacook.parfehh.util.logging;

import com.javacook.parfehh.util.properties.JavaCookProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * Created by vollmer on 02.12.16.
 */
public class LoggingUtils {

    public static final String PROPERTY_KEY_LOGGING_FILE_NAME = "logging";
    public static final String DEFAULT_LOGGING_FILE_NAME      = "logging.properties";

    public static void configureLogging(JavaCookProperties finalProperties) throws IOException {
        System.out.println("--- Configuring logging");
        final String loggingPropertiesFileName = getLoggingFileName(finalProperties);

        // Reading logging properties...
        //
        final File loggingPropertiesFile = new File(loggingPropertiesFileName);
        if (loggingPropertiesFile.exists()) {
            System.out.println("Loading logging properties from file: " + loggingPropertiesFile.getAbsolutePath());
            InputStream is = new FileInputStream(loggingPropertiesFile);
            LogManager.getLogManager().readConfiguration(is);
            System.out.println("... logging properties successfully read.");
        }
        else {
            System.out.println("A logging properties file could not be found at '" + loggingPropertiesFile.getAbsolutePath() +"'");
            System.out.println("Loading logging properties as resource: '" + loggingPropertiesFileName + "' ...");
            final InputStream is = ClassLoader.getSystemResourceAsStream(loggingPropertiesFileName);
            if (is == null) {
                System.out.println("... there is no resource: '" + loggingPropertiesFileName + "'; using defaults.");
            }
            else {
                LogManager.getLogManager().readConfiguration(is);
                System.out.println("... logging properties successfully read.");
            }
        }
        System.out.println("--- End of configuring logging");
    }

    private static String getLoggingFileName(JavaCookProperties properties) {
        return properties.getProperty(PROPERTY_KEY_LOGGING_FILE_NAME, DEFAULT_LOGGING_FILE_NAME);
    }

}
