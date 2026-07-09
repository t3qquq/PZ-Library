// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

import zombie.debug.DebugLog;

/**
 * TurboTuTone.
 */
public class ClimateMoon {
    private static final int[] day_year = new int[]{-1, -1, 30, 58, 89, 119, 150, 180, 211, 241, 272, 303, 333};
    private static final String[] moon_phase_name = new String[]{
        "New", "Waxing crescent", "First quarter", "Waxing gibbous", "Full", "Waning gibbous", "Third quarter", "Waning crescent"
    };
    private static final float[] units = new float[]{0.0F, 0.25F, 0.5F, 0.75F, 1.0F, 0.75F, 0.5F, 0.25F};
    private static int last_year;
    private static int last_month;
    private static int last_day;
    private static int current_phase = 0;
    private static float current_float = 0.0F;
    private static ClimateMoon instance = new ClimateMoon();

    public static ClimateMoon getInstance() {
        return instance;
    }

    public static void updatePhase(int year, int month, int day) {
        if (year != last_year || month != last_month || day != last_day) {
            last_year = year;
            last_month = month;
            last_day = day;
            current_phase = getMoonPhase(year, month, day);
            if (current_phase > 7) {
                current_phase = 7;
            }

            if (current_phase < 0) {
                current_phase = 0;
            }

            current_float = units[current_phase];
            DebugLog.log("Updated MoonPhase = " + getPhaseName() + ", float = " + current_float + ", int = " + current_phase);
        }
    }

    public static String getPhaseName() {
        return moon_phase_name[current_phase];
    }

    public static float getMoonFloat() {
        return current_float;
    }

    public int getCurrentMoonPhase() {
        return current_phase;
    }

    private static int getMoonPhase(int int3, int int0, int int2) {
        if (int0 < 0 || int0 > 12) {
            int0 = 0;
        }

        int int1 = int2 + day_year[int0];
        if (int0 > 2 && isLeapYearP(int3)) {
            int1++;
        }

        int int4 = int3 / 100 + 1;
        int int5 = int3 % 19 + 1;
        int int6 = (11 * int5 + 20 + (8 * int4 + 5) / 25 - 5 - (3 * int4 / 4 - 12)) % 30;
        if (int6 <= 0) {
            int6 += 30;
        }

        if (int6 == 25 && int5 > 11 || int6 == 24) {
            int6++;
        }

        return ((int1 + int6) * 6 + 11) % 177 / 22 & 7;
    }

    private static int daysInMonth(int int1, int int2) {
        int int0 = 31;
        switch (int1) {
            case 2:
                int0 = isLeapYearP(int2) ? 29 : 28;
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            default:
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                int0 = 30;
        }

        return int0;
    }

    private static boolean isLeapYearP(int int0) {
        return int0 % 4 == 0 && (int0 % 400 == 0 || int0 % 100 != 0);
    }
}
