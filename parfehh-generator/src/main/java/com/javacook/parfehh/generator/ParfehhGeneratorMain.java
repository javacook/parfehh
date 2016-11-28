package com.javacook.parfehh.generator;

import com.javacook.parfehh.domain.TestSeries;
import com.javacook.easyexcelaccess.ExcelCoordinateAccessor;
import com.javacook.parfehh.util.util.JavaCookProperties;
import com.jiowa.codegen.JiowaCodeGeneratorEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.stream.Collectors;


public class ParfehhGeneratorMain {

    public static final Logger log = Logger.getLogger("ParfehhGeneratorMain");
    public static final String PROPERTY_KEY_CONFIG_FILE_NAME  = "config";
    public static final String PROPERTY_KEY_LOGGING_FILE_NAME = "logging";
    public static final String PROPERTY_KEY_INPUT_FILE_NAME   = "excel.file";
    public static final String PROPERTY_KEY_INPUT_FILE_SHEETS = "excel.sheets";
    public static final String DEFAULT_CONFIG_FILE_NAME       = "config.properties";
    public static final String DEFAULT_LOGGING_FILE_NAME      = "logging.properties";
    public static final String BASE_CONFIG_RESOURCE_NAME      = "jiowa.codegen.properties";

    /**
     *
     * @param arguments
     * @throws IOException
     */
    public static void main(String[] arguments) throws IOException {
        try {
            JavaCookProperties argProperties   = new JavaCookProperties();
            JavaCookProperties configProperties = new JavaCookProperties();

            // Loading basic config properties from resource jiowa.codegen.properties
            configProperties.loadFromResource(false, BASE_CONFIG_RESOURCE_NAME);

            // Loading (and perhaps overwriting) properties from the argument list "arguments"
            argProperties.loadFromKeyValuePairs(true, arguments);

            // Check whether there is an property "config=..." in the argument list
            final String configPropertiesFileName = getConfigFileName(argProperties);

            // Adds properties from the config file into the object "configProperties"
            loadConfigProperties(configProperties, configPropertiesFileName);

            // Adds and overwrites the final properties with them of the argument list
            configProperties.addProperties(true, argProperties);

            configureLogging(configProperties);

            generate(configProperties);
        }
        catch (IllegalArgumentException e) {
            log.warning(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }// main



    private static void loadConfigProperties(JavaCookProperties finalProperties, String configPropertiesFileName) throws IOException {
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
                throw new IllegalArgumentException(
                        "Please specify the config file using '" + DEFAULT_CONFIG_FILE_NAME +
                                "=...' as command line argument or define it in the config properties.");
            }
        }
    }

    private static void configureLogging(JavaCookProperties finalProperties) throws IOException {
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


    /**
     * Complete generation
     * @param finalProperties the accumulated properties
     * @throws IOException if there are problems reading the excel file
     */
    private static void generate(JavaCookProperties finalProperties) throws IOException {
        log.info("======================== Start of INtegraion TEst GeneRAtion ============================");
        final String excelFileName = getExcelFileName(finalProperties);
        final String excelSheets = getSheetNumbers(finalProperties);

        // Do the generation for all sheets...
        for (int excelSheet : parseIntList(excelSheets)) {
            generateForOneSheet(finalProperties, excelFileName, excelSheet);
        } // for
        log.info("======================== End of INtegraion TEst GeneRAtion ===============================");
    }

    /**
     * Generation for a single Excel sheet
     * @param finalProperties the accumulated properties
     * @param excelFileName file or resource name (resource name not starting with /)
     * @param excelSheet sheet no starting with 1
     * @throws IOException
     */
    private static void generateForOneSheet(JavaCookProperties finalProperties,
                                            String excelFileName,
                                            int excelSheet) throws IOException {
        log.info("--- Processing sheet no " + excelSheet);
        final ExcelCoordinateAccessor excelAccessor;
        final File inputFile = new File(excelFileName);
        if (inputFile.exists()) {
            log.info("Loading input file from: " + inputFile.getAbsolutePath());
            excelAccessor = new ExcelCoordinateAccessor(inputFile, excelSheet);
        }
        else {
            log.info("The input file does not exist at: " + inputFile.getAbsolutePath());
            log.info("Try to load '" + excelFileName + "' as resource ...");
            excelAccessor = new ExcelCoordinateAccessor(excelFileName, excelSheet);
            log.info("... input file successfully loaded.");
        }

        ParfehhCodeGenConfig config = new ParfehhCodeGenConfig(finalProperties);
        TestSeries testSeries = new ExcelToTestDomain(excelAccessor).createTestSeries();
        ParfehhGenerator parfehhGenerator = new ParfehhGenerator(testSeries, config);
        JiowaCodeGeneratorEngine engine = new JiowaCodeGeneratorEngine(parfehhGenerator);
        engine.start();
        log.info("--- End of processing sheet no " + excelSheet);
    }


    /*---------------------------------------------------------*\
     * Reading data from the properties                        *
    \*---------------------------------------------------------*/

    private static String getLoggingFileName(JavaCookProperties properties) {
        return properties.getProperty(PROPERTY_KEY_LOGGING_FILE_NAME, DEFAULT_LOGGING_FILE_NAME);
    }

    private static String getExcelFileName(JavaCookProperties properties) {
        final String inputFileName = properties.getProperty(PROPERTY_KEY_INPUT_FILE_NAME);
        if (inputFileName == null) {
            throw new IllegalArgumentException(
                    "Please specify the input file using '" + PROPERTY_KEY_INPUT_FILE_NAME +
                    "=...' as command line argument or define it in the config properties.");
        }
        return inputFileName;
    }

    private static String getSheetNumbers(JavaCookProperties properties) {
        return properties.getProperty(PROPERTY_KEY_INPUT_FILE_SHEETS, "0");
    }

    private static String getConfigFileName(JavaCookProperties properties) {
        return properties.getProperty(PROPERTY_KEY_CONFIG_FILE_NAME, DEFAULT_CONFIG_FILE_NAME);
    }

    /*---------------------------------------------------------*\
     * Utils                                                   *
    \*---------------------------------------------------------*/

    /**
     * Extractes a list of numbers out of the input <code>str</code>.
     * @param str like "1,3, 4"
     * @return [1,3,4]
     */
    private static List<Integer> parseIntList(String str) {
        try {
            return Arrays.asList(str.split(" *, *"))
                    .stream()
                    .map(Integer::parseInt)
                    .filter(t -> t > 0)
                    // .map(t -> t - 1) // Excel sheets should start with 1
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalArgumentException("Format error in '" + str + "'. Please use a comma seprated list of numbers.");
        }
    }

}
