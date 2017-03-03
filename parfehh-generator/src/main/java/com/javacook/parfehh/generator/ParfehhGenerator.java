package com.javacook.parfehh.generator;

import com.javacook.integra.template.bean.*;
import com.javacook.integra.template.bean.TestCase_jgt.foreachEffect.Effect_jit;
import com.javacook.integra.template.bean.TestCase_jgt.foreachTestStep.TestStep_jit;
import com.javacook.integra.template.bean.TestCase_jgt.foreachTestStep.TestStep_jit.foreachAction.Action_jit;
import com.javacook.parfehh.domain.*;
import com.javacook.parfehh.domain.util.DynamicList;
import com.javacook.parfehh.util.string.Namer;
import com.jiowa.codegen.config.JiowaCodeGenConfig;
import com.jiowa.codegen.generator.AbstractGenerator;

import java.util.logging.Logger;

import static com.javacook.parfehh.util.string.StringUtils.escapeJavaDoc;

public class ParfehhGenerator extends AbstractGenerator {

    public static final String PACKAGE_NAME_PROPERTY_KEY = "jiowa.codegen.generator.output.package";
    public static final String TEST_SERIES_BASE_CLASS_PROPERTY_KEY = "jiowa.codegen.generator.baseclass.series";
    public static final String TEST_BRIDGE_BASE_CLASS_PROPERTY_KEY = "jiowa.codegen.generator.baseclass.bridge";
    protected TestSeries testSeries;

    public final String packageName;
    public final String testSeriesBaseClass;
    public final String testBridgeBaseClass;

    /*------------------------------------------------------------------------*\
     * Constructor:                                                           *
    \*------------------------------------------------------------------------*/

    public ParfehhGenerator(TestSeries testSeries, JiowaCodeGenConfig config) {
        super(config);
        packageName = config.getProperty(PACKAGE_NAME_PROPERTY_KEY);
        testSeriesBaseClass = config.getProperty(TEST_SERIES_BASE_CLASS_PROPERTY_KEY, true);
        testBridgeBaseClass = config.getProperty(TEST_BRIDGE_BASE_CLASS_PROPERTY_KEY, true);
        this.testSeries = testSeries;
    }

    /*------------------------------------------------------------------------*\
     * Public Methods:                                                        *
    \*------------------------------------------------------------------------*/

    private static void cleanTestSeries(TestSeries testSeries) {
        DynamicList<TestCase> newCases = new DynamicList<>();
        for (TestCase testCase : testSeries.testCases) {
            if (testCase.title != null) newCases.add(testCase);
        }
        testSeries.testCases = newCases;
    }

    private static void assignUniqueNames(TestSeries testSeries) {

        testSeries.uniqueName = new ParfehhNamer().createUniqueName(testSeries.title);
        log("testSeries.uniqueName", testSeries.uniqueName);

        final Namer preConditionNamer = new ParfehhNamer();
        for (PreCondition preCondition : testSeries.getAllPreConditions(true)) {
            if (preCondition == null) continue;
            preCondition.uniqueName = preConditionNamer.createUniqueName(preCondition.description);
            log("preCondition.uniqueName", preCondition.uniqueName);
        }

        final Namer postConditionNamer = new ParfehhNamer();
        for (PostCondition postCondition : testSeries.getAllPostConditions(true)) {
            if (postCondition == null) continue;
            postCondition.uniqueName = postConditionNamer.createUniqueName(postCondition.description);
            log("postCondition.uniqueName", postCondition.uniqueName);
        }

        final Namer testCaseNamer = new ParfehhNamer();
        final Namer actionNamer = new ParfehhNamer();
        final Namer effectNamer = new ParfehhNamer();

        for (TestCase testCase : testSeries.testCases) {
            if (testCase == null) continue;
            testCase.uniqueName = testCaseNamer.createUniqueName(testCase.title);
            log("testCase.uniqueName", testCase.uniqueName);

            for (TestStep testStep : testCase.testSteps) {
                if (testStep == null) continue;
                for (Action action : testStep.actions) {
                    if (action == null) continue;
                    action.uniqueName = actionNamer.createUniqueName(action.description);
                    log("action.uniqueName", action.uniqueName);
                }
            }
            for (Effect<String> effect : testCase.effects) {
                effect.uniqueName = effectNamer.createUniqueName(effect.description);
                log("effect.uniqueName", effect.uniqueName);
            }
        }
    }

