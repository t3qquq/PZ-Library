// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

import zombie.core.Core;
import zombie.core.math.PZMath;

public class Bits {
    public static final boolean ENABLED = true;
    public static final int BIT_0 = 0;
    public static final int BIT_1 = 1;
    public static final int BIT_2 = 2;
    public static final int BIT_3 = 4;
    public static final int BIT_4 = 8;
    public static final int BIT_5 = 16;
    public static final int BIT_6 = 32;
    public static final int BIT_7 = 64;
    public static final int BIT_BYTE_MAX = 64;
    public static final int BIT_8 = 128;
    public static final int BIT_9 = 256;
    public static final int BIT_10 = 512;
    public static final int BIT_11 = 1024;
    public static final int BIT_12 = 2048;
    public static final int BIT_13 = 4096;
    public static final int BIT_14 = 8192;
    public static final int BIT_15 = 16384;
    public static final int BIT_SHORT_MAX = 16384;
    public static final int BIT_16 = 32768;
    public static final int BIT_17 = 65536;
    public static final int BIT_18 = 131072;
    public static final int BIT_19 = 262144;
    public static final int BIT_20 = 524288;
    public static final int BIT_21 = 1048576;
    public static final int BIT_22 = 2097152;
    public static final int BIT_23 = 4194304;
    public static final int BIT_24 = 8388608;
    public static final int BIT_25 = 16777216;
    public static final int BIT_26 = 33554432;
    public static final int BIT_27 = 67108864;
    public static final int BIT_28 = 134217728;
    public static final int BIT_29 = 268435456;
    public static final int BIT_30 = 536870912;
    public static final int BIT_31 = 1073741824;
    public static final int BIT_INT_MAX = 1073741824;
    public static final long BIT_32 = 2147483648L;
    public static final long BIT_33 = 4294967296L;
    public static final long BIT_34 = 8589934592L;
    public static final long BIT_35 = 17179869184L;
    public static final long BIT_36 = 34359738368L;
    public static final long BIT_37 = 68719476736L;
    public static final long BIT_38 = 137438953472L;
    public static final long BIT_39 = 274877906944L;
    public static final long BIT_40 = 549755813888L;
    public static final long BIT_41 = 1099511627776L;
    public static final long BIT_42 = 2199023255552L;
    public static final long BIT_43 = 4398046511104L;
    public static final long BIT_44 = 8796093022208L;
    public static final long BIT_45 = 17592186044416L;
    public static final long BIT_46 = 35184372088832L;
    public static final long BIT_47 = 70368744177664L;
    public static final long BIT_48 = 140737488355328L;
    public static final long BIT_49 = 281474976710656L;
    public static final long BIT_50 = 562949953421312L;
    public static final long BIT_51 = 1125899906842624L;
    public static final long BIT_52 = 2251799813685248L;
    public static final long BIT_53 = 4503599627370496L;
    public static final long BIT_54 = 9007199254740992L;
    public static final long BIT_55 = 18014398509481984L;
    public static final long BIT_56 = 36028797018963968L;
    public static final long BIT_57 = 72057594037927936L;
    public static final long BIT_58 = 144115188075855872L;
    public static final long BIT_59 = 288230376151711744L;
    public static final long BIT_60 = 576460752303423488L;
    public static final long BIT_61 = 1152921504606846976L;
    public static final long BIT_62 = 2305843009213693952L;
    public static final long BIT_63 = 4611686018427387904L;
    public static final long BIT_LONG_MAX = 4611686018427387904L;
    private static StringBuilder sb = new StringBuilder();

    public static byte packFloatUnitToByte(float float0) {
        if (float0 < 0.0F || float0 > 1.0F) {
            if (Core.bDebug) {
                throw new RuntimeException("UtilsIO Cannot pack float units out of the range 0.0 to 1.0");
            }

            float0 = PZMath.clamp(float0, 0.0F, 1.0F);
        }

        return (byte)(float0 * 255.0F + -128.0F);
    }

    public static float unpackByteToFloatUnit(byte byte0) {
        return (byte0 - -128) / 255.0F;
    }

    public static byte addFlags(byte byte1, int int0) {
        if (int0 >= 0 && int0 <= 64) {
            byte byte0;
            return byte0 = (byte)(byte1 | int0);
        } else {
            throw new RuntimeException("Cannot add flags, exceeding byte bounds or negative number flags. (" + int0 + ")");
        }
    }

