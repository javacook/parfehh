package com.javacook.parfehh.generator;

import com.javacook.parfehh.domain.*;
import com.javacook.parfehh.util.string.StringUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * Created by vollmer on 29.11.16.
 */
public class ConsistencyCheckTest {

    private ByteArrayOutputStream baosError;
    private ByteArrayOutputStream baosWarn;
    private PrintStream psError;
    private PrintStream psWarn;

    @Before
    public void before() {
        baosError = new ByteArrayOutputStream();
        psError = new PrintStream(baosError);
        System.setErr(psError);
        baosWarn = new ByteArrayOutputStream();
        psWarn = new PrintStream(baosWarn);
        System.setOut(psWarn);
    }

    @After
    public void after() {
        psError.close();
        psWarn.close();
        System.setErr(System.err);
        System.setOut(System.out);
    }

    private String getSystemErr() {
        return baosError.toString();
    }

    private String getSystemOut() {
        return baosWarn.toString();
    }

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void checkTestCaseTitle() throws Exception {
        ConsistencyCheck.checkTestCase(new TestCase());
        Assert.assertTrue(StringUtils.matches(getSystemErr(), ".*The title of .*? is null\\..*"));
        Assert.assertTrue(StringUtils.matches(getSystemOut(), ".*The description of .*? is null\\..*"));
    }


    //
    // Pre Condition
    //

