package com.javacook.parfehh.util.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vollmer on 22.11.16.
 */
public class StringUtils {

    public static boolean matches(String str, String regExp) {
        Pattern pattern = Pattern.compile(regExp, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

}
