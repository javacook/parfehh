package com.javacook.parfehh.util.converter;

import com.javacook.parfehh.util.string.Namer;

/**
 * Created by vollmerj on 30.09.16.
 */
public class DefaultConverter implements Namer.PreConverter {

    @Override
    public String convert(String str) {
        return ConverterUtils.toJavaIdentifier(str);
    }
}
