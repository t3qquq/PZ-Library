// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.util.function.BiFunction;

public class StringUtils {
    public static final String s_emptyString = "";
    public static final char UTF8_BOM = '\ufeff';

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isNullOrWhitespace(String string) {
        return isNullOrEmpty(string) || isWhitespace(string);
    }

    private static boolean isWhitespace(String string) {
        int int0 = string.length();
        if (int0 <= 0) {
            return false;
        } else {
            int int1 = 0;
            int int2 = int0 / 2;

            for (int int3 = int0 - 1; int1 <= int2; int3--) {
                if (!Character.isWhitespace(string.charAt(int1)) || !Character.isWhitespace(string.charAt(int3))) {
                    return false;
                }

                int1++;
            }

            return true;
        }
    }

    public static String discardNullOrWhitespace(String string) {
        return isNullOrWhitespace(string) ? null : string;
    }

    public static String trimPrefix(String string0, String string1) {
        return string0.startsWith(string1) ? string0.substring(string1.length()) : string0;
    }

    public static String trimSuffix(String string0, String string1) {
        return string0.endsWith(string1) ? string0.substring(0, string0.length() - string1.length()) : string0;
    }

    public static boolean equals(String string0, String string1) {
        return string0 == string1 ? true : string0 != null && string0.equals(string1);
    }

    public static boolean startsWithIgnoreCase(String string0, String string1) {
        return string0.regionMatches(true, 0, string1, 0, string1.length());
    }

    public static boolean endsWithIgnoreCase(String string1, String string0) {
        int int0 = string0.length();
        return string1.regionMatches(true, string1.length() - int0, string0, 0, int0);
    }

    public static boolean containsIgnoreCase(String string1, String string0) {
        for (int int0 = string1.length() - string0.length(); int0 >= 0; int0--) {
            if (string1.regionMatches(true, int0, string0, 0, string0.length())) {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsIgnoreCase(String string0, String string1) {
        return string0 == string1 ? true : string0 != null && string0.equalsIgnoreCase(string1);
    }

    public static boolean tryParseBoolean(String string0) {
        if (isNullOrWhitespace(string0)) {
            return false;
        } else {
            String string1 = string0.trim();
            return string1.equalsIgnoreCase("true") || string1.equals("1") || string1.equals("1.0");
        }
    }

    public static boolean isBoolean(String string1) {
        String string0 = string1.trim();
        return string0.equalsIgnoreCase("true") || string0.equals("1") || string0.equals("1.0")
            ? true
            : string0.equalsIgnoreCase("false") || string0.equals("0") || string0.equals("0.0");
    }

    public static boolean contains(String[] strings, String string, BiFunction<String, String, Boolean> biFunction) {
        return indexOf(strings, string, biFunction) > -1;
    }

    public static int indexOf(String[] strings, String string, BiFunction<String, String, Boolean> biFunction) {
        int int0 = -1;

        for (int int1 = 0; int1 < strings.length; int1++) {
            if ((Boolean)biFunction.apply(strings[int1], string)) {
                int0 = int1;
                break;
            }
        }

        return int0;
    }

    public static String indent(String string) {
        return indent(string, "", "\t");
    }

    private static String indent(String string1, String string2, String string3) {
        String string0 = System.lineSeparator();
        return indent(string1, string0, string2, string3);
    }

    private static String indent(String string0, String string1, String string2, String string3) {
        if (isNullOrEmpty(string0)) {
            return string0;
        } else {
            int int0 = string0.length();
            StringBuilder stringBuilder0 = new StringBuilder(int0);
            StringBuilder stringBuilder1 = new StringBuilder(int0);
            int int1 = 0;

            for (int int2 = 0; int2 < int0; int2++) {
                char char0 = string0.charAt(int2);
                switch (char0) {
                    case '\n':
                        stringBuilder0.append((CharSequence)stringBuilder1);
                        stringBuilder0.append(string1);
                        stringBuilder1.setLength(0);
                        int1++;
                    case '\r':
                        break;
                    default:
                        if (stringBuilder1.length() == 0) {
                            if (int1 == 0) {
                                stringBuilder1.append(string2);
                            } else {
                                stringBuilder1.append(string3);
                            }
                        }

                        stringBuilder1.append(char0);
                }
            }

            stringBuilder0.append((CharSequence)stringBuilder1);
            stringBuilder1.setLength(0);
            return stringBuilder0.toString();
        }
    }

    public static String leftJustify(String string0, int int0) {
        if (string0 == null) {
            return leftJustify("", int0);
        } else {
            int int1 = string0.length();
            if (int1 >= int0) {
                return string0;
            } else {
                int int2 = int0 - int1;
                char[] chars = new char[int2];

                for (int int3 = 0; int3 < int2; int3++) {
                    chars[int3] = ' ';
                }

                String string1 = new String(chars);
                return string0 + string1;
            }
        }
    }

    public static String moduleDotType(String string1, String string0) {
        if (string0 == null) {
            return null;
        } else {
            return string0.contains(".") ? string0 : string1 + "." + string0;
        }
    }

    public static String stripBOM(String string) {
        return string != null && string.length() > 0 && string.charAt(0) == '\ufeff' ? string.substring(1) : string;
    }

    public static boolean containsDoubleDot(String string) {
        return isNullOrEmpty(string) ? false : string.contains("..") || string.contains("\u0000.\u0000.");
    }
}
