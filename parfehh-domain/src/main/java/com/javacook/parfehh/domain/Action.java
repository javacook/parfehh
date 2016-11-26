package com.javacook.parfehh.domain;

/**
 * Created by vollmer on 01.08.16.
 */
public class Action<T> extends ParamDescrBase<T> {

    public Action() {
    }

    public Action(String id, String uniqueName, String description, T parameter) {
        super(id, uniqueName, description, parameter);
    }

    @Override
    public String toString() {
        return "Action{" +
                "description='" + description + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}
