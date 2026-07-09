// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public final class Options {
    public static final boolean DEBUG = hasOption(System.getProperty("joml.debug", "false"));
    public static final boolean NO_UNSAFE = hasOption(System.getProperty("joml.nounsafe", "false"));
    public static final boolean FORCE_UNSAFE = hasOption(System.getProperty("joml.forceUnsafe", "false"));
    public static final boolean FASTMATH = hasOption(System.getProperty("joml.fastmath", "false"));
    public static final boolean SIN_LOOKUP = hasOption(System.getProperty("joml.sinLookup", "false"));
    public static final int SIN_LOOKUP_BITS = Integer.parseInt(System.getProperty("joml.sinLookup.bits", "14"));
    public static final boolean useNumberFormat = hasOption(System.getProperty("joml.format", "true"));
    public static final boolean USE_MATH_FMA = hasOption(System.getProperty("joml.useMathFma", "false"));
    public static final int numberFormatDecimals = Integer.parseInt(System.getProperty("joml.format.decimals", "3"));
    public static final NumberFormat NUMBER_FORMAT = decimalFormat();

    private Options() {
    }

    private static NumberFormat decimalFormat() {
        Object object;
        if (useNumberFormat) {
            char[] chars = new char[numberFormatDecimals];
            Arrays.fill(chars, '0');
            object = new DecimalFormat(" 0." + new String(chars) + "E0;-");
        } else {
            object = NumberFormat.getNumberInstance(Locale.ENGLISH);
            object.setGroupingUsed(false);
        }

        return (NumberFormat)object;
    }

    private static boolean hasOption(String string) {
        if (string == null) {
            return false;
        } else {
            return string.trim().length() == 0 ? true : Boolean.valueOf(string);
        }
    }
}