    public static byte addFlags(byte byte0, long long0) {
        if (long0 >= 0L && long0 <= 64L) {
            return (byte)(byte0 | long0);
        } else {
            throw new RuntimeException("Cannot add flags, exceeding byte bounds or negative number flags. (" + long0 + ")");
        }
    }

    public static short addFlags(short short1, int int0) {
        if (int0 >= 0 && int0 <= 16384) {
            short short0;
            return short0 = (short)(short1 | int0);
        } else {
            throw new RuntimeException("Cannot add flags, exceeding short bounds or negative number flags. (" + int0 + ")");
        }
    }

    public static short addFlags(short short0, long long0) {
        if (long0 >= 0L && long0 <= 16384L) {
            return (short)(short0 | long0);
        } else {
            throw new RuntimeException("Cannot add flags, exceeding short bounds or negative number flags. (" + long0 + ")");
        }
    }

    public static int addFlags(int int2, int int0) {
        if (int0 >= 0 && int0 <= 1073741824) {
            int int1;
            return int1 = int2 | int0;
        } else {
            throw new RuntimeException("Cannot add flags, exceeding short bounds or negative number flags. (" + int0 + ")");
        }
    }

    public static int addFlags(int int0, long long0) {
        if (long0 >= 0L && long0 <= 1073741824L) {
            return (int)(int0 | long0);
        } else {
            throw new RuntimeException("Cannot add flags, exceeding integer bounds or negative number flags. (" + long0 + ")");
        }
    }

    public static long addFlags(long long1, int int0) {
        if (int0 >= 0 && int0 <= 4611686018427387904L) {
            long long0;
            return long0 = long1 | int0;
        } else {
            throw new RuntimeException("Cannot add flags, exceeding long bounds or negative number flags. (" + int0 + ")");
        }
    }

    public static long addFlags(long long2, long long0) {
        if (long0 >= 0L && long0 <= 4611686018427387904L) {
            long long1;
            return long1 = long2 | long0;
        } else {
            throw new RuntimeException("Cannot add flags, exceeding long bounds or negative number flags. (" + long0 + ")");
        }
    }

    public static boolean hasFlags(byte byte0, int int0) {
        return checkFlags(byte0, int0, 64, Bits.CompareOption.ContainsAll);
    }

    public static boolean hasFlags(byte byte0, long long0) {
        return checkFlags((long)byte0, long0, 64L, Bits.CompareOption.ContainsAll);
    }

    public static boolean hasEitherFlags(byte byte0, int int0) {
        return checkFlags(byte0, int0, 64, Bits.CompareOption.HasEither);
    }

    public static boolean hasEitherFlags(byte byte0, long long0) {
        return checkFlags((long)byte0, long0, 64L, Bits.CompareOption.HasEither);
    }

    public static boolean notHasFlags(byte byte0, int int0) {
        return checkFlags(byte0, int0, 64, Bits.CompareOption.NotHas);
    }

    public static boolean notHasFlags(byte byte0, long long0) {
        return checkFlags((long)byte0, long0, 64L, Bits.CompareOption.NotHas);
    }

    public static boolean hasFlags(short short0, int int0) {
        return checkFlags(short0, int0, 16384, Bits.CompareOption.ContainsAll);
    }

    public static boolean hasFlags(short short0, long long0) {
        return checkFlags((long)short0, long0, 16384L, Bits.CompareOption.ContainsAll);
    }

    public static boolean hasEitherFlags(short short0, int int0) {
        return checkFlags(short0, int0, 16384, Bits.CompareOption.HasEither);
    }

    public static boolean hasEitherFlags(short short0, long long0) {
        return checkFlags((long)short0, long0, 16384L, Bits.CompareOption.HasEither);
    }

    public static boolean notHasFlags(short short0, int int0) {
        return checkFlags(short0, int0, 16384, Bits.CompareOption.NotHas);
    }

    public static boolean notHasFlags(short short0, long long0) {
        return checkFlags((long)short0, long0, 16384L, Bits.CompareOption.NotHas);
    }

    public static boolean hasFlags(int int0, int int1) {
        return checkFlags(int0, int1, 1073741824, Bits.CompareOption.ContainsAll);
    }

    public static boolean hasFlags(int int0, long long0) {
        return checkFlags((long)int0, long0, 1073741824L, Bits.CompareOption.ContainsAll);
    }

