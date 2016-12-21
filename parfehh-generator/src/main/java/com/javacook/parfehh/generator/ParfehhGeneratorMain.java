package com.javacook.parfehh.generator;

import com.javacook.easyexcelaccess.ExcelCoordinateAccessor;
import com.javacook.parfehh.domain.TestSeries;
import com.javacook.parfehh.util.logging.LoggingUtils;
import com.javacook.parfehh.util.properties.JavaCookProperties;
import com.javacook.parfehh.util.properties.LoadConfigUtil;
import com.jiowa.codegen.JiowaCodeGeneratorEngine;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class ParfehhGeneratorMain {

    public static final Logger LOG = Logger.getLogger(ParfehhGeneratorMain.class.getSimpleName());
    public static final String PROPERTY_KEY_INPUT_FILE_NAME   = "excel.file";
    public static final String PROPERTY_KEY_INPUT_FILE_SHEETS = "excel.sheets";
    public static final String PROPERTY_KEY_TEST_CASE_READER  = "testCaseReader";

    /**
     *
     * @param arguments
     * @throws IOException
     */
    public static void main(String[] arguments) throws IOException {
        try {
            JavaCookProperties configProperties = LoadConfigUtil.process(arguments);
            LoggingUtils.configureLogging(configProperties);
            generate(configProperties);
        }
        catch (IllegalArgumentException | IOException e) {
            LOG.warning(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }// main


    /**
     * Complete generation
     * @param finalProperties the accumulated properties
     * @throws IOException if there are problems reading the excel file
     */
    private static void generate(JavaCookProperties finalProperties) throws IOException {
        LOG.info("======================== Start of INtegraion TEst GeneRAtion ============================");
        final String excelFileName = getExcelFileName(finalProperties);
        final String excelSheets = getSheetNumbers(finalProperties);

        // Do the generation for all sheets...
        for (int excelSheet : parseIntList(excelSheets)) {
            generateForOneSheet(finalProperties, excelFileName, excelSheet);
        } // for
        LOG.info("======================== End of INtegraion TEst GeneRAtion ===============================");
    }

    /**
     * Generation for a single Excel sheet
     * @param finalProperties the accumulated properties
     * @param excelFileName file or resource name (resource name not starting with /)
     * @param excelSheet sheet no starting with 0
     * @throws IOException
     */
    private static void generateForOneSheet(JavaCookProperties finalProperties,
                                            String excelFileName,
                                            int excelSheet) throws IOException {
        LOG.info("--- Processing sheet no " + excelSheet);
        final ExcelCoordinateAccessor excelAccessor;
        final File inputFile = new File(excelFileName);
        if (inputFile.exists()) {
            LOG.info("Loading input file from: " + inputFile.getAbsolutePath());
            excelAccessor = new ExcelCoordinateAccessor(inputFile, excelSheet);
        }
        else {
            LOG.info("The input file does not exist at: " + inputFile.getAbsolutePath());
            LOG.info("Try to load '" + excelFileName + "' as resource ...");
            excelAccessor = new ExcelCoordinateAccessor(excelFileName, excelSheet);
            LOG.info("... input file successfully loaded.");
        }

        ParfehhCodeGenConfig config = new ParfehhCodeGenConfig(finalProperties);

        final TestCaseReader testCaseReader = createTestCaseReader(excelAccessor, config);
        if (testCaseReader != null) {
            final TestSeries testSeries = testCaseReader.createTestSeries();
            ParfehhGenerator parfehhGenerator = new ParfehhGenerator(testSeries, config);
            JiowaCodeGeneratorEngine engine = new JiowaCodeGeneratorEngine(parfehhGenerator);
            engine.start();
        }
        LOG.info("--- End of processing sheet no " + excelSheet);
    }

    private static TestCaseReader createTestCaseReader(ExcelCoordinateAccessor excelAccessor, ParfehhCodeGenConfig config) {
        final TestCaseReader testCaseReader;
        final String testCaseReaderClassName = config.getProperty(PROPERTY_KEY_TEST_CASE_READER, true);
        if (testCaseReaderClassName == null) {
            LOG.info("No custom test case reader specified, using default reader...");
            testCaseReader = new ExcelToTestDomain(excelAccessor);
        }
        else {
            LOG.info("Using custom test case reader '" + testCaseReaderClassName + "'...");
            try {
                final Class<TestCaseReader> aClass = (Class<TestCaseReader>) Class.forName(testCaseReaderClassName);
                testCaseReader = aClass.getConstructor(ExcelCoordinateAccessor.class).newInstance(excelAccessor);
            } catch (ReflectiveOperationException e) {
                LOG.severe("Problems when creating class '" + testCaseReaderClassName + "'" + e);
                return null;
            }
        }
        return testCaseReader;
    }


    /*---------------------------------------------------------*\
     * Reading data from the properties                        *
    \*---------------------------------------------------------*/

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
