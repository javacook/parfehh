package com.javacook.parfehh.generator;

import com.javacook.parfehh.util.string.Namer;

import static com.javacook.parfehh.util.converter.ConverterUtils.convertSpecialChars;
import static com.javacook.parfehh.util.converter.ConverterUtils.cut;
import static com.javacook.parfehh.util.converter.ConverterUtils.toJavaIdentifier;

/**
 * Created by vollmerj on 30.09.16.
 */
public class ParfehhNamer extends Namer {

    public ParfehhNamer() {
        super(str -> cut(convertSpecialChars(toJavaIdentifier(str)), 80));
    }

    public static void main(String[] args) {
        final ParfehhNamer namer = new ParfehhNamer();
        System.out.println(namer.createUniqueName("Abfrage der Gläubiger zu einem Mitglied"));
        System.out.println(namer.createUniqueName("Abfrage der Gläubiger zu einem Mitglied"));
    }
}