    public static boolean hasEitherFlags(int int0, int int1) {
        return checkFlags(int0, int1, 1073741824, Bits.CompareOption.HasEither);
    }

    public static boolean hasEitherFlags(int int0, long long0) {
        return checkFlags((long)int0, long0, 1073741824L, Bits.CompareOption.HasEither);
    }

    public static boolean notHasFlags(int int0, int int1) {
        return checkFlags(int0, int1, 1073741824, Bits.CompareOption.NotHas);
    }

    public static boolean notHasFlags(int int0, long long0) {
        return checkFlags((long)int0, long0, 1073741824L, Bits.CompareOption.NotHas);
    }

    public static boolean hasFlags(long long0, int int0) {
        return checkFlags(long0, (long)int0, 4611686018427387904L, Bits.CompareOption.ContainsAll);
    }

    public static boolean hasFlags(long long0, long long1) {
        return checkFlags(long0, long1, 4611686018427387904L, Bits.CompareOption.ContainsAll);
    }

    public static boolean hasEitherFlags(long long0, int int0) {
        return checkFlags(long0, (long)int0, 4611686018427387904L, Bits.CompareOption.HasEither);
    }

    public static boolean hasEitherFlags(long long0, long long1) {
        return checkFlags(long0, long1, 4611686018427387904L, Bits.CompareOption.HasEither);
    }

    public static boolean notHasFlags(long long0, int int0) {
        return checkFlags(long0, (long)int0, 4611686018427387904L, Bits.CompareOption.NotHas);
    }

    public static boolean notHasFlags(long long0, long long1) {
        return checkFlags(long0, long1, 4611686018427387904L, Bits.CompareOption.NotHas);
    }

    public static boolean checkFlags(int int2, int int0, int int1, Bits.CompareOption compareOption) {
        if (int0 < 0 || int0 > int1) {
            throw new RuntimeException("Cannot check for flags, exceeding byte bounds or negative number flags. (" + int0 + ")");
        } else if (compareOption == Bits.CompareOption.ContainsAll) {
            return (int2 & int0) == int0;
        } else if (compareOption == Bits.CompareOption.HasEither) {
            return (int2 & int0) != 0;
        } else if (compareOption == Bits.CompareOption.NotHas) {
            return (int2 & int0) == 0;
        } else {
            throw new RuntimeException("No valid compare option.");
        }
    }

    public static boolean checkFlags(long long2, long long0, long long1, Bits.CompareOption compareOption) {
        if (long0 < 0L || long0 > long1) {
            throw new RuntimeException("Cannot check for flags, exceeding byte bounds or negative number flags. (" + long0 + ")");
        } else if (compareOption == Bits.CompareOption.ContainsAll) {
            return (long2 & long0) == long0;
        } else if (compareOption == Bits.CompareOption.HasEither) {
            return (long2 & long0) != 0L;
        } else if (compareOption == Bits.CompareOption.NotHas) {
            return (long2 & long0) == 0L;
        } else {
            throw new RuntimeException("No valid compare option.");
        }
    }

    public static int getLen(byte var0) {
        return 1;
    }

    public static int getLen(short var0) {
        return 2;
    }

    public static int getLen(int var0) {
        return 4;
    }

    public static int getLen(long var0) {
        return 8;
    }

    private static void clearStringBuilder() {
        if (sb.length() > 0) {
            sb.delete(0, sb.length());
        }
    }

    public static String getBitsString(byte byte0) {
        return getBitsString(byte0, 8);
    }

    public static String getBitsString(short short0) {
        return getBitsString(short0, 16);
    }

    public static String getBitsString(int int0) {
        return getBitsString(int0, 32);
    }

    public static String getBitsString(long long0) {
        return getBitsString(long0, 64);
    }

    private static String getBitsString(long long0, int int0) {
        clearStringBuilder();
        if (long0 != 0L) {
            sb.append("Bits(" + (int0 - 1) + "): ");
            long long1 = 1L;

            for (int int1 = 1; int1 < int0; int1++) {
                sb.append("[" + int1 + "]");
                if ((long0 & long1) == long1) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }

                if (int1 < int0 - 1) {
                    sb.append(" ");
                }

                long1 *= 2L;
            }
        } else {
            sb.append("No bits saved, 0x0.");
        }

        return sb.toString();
    }

    public static enum CompareOption {
        ContainsAll,
        HasEither,
        NotHas;
    }
}
