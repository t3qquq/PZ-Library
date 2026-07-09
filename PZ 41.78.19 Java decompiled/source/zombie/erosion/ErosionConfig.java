// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;

public final class ErosionConfig {
    public final ErosionConfig.Seeds seeds = new ErosionConfig.Seeds();
    public final ErosionConfig.Time time = new ErosionConfig.Time();
    public final ErosionConfig.Debug debug = new ErosionConfig.Debug();
    public final ErosionConfig.Season season = new ErosionConfig.Season();

    public void save(ByteBuffer bb) {
        bb.putInt(this.seeds.seedMain_0);
        bb.putInt(this.seeds.seedMain_1);
        bb.putInt(this.seeds.seedMain_2);
        bb.putInt(this.seeds.seedMoisture_0);
        bb.putInt(this.seeds.seedMoisture_1);
        bb.putInt(this.seeds.seedMoisture_2);
        bb.putInt(this.seeds.seedMinerals_0);
        bb.putInt(this.seeds.seedMinerals_1);
        bb.putInt(this.seeds.seedMinerals_2);
        bb.putInt(this.seeds.seedKudzu_0);
        bb.putInt(this.seeds.seedKudzu_1);
        bb.putInt(this.seeds.seedKudzu_2);
        bb.putInt(this.time.tickunit);
        bb.putInt(this.time.ticks);
        bb.putInt(this.time.eticks);
        bb.putInt(this.time.epoch);
        bb.putInt(this.season.lat);
        bb.putInt(this.season.tempMax);
        bb.putInt(this.season.tempMin);
        bb.putInt(this.season.tempDiff);
        bb.putInt(this.season.seasonLag);
        bb.putFloat(this.season.noon);
        bb.putInt(this.season.seedA);
        bb.putInt(this.season.seedB);
        bb.putInt(this.season.seedC);
        bb.putFloat(this.season.jan);
        bb.putFloat(this.season.feb);
        bb.putFloat(this.season.mar);
        bb.putFloat(this.season.apr);
        bb.putFloat(this.season.may);
        bb.putFloat(this.season.jun);
        bb.putFloat(this.season.jul);
        bb.putFloat(this.season.aug);
        bb.putFloat(this.season.sep);
        bb.putFloat(this.season.oct);
        bb.putFloat(this.season.nov);
        bb.putFloat(this.season.dec);
    }

    public void load(ByteBuffer bb) {
        this.seeds.seedMain_0 = bb.getInt();
        this.seeds.seedMain_1 = bb.getInt();
        this.seeds.seedMain_2 = bb.getInt();
        this.seeds.seedMoisture_0 = bb.getInt();
        this.seeds.seedMoisture_1 = bb.getInt();
        this.seeds.seedMoisture_2 = bb.getInt();
        this.seeds.seedMinerals_0 = bb.getInt();
        this.seeds.seedMinerals_1 = bb.getInt();
        this.seeds.seedMinerals_2 = bb.getInt();
        this.seeds.seedKudzu_0 = bb.getInt();
        this.seeds.seedKudzu_1 = bb.getInt();
        this.seeds.seedKudzu_2 = bb.getInt();
        this.time.tickunit = bb.getInt();
        this.time.ticks = bb.getInt();
        this.time.eticks = bb.getInt();
        this.time.epoch = bb.getInt();
        this.season.lat = bb.getInt();
        this.season.tempMax = bb.getInt();
        this.season.tempMin = bb.getInt();
        this.season.tempDiff = bb.getInt();
        this.season.seasonLag = bb.getInt();
        this.season.noon = bb.getFloat();
        this.season.seedA = bb.getInt();
        this.season.seedB = bb.getInt();
        this.season.seedC = bb.getInt();
        this.season.jan = bb.getFloat();
        this.season.feb = bb.getFloat();
        this.season.mar = bb.getFloat();
        this.season.apr = bb.getFloat();
        this.season.may = bb.getFloat();
        this.season.jun = bb.getFloat();
        this.season.jul = bb.getFloat();
        this.season.aug = bb.getFloat();
        this.season.sep = bb.getFloat();
        this.season.oct = bb.getFloat();
        this.season.nov = bb.getFloat();
        this.season.dec = bb.getFloat();
    }

    public void writeFile(String _file) {
        ZomboidFileSystem.instance.validatePrefix(_file);

        try {
            if (Core.getInstance().isNoSave()) {
                return;
            }

            File file = new File(_file);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write("seeds.seedMain_0 = " + this.seeds.seedMain_0 + "\n");
            fileWriter.write("seeds.seedMain_1 = " + this.seeds.seedMain_1 + "\n");
            fileWriter.write("seeds.seedMain_2 = " + this.seeds.seedMain_2 + "\n");
            fileWriter.write("seeds.seedMoisture_0 = " + this.seeds.seedMoisture_0 + "\n");
            fileWriter.write("seeds.seedMoisture_1 = " + this.seeds.seedMoisture_1 + "\n");
            fileWriter.write("seeds.seedMoisture_2 = " + this.seeds.seedMoisture_2 + "\n");
            fileWriter.write("seeds.seedMinerals_0 = " + this.seeds.seedMinerals_0 + "\n");
            fileWriter.write("seeds.seedMinerals_1 = " + this.seeds.seedMinerals_1 + "\n");
            fileWriter.write("seeds.seedMinerals_2 = " + this.seeds.seedMinerals_2 + "\n");
            fileWriter.write("seeds.seedKudzu_0 = " + this.seeds.seedKudzu_0 + "\n");
            fileWriter.write("seeds.seedKudzu_1 = " + this.seeds.seedKudzu_1 + "\n");
            fileWriter.write("seeds.seedKudzu_2 = " + this.seeds.seedKudzu_2 + "\n");
            fileWriter.write("\n");
            fileWriter.write("time.tickunit = " + this.time.tickunit + "\n");
            fileWriter.write("time.ticks = " + this.time.ticks + "\n");
            fileWriter.write("time.eticks = " + this.time.eticks + "\n");
            fileWriter.write("time.epoch = " + this.time.epoch + "\n");
            fileWriter.write("\n");
            fileWriter.write("season.lat = " + this.season.lat + "\n");
            fileWriter.write("season.tempMax = " + this.season.tempMax + "\n");
            fileWriter.write("season.tempMin = " + this.season.tempMin + "\n");
            fileWriter.write("season.tempDiff = " + this.season.tempDiff + "\n");
            fileWriter.write("season.seasonLag = " + this.season.seasonLag + "\n");
            fileWriter.write("season.noon = " + this.season.noon + "\n");
            fileWriter.write("season.seedA = " + this.season.seedA + "\n");
            fileWriter.write("season.seedB = " + this.season.seedB + "\n");
            fileWriter.write("season.seedC = " + this.season.seedC + "\n");
            fileWriter.write("season.jan = " + this.season.jan + "\n");
            fileWriter.write("season.feb = " + this.season.feb + "\n");
            fileWriter.write("season.mar = " + this.season.mar + "\n");
            fileWriter.write("season.apr = " + this.season.apr + "\n");
            fileWriter.write("season.may = " + this.season.may + "\n");
            fileWriter.write("season.jun = " + this.season.jun + "\n");
            fileWriter.write("season.jul = " + this.season.jul + "\n");
            fileWriter.write("season.aug = " + this.season.aug + "\n");
            fileWriter.write("season.sep = " + this.season.sep + "\n");
            fileWriter.write("season.oct = " + this.season.oct + "\n");
            fileWriter.write("season.nov = " + this.season.nov + "\n");
            fileWriter.write("season.dec = " + this.season.dec + "\n");
            fileWriter.write("\n");
            fileWriter.write("debug.enabled = " + this.debug.enabled + "\n");
            fileWriter.write("debug.startday = " + this.debug.startday + "\n");
            fileWriter.write("debug.startmonth = " + this.debug.startmonth + "\n");
            fileWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean readFile(String _file) {
        try {
            File file = new File(_file);
            if (!file.exists()) {
                return false;
            } else {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                while (true) {
                    String string0 = bufferedReader.readLine();
                    if (string0 == null) {
                        bufferedReader.close();
                        return true;
                    }

                    if (!string0.trim().startsWith("--")) {
                        if (!string0.contains("=")) {
                            if (!string0.trim().isEmpty()) {
                                DebugLog.log("ErosionConfig: unknown \"" + string0 + "\"");
                            }
                        } else {
                            String[] strings = string0.split("=");
                            if (strings.length != 2) {
                                DebugLog.log("ErosionConfig: unknown \"" + string0 + "\"");
                            } else {
                                String string1 = strings[0].trim();
                                String string2 = strings[1].trim();
                                if (string1.startsWith("seeds.")) {
                                    if ("seeds.seedMain_0".equals(string1)) {
                                        this.seeds.seedMain_0 = Integer.parseInt(string2);
                                    } else if ("seeds.seedMain_1".equals(string1)) {
                                        this.seeds.seedMain_1 = Integer.parseInt(string2);
                                    } else if ("seeds.seedMain_2".equals(string1)) {
                                        this.seeds.seedMain_2 = Integer.parseInt(string2);
                                    } else if ("seeds.seedMoisture_0".equals(string1)) {
                                        this.seeds.seedMoisture_0 = Integer.parseInt(string2);
                                    } else if ("seeds.seedMoisture_1".equals(string1)) {
                                        this.seeds.seedMoisture_1 = Integer.parseInt(string2);
                                    } else if ("seeds.seedMoisture_2".equals(string1)) {
                                        this.seeds.seedMoisture_2 = Integer.parseInt(string2);
                                    } else if ("seeds.seedMinerals_0".equals(string1)) {
                                        this.seeds.seedMinerals_0 = Integer.parseInt(string2);
                                    } else if ("seeds.seedMinerals_1".equals(string1)) {
                                        this.seeds.seedMinerals_1 = Integer.parseInt(string2);
                                    } else if ("seeds.seedMinerals_2".equals(string1)) {
                                        this.seeds.seedMinerals_2 = Integer.parseInt(string2);
                                    } else if ("seeds.seedKudzu_0".equals(string1)) {
                                        this.seeds.seedKudzu_0 = Integer.parseInt(string2);
                                    } else if ("seeds.seedKudzu_1".equals(string1)) {
                                        this.seeds.seedKudzu_1 = Integer.parseInt(string2);
                                    } else if ("seeds.seedKudzu_2".equals(string1)) {
                                        this.seeds.seedKudzu_2 = Integer.parseInt(string2);
                                    } else {
                                        DebugLog.log("ErosionConfig: unknown \"" + string0 + "\"");
                                    }
                                } else if (string1.startsWith("time.")) {
                                    if ("time.tickunit".equals(string1)) {
                                        this.time.tickunit = Integer.parseInt(string2);
                                    } else if ("time.ticks".equals(string1)) {
                                        this.time.ticks = Integer.parseInt(string2);
                                    } else if ("time.eticks".equals(string1)) {
                                        this.time.eticks = Integer.parseInt(string2);
                                    } else if ("time.epoch".equals(string1)) {
                                        this.time.epoch = Integer.parseInt(string2);
                                    } else {
                                        DebugLog.log("ErosionConfig: unknown \"" + string0 + "\"");
                                    }
                                } else if (string1.startsWith("season.")) {
                                    if ("season.lat".equals(string1)) {
                                        this.season.lat = Integer.parseInt(string2);
                                    } else if ("season.tempMax".equals(string1)) {
                                        this.season.tempMax = Integer.parseInt(string2);
                                    } else if ("season.tempMin".equals(string1)) {
                                        this.season.tempMin = Integer.parseInt(string2);
                                    } else if ("season.tempDiff".equals(string1)) {
                                        this.season.tempDiff = Integer.parseInt(string2);
                                    } else if ("season.seasonLag".equals(string1)) {
                                        this.season.seasonLag = Integer.parseInt(string2);
                                    } else if ("season.noon".equals(string1)) {
                                        this.season.noon = Float.parseFloat(string2);
                                    } else if ("season.seedA".equals(string1)) {
                                        this.season.seedA = Integer.parseInt(string2);
                                    } else if ("season.seedB".equals(string1)) {
                                        this.season.seedB = Integer.parseInt(string2);
                                    } else if ("season.seedC".equals(string1)) {
                                        this.season.seedC = Integer.parseInt(string2);
                                    } else if ("season.jan".equals(string1)) {
                                        this.season.jan = Float.parseFloat(string2);
                                    } else if ("season.feb".equals(string1)) {
                                        this.season.feb = Float.parseFloat(string2);
                                    } else if ("season.mar".equals(string1)) {
                                        this.season.mar = Float.parseFloat(string2);
                                    } else if ("season.apr".equals(string1)) {
                                        this.season.apr = Float.parseFloat(string2);
                                    } else if ("season.may".equals(string1)) {
                                        this.season.may = Float.parseFloat(string2);
                                    } else if ("season.jun".equals(string1)) {
                                        this.season.jun = Float.parseFloat(string2);
                                    } else if ("season.jul".equals(string1)) {
                                        this.season.jul = Float.parseFloat(string2);
                                    } else if ("season.aug".equals(string1)) {
                                        this.season.aug = Float.parseFloat(string2);
                                    } else if ("season.sep".equals(string1)) {
                                        this.season.sep = Float.parseFloat(string2);
                                    } else if ("season.oct".equals(string1)) {
                                        this.season.oct = Float.parseFloat(string2);
                                    } else if ("season.nov".equals(string1)) {
                                        this.season.nov = Float.parseFloat(string2);
                                    } else if ("season.dec".equals(string1)) {
                                        this.season.dec = Float.parseFloat(string2);
                                    } else {
                                        DebugLog.log("ErosionConfig: unknown \"" + string0 + "\"");
                                    }
                                } else if (string1.startsWith("debug.")) {
                                    if ("debug.enabled".equals(string1)) {
                                        this.debug.enabled = Boolean.parseBoolean(string2);
                                    } else if ("debug.startday".equals(string1)) {
                                        this.debug.startday = Integer.parseInt(string2);
                                    } else if ("debug.startmonth".equals(string1)) {
                                        this.debug.startmonth = Integer.parseInt(string2);
                                    }
                                } else {
                                    DebugLog.log("ErosionConfig: unknown \"" + string0 + "\"");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public ErosionConfig.Debug getDebug() {
        return this.debug;
    }

    public void consolePrint() {
    }

    public static final class Debug {
        boolean enabled = false;
        int startday = 26;
        int startmonth = 11;

        public boolean getEnabled() {
            return this.enabled;
        }

        public int getStartDay() {
            return this.startday;
        }

        public int getStartMonth() {
            return this.startmonth;
        }
    }

    public static final class Season {
        int lat = 38;
        int tempMax = 25;
        int tempMin = 0;
        int tempDiff = 7;
        int seasonLag = 31;
        float noon = 12.5F;
        int seedA = 64;
        int seedB = 128;
        int seedC = 255;
        float jan = 0.39F;
        float feb = 0.35F;
        float mar = 0.39F;
        float apr = 0.4F;
        float may = 0.35F;
        float jun = 0.37F;
        float jul = 0.29F;
        float aug = 0.26F;
        float sep = 0.23F;
        float oct = 0.23F;
        float nov = 0.3F;
        float dec = 0.32F;
    }

    public static final class Seeds {
        int seedMain_0 = 16;
        int seedMain_1 = 32;
        int seedMain_2 = 64;
        int seedMoisture_0 = 96;
        int seedMoisture_1 = 128;
        int seedMoisture_2 = 144;
        int seedMinerals_0 = 196;
        int seedMinerals_1 = 255;
        int seedMinerals_2 = 0;
        int seedKudzu_0 = 200;
        int seedKudzu_1 = 125;
        int seedKudzu_2 = 50;
    }

    public static final class Time {
        int tickunit = 144;
        int ticks = 0;
        int eticks = 0;
        int epoch = 0;
    }
}
