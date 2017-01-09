package com.javacook.parfehh.generator;

import com.javacook.parfehh.domain.*;
import com.javacook.parfehh.util.collection.CollectionUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Checks various constellations on the (integra-) testdatamodel. In some cases warning are printed out in
 * server cases even an exception is thrown to cancel the generation.
 */
public class ConsistencyCheck {

    public static void check(TestSeries testSeries)
    {
        if (testSeries.title == null) warn("'testSeries.title' is null.");
        if (testSeries.description == null) warn("'testSeries.description' is null.");
        if (testSeries.testCases == null) error("'testSeries.testCases' is null.");

        CollectionUtils.pairwiseDifferent(testSeries.testCases, tc -> tc.title,
            title -> {
                throw new IllegalArgumentException("The test case title '" + title + "' is used twice.");
        });

        CollectionUtils.pairwiseDifferent(testSeries.getAllPreConditions(false), tc -> tc.id,
            id -> {
                throw new IllegalArgumentException("The pre condition id '" + id + "' is used twice.");
        });

        CollectionUtils.pairwiseDifferent(testSeries.getAllActions(false), tc -> tc.id,
            id -> {
                throw new IllegalArgumentException("The action id '" + id + "' is used twice.");
        });

        CollectionUtils.pairwiseDifferent(testSeries.getAllEffects(false), tc -> tc.id,
            id -> {
                throw new IllegalArgumentException("The effect id '" + id + "' is used twice.");
        });

        CollectionUtils.pairwiseDifferent(testSeries.getAllPostConditions(false), tc -> tc.id,
            id -> {
                throw new IllegalArgumentException("The post condition id '" + id + "' is used twice.");
        });

        for (TestCase testCase : testSeries.testCases) checkTestCase(testCase);

        final Collection<PreCondition> allPreConditions = testSeries.getAllPreConditions(true);
        final Collection<Action> allActions = testSeries.getAllActions(true);
        final Collection<Effect> allEffects = testSeries.getAllEffects(true);
        final Collection<PostCondition> allPostConditions = testSeries.getAllPostConditions(true);

        checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(allPreConditions, "pre condition");
        checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(allActions, "action");
        checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(allEffects, "effect");
        checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(allPostConditions, "post condition");

        CollectionUtils.pairwiseDifferent(testSeries.getAllPreConditions(false), tc -> tc.description,
                description -> error("The pre condition description '" + description + "' is used twice."));

        CollectionUtils.pairwiseDifferent(testSeries.getAllActions(false), tc -> tc.description,
                description -> error("The action description '" + description + "' is used twice."));

        CollectionUtils.pairwiseDifferent(testSeries.getAllEffects(false), tc -> tc.description,
                description -> error("The effect description '" + description + "' is used twice."));

        CollectionUtils.pairwiseDifferent(testSeries.getAllPostConditions(false), tc -> tc.description,
                description -> error("The post condition description '" + description + "' is used twice."));
    }


    /**
     * This must be checked (s. methode name) because the generated methode signatures would be
     * different otherwise: in the one case with parameter in the other case without.
     * @param allOf (pre conditions, actions, effects or post conditions)
     * @param kindOfCollection the name of ("pre conditions", "actions", "effects" or "post conditions")
     *                         used for the exception message.
     */
    public static void checkWhetherParametersOfOneIdAreSetAndAlsoNotSet(
            Collection<? extends ParamDescrBase> allOf, String kindOfCollection)
    {
        // Note that distinct is necessary because many elements (of many test cases) belong to the
        // same id.
        final Stream<String> ids = allOf.stream().map(t -> t.id).distinct();
        ids.forEach(id -> {
            boolean parametersEitherAssignedAndAlsoNotAssigned = allOf.stream()
                    .filter(pd -> id.equals(pd.id))
                    .map(pd -> pd.parameter == null)
                    .distinct().count() > 1;

            if (parametersEitherAssignedAndAlsoNotAssigned) {
                error("Some parameters of " + kindOfCollection + " (id = " + id + ") are set, some are not.");
            }
        });
    }


    public static void checkTestCase(TestCase testCase) {
        if (testCase.title == null) error("The title of " + testCase + " is null.");
        if (testCase.description == null) warn("The description of " + testCase + " is null.");

        for (PreCondition preCondition : testCase.preConditions) checkPreCondition(preCondition);
        for (PostCondition postCondition : testCase.postConditions)  checkPreCondition(postCondition);
        for (Effect effect : testCase.effects) checkEffect(effect);
        for (TestStep testStep : testCase.testSteps) checkTeststep(testStep);
    }


    public static void checkPreCondition(PreCondition preCondition) {
        Objects.requireNonNull(preCondition, "Argument 'preCondition' is null.");
        if (preCondition.uniqueName == null) error("'preCondition.uniqueName' is null.");
        if (preCondition.description == null) error("'preCondition.description' is null.");
    }

    public static void checkPreCondition(PostCondition postCondition) {
        Objects.requireNonNull(postCondition, "Argument 'postCondition' is null.");
        if (postCondition.uniqueName == null) error("'postCondition.uniqueName' is null.");
        if (postCondition.description == null) error("'postCondition.description' is null.");
    }

    public static void checkEffect(Effect effect) {
        Objects.requireNonNull(effect, "Argument 'effect' is null.");
        if (effect.uniqueName == null) error("'effect.uniqueName' is null.");
        if (effect.description == null) error("'effect.description' is null.");
    }

    public static void checkTeststep(TestStep testStep) {
        Objects.requireNonNull(testStep, "Argument 'testStep' is null.");
        if (testStep.title == null) error("'effect.title' is null.");
        for (Action action : testStep.actions) checkAction(action);
    }

    public static void checkAction(Action action) {
        Objects.requireNonNull(action, "Argument 'action' is null.");
        if (action.uniqueName == null) error("'action.uniqueName' is null.");
        if (action.description == null) error("'action.description' is null.");
    }


    private static void warn(String mess) {
        System.out.println("[WARNING] {ConsistencyCheck}: " + mess);
    }

    private static void error(String mess) {
        System.err.println("[ERROR] {ConsistencyCheck}: " + mess);
    }

}