    @Test
    public void checkTestCasePreConditionSameDescriptionTwice() throws Exception {
        final TestCase testCase = new TestCase();
        final PreCondition<String> preCondition1 = new PreCondition<>("PC001", "eins", "eins", null);
        final PreCondition<String> preCondition2 = new PreCondition<>("PC002", "eins", "eins", null);
        testCase.preConditions.addAll(Arrays.asList(preCondition1, preCondition2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
        Assert.assertTrue(StringUtils.matches(getSystemErr(), ".*The pre condition description 'eins' is used twice.*"));
    }

    @Test
    public void checkTestCasePreConditionSameIdTwice() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("The pre condition id 'PC001' is used twice.");
        final TestCase testCase = new TestCase();
        final PreCondition<String> preCondition1 = new PreCondition<>("PC001", "eins", "eins", null);
        final PreCondition<String> preCondition2 = new PreCondition<>("PC001", "zwei", "zwei", null);
        testCase.preConditions.addAll(Arrays.asList(preCondition1, preCondition2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
    }


    @Test
    public void checkTestCasePreConditionNegative() throws Exception {
        final TestCase testCase = new TestCase();
        final PreCondition<String> preCondition1 = new PreCondition<>("PC001", "eins", "eins", null);
        final PreCondition<String> preCondition2 = new PreCondition<>("PC001", "eins", "eins", null);
        testCase.preConditions.addAll(Arrays.asList(preCondition1, preCondition2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
        Assert.assertFalse(StringUtils.matches(getSystemErr(), ".*The pre condition description .*? is used twice.*"));
    }


    //
    // Action
    //

    @Test
    public void checkTestCaseActionSameDescriptionTwice() throws Exception {
        final TestCase testCase = new TestCase();
        final Action<String> action1 = new Action<>("PC001", "eins", "eins", null);
        final Action<String> action2 = new Action<>("PC002", "eins", "eins", null);
        final TestStep testStep = new TestStep();
        testStep.actions.addAll(Arrays.asList(action1, action2));
        testCase.testSteps.add(testStep);
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
        Assert.assertTrue(StringUtils.matches(getSystemErr(), ".*The action description 'eins' is used twice.*"));
    }

    @Test
    public void checkTestCaseActionSameIdTwice() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("The action id 'PC001' is used twice.");
        final TestCase testCase = new TestCase();
        final Action<String> action1 = new Action<>("PC001", "eins", "eins", null);
        final Action<String> action2 = new Action<>("PC001", "zwei", "zwei", null);
        final TestStep testStep = new TestStep();
        testStep.actions.addAll(Arrays.asList(action1, action2));
        testCase.testSteps.add(testStep);
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
    }


    @Test
    public void checkTestCaseActionNegative() throws Exception {
        final TestCase testCase = new TestCase();
        final Action<String> action1 = new Action<>("PC001", "eins", "eins", null);
        final Action<String> action2 = new Action<>("PC001", "eins", "eins", null);
        final TestStep testStep = new TestStep();
        testStep.actions.addAll(Arrays.asList(action1, action2));
        testCase.testSteps.add(testStep);
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
        Assert.assertFalse(StringUtils.matches(getSystemErr(), ".*The action .*? is used twice.*"));
    }


    //
    // Effect
    //

    @Test
    public void checkTestCaseEffectSameDescriptionTwice() throws Exception {
        final TestCase testCase = new TestCase();
        final Effect<String> effect1 = new Effect<>("PC001", "eins", "eins", null);
        final Effect<String> effect2 = new Effect<>("PC002", "eins", "eins", null);
        testCase.effects.addAll(Arrays.asList(effect1, effect2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
        Assert.assertTrue(StringUtils.matches(getSystemErr(), ".*The effect description 'eins' is used twice.*"));
    }

    @Test
    public void checkTestCaseEffectSameIdTwice() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("The effect id 'PC001' is used twice.");
        final TestCase testCase = new TestCase();
        final Effect<String> effect1 = new Effect<>("PC001", "eins", "eins", null);
        final Effect<String> effect2 = new Effect<>("PC001", "zwei", "zwei", null);
        testCase.effects.addAll(Arrays.asList(effect1, effect2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
    }


    @Test
    public void checkTestCaseEffectNegative() throws Exception {
        final TestCase testCase = new TestCase();
        final Effect<String> effect1 = new Effect<>("PC001", "eins", "eins", null);
        final Effect<String> effect2 = new Effect<>("PC001", "eins", "eins", null);
        testCase.effects.addAll(Arrays.asList(effect1, effect2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
        Assert.assertFalse(StringUtils.matches(getSystemErr(), ".*The effect description .*? is used twice.*"));
    }


    //
    // Post Condition
    //

    @Test
    public void checkTestCasePostConditionSameDescriptionTwice() throws Exception {
        final TestCase testCase = new TestCase();
        final PostCondition<String> postCondition1 = new PostCondition<>("PC001", "eins", "eins", null);
        final PostCondition<String> postCondition2 = new PostCondition<>("PC002", "eins", "eins", null);
        testCase.postConditions.addAll(Arrays.asList(postCondition1, postCondition2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
        Assert.assertTrue(StringUtils.matches(getSystemErr(), ".*The post condition description 'eins' is used twice.*"));
    }

    @Test
    public void checkTestCasePostConditionSameIdTwice() throws Exception {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("The post condition id 'PC001' is used twice.");
        final TestCase testCase = new TestCase();
        final PostCondition<String> postCondition1 = new PostCondition<>("PC001", "eins", "eins", null);
        final PostCondition<String> postCondition2 = new PostCondition<>("PC001", "zwei", "zwei", null);
        testCase.postConditions.addAll(Arrays.asList(postCondition1, postCondition2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
    }


    @Test
    public void checkTestCasePostConditionNegative() throws Exception {
        final TestCase testCase = new TestCase();
        final PostCondition<String> postCondition1 = new PostCondition<>("PC001", "eins", "eins", null);
        final PostCondition<String> postCondition2 = new PostCondition<>("PC001", "eins", "eins", null);
        testCase.postConditions.addAll(Arrays.asList(postCondition1, postCondition2));
        final TestSeries testSeries = new TestSeries(testCase);
        ConsistencyCheck.check(testSeries);
        Assert.assertFalse(StringUtils.matches(getSystemErr(), ".*The post condition description .*? is used twice.*"));
    }



    @Test
    public void checkWhetherPreConditionParametersAreSetAndAlsoNotSetPositiv() throws Exception {
        final TestSeries testSeries = createTestSeries();

        testSeries.testCases.get(0).preConditions.get(0).parameter = "something";
        testSeries.testCases.get(0).preConditions.get(1).parameter = "something";

        ConsistencyCheck.checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(testSeries.getAllPreConditions(true), "pre condition");
        Assert.assertEquals("", getSystemErr());
    }


    @Test
    public void checkWhetherPreConditionParametersAreSetAndAlsoNotSetNegativ() throws Exception {
        final TestSeries testSeries = createTestSeries();

        testSeries.testCases.get(0).preConditions.get(0).parameter = "something";

        ConsistencyCheck.checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(testSeries.getAllPreConditions(true), "pre condition");
        Assert.assertNotEquals("", getSystemErr());
    }


    @Test
    public void checkWhetherEffectParametersAreSetAndAlsoNotSetPositiv() throws Exception {
        final TestSeries testSeries = createTestSeries();

        testSeries.testCases.get(0).effects.get(0).parameter = "something";
        testSeries.testCases.get(0).effects.get(1).parameter = "something";

        ConsistencyCheck.checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(testSeries.getAllEffects(true), "effects");
        Assert.assertEquals("", getSystemErr());
    }


    @Test
    public void checkWhetherEffectParametersAreSetAndAlsoNotSetNegativ() throws Exception {
        final TestSeries testSeries = createTestSeries();

        testSeries.testCases.get(0).effects.get(0).parameter = "something";

        ConsistencyCheck.checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(testSeries.getAllEffects(true), "effects");
        Assert.assertNotEquals("", getSystemErr());
    }


    @Test
    public void checkWhetherActionParametersAreSetAndAlsoNotSetPositiv() throws Exception {
        final TestSeries testSeries = createTestSeries();

        testSeries.testCases.get(0).testSteps.get(0).actions.get(0).parameter = "something";
        testSeries.testCases.get(0).testSteps.get(0).actions.get(1).parameter = "something";

        ConsistencyCheck.checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(testSeries.getAllActions(true), "action");
        Assert.assertEquals("", getSystemErr());
    }


    @Test
    public void checkWhetherActionParametersAreSetAndAlsoNotSetNegativ() throws Exception {
        final TestSeries testSeries = createTestSeries();

        testSeries.testCases.get(0).testSteps.get(0).actions.get(0).parameter = "something";

        ConsistencyCheck.checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(testSeries.getAllActions(true), "action");
        Assert.assertNotEquals("", getSystemErr());
    }



    @Test
    public void checkWhetherPostConditionParametersAreSetAndAlsoNotSetPositiv() throws Exception {
        final TestSeries testSeries = createTestSeries();

        testSeries.testCases.get(0).postConditions.get(0).parameter = "something";
        testSeries.testCases.get(0).postConditions.get(1).parameter = "something";

        ConsistencyCheck.checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(testSeries.getAllPostConditions(true), "post condition");
        Assert.assertEquals("", getSystemErr());
    }


    @Test
    public void checkWhetherPostConditionParametersAreSetAndAlsoNotSetNegativ() throws Exception {
        final TestSeries testSeries = createTestSeries();

        testSeries.testCases.get(0).postConditions.get(0).parameter = "something";

        ConsistencyCheck.checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(testSeries.getAllPostConditions(true), "post condition");
        Assert.assertNotEquals("", getSystemErr());
    }




    private TestSeries createTestSeries() {
        final TestSeries testSeries = new TestSeries();
        final TestCase testCase1 = new TestCase();
        final TestCase testCase2 = new TestCase();

        final PreCondition preCondition11 = new PreCondition();
        final PreCondition preCondition12 = new PreCondition();
        final PreCondition preCondition21 = new PreCondition();
        final PreCondition preCondition22 = new PreCondition();

        preCondition11.id = "1";
        preCondition12.id = "1";
        preCondition21.id = "2";
        preCondition22.id = "2";

        testCase1.preConditions.add(preCondition11);
        testCase1.preConditions.add(preCondition12);
        testCase2.preConditions.add(preCondition21);
        testCase2.preConditions.add(preCondition22);

        final Effect effect11 = new Effect();
        final Effect effect12 = new Effect();
        final Effect effect21 = new Effect();
        final Effect effect22 = new Effect();

        effect11.id = "1";
        effect12.id = "1";
        effect21.id = "2";
        effect22.id = "2";

        testCase1.effects.add(effect11);
        testCase1.effects.add(effect12);
        testCase2.effects.add(effect21);
        testCase2.effects.add(effect22);


        final Action action11 = new Action();
        final Action action12 = new Action();
        final Action action21 = new Action();
        final Action action22 = new Action();

        action11.id = "1";
        action12.id = "1";
        action21.id = "2";
        action22.id = "2";

        final TestStep testStep1 = new TestStep();
        testCase1.testSteps.add(testStep1);
        final TestStep testStep2 = new TestStep();
        testCase2.testSteps.add(testStep1);

        testCase1.testSteps.get(0).actions.add(action11);
        testCase1.testSteps.get(0).actions.add(action12);
        testCase2.testSteps.get(0).actions.add(action21);
        testCase2.testSteps.get(0).actions.add(action22);

        final PostCondition postCondition11 = new PostCondition();
        final PostCondition postCondition12 = new PostCondition();
        final PostCondition postCondition21 = new PostCondition();
        final PostCondition postCondition22 = new PostCondition();

        postCondition11.id = "1";
        postCondition12.id = "1";
        postCondition21.id = "2";
        postCondition22.id = "2";

        testCase1.postConditions.add(postCondition11);
        testCase1.postConditions.add(postCondition12);
        testCase2.postConditions.add(postCondition21);
        testCase2.postConditions.add(postCondition22);

        testSeries.testCases.add(testCase1);
        testSeries.testCases.add(testCase2);
        return testSeries;
    }

}