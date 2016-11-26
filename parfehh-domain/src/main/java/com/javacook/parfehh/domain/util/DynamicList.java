package com.javacook.parfehh.domain.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by vollmer on 24.08.16.
 */
public class DynamicList<T> extends ArrayList<T> {

    public DynamicList() {
    }

    public DynamicList(Collection<? extends T> c) {
        super(c);
    }

    public <S extends T> DynamicList(S[] array) {
        super(Arrays.asList(array));
    }

    public void setOrNew(int index, T item) {
        int loops = index - size() + 1;
        for (int i = 0; i < loops; i++) {
            add(null);
        }
        set(index, item);
    }


    @FunctionalInterface
    public interface Factory<T> {
        T create();
    }

    public T getOrNew(int index, Factory<T> factory) {
        if (index < size()) {
            T elementAtIndex = get(index);
            if (elementAtIndex != null) return elementAtIndex;
            T newElement = factory.create();
            set(index, newElement);
            return newElement;
        }
        else {
            T newElement = factory.create();
            setOrNew(index, newElement);
            return newElement;
        }
    }

    public T getNextNew(Factory<T> factory) {
        return getOrNew(size(), factory);
    }

    @Override
    public String toString() {
        String result = "";
        int cnt = 0;
        for (T t : this) {
            if (cnt++ > 0) result += System.lineSeparator();
            result += t;
        }
        return result;
    }
}


