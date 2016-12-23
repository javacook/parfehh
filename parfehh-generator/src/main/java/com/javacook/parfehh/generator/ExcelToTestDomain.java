package com.javacook.parfehh.generator;

import com.javacook.easyexcelaccess.ExcelCoordinate;
import com.javacook.easyexcelaccess.ExcelCoordinateAccessor;
import com.javacook.easyexcelaccess.ExcelCoordinateSequencer;
import com.javacook.parfehh.domain.*;

import java.util.logging.Logger;

/**
 * Reads the test cases (data) from an Excel file and stores them into the domain testdatamodel
 * (look also at the project integrationtest-domain). This is a class that must be indiviually
 * written for your custom Excel format.
 * Hint: This is a default implementation and can be replaced with a custom one by defining it
 * in the <code>config.properties</code> using the key testCaseReader=<qualified class name>.
 * An Example of the usage can be found in <code>ExcelToTestDomainMain</code>.
 */
public class ExcelToTestDomain implements TestCaseReader {

    final ExcelCoordinateAccessor excel;
    final TestSeries testSeries; // The (eventuel) result of method createTestSeries

    public static final ExcelCoordinate COORD_TEST_SERIES_TITLE = new ExcelCoordinate(1,1);
    public static final ExcelCoordinate COORD_TEST_SERIES_DESCR = new ExcelCoordinate(1,2);
    public static final ExcelCoordinate COORD_START_TEST_CASE_TITLE = new ExcelCoordinate("F", 1);
    public static final ExcelCoordinate COORD_START_TEST_CASE_DESCR = new ExcelCoordinate("F", 2);
    public static final ExcelCoordinate COORD_START_TEST_CASE_PRIO = new ExcelCoordinate("F", 3);
    public static final String COL_ALL_DESCR = "D";
    public static final String LABEL_VORBEDINGUNGEN  = "Vorbedingungen";
    public static final String LABEL_NACHBEDINGUNGEN = "Nachbedingungen";
    public static final String LABEL_TESTSCHRITT_1   = "Eingabe der Funktionsparameter";
    public static final String LABEL_TESTSCHRITT_2   = "Aufruf des Service";
    public static final String LABEL_WIRKUNGEN       = "Wirkungen";

    /**
     * Constructor getting an object to access the Excel data.
     * @param excelCoordinateAccessor
     */
    public ExcelToTestDomain(ExcelCoordinateAccessor excelCoordinateAccessor) {
        this.testSeries = new TestSeries();
        this.excel = excelCoordinateAccessor;
    }

    /**
     * Creates the domain testdatamodel of the test cases
     * @return test series
     */
    public TestSeries createTestSeries() {
        testSeries.title = excel.readString(COORD_TEST_SERIES_TITLE);
        testSeries.description = excel.readString(COORD_TEST_SERIES_DESCR);
        assignTestCaseTitles();
        assignTestCaseDescriptions();
        assignTestCasePriorities();
        assignPreConditions();
        assignPostConditions();
        assignTestCaseActions();
        assignEffects();
        return testSeries;
    }


    void assignTestCaseTitles() {
        new ExcelCoordinateSequencer()
                .forCoord(COORD_START_TEST_CASE_TITLE).toColMax().horStep(2)
                .stopWhen(coord -> excel.isEmpty(coord))
                .forEach( (coord, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    testCase.title = excel.readString(coord);
                    logCase("assignTestCaseTitles", coord,"testCase.title", testCase.title);
                });
    }


    void assignTestCaseDescriptions() {
        new ExcelCoordinateSequencer()
                .forCoord(COORD_START_TEST_CASE_DESCR).toColMax().horStep(2)
                .stopWhen(coord -> excel.isEmpty(coord.setRow(COORD_START_TEST_CASE_TITLE.row())))
                .forEach( (coord, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    testCase.description = excel.readString(coord);
                    logCase("assignTestCaseDescriptions", coord, "testCase.description", testCase.description);
                });
    }


    void assignTestCasePriorities() {
        new ExcelCoordinateSequencer()
                .forCoord(COORD_START_TEST_CASE_PRIO).toColMax().horStep(2)
                .stopWhen(coordHor -> excel.isEmpty(coordHor.setRow(COORD_START_TEST_CASE_TITLE.row())))
                .forEach( (coordHor, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    String priority = excel.readString(coordHor);
                    switch (priority) {
                        case "high":
                            testCase.priority = 2;
                            break;
                        case "medium":
                            testCase.priority = 1;
                            break;
                        case "low":
                            testCase.priority = 0;
                            break;
                        default:
                            throw new IllegalArgumentException("Unexpected value for priority: " + priority);
                    }
                    logCase("assignTestCasePriorities", coordHor, "testCase.priority", testCase.priority);
                });
    }// assignTestCasePriorities


    void assignPreConditions() {
        new ExcelCoordinateSequencer()
                .forRow(excel.find(LABEL_VORBEDINGUNGEN).row()+1)
                .fromCol(COORD_START_TEST_CASE_TITLE.col()).toColMax().horStep(2)
                .stopWhen(coordHor -> excel.isEmpty(coordHor.setRow(COORD_START_TEST_CASE_TITLE.row())))
                .forEach( (coordHor, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);

                    new ExcelCoordinateSequencer()
                            .forCol(COL_ALL_DESCR).fromRow(coordHor.row()).toRowMax().enter()
                            .from(coordHor).toRowMax().width(1)
                            .stopWhenPair( (coordDescr, coordShopping) -> excel.isEmpty(coordDescr))
                            .forEachPair((coordDescr, coordsShopping, j) -> {
                                String xOrNot = excel.readString(coordsShopping);
                                if ("x".equals(xOrNot)) {
                                    PreCondition preCondition = testCase.preConditions.getNextNew(PreCondition::new);
                                    ExcelCoordinate coordParameter = coordsShopping.incCol();
                                    ExcelCoordinate coordId = coordDescr.decCol();
                                    preCondition.description = excel.readString(coordDescr);
                                    preCondition.parameter = excel.readString(coordParameter);
                                    preCondition.id = excel.readString(coordId);
                                    logCase("assignPreConditions", coordId, "preCondition.id", preCondition.id);
                                    logFine("assignPreConditions", coordParameter, "preCondition.parameter", preCondition.parameter);
                                    logCase("assignPreConditions", coordDescr, "preCondition.description", preCondition.description);
                                }
                            });
                });
    }// assignPreConditions


    void assignPostConditions() {
        new ExcelCoordinateSequencer()
                .forRow(excel.find(LABEL_NACHBEDINGUNGEN).row()+1)
                .fromCol(COORD_START_TEST_CASE_TITLE.col()).toColMax().horStep(2)
                .stopWhen(coordHor -> excel.isEmpty(coordHor.setRow(COORD_START_TEST_CASE_TITLE.row())))
                .forEach( (coordHor, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    new ExcelCoordinateSequencer()
                            .forCol(COL_ALL_DESCR).fromRow(coordHor.row()).toRowMax().enter()
                            .from(coordHor).height(3).width(1)
                            .stopWhenPair( (coordDescr, coordShopping) -> excel.isEmpty(coordDescr))
                            .forEachPair((coordDescr, coordsShopping, j) -> {
                                String xOrNot = excel.readString(coordsShopping);
                                if ("x".equals(xOrNot)) {
                                    PostCondition postCondition = testCase.postConditions.getNextNew(PostCondition::new);
                                    ExcelCoordinate coordParameter = coordsShopping.incCol();
                                    ExcelCoordinate coordId = coordDescr.decCol();
                                    postCondition.description = excel.readString(coordDescr);
                                    postCondition.parameter = excel.readString(coordParameter);
                                    postCondition.id = excel.readString(coordId);
                                    logFine("assignPreConditions", coordParameter, "preCondition.parameter", postCondition.parameter);
                                    logCase("assignPostConditions", coordDescr, "postCondition.description", postCondition.description);
                                    logCase("assignTestCaseActions", coordId, "postCondition.id", postCondition.id);
                                }
                            });
                });
    }// assignPostConditions


    void assignTestCaseActions() {
        new ExcelCoordinateSequencer()
                .forRow(excel.find(LABEL_TESTSCHRITT_1).row()+1)
                .fromCol(COORD_START_TEST_CASE_TITLE.col()).toColMax().horStep(2).enter()
                .forRow(excel.find(LABEL_TESTSCHRITT_2).row()+1)
                .fromCol(COORD_START_TEST_CASE_TITLE.col()).toColMax().horStep(2)
                .stopWhenPair( (coordHor1, coordHor2) -> excel.isEmpty(coordHor1.setRow(COORD_START_TEST_CASE_TITLE.row())))
                .forEachPair( (coordHor1, coordHor2, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    TestStep testStep1 = testCase.testSteps.getOrNew(0, TestStep::new);
                    testStep1.title = LABEL_TESTSCHRITT_1;
                    TestStep testStep2 = testCase.testSteps.getOrNew(1, TestStep::new);
                    testStep2.title = LABEL_TESTSCHRITT_2;

                    new ExcelCoordinateSequencer()
                        .forCol(COL_ALL_DESCR).fromRow(coordHor1.row()).toRowMax().enter()
                        .from(coordHor1).toRowMax().width(1)
                        .stopWhenPair((coordDescr, coordsShopping) -> excel.isEmpty(coordDescr))
                        .forEachPair((coordDescr, coordsShopping) -> {
                            String xOrNot = excel.readString(coordsShopping);
                            if ("x".equals(xOrNot)) {
                                Action<Object> action = testStep1.actions.getNextNew(Action::new);
                                ExcelCoordinate coordParameter = coordsShopping.addCol(1);
                                ExcelCoordinate coordId = coordDescr.decCol();
                                action.parameter = excel.read(coordParameter);
                                action.description = excel.readString(coordDescr);
                                action.id = excel.readString(coordId);
                                logCase("assignTestCaseActions", coordId, "action.id", action.id);
                                logFine("assignTestCaseActions", coordParameter, "action.parameter", action.parameter);
                                logCase("assignTestCaseActions", coordDescr, "action.description", action.description);
                            }
                        });
                    new ExcelCoordinateSequencer()
                            .forCol(COL_ALL_DESCR).fromRow(coordHor2.row()).toRowMax().enter()
                            .from(coordHor2).toRowMax().width(1)
                            .stopWhenPair((coordDescr, coordsShopping) -> excel.isEmpty(coordDescr))
                            .forEachPair((coordDescr, coordsShopping) -> {
                                String xOrNot = excel.readString(coordsShopping);
                                if ("x".equals(xOrNot)) {
                                    Action<Object> action = testStep2.actions.getNextNew(Action::new);
                                    ExcelCoordinate coordTestData = coordsShopping.addCol(1);
                                    ExcelCoordinate coordId = coordDescr.decCol();
                                    action.parameter = excel.read(coordTestData);
                                    action.description = excel.readString(coordDescr);
                                    action.id = excel.readString(coordId);
                                    logCase("assignTestCaseActions", coordId, "action.id", action.id);
                                    logFine("assignTestCaseActions", coordTestData, "action.parameter", action.parameter);
                                    logCase("assignTestCaseActions", coordDescr, "action.description", action.description);
                                }
                            });
                    });
    }// assignTestCaseActions


    void assignEffects() {
        new ExcelCoordinateSequencer()
                .forRow(excel.find(LABEL_WIRKUNGEN).row()+1)
                .fromCol(COORD_START_TEST_CASE_TITLE.col()).toColMax().horStep(2)
                .stopWhen(coordHor -> excel.isEmpty(coordHor.setRow(COORD_START_TEST_CASE_TITLE.row())))
                .forEach( (coordHor, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    new ExcelCoordinateSequencer()
                            .forCol(COL_ALL_DESCR).fromRow(coordHor.row()).toRowMax().enter()
                            .from(coordHor).toRowMax().width(1)
                            .stopWhenPair( (coordDescr, coordsShopping) -> excel.isEmpty(coordDescr))
                            .forEachPair( (coordDescr, coordsShopping, j) -> {
                                String xOrNot = excel.readString(coordsShopping);
                                if ("x".equals(xOrNot)) {
                                    Effect<Object> effect = testCase.effects.getNextNew(Effect::new);
                                    ExcelCoordinate coordExpectedValue = coordsShopping.incCol();
                                    ExcelCoordinate coordId = coordDescr.decCol();
                                    effect.parameter = excel.read(coordExpectedValue);
                                    effect.id = excel.readString(coordId);
                                    effect.description = excel.readString(coordDescr);
                                    logFine("assignEffects", coordExpectedValue, "effect.parameter", effect.parameter);
                                    logCase("assignEffects", coordDescr, "effect.id", effect.id);
                                    logCase("assignEffects", coordId, "effect.description", effect.description);
                                }
                            });
                });
    }// assignEffects


    /*********************************************************\
     * Logging                                               *
    \*********************************************************/

    private static void logCase(String method, ExcelCoordinate coord, String mess, Object nullOrNot) {
        final Logger LOG = Logger.getLogger("ParfehhGenerator." + method);
        if (nullOrNot == null) {
            LOG.warning(coord + ": " + mess + " = " + nullOrNot);
        }
        else {
            LOG.fine(coord + ": " + mess + " = " + nullOrNot);
        }
    }

    private static void logFine(String method, ExcelCoordinate coord, String mess, Object nullOrNot) {
        final Logger LOG = Logger.getLogger("ParfehhGenerator." + method);
        LOG.fine(coord + ": " + mess + " = " + nullOrNot);
    }

}