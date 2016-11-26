package com.javacook.parfehh.domain;

/**
 * Created by vollmer on 21.11.16.
 */
public abstract class ParamDescrBase<T> extends TestDomainBase {

    protected ParamDescrBase() {
    }

    protected ParamDescrBase(String id, String uniqueName, String description, T parameter) {
        this.id = id;
        this.uniqueName = uniqueName;
        this.description = description;
        this.parameter = parameter;
    }

    public String description;
    public T parameter;

}