    private static void log(String mess, Object nullOrNot) {
        final Logger LOG = Logger.getLogger("ParfehhGenerator.assignUniqueNames");
        if (nullOrNot == null) LOG.warning(mess + " = " + nullOrNot);
        else LOG.fine(mess + " = " + nullOrNot);
    }


    @Override
    public void generate() {
        cleanTestSeries(testSeries);
        assignUniqueNames(testSeries);
        try {
            ConsistencyCheck.check(testSeries);
        }
        catch (IllegalArgumentException e) {
            final Logger LOG = Logger.getLogger("ConsistencyCheck.check");
            LOG.warning(e.getMessage() + " Generation must be aborded!");
            return;
        }

        // TestBridge:
        //
        String testBridgeName = "ITTestBridge_" + testSeries.uniqueName;
        String testClassName  = "ITTestSeries_" + testSeries.uniqueName;

        TestBridge_jgt testBridge_jgt = new TestBridge_jgt()
                .setPackageName(packageName)
                .setTestBridgeTitle(testBridgeName)
                .setTestBridgeComment(escapeJavaDoc(testSeries.description))
                .setTestSeriesTitle(testClassName);

        if (testBridgeBaseClass == null || testSeriesBaseClass.isEmpty()) {
            testBridge_jgt.setTestBridgeExtends("");
        }
        else {
            testBridge_jgt.setTestBridgeExtends("extends " + testBridgeBaseClass);
        }


        for (PreCondition preCondition : testSeries.getAllPreConditions(false)) {
            PreCondition_jgt preCondition_jgt = new PreCondition_jgt()
                    .setPreConditionMethodName(preCondition.uniqueName)
                    .setPreConditionMethodComment(escapeJavaDoc(preCondition.description))
                    .setPreConditionArgument(generateArgPair(preCondition.parameter))
                    .setPreConditionJavaDoc(preCondition.parameter == null?
                            "" : "@param parameter additional configuration parameter")
                    .setPreConditionProtectedRegion(preCondition.id);
            testBridge_jgt.foreachPreCondition.append(preCondition_jgt);
        }

        for (Action action : testSeries.getAllActions(false)) {
            Action_jgt action_jgt = new Action_jgt()
                    .setActionMethodName(action.uniqueName)
                    .setActionMethodComment(escapeJavaDoc(action.description))
                    .setActionArgument(generateArgPair(action.parameter))
                    .setActionArgumentJavaDoc(action.parameter == null?
                            "" : "@param parameter input value")
                    .setActionProtectedRegion(action.id);
            testBridge_jgt.foreachAction.append(action_jgt);
        }

        for (Effect effect : testSeries.getAllEffects(false)) {
            Effect_jgt action_jgt = new Effect_jgt()
                    .setEffectMethodName(effect.uniqueName)
                    .setEffectMethodComment(escapeJavaDoc(effect.description))
                    .setEffectArgument(generateArgPair(effect.parameter))
                    .setEffectArgumentJavaDoc(effect.parameter == null?
                            "" : "@param parameter expected result")
                    .setEffectProtectedRegion(effect.id);
            testBridge_jgt.foreachEffect.append(action_jgt);
        }

        for (PostCondition postCondition : testSeries.getAllPostConditions(false)) {
            PostCondition_jgt postCondition_jgt = new PostCondition_jgt()
                    .setPostConditionMethodName(postCondition.uniqueName)
                    .setPostConditionArgument(generateArgPair(postCondition.parameter))
                    .setPostConditionJavaDoc(postCondition.parameter == null?
                            "" : "@param parameter additional configuration parameter")
                    .setPostConditionMethodComment(escapeJavaDoc(postCondition.description))
                    .setPostConditionProtectedRegion(postCondition.id);
            testBridge_jgt.foreachPostCondition.append(postCondition_jgt);
        }


        // Test-Series:
        //
        TestSeries_jgt testSeries_jgt = new TestSeries_jgt()
                .setPackageName(packageName)
                .setTestSeriesTitle(testClassName)
                .setTestSeriesComment(escapeJavaDoc(testSeries.description));

        if (testSeriesBaseClass == null || testSeriesBaseClass.isEmpty()) {
            testSeries_jgt.setTestSeriesExtends("");
        }
        else {
            testSeries_jgt.setTestSeriesExtends("extends " + testSeriesBaseClass);
        }

        for (TestCase testCase : testSeries.testCases) {

            TestCase_jgt testCase_jgt = new TestCase_jgt()
                    .setTestCaseMethodName(testCase.uniqueName)
                    .setTestCaseMethodComment(escapeJavaDoc(testCase.description))
                    .setTestBridgeTitle(testBridgeName)
                    .setDisable(testCase.uniqueName.startsWith("_")? "// " : "");

            for (PreCondition preCondition : testCase.preConditions) {
                testCase_jgt.foreachPreCondition.append(
                        new TestCase_jgt.foreachPreCondition.PreCondition_jit()
                                .setPreConditionArgument(generateArgStr(preCondition.parameter))
                                .setPreConditionMethodName(preCondition.uniqueName));
            }

            for (PostCondition postCondition : testCase.postConditions) {
                testCase_jgt.foreachPostCondition.append(
                        new TestCase_jgt.foreachPostCondition.PostCondition_jit()
                                .setPostConditionArgument(generateArgStr(postCondition.parameter))
                                .setPostConditionMethodName(postCondition.uniqueName));
            }

            for (TestStep testStep : testCase.testSteps)
            {
                TestStep_jit testStep_jit = new TestStep_jit()
                        .setTestStepDescription(testStep.title);
                testCase_jgt.foreachTestStep.append(testStep_jit);
                for (Action action : testStep.actions) {
                    Action_jit action_jit = new Action_jit()
                            .setActionMethodName(action.uniqueName)
                            .setActionMethodArg(generateArgStr(action.parameter));
                    testStep_jit.foreachAction.append(action_jit);
                }
            }
            for (Effect effect : testCase.effects) {
                Effect_jit action_jit = new Effect_jit()
                        .setEffectMethodName(effect.uniqueName)
                        .setEffectMethodArg(generateArgStr(effect.parameter));
                testCase_jgt.foreachEffect.append(action_jit);
            }
            testSeries_jgt.foreachTestCase.append(testCase_jgt);
        }

        String outputPath = testSeries_jgt.getPackageName().replace('.', '/');
        updateSourceFile(outputPath + "/" + testClassName + ".java", testSeries_jgt.toString());
        updateSourceFile(outputPath + "/" + testBridgeName + ".java", testBridge_jgt.toString());
    }


    private String generateArgPair(Object parameter) {
        return parameter == null? "" : parameter.getClass().getSimpleName() + " parameter";
    }

    /**
     * Converts <code>parameter</code> to a String representation to put in into a method call. Example:
     * an integer 5 -> 5, an String "Hello" -> "Hello", a
     * @param parameter an Object got from an Excel cell.
     * @return A String representation of parameter
     */
    private String generateArgStr(Object parameter) {
        if (parameter == null) return "";

        if (parameter instanceof Long) {
            return parameter.toString() + "L";
        }
        else if (parameter instanceof Number) {
            return parameter.toString();
        }
        else {
            return '"' + parameter.toString() + '"';
        }
    }

}
