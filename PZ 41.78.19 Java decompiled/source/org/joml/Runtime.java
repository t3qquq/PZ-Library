// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.text.NumberFormat;

public final class Runtime {
    public static final boolean HAS_floatToRawIntBits = hasFloatToRawIntBits();
    public static final boolean HAS_doubleToRawLongBits = hasDoubleToRawLongBits();
    public static final boolean HAS_Long_rotateLeft = hasLongRotateLeft();
    public static final boolean HAS_Math_fma = Options.USE_MATH_FMA && hasMathFma();

    private static boolean hasMathFma() {
        try {
            java.lang.Math.class.getDeclaredMethod("fma", float.class, float.class, float.class);
            return true;
        } catch (NoSuchMethodException noSuchMethodException) {
            return false;
        }
    }

    private Runtime() {
    }

    private static boolean hasFloatToRawIntBits() {
        try {
            Float.class.getDeclaredMethod("floatToRawIntBits", float.class);
            return true;
        } catch (NoSuchMethodException noSuchMethodException) {
            return false;
        }
    }

    private static boolean hasDoubleToRawLongBits() {
        try {
            Double.class.getDeclaredMethod("doubleToRawLongBits", double.class);
            return true;
        } catch (NoSuchMethodException noSuchMethodException) {
            return false;
        }
    }

    private static boolean hasLongRotateLeft() {
        try {
            Long.class.getDeclaredMethod("rotateLeft", long.class, int.class);
            return true;
        } catch (NoSuchMethodException noSuchMethodException) {
            return false;
        }
    }

    public static int floatToIntBits(float float0) {
        return HAS_floatToRawIntBits ? floatToIntBits1_3(float0) : floatToIntBits1_2(float0);
    }

    private static int floatToIntBits1_3(float float0) {
        return Float.floatToRawIntBits(float0);
    }

    private static int floatToIntBits1_2(float float0) {
        return Float.floatToIntBits(float0);
    }

    public static long doubleToLongBits(double double0) {
        return HAS_doubleToRawLongBits ? doubleToLongBits1_3(double0) : doubleToLongBits1_2(double0);
    }

    private static long doubleToLongBits1_3(double double0) {
        return Double.doubleToRawLongBits(double0);
    }

    private static long doubleToLongBits1_2(double double0) {
        return Double.doubleToLongBits(double0);
    }

    public static String formatNumbers(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        int int0 = Integer.MIN_VALUE;

        for (int int1 = 0; int1 < string.length(); int1++) {
            char char0 = string.charAt(int1);
            if (char0 == 'E') {
                int0 = int1;
            } else {
                if (char0 == ' ' && int0 == int1 - 1) {
                    stringBuffer.append('+');
                    continue;
                }

                if (Character.isDigit(char0) && int0 == int1 - 1) {
                    stringBuffer.append('+');
                }
            }

            stringBuffer.append(char0);
        }

        return stringBuffer.toString();
    }

    public static String format(double double0, NumberFormat numberFormat) {
        if (Double.isNaN(double0)) {
            return padLeft(numberFormat, " NaN");
        } else {
            return Double.isInfinite(double0) ? padLeft(numberFormat, double0 > 0.0 ? " +Inf" : " -Inf") : numberFormat.format(double0);
        }
    }

    private static String padLeft(NumberFormat numberFormat, String string) {
        int int0 = numberFormat.format(0.0).length();
        StringBuffer stringBuffer = new StringBuffer();

        for (int int1 = 0; int1 < int0 - string.length() + 1; int1++) {
            stringBuffer.append(" ");
        }

        return stringBuffer.append(string).toString();
    }

    public static boolean equals(float float1, float float2, float float0) {
        return Float.floatToIntBits(float1) == Float.floatToIntBits(float2) || Math.abs(float1 - float2) <= float0;
    }

    public static boolean equals(double double1, double double2, double double0) {
        return Double.doubleToLongBits(double1) == Double.doubleToLongBits(double2) || Math.abs(double1 - double2) <= double0;
    }
}
