package com.javacook.parfehh.util.string;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by vollmer on 03.03.17.
 */
abstract public class Enumerator {

    protected Set<String> enumerateSeparation(String str, String delimSeparation, String delimInterval) {
        Objects.requireNonNull(str, "Argument 'str' is null.");
        Objects.requireNonNull(delimSeparation, "Argument 'delimSeparation' is null.");
        Objects.requireNonNull(delimInterval, "Argument 'delimInterval' is null.");

        if (str.isEmpty()) return Collections.EMPTY_SET;

        final String[] parts = str.split(delimSeparation);

        return Arrays.asList(parts).stream()
                .map(t -> enumerateInterval(t, delimInterval))
                .flatMap(s -> s.stream())
                .collect(Collectors.toSet());
    }


    protected Set<String> enumerateInterval(String str, String delimeter) {
        final String[] parts = str.split(delimeter);
        switch (parts.length) {
            case 0: return Collections.EMPTY_SET;
            case 1: return new HashSet<String>() {{
                                add(formatLiteral(parseLiteral(parts[0])));
                            }};
            case 2: return new HashSet<String>() {{
                                IntStream.rangeClosed(parseLiteral(parts[0]), parseLiteral(parts[1]))
                                        .forEach(t -> add(formatLiteral(t)));
                            }};
            default:
                throw new IllegalArgumentException("The value '" + str + "' is an invalid interval");
        }
    }

    abstract protected int parseLiteral(String str);

    abstract protected String formatLiteral(int i);


    /**
     * main
     * @param args
     */
    public static void main(String[] args) {

        final Enumerator enumerator = new Enumerator() {
            protected int parseLiteral(String str) {
                return Integer.parseInt(str.trim().substring(4));
            }
            protected String formatLiteral(int i) {
                return "VZVG" + String.format("%04d", i);
            }
        };

        final Set<String> result = enumerator.enumerateSeparation("VZVG0123 -VZVG0127, VZVG0999, VZVG0007- VZVG0009", ",", "-");
        System.out.println(result);
    }

}
