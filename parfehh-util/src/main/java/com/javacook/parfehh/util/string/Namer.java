package com.javacook.parfehh.util.string;

import com.javacook.parfehh.util.converter.DefaultConverter;

import java.util.HashSet;
import java.util.Set;

/**
 * An util to create names / ids that have not been used before.
 */
public class Namer {

    Set<String> usedNamesCut = new HashSet<>();
    Set<String> usedNamesOrig = new HashSet<>();
    private PreConverter preConverter;

    @FunctionalInterface
    public interface PreConverter {
        String convert (String str);
    }

    public Namer() {
        this(new DefaultConverter());
    }


    public Namer(PreConverter preConverter) {
        this.preConverter = preConverter;
    }


    public String createUniqueName(String str) {
        final String strConv = (preConverter == null)? str : preConverter.convert(str);

        if (usedNamesOrig.contains(str)) return strConv;
        usedNamesOrig.add(str);

        String result = generateIdentifierNotUsedBefore(strConv);
        usedNamesCut.add(result);
        return result;
    }

    private String generateIdentifierNotUsedBefore(String modStr) {
        String result = modStr;
        if (usedNamesCut.contains(result)) {
            int counter = 2;
            do {
                result = modStr + counter++;
            } while (usedNamesCut.contains(result));
        }
        return result;
    }

}