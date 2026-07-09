// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

import zombie.SandboxOptions;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;

/**
 * TurboTuTone.
 */
public class Temperature {
    public static boolean DO_DEFAULT_BASE = false;
    public static boolean DO_DAYLEN_MOD = true;
    public static String CELSIUS_POSTFIX = "\u00b0C";
    public static String FAHRENHEIT_POSTFIX = "\u00b0F";
    public static final float skinCelciusMin = 20.0F;
    public static final float skinCelciusFavorable = 33.0F;
    public static final float skinCelciusMax = 42.0F;
    public static final float homeostasisDefault = 37.0F;
    public static final float FavorableNakedTemp = 27.0F;
    public static final float FavorableRoomTemp = 22.0F;
    public static final float coreCelciusMin = 20.0F;
    public static final float coreCelciusMax = 42.0F;
    public static final float neutralZone = 27.0F;
    public static final float Hypothermia_1 = 36.5F;
    public static final float Hypothermia_2 = 35.0F;
    public static final float Hypothermia_3 = 30.0F;
    public static final float Hypothermia_4 = 25.0F;
    public static final float Hyperthermia_1 = 37.5F;
    public static final float Hyperthermia_2 = 39.0F;
    public static final float Hyperthermia_3 = 40.0F;
    public static final float Hyperthermia_4 = 41.0F;
    public static final float TrueInsulationMultiplier = 2.0F;
    public static final float TrueWindresistMultiplier = 1.0F;
    public static final float BodyMinTemp = 20.0F;
    public static final float BodyMaxTemp = 42.0F;
    private static String cacheTempString = "";
    private static float cacheTemp = -9000.0F;
    private static Color tempColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    private static Color col_0 = new Color(29, 34, 237);
    private static Color col_25 = new Color(0, 255, 234);
    private static Color col_50 = new Color(84, 255, 55);
    private static Color col_75 = new Color(255, 246, 0);
    private static Color col_100 = new Color(255, 0, 0);

    public static String getCelsiusPostfix() {
        return CELSIUS_POSTFIX;
    }

    public static String getFahrenheitPostfix() {
        return FAHRENHEIT_POSTFIX;
    }

    public static String getTemperaturePostfix() {
        return Core.OptionTemperatureDisplayCelsius ? CELSIUS_POSTFIX : FAHRENHEIT_POSTFIX;
    }

    public static String getTemperatureString(float celsius) {
        float float0 = Core.OptionTemperatureDisplayCelsius ? celsius : CelsiusToFahrenheit(celsius);
        float0 = Math.round(float0 * 10.0F) / 10.0F;
        if (cacheTemp != float0) {
            cacheTemp = float0;
            cacheTempString = float0 + " " + getTemperaturePostfix();
        }

        return cacheTempString;
    }

    public static float CelsiusToFahrenheit(float celsius) {
        return celsius * 1.8F + 32.0F;
    }

    public static float FahrenheitToCelsius(float fahrenheit) {
        return (fahrenheit - 32.0F) / 1.8F;
    }

    public static float WindchillCelsiusKph(float t, float v) {
        float float0 = 13.12F + 0.6215F * t - 11.37F * (float)Math.pow(v, 0.16F) + 0.3965F * t * (float)Math.pow(v, 0.16F);
        return float0 < t ? float0 : t;
    }

    public static float getTrueInsulationValue(float insulation) {
        return insulation * 2.0F + 0.5F * insulation * insulation * insulation;
    }

    public static float getTrueWindresistanceValue(float windresist) {
        return windresist * 1.0F + 0.5F * windresist * windresist;
    }

    public static void reset() {
    }

    public static float getFractionForRealTimeRatePerMin(float rate) {
        if (DO_DEFAULT_BASE) {
            return rate / (1440.0F / SandboxOptions.instance.getDayLengthMinutesDefault());
        } else if (!DO_DAYLEN_MOD) {
            return rate / (1440.0F / SandboxOptions.instance.getDayLengthMinutes());
        } else {
            float float0 = (float)SandboxOptions.instance.getDayLengthMinutes() / SandboxOptions.instance.getDayLengthMinutesDefault();
            if (float0 < 1.0F) {
                float0 = 0.5F + 0.5F * float0;
            } else if (float0 > 1.0F) {
                float0 = 1.0F + float0 / 16.0F;
            }

            return rate / (1440.0F / SandboxOptions.instance.getDayLengthMinutes()) * float0;
        }
    }

    public static Color getValueColor(float val) {
        val = ClimateManager.clamp(0.0F, 1.0F, val);
        tempColor.set(0.0F, 0.0F, 0.0F, 1.0F);
        float float0 = 0.0F;
        if (val < 0.25F) {
            float0 = val / 0.25F;
            col_0.interp(col_25, float0, tempColor);
        } else if (val < 0.5F) {
            float0 = (val - 0.25F) / 0.25F;
            col_25.interp(col_50, float0, tempColor);
        } else if (val < 0.75F) {
            float0 = (val - 0.5F) / 0.25F;
            col_50.interp(col_75, float0, tempColor);
        } else {
            float0 = (val - 0.75F) / 0.25F;
            col_75.interp(col_100, float0, tempColor);
        }

        return tempColor;
    }

    public static float getWindChillAmountForPlayer(IsoPlayer player) {
        if (player.getVehicle() == null && (player.getSquare() == null || !player.getSquare().isInARoom())) {
            ClimateManager climateManager = ClimateManager.getInstance();
            float float0 = climateManager.getAirTemperatureForCharacter(player, true);
            float float1 = 0.0F;
            if (float0 < climateManager.getTemperature()) {
                float1 = climateManager.getTemperature() - float0;
            }

            return float1;
        } else {
            return 0.0F;
        }
    }
}
