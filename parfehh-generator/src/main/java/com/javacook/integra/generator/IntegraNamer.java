package com.javacook.integra.generator;


import static com.javacook.parfehh.util.util.ConverterUtils.*;
import com.javacook.parfehh.util.util.Namer;


/**
 * Created by vollmerj on 30.09.16.
 */
public class IntegraNamer extends Namer {

    public IntegraNamer() {
        super(str -> cut(convertSpecialChars(toJavaIdentifier(str)), 80));
    }

    public static void main(String[] args) {
        final IntegraNamer namer = new IntegraNamer();
        System.out.println(namer.createUniqueName("Abfrage der Gläubiger zu einem Mitglied"));
        System.out.println(namer.createUniqueName("Abfrage der Gläubiger zu einem Mitglied"));
    }
}
