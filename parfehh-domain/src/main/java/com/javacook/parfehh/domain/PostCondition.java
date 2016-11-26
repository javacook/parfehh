package com.javacook.parfehh.domain;

/**
 * Created by vollmer on 01.08.16.
 */
public class PostCondition<T> extends ParamDescrBase<T> {

    public PostCondition() {
    }

    public PostCondition(String id, String uniqueName, String description, T parameter) {
        super(id, uniqueName, description, parameter);
    }

    @Override
    public String toString() {
        return "PostCondition{" +
                "description='" + description + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}
