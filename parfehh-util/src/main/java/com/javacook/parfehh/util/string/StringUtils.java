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

    public static String escapeJavaDoc(String str) {
        if (str == null) {
            return null;
        }
        String result = "";
        for (char ch : str.toCharArray()) {
            switch (ch) {
                case '&': result += "&amp;";
                    break;
                case '@': result += "&#064;";
                    break;
                case '*': result += "&#042;";
                    break;
                default: result += ch;
            }
        }
        return result;
    }

}
