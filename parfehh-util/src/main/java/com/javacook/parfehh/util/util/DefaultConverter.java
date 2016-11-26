package com.javacook.parfehh.util.util;

/**
 * Created by vollmerj on 30.09.16.
 */
public class DefaultConverter implements Namer.PreConverter {

    @Override
    public String convert(String str) {
        return ConverterUtils.toJavaIdentifier(str);
    }
}
