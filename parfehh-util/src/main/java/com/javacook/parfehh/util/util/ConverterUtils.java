package com.javacook.parfehh.util.util;

/**
 * Created by vollmer on 22.09.16.
 */
public class ConverterUtils {

    public static String convertSpecialChars(String str) {
        if (str == null) return null;

        String result = "";

        for (char c :str.toCharArray()) {
            switch (c) {
                case 'ä': result += "ae"; break;
                case 'Ä': result += "Ae"; break;
                case 'ö': result += "oe"; break;
                case 'Ö': result += "Oe"; break;
                case 'ü': result += "ue"; break;
                case 'Ü': result += "Ue"; break;
                case 'ß': result += "ss"; break;
                default:
                    if (Character.isJavaIdentifierPart(c)) result += c;
                    else throw new IllegalArgumentException("Invalid characater '"+c+"'.");
            }
        }
        return result;
    }


    public static String cut(String str, int maxLength) {
        if (str == null) return null;
        return (str.length() < maxLength)? str : str.substring(0, maxLength);
    }


    public static String toJavaIdentifier(String strCut) {
        if (strCut == null) return null;
        String modStr = "";
        for (int i = 0; i < strCut.length(); i++) {
            char ch = strCut.charAt(i);
            if (Character.isJavaIdentifierPart(ch)) modStr += ch;
            else {
                switch (ch) {
                    case ' ':
                    case '_':
                    case '-':
                    case '/': modStr += '_';
                }
            }
        }
        while (modStr.contains("__")) {
            modStr = modStr.replaceAll("__", "_");
        }
        return modStr;
    }

}
