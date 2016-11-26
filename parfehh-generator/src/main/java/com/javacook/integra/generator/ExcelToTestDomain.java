package com.javacook.integra.generator;

import com.javacook.easyexcelaccess.ExcelCoordinate;
import com.javacook.easyexcelaccess.ExcelCoordinateAccessor;
import com.javacook.easyexcelaccess.ExcelCoordinateSequencer;
import com.javacook.parfehh.domain.*;

import java.util.logging.Logger;
import java.io.IOException;

/**
 * Reads the test cases (data) from an Excel file and stores them into
 * the domain model (look also at the project integrationtest-domain).
 * This is a class that must be indiviually written for your custom
 * Excel format.
 */
public class ExcelToTestDomain {

    final static Logger LOG = Logger.getLogger("ExcelToTestDomain");
    final ExcelCoordinateAccessor excel;
    final TestSeries testSeries;

    /**
     * Constructor getting an object to access the Excel data.
     * @param excelCoordinateAccessor
     */
    public ExcelToTestDomain(ExcelCoordinateAccessor excelCoordinateAccessor) {
        this.testSeries = new TestSeries();
        this.excel = excelCoordinateAccessor;
    }

    /**
     * Creates the domain model of the test cases
     * @return test series
     */
    public TestSeries createTestSeries() {
        testSeries.title = excel.readString(new ExcelCoordinate(1, 1));
        testSeries.description = excel.readString(new ExcelCoordinate(1, 2));
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
                .forRow(1).fromCol("F").toColMax().horStep(2)
                .stopWhen(coord -> excel.isEmpty(coord))
                .forEach( (coord, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    testCase.title = excel.readString(coord);
                    logCase("assignTestCaseTitles", coord,"testCase.title", testCase.title);
                });
    }


    void assignTestCaseDescriptions() {
        new ExcelCoordinateSequencer()
                .forRow(2).fromCol("F").toColMax().horStep(2)
                .stopWhen(coord -> excel.isEmpty(coord.setRow(1)))
                .forEach( (coord, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    testCase.description = excel.readString(coord);
                    logCase("assignTestCaseDescriptions", coord, "testCase.description", testCase.description);
                });
    }


    void assignTestCasePriorities() {
        new ExcelCoordinateSequencer()
                .forRow(3).fromCol("F").toColMax().horStep(2)
                .stopWhen(coord -> excel.isEmpty(coord.setRow(1)))
                .forEach( (coord, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    String priority = excel.readString(coord);
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
                    logCase("assignTestCasePriorities", coord, "testCase.priority", testCase.priority);
                });
    }// assignTestCasePriorities


    void assignPreConditions() {
        new ExcelCoordinateSequencer()
                .forRow(excel.find("Vorbedingungen").row()+1)
                .fromCol("F").toColMax().horStep(2)
                .stopWhen(coordHori -> excel.isEmpty(coordHori.setRow(1)))
                .forEach( (coordHori, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);

                    new ExcelCoordinateSequencer()
                            .forCol("D").fromRow(coordHori.row()).toRowMax().enter()
                            .from(coordHori).toRowMax().width(1)
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
                .forRow(excel.find("Nachbedingungen").row()+1)
                .fromCol("F").toColMax().horStep(2)
                .stopWhen(coordHor -> excel.isEmpty(coordHor.setRow(1)))
                .forEach( (coordHor, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    new ExcelCoordinateSequencer()
                            .forCol("D").fromRow(coordHor.row()).toRowMax().enter()
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
        final String MARKER1 = "Eingabe der Funktionsparameter";
        final String MARKER2 = "Aufruf des Service";

        new ExcelCoordinateSequencer()
                .forRow(excel.find(MARKER1).row()+1)
                .fromCol("F").toColMax().horStep(2).enter()
                .forRow(excel.find(MARKER2).row()+1)
                .fromCol("F").toColMax().horStep(2)
                .stopWhenPair( (coordHor1, coordHor2) -> excel.isEmpty(coordHor1.setRow(1)))
                .forEachPair( (coordHor1, coordHor2, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    TestStep testStep1 = testCase.testSteps.getOrNew(0, TestStep::new);
                    testStep1.title = MARKER1;
                    TestStep testStep2 = testCase.testSteps.getOrNew(1, TestStep::new);
                    testStep2.title = MARKER2;

                    new ExcelCoordinateSequencer()
                        .forCol("D").fromRow(coordHor1.row()).toRowMax().enter()
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
                            .forCol("D").fromRow(coordHor2.row()).toRowMax().enter()
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
        final String MARKER1 = "Wirkungen";

        new ExcelCoordinateSequencer()
                .forRow(excel.find(MARKER1).row()+1)
                .fromCol("F").toColMax().horStep(2)
                .stopWhen(coordHor -> excel.isEmpty(coordHor.setRow(1)))
                .forEach( (coordHor, i) -> {
                    TestCase testCase = testSeries.testCases.getOrNew(i, TestCase::new);
                    new ExcelCoordinateSequencer()
                            .forCol("D").fromRow(coordHor.row()).toRowMax().enter()
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


    private static void logCase(String method, ExcelCoordinate coord, String mess, Object nullOrNot) {
        final Logger LOG = Logger.getLogger("IntegraGenerator." + method);
        if (nullOrNot == null) {
            LOG.warning(coord + ": " + mess + " = " + nullOrNot);
        }
        else {
            LOG.fine(coord + ": " + mess + " = " + nullOrNot);
        }
    }


    private static void logFine(String method, ExcelCoordinate coord, String mess, Object nullOrNot) {
        final Logger LOG = Logger.getLogger("IntegraGenerator." + method);
        LOG.fine(coord + ": " + mess + " = " + nullOrNot);
    }


    /*********************************************************\
     * main                                                  *
    \*********************************************************/

    public static void main(String[] args) throws IOException {
        ExcelCoordinateAccessor excelAccessor = new ExcelCoordinateAccessor("/MyTests.xls");
        ExcelToTestDomain factory = new ExcelToTestDomain(excelAccessor);
        factory.createTestSeries();
        System.out.println(factory.testSeries);
    }

}