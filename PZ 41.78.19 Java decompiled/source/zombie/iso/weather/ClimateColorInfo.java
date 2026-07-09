// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import zombie.ZomboidFileSystem;
import zombie.core.Color;
import zombie.debug.DebugLog;

/**
 * TurboTuTone.  A pair of colors for global light interior and exterior, the alpha of the colors is blend intensity.  When outside the shader is used to apply global light, when inside a room its using a different method (using the weather mask) to do the coloring of outside parts.  This requires separate balancing of colors as using one and the same for both methods doesn't always look right.
 */
public class ClimateColorInfo {
    private Color interior = new Color(0, 0, 0, 1);
    private Color exterior = new Color(0, 0, 0, 1);
    private static BufferedWriter writer;

    public ClimateColorInfo() {
    }

    public ClimateColorInfo(float r, float g, float b, float a) {
        this(r, g, b, a, r, g, b, a);
    }

    public ClimateColorInfo(float r, float g, float b, float a, float r2, float g2, float b2, float a2) {
        this.interior.r = r;
        this.interior.g = g;
        this.interior.b = b;
        this.interior.a = a;
        this.exterior.r = r2;
        this.exterior.g = g2;
        this.exterior.b = b2;
        this.exterior.a = a2;
    }

    public void setInterior(Color other) {
        this.interior.set(other);
    }

    public void setInterior(float r, float g, float b, float a) {
        this.interior.r = r;
        this.interior.g = g;
        this.interior.b = b;
        this.interior.a = a;
    }

    public Color getInterior() {
        return this.interior;
    }

    public void setExterior(Color other) {
        this.exterior.set(other);
    }

    public void setExterior(float r, float g, float b, float a) {
        this.exterior.r = r;
        this.exterior.g = g;
        this.exterior.b = b;
        this.exterior.a = a;
    }

    public Color getExterior() {
        return this.exterior;
    }

    public void setTo(ClimateColorInfo other) {
        this.interior.set(other.interior);
        this.exterior.set(other.exterior);
    }

    public ClimateColorInfo interp(ClimateColorInfo to, float t, ClimateColorInfo result) {
        this.interior.interp(to.interior, t, result.interior);
        this.exterior.interp(to.exterior, t, result.exterior);
        return result;
    }

    public void scale(float val) {
        this.interior.scale(val);
        this.exterior.scale(val);
    }

    public static ClimateColorInfo interp(ClimateColorInfo source, ClimateColorInfo target, float t, ClimateColorInfo resultColorInfo) {
        return source.interp(target, t, resultColorInfo);
    }

    public void write(ByteBuffer output) {
        output.putFloat(this.interior.r);
        output.putFloat(this.interior.g);
        output.putFloat(this.interior.b);
        output.putFloat(this.interior.a);
        output.putFloat(this.exterior.r);
        output.putFloat(this.exterior.g);
        output.putFloat(this.exterior.b);
        output.putFloat(this.exterior.a);
    }

    public void read(ByteBuffer input) {
        this.interior.r = input.getFloat();
        this.interior.g = input.getFloat();
        this.interior.b = input.getFloat();
        this.interior.a = input.getFloat();
        this.exterior.r = input.getFloat();
        this.exterior.g = input.getFloat();
        this.exterior.b = input.getFloat();
        this.exterior.a = input.getFloat();
    }

    public void save(DataOutputStream output) throws IOException {
        output.writeFloat(this.interior.r);
        output.writeFloat(this.interior.g);
        output.writeFloat(this.interior.b);
        output.writeFloat(this.interior.a);
        output.writeFloat(this.exterior.r);
        output.writeFloat(this.exterior.g);
        output.writeFloat(this.exterior.b);
        output.writeFloat(this.exterior.a);
    }

    public void load(DataInputStream input, int worldVersion) throws IOException {
        this.interior.r = input.readFloat();
        this.interior.g = input.readFloat();
        this.interior.b = input.readFloat();
        this.interior.a = input.readFloat();
        this.exterior.r = input.readFloat();
        this.exterior.g = input.readFloat();
        this.exterior.b = input.readFloat();
        this.exterior.a = input.readFloat();
    }

    public static boolean writeColorInfoConfig() {
        boolean boolean0 = false;

        try {
            String string0 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String string1 = "ClimateMain_" + string0;
            String string2 = ZomboidFileSystem.instance.getCacheDir() + File.separator + string1 + ".lua";
            DebugLog.log("Attempting to save color config to: " + string2);
            File file = new File(string2);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, false))) {
                writer = bufferedWriter;
                ClimateManager climateManager = ClimateManager.getInstance();
                write("--[[");
                write("-- Generated file (" + string1 + ")");
                write("-- Climate color configuration");
                write("-- File should be placed in: media/lua/server/Climate/ClimateMain.lua (remove date stamp)");
                write("--]]");
                bufferedWriter.newLine();
                write("ClimateMain = {};");
                write("ClimateMain.versionStamp = \"" + string0 + "\";");
                bufferedWriter.newLine();
                write("local WARM,NORMAL,CLOUDY = 0,1,2;");
                bufferedWriter.newLine();
                write("local SUMMER,FALL,WINTER,SPRING = 0,1,2,3;");
                bufferedWriter.newLine();
                write("function ClimateMain.onClimateManagerInit(_clim)");
                byte byte0 = 1;
                write(byte0, "local c;");
                write(byte0, "c = _clim:getColNightNoMoon();");
                writeColor(byte0, climateManager.getColNightNoMoon());
                bufferedWriter.newLine();
                write(byte0, "c = _clim:getColNightMoon();");
                writeColor(byte0, climateManager.getColNightMoon());
                bufferedWriter.newLine();
                write(byte0, "c = _clim:getColFog();");
                writeColor(byte0, climateManager.getColFog());
                bufferedWriter.newLine();
                write(byte0, "c = _clim:getColFogLegacy();");
                writeColor(byte0, climateManager.getColFogLegacy());
                bufferedWriter.newLine();
                write(byte0, "c = _clim:getColFogNew();");
                writeColor(byte0, climateManager.getColFogNew());
                bufferedWriter.newLine();
                write(byte0, "c = _clim:getFogTintStorm();");
                writeColor(byte0, climateManager.getFogTintStorm());
                bufferedWriter.newLine();
                write(byte0, "c = _clim:getFogTintTropical();");
                writeColor(byte0, climateManager.getFogTintTropical());
                bufferedWriter.newLine();
                WeatherPeriod weatherPeriod = climateManager.getWeatherPeriod();
                write(byte0, "local w = _clim:getWeatherPeriod();");
                bufferedWriter.newLine();
                write(byte0, "c = w:getCloudColorReddish();");
                writeColor(byte0, weatherPeriod.getCloudColorReddish());
                bufferedWriter.newLine();
                write(byte0, "c = w:getCloudColorGreenish();");
                writeColor(byte0, weatherPeriod.getCloudColorGreenish());
                bufferedWriter.newLine();
                write(byte0, "c = w:getCloudColorBlueish();");
                writeColor(byte0, weatherPeriod.getCloudColorBlueish());
                bufferedWriter.newLine();
                write(byte0, "c = w:getCloudColorPurplish();");
                writeColor(byte0, weatherPeriod.getCloudColorPurplish());
                bufferedWriter.newLine();
                write(byte0, "c = w:getCloudColorTropical();");
                writeColor(byte0, weatherPeriod.getCloudColorTropical());
                bufferedWriter.newLine();
                write(byte0, "c = w:getCloudColorBlizzard();");
                writeColor(byte0, weatherPeriod.getCloudColorBlizzard());
                bufferedWriter.newLine();
                String[] strings0 = new String[]{"Dawn", "Day", "Dusk"};
                String[] strings1 = new String[]{"SUMMER", "FALL", "WINTER", "SPRING"};
                String[] strings2 = new String[]{"WARM", "NORMAL", "CLOUDY"};

                for (int int0 = 0; int0 < 3; int0++) {
                    write(byte0, "-- ###################### " + strings0[int0] + " ######################");

                    for (int int1 = 0; int1 < 4; int1++) {
                        for (int int2 = 0; int2 < 3; int2++) {
                            if (int2 == 0 || int2 == 2 || int2 == 1 && int0 == 2) {
                                ClimateColorInfo climateColorInfo = climateManager.getSeasonColor(int0, int2, int1);
                                writeSeasonColor(byte0, climateColorInfo, strings0[int0], strings1[int1], strings2[int2]);
                                bufferedWriter.newLine();
                            }
                        }
                    }
                }

                write("end");
                bufferedWriter.newLine();
                write("Events.OnClimateManagerInit.Add(ClimateMain.onClimateManagerInit);");
                writer = null;
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (Exception exception0) {
                exception0.printStackTrace();
            }
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }

        return boolean0;
    }

    private static void writeSeasonColor(int int0, ClimateColorInfo climateColorInfo, String string2, String string0, String string1) throws IOException {
        Color color = climateColorInfo.exterior;
        write(
            int0,
            "_clim:setSeasonColor"
                + string2
                + "("
                + string1
                + ","
                + string0
                + ","
                + color.r
                + ","
                + color.g
                + ","
                + color.b
                + ","
                + color.a
                + ",true);\t\t--exterior"
        );
        color = climateColorInfo.interior;
        write(
            int0,
            "_clim:setSeasonColor"
                + string2
                + "("
                + string1
                + ","
                + string0
                + ","
                + color.r
                + ","
                + color.g
                + ","
                + color.b
                + ","
                + color.a
                + ",false);\t\t--interior"
        );
    }

    private static void writeColor(int int0, ClimateColorInfo climateColorInfo) throws IOException {
        Color color = climateColorInfo.exterior;
        write(int0, "c:setExterior(" + color.r + "," + color.g + "," + color.b + "," + color.a + ");");
        color = climateColorInfo.interior;
        write(int0, "c:setInterior(" + color.r + "," + color.g + "," + color.b + "," + color.a + ");");
    }

    private static void write(int int0, String string1) throws IOException {
        String string0 = new String(new char[int0]).replace("\u0000", "\t");
        writer.write(string0);
        writer.write(string1);
        writer.newLine();
    }

    private static void write(String string) throws IOException {
        writer.write(string);
        writer.newLine();
    }
}
