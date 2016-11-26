package com.javacook.parfehh.domain;


import com.javacook.parfehh.domain.util.DynamicList;

/**
 * Created by vollmer on 01.08.16.
 */
public class TestStep {

    public String title;
    public DynamicList<Action> actions = new DynamicList<>();

    @Override
    public String toString() {
        return "TestStep{" +
                "title='" + title + '\'' +
                ", actions=" + actions +
                '}';
    }
}
