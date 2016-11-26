package com.javacook.parfehh.domain;


import com.javacook.parfehh.domain.util.DynamicList;

/**
 * Created by vollmer on 01.08.16.
 */
public class TestCase extends TestDomainBase {

    public String title;
    public String description;
    public String comment;
    public int priority;

    public DynamicList<PreCondition> preConditions = new DynamicList<>();
    public DynamicList<PostCondition> postConditions  = new DynamicList<>();;
    public DynamicList<TestStep> testSteps  = new DynamicList<>();
    public DynamicList<Effect> effects = new DynamicList<>();


    @Override
    public String toString() {
        return "TestCase{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", comment='" + comment + '\'' +
                ", priority=" + priority +
                ", preConditions=" + preConditions +
                ", postConditions=" + postConditions +
                ", testSteps=" + testSteps +
                ", effects=" + effects +
                '}';
    }
}
