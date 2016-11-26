package com.javacook.parfehh.domain;

/**
 * Created by vollmer on 04.08.16.
 */
public class TestDomainBase {

    /**
     * Mainly used as identifier for the Protected Regions
     */
    public String id;

    /**
     * Mainly used for method names in classes which must be unique
     */
    public String uniqueName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestDomainBase that = (TestDomainBase) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return uniqueName != null ? uniqueName.equals(that.uniqueName) : that.uniqueName == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uniqueName != null ? uniqueName.hashCode() : 0);
        return result;
    }
}
