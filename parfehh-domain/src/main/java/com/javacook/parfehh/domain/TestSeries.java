package com.javacook.parfehh.domain;


import com.javacook.parfehh.domain.util.DynamicList;

import java.util.*;

/**
 * Parent domain object for the complete test series
 */
public class TestSeries extends TestDomainBase {

    public String title;
    public String description;
    public DynamicList<TestCase> testCases = new DynamicList<>();

    public TestSeries(TestCase... testCases) {
        if (testCases != null) {
            this.testCases.addAll(Arrays.asList(testCases));
        }
    }
    
    public Collection<PreCondition> getAllPreConditions(boolean withDuplicates) {
        Collection<PreCondition> allPreConditions = withDuplicates? new ArrayList<>() : new LinkedHashSet<>();
        for (TestCase testCase : testCases) {
            if (testCase.preConditions == null) continue;
            allPreConditions.addAll(testCase.preConditions);
        }
        return allPreConditions;
    }

    public Collection<Action> getAllActions(boolean withDuplicates) {
        Collection<Action> allActions = withDuplicates? new ArrayList<>() : new LinkedHashSet<>();
        for (TestCase testCase : testCases) {
            if (testCase == null) continue;
            for (TestStep testStep : testCase.testSteps) {
                allActions.addAll(testStep.actions);
            }
        }
        return allActions;
    }

    public Collection<Effect> getAllEffects(boolean withDuplicates) {
        Collection<Effect> allEffects = withDuplicates? new ArrayList<>() : new LinkedHashSet<>();
        for (TestCase testCase : testCases) {
            if (testCase == null) continue;
            allEffects.addAll(testCase.effects);
        }
        return allEffects;
    }

    public Collection<PostCondition> getAllPostConditions(boolean withDuplicates) {
        Collection<PostCondition> allPostConditions = withDuplicates? new ArrayList<>() : new LinkedHashSet<>();
        for (TestCase testCase : testCases) {
            if (testCase.postConditions == null) continue;
            allPostConditions.addAll(testCase.postConditions);
        }
        return allPostConditions;
    }


    @Override
    public String toString() {
        return "TestSeries{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", testCases=" + testCases +
                '}';
    }
}
