package com.javacook.parfehh.util.misc;

/**
 * Created by vollmer on 29.11.16.
 */
public class MutableInt {

    protected int value = 0;

    public int inc() {
        return value++;
    }

    public int dec() {
        return value--;
    }

    public int cin() {
        return ++value;
    }

    public int ced() {
        return --value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
