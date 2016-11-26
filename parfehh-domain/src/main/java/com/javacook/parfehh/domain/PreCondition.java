package com.javacook.parfehh.domain;

/**
 * Created by vollmer on 01.08.16.
 */
public class PreCondition<T> extends ParamDescrBase<T> {

    public PreCondition() {
    }

    public PreCondition(String id, String uniqueName, String description, T parameter) {
        super(id, uniqueName, description, parameter);
    }

    @Override
    public String toString() {
        return "PreCondition{" +
                "description='" + description + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}
