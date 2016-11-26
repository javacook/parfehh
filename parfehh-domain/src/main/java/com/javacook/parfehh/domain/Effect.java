package com.javacook.parfehh.domain;

/**
 * Created by vollmer on 01.08.16.
 */
public class Effect<T> extends ParamDescrBase<T> {

    public Effect() {
    }

    public Effect(String id, String uniqueName, String description, T parameter) {
        super(id, uniqueName, description, parameter);
    }


    @Override
    public String toString() {
        return "Effect{" +
                "description='" + description + '\'' +
                ", parameter=" + parameter +
                '}';
    }
}
