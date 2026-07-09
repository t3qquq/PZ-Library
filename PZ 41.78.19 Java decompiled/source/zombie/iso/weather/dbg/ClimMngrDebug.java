// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather.dbg;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.weather.ClimateManager;
import zombie.iso.weather.SimplexNoise;
import zombie.iso.weather.WeatherPeriod;
import zombie.network.GameClient;

public class ClimMngrDebug extends ClimateManager {
    private GregorianCalendar calendar;
    private double worldAgeHours = 0.0;
    private double worldAgeHoursStart = 0.0;
    private double weatherPeriodTime = 0.0;
    private double simplexOffsetA;
    private ClimateManager.AirFront currentFront;
    private WeatherPeriod weatherPeriod;
    private boolean tickIsDayChange = false;
    public ArrayList<ClimMngrDebug.RunInfo> runs = new ArrayList<>();
    private ClimMngrDebug.RunInfo currentRun;
    private ErosionSeason season;
    private int TotalDaysPeriodIndexMod = 5;
    private boolean DoOverrideSandboxRainMod = false;
    private int SandboxRainModOverride = 3;
    private int durDays = 0;
    private static final int WEATHER_NORMAL = 0;
    private static final int WEATHER_STORM = 1;
    private static final int WEATHER_TROPICAL = 2;
    private static final int WEATHER_BLIZZARD = 3;
    private FileWriter writer;

    public ClimMngrDebug() {
        this.currentFront = new ClimateManager.AirFront();
        this.weatherPeriod = new WeatherPeriod(this, null);
        this.weatherPeriod.setPrintStuff(false);
    }

    public void setRainModOverride(int int0) {
        this.DoOverrideSandboxRainMod = true;
        this.SandboxRainModOverride = int0;
    }

    public void unsetRainModOverride() {
        this.DoOverrideSandboxRainMod = false;
        this.SandboxRainModOverride = 3;
    }

    public void SimulateDays(int int0, int int1) {
        this.durDays = int0;
        DebugLog.log("Starting " + int1 + " simulations of " + int0 + " days per run...");
        byte byte0 = 0;
        byte byte1 = 0;
        DebugLog.log("Year: " + GameTime.instance.getYear() + ", Month: " + byte0 + ", Day: " + byte1);

        for (int int2 = 0; int2 < int1; int2++) {
            this.calendar = new GregorianCalendar(GameTime.instance.getYear(), byte0, byte1, 0, 0);
            this.season = ClimateManager.getInstance().getSeason().clone();
            this.season
                .init(
                    this.season.getLat(),
                    this.season.getTempMax(),
                    this.season.getTempMin(),
                    this.season.getTempDiff(),
                    this.season.getSeasonLag(),
                    this.season.getHighNoon(),
                    Rand.Next(0, 255),
                    Rand.Next(0, 255),
                    Rand.Next(0, 255)
                );
            this.simplexOffsetA = Rand.Next(0, 8000);
            this.worldAgeHours = 250.0;
            this.weatherPeriodTime = this.worldAgeHours;
            this.worldAgeHoursStart = this.worldAgeHours;
            double double0 = this.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier());
            float float0 = (float)SimplexNoise.noise(this.simplexOffsetA, this.worldAgeHours / double0);
            int int3 = float0 < 0.0F ? -1 : 1;
            this.currentFront.setFrontType(int3);
            this.weatherPeriod.stopWeatherPeriod();
            double double1 = this.worldAgeHours + 24.0;
            int int4 = int0 * 24;
            this.currentRun = new ClimMngrDebug.RunInfo();
            this.currentRun.durationDays = int0;
            this.currentRun.durationHours = int4;
            this.currentRun.seedA = this.simplexOffsetA;
            this.runs.add(this.currentRun);

            for (int int5 = 0; int5 < int4; int5++) {
                this.tickIsDayChange = false;
                this.worldAgeHours++;
                if (this.worldAgeHours >= double1) {
                    this.tickIsDayChange = true;
                    double1 += 24.0;
                    this.calendar.add(5, 1);
                    int int6 = this.calendar.get(5);
                    int int7 = this.calendar.get(2);
                    int int8 = this.calendar.get(1);
                    this.season.setDay(int6, int7, int8);
                }

                this.update_sim();
            }
        }

        this.saveData();
    }

    private void update_sim() {
        double double0 = this.getAirMassNoiseFrequencyMod(SandboxOptions.instance.getRainModifier());
        float float0 = (float)SimplexNoise.noise(this.simplexOffsetA, this.worldAgeHours / double0);
        int int0 = float0 < 0.0F ? -1 : 1;
        if (this.currentFront.getType() != int0) {
            if (this.worldAgeHours > this.weatherPeriodTime) {
                this.weatherPeriod.initSimulationDebug(this.currentFront, this.worldAgeHours);
                this.recordAndCloseWeatherPeriod();
            }

            this.currentFront.setFrontType(int0);
        }

        if (!WINTER_IS_COMING
            && !THE_DESCENDING_FOG
            && this.worldAgeHours >= this.worldAgeHoursStart + 72.0
            && this.worldAgeHours <= this.worldAgeHoursStart + 96.0
            && !this.weatherPeriod.isRunning()
            && this.worldAgeHours > this.weatherPeriodTime
            && Rand.Next(0, 1000) < 50) {
            this.triggerCustomWeatherStage(3, 10.0F);
        }

        if (this.tickIsDayChange) {
            double double1 = Math.floor(this.worldAgeHours) + 12.0;
            float float1 = (float)SimplexNoise.noise(this.simplexOffsetA, double1 / double0);
            int0 = float1 < 0.0F ? -1 : 1;
            if (int0 == this.currentFront.getType()) {
                this.currentFront.addDaySample(float1);
            }
        }
    }

    private void recordAndCloseWeatherPeriod() {
        if (this.weatherPeriod.isRunning()) {
            if (this.worldAgeHours - this.weatherPeriodTime > 0.0) {
                this.currentRun.addRecord(this.worldAgeHours - this.weatherPeriodTime);
            }

            this.weatherPeriodTime = this.worldAgeHours + Math.ceil(this.weatherPeriod.getDuration());
            boolean boolean0 = false;
            boolean boolean1 = false;
            boolean boolean2 = false;

            for (WeatherPeriod.WeatherStage weatherStage : this.weatherPeriod.getWeatherStages()) {
                if (weatherStage.getStageID() == 3) {
                    boolean0 = true;
                }

                if (weatherStage.getStageID() == 8) {
                    boolean1 = true;
                }

                if (weatherStage.getStageID() == 7) {
                    boolean2 = true;
                }
            }

            this.currentRun
                .addRecord(
                    this.currentFront.getType(),
                    this.weatherPeriod.getDuration(),
                    this.weatherPeriod.getFrontCache().getStrength(),
                    boolean0,
                    boolean1,
                    boolean2
                );
        }

        this.weatherPeriod.stopWeatherPeriod();
    }

    @Override
    public boolean triggerCustomWeatherStage(int int0, float float0) {
        if (!GameClient.bClient && !this.weatherPeriod.isRunning()) {
            ClimateManager.AirFront airFront = new ClimateManager.AirFront();
            airFront.setFrontType(1);
            airFront.setStrength(0.95F);
            this.weatherPeriod.initSimulationDebug(airFront, this.worldAgeHours, int0, float0);
            this.recordAndCloseWeatherPeriod();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected double getAirMassNoiseFrequencyMod(int int0) {
        return this.DoOverrideSandboxRainMod ? super.getAirMassNoiseFrequencyMod(this.SandboxRainModOverride) : super.getAirMassNoiseFrequencyMod(int0);
    }

    @Override
    protected float getRainTimeMultiplierMod(int int0) {
        return this.DoOverrideSandboxRainMod ? super.getRainTimeMultiplierMod(this.SandboxRainModOverride) : super.getRainTimeMultiplierMod(int0);
    }

    @Override
    public ErosionSeason getSeason() {
        return this.season;
    }

    @Override
    public float getDayMeanTemperature() {
        return this.season.getDayMeanTemperature();
    }

    @Override
    public void resetOverrides() {
    }

    private ClimMngrDebug.RunInfo calculateTotal() {
        ClimMngrDebug.RunInfo runInfo0 = new ClimMngrDebug.RunInfo();
        runInfo0.totalDaysPeriod = new int[50];
        double double0 = 0.0;
        double double1 = 0.0;
        float float0 = 0.0F;
        float float1 = 0.0F;
        float float2 = 0.0F;

        for (ClimMngrDebug.RunInfo runInfo1 : this.runs) {
            if (runInfo1.totalPeriodDuration < runInfo0.mostDryPeriod) {
                runInfo0.mostDryPeriod = runInfo1.totalPeriodDuration;
            }

            if (runInfo1.totalPeriodDuration > runInfo0.mostWetPeriod) {
                runInfo0.mostWetPeriod = runInfo1.totalPeriodDuration;
            }

            runInfo0.totalPeriodDuration = runInfo0.totalPeriodDuration + runInfo1.totalPeriodDuration;
            if (runInfo1.longestPeriod > runInfo0.longestPeriod) {
                runInfo0.longestPeriod = runInfo1.longestPeriod;
            }

            if (runInfo1.shortestPeriod < runInfo0.shortestPeriod) {
                runInfo0.shortestPeriod = runInfo1.shortestPeriod;
            }

            runInfo0.totalPeriods = runInfo0.totalPeriods + runInfo1.totalPeriods;
            runInfo0.averagePeriod = runInfo0.averagePeriod + runInfo1.averagePeriod;
            if (runInfo1.longestEmpty > runInfo0.longestEmpty) {
                runInfo0.longestEmpty = runInfo1.longestEmpty;
            }

            if (runInfo1.shortestEmpty < runInfo0.shortestEmpty) {
                runInfo0.shortestEmpty = runInfo1.shortestEmpty;
            }

            runInfo0.totalEmpty = runInfo0.totalEmpty + runInfo1.totalEmpty;
            runInfo0.averageEmpty = runInfo0.averageEmpty + runInfo1.averageEmpty;
            if (runInfo1.highestStrength > runInfo0.highestStrength) {
                runInfo0.highestStrength = runInfo1.highestStrength;
            }

            if (runInfo1.lowestStrength < runInfo0.lowestStrength) {
                runInfo0.lowestStrength = runInfo1.lowestStrength;
            }

            runInfo0.averageStrength = runInfo0.averageStrength + runInfo1.averageStrength;
            if (runInfo1.highestWarmStrength > runInfo0.highestWarmStrength) {
                runInfo0.highestWarmStrength = runInfo1.highestWarmStrength;
            }

            if (runInfo1.lowestWarmStrength < runInfo0.lowestWarmStrength) {
                runInfo0.lowestWarmStrength = runInfo1.lowestWarmStrength;
            }

            runInfo0.averageWarmStrength = runInfo0.averageWarmStrength + runInfo1.averageWarmStrength;
            if (runInfo1.highestColdStrength > runInfo0.highestColdStrength) {
                runInfo0.highestColdStrength = runInfo1.highestColdStrength;
            }

            if (runInfo1.lowestColdStrength < runInfo0.lowestColdStrength) {
                runInfo0.lowestColdStrength = runInfo1.lowestColdStrength;
            }

            runInfo0.averageColdStrength = runInfo0.averageColdStrength + runInfo1.averageColdStrength;
            runInfo0.countNormalWarm = runInfo0.countNormalWarm + runInfo1.countNormalWarm;
            runInfo0.countNormalCold = runInfo0.countNormalCold + runInfo1.countNormalCold;
            runInfo0.countStorm = runInfo0.countStorm + runInfo1.countStorm;
            runInfo0.countTropical = runInfo0.countTropical + runInfo1.countTropical;
            runInfo0.countBlizzard = runInfo0.countBlizzard + runInfo1.countBlizzard;

            for (int int0 = 0; int0 < runInfo1.dayCountPeriod.length; int0++) {
                runInfo0.dayCountPeriod[int0] = runInfo0.dayCountPeriod[int0] + runInfo1.dayCountPeriod[int0];
            }

            for (int int1 = 0; int1 < runInfo1.dayCountWarmPeriod.length; int1++) {
                runInfo0.dayCountWarmPeriod[int1] = runInfo0.dayCountWarmPeriod[int1] + runInfo1.dayCountWarmPeriod[int1];
            }

            for (int int2 = 0; int2 < runInfo1.dayCountColdPeriod.length; int2++) {
                runInfo0.dayCountColdPeriod[int2] = runInfo0.dayCountColdPeriod[int2] + runInfo1.dayCountColdPeriod[int2];
            }

            for (int int3 = 0; int3 < runInfo1.dayCountEmpty.length; int3++) {
                runInfo0.dayCountEmpty[int3] = runInfo0.dayCountEmpty[int3] + runInfo1.dayCountEmpty[int3];
            }

            for (int int4 = 0; int4 < runInfo1.exceedingPeriods.size(); int4++) {
                runInfo0.exceedingPeriods.add(runInfo1.exceedingPeriods.get(int4));
            }

            for (int int5 = 0; int5 < runInfo1.exceedingEmpties.size(); int5++) {
                runInfo0.exceedingEmpties.add(runInfo1.exceedingEmpties.get(int5));
            }

            int int6 = (int)(runInfo1.totalPeriodDuration / (this.TotalDaysPeriodIndexMod * 24));
            if (int6 < runInfo0.totalDaysPeriod.length) {
                runInfo0.totalDaysPeriod[int6]++;
            } else {
                DebugLog.log("Total days Period is longer than allowed array, days = " + int6 * this.TotalDaysPeriodIndexMod);
            }
        }

        if (this.runs.size() > 0) {
            int int7 = this.runs.size();
            runInfo0.totalPeriodDuration /= int7;
            runInfo0.averagePeriod /= int7;
            runInfo0.averageEmpty /= int7;
            runInfo0.averageStrength /= int7;
            runInfo0.averageWarmStrength /= int7;
            runInfo0.averageColdStrength /= int7;
        }

        return runInfo0;
    }

    private void saveData() {
        if (this.runs.size() > 0) {
            try {
                for (ClimMngrDebug.RunInfo runInfo0 : this.runs) {
                    runInfo0.calculate();
                }

                ClimMngrDebug.RunInfo runInfo1 = this.calculateTotal();
                String string0 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                ZomboidFileSystem.instance.getFileInCurrentSave("climate").mkdirs();
                File file0 = ZomboidFileSystem.instance.getFileInCurrentSave("climate");
                if (file0.exists() && file0.isDirectory()) {
                    String string1 = ZomboidFileSystem.instance.getFileNameInCurrentSave("climate", string0 + ".txt");
                    DebugLog.log("Attempting to save test data to: " + string1);
                    File file1 = new File(string1);
                    DebugLog.log("Saving climate test data: " + string1);

                    try (FileWriter fileWriter0 = new FileWriter(file1, false)) {
                        this.writer = fileWriter0;
                        int int0 = this.runs.size();
                        this.write("Simulation results." + System.lineSeparator());
                        this.write("Runs: " + this.runs.size() + ", days per cycle: " + this.durDays);
                        if (this.DoOverrideSandboxRainMod) {
                            this.write("RainModifier used: " + this.SandboxRainModOverride);
                        } else {
                            this.write("RainModifier used: " + SandboxOptions.instance.getRainModifier());
                        }

                        this.write("");
                        this.write("===================================================================");
                        this.write(" TOTALS OVERVIEW");
                        this.write("===================================================================");
                        this.write("");
                        this.write("Total weather periods: " + runInfo1.totalPeriods + ", average per cycle: " + runInfo1.totalPeriods / int0);
                        this.write("Longest weather: " + this.formatDuration(runInfo1.longestPeriod));
                        this.write("Shortest weather: " + this.formatDuration(runInfo1.shortestPeriod));
                        this.write("Average weather: " + this.formatDuration(runInfo1.averagePeriod));
                        this.write("");
                        this.write("Average total weather days per cycle: " + this.formatDuration(runInfo1.totalPeriodDuration));
                        this.write("");
                        this.write("Driest cycle total weather days: " + this.formatDuration(runInfo1.mostDryPeriod));
                        this.write("Wettest cycle total weather days: " + this.formatDuration(runInfo1.mostWetPeriod));
                        this.write("");
                        this.write("Total clear periods: " + runInfo1.totalEmpty + ", average per cycle: " + runInfo1.totalEmpty / int0);
                        this.write("Longest clear: " + this.formatDuration(runInfo1.longestEmpty));
                        this.write("Shortest clear: " + this.formatDuration(runInfo1.shortestEmpty));
                        this.write("Average clear: " + this.formatDuration(runInfo1.averageEmpty));
                        this.write("");
                        this.write("Highest Front strength: " + runInfo1.highestStrength);
                        this.write("Lowest Front strength: " + runInfo1.lowestStrength);
                        this.write("Average Front strength: " + runInfo1.averageStrength);
                        this.write("");
                        this.write("Highest WarmFront strength: " + runInfo1.highestWarmStrength);
                        this.write("Lowest WarmFront strength: " + runInfo1.lowestWarmStrength);
                        this.write("Average WarmFront strength: " + runInfo1.averageWarmStrength);
                        this.write("");
                        this.write("Highest ColdFront strength: " + runInfo1.highestColdStrength);
                        this.write("Lowest ColdFront strength: " + runInfo1.lowestColdStrength);
                        this.write("Average ColdFront strength: " + runInfo1.averageColdStrength);
                        this.write("");
                        this.write("Weather period types:");
                        double double0 = int0;
                        this.write("Normal warm: " + runInfo1.countNormalWarm + ", average: " + this.round(runInfo1.countNormalWarm / double0));
                        this.write("Normal cold: " + runInfo1.countNormalCold + ", average: " + this.round(runInfo1.countNormalCold / double0));
                        this.write("Normal storm: " + runInfo1.countStorm + ", average: " + this.round((double)runInfo1.countStorm / int0));
                        this.write("Normal tropical: " + runInfo1.countTropical + ", average: " + this.round(runInfo1.countTropical / double0));
                        this.write("Normal blizzard: " + runInfo1.countBlizzard + ", average: " + this.round(runInfo1.countBlizzard / double0));
                        this.write("");
                        this.write("Distribution duration in days (total periods)");
                        this.printCountTable(fileWriter0, runInfo1.dayCountPeriod);
                        this.write("");
                        this.write("Distribution duration in days (WARM periods)");
                        this.printCountTable(fileWriter0, runInfo1.dayCountWarmPeriod);
                        this.write("");
                        this.write("Distribution duration in days (COLD periods)");
                        this.printCountTable(fileWriter0, runInfo1.dayCountColdPeriod);
                        this.write("");
                        this.write("Distribution duration in days (clear periods)");
                        this.printCountTable(fileWriter0, runInfo1.dayCountEmpty);
                        this.write("");
                        this.write("Amount of weather periods exceeding threshold: " + runInfo1.exceedingPeriods.size());
                        if (runInfo1.exceedingPeriods.size() > 0) {
                            for (Integer integer0 : runInfo1.exceedingPeriods) {
                                this.writer.write(integer0 + " days, ");
                            }
                        }

                        this.write("");
                        this.write("");
                        this.write("Amount of clear periods exceeding threshold: " + runInfo1.exceedingEmpties.size());
                        if (runInfo1.exceedingEmpties.size() > 0) {
                            for (Integer integer1 : runInfo1.exceedingEmpties) {
                                this.writer.write(integer1 + " days, ");
                            }
                        }

                        this.write("");
                        this.write("");
                        this.write("Distribution duration total weather days:");
                        this.printCountTable(this.writer, runInfo1.totalDaysPeriod, this.TotalDaysPeriodIndexMod);
                        this.writeDataExtremes();
                        this.writer = null;
                    } catch (Exception exception0) {
                        exception0.printStackTrace();
                    }

                    file1 = ZomboidFileSystem.instance.getFileInCurrentSave("climate", string0 + "_DATA.txt");

                    try (FileWriter fileWriter1 = new FileWriter(file1, false)) {
                        this.writer = fileWriter1;
                        this.writeData();
                        this.writer = null;
                    } catch (Exception exception1) {
                        exception1.printStackTrace();
                    }

                    file1 = ZomboidFileSystem.instance.getFileInCurrentSave("climate", string0 + "_PATTERNS.txt");

                    try (FileWriter fileWriter2 = new FileWriter(file1, false)) {
                        this.writer = fileWriter2;
                        this.writePatterns();
                        this.writer = null;
                    } catch (Exception exception2) {
                        exception2.printStackTrace();
                    }
                }
            } catch (Exception exception3) {
                exception3.printStackTrace();
            }
        }
    }

    private double round(double double0) {
        return Math.round(double0 * 100.0) / 100.0;
    }

    private void writeRunInfo(ClimMngrDebug.RunInfo runInfo, int int0) throws Exception {
        this.write("===================================================================");
        this.write(" RUN NR: " + int0);
        this.write("===================================================================");
        this.write("");
        this.write("Total weather periods: " + runInfo.totalPeriods);
        this.write("Longest weather: " + this.formatDuration(runInfo.longestPeriod));
        this.write("Shortest weather: " + this.formatDuration(runInfo.shortestPeriod));
        this.write("Average weather: " + this.formatDuration(runInfo.averagePeriod));
        this.write("");
        this.write("Total weather days for cycle: " + this.formatDuration(runInfo.totalPeriodDuration));
        this.write("");
        this.write("Total clear periods: " + runInfo.totalEmpty);
        this.write("Longest clear: " + this.formatDuration(runInfo.longestEmpty));
        this.write("Shortest clear: " + this.formatDuration(runInfo.shortestEmpty));
        this.write("Average clear: " + this.formatDuration(runInfo.averageEmpty));
        this.write("");
        this.write("Highest Front strength: " + runInfo.highestStrength);
        this.write("Lowest Front strength: " + runInfo.lowestStrength);
        this.write("Average Front strength: " + runInfo.averageStrength);
        this.write("");
        this.write("Highest WarmFront strength: " + runInfo.highestWarmStrength);
        this.write("Lowest WarmFront strength: " + runInfo.lowestWarmStrength);
        this.write("Average WarmFront strength: " + runInfo.averageWarmStrength);
        this.write("");
        this.write("Highest ColdFront strength: " + runInfo.highestColdStrength);
        this.write("Lowest ColdFront strength: " + runInfo.lowestColdStrength);
        this.write("Average ColdFront strength: " + runInfo.averageColdStrength);
        this.write("");
        this.write("Weather period types:");
        this.write("Normal warm: " + runInfo.countNormalWarm);
        this.write("Normal cold: " + runInfo.countNormalCold);
        this.write("Normal storm: " + runInfo.countStorm);
        this.write("Normal tropical: " + runInfo.countTropical);
        this.write("Normal blizzard: " + runInfo.countBlizzard);
        this.write("");
        this.write("Distribution duration in days (total periods)");
        this.printCountTable(this.writer, runInfo.dayCountPeriod);
        this.write("");
        this.write("Distribution duration in days (WARM periods)");
        this.printCountTable(this.writer, runInfo.dayCountWarmPeriod);
        this.write("");
        this.write("Distribution duration in days (COLD periods)");
        this.printCountTable(this.writer, runInfo.dayCountColdPeriod);
        this.write("");
        this.write("Distribution duration in days (clear periods)");
        this.printCountTable(this.writer, runInfo.dayCountEmpty);
        this.write("");
        this.write("Amount of weather periods exceeding threshold: " + runInfo.exceedingPeriods.size());
        if (runInfo.exceedingPeriods.size() > 0) {
            for (Integer integer0 : runInfo.exceedingPeriods) {
                this.write(integer0 + " days.");
            }
        }

        this.write("");
        this.write("Amount of clear periods exceeding threshold: " + runInfo.exceedingEmpties.size());
        if (runInfo.exceedingEmpties.size() > 0) {
            for (Integer integer1 : runInfo.exceedingEmpties) {
                this.write(integer1 + " days.");
            }
        }
    }

    private void write(String string) throws Exception {
        this.writer.write(string + System.lineSeparator());
    }

    private void writeDataExtremes() throws Exception {
        int int0 = 0;
        int int1 = -1;
        int int2 = -1;
        ClimMngrDebug.RunInfo runInfo0 = null;
        ClimMngrDebug.RunInfo runInfo1 = null;

        for (ClimMngrDebug.RunInfo runInfo2 : this.runs) {
            int0++;
            if (runInfo0 == null || runInfo2.totalPeriodDuration < runInfo0.totalPeriodDuration) {
                runInfo0 = runInfo2;
                int1 = int0;
            }

            if (runInfo1 == null || runInfo2.totalPeriodDuration > runInfo1.totalPeriodDuration) {
                runInfo1 = runInfo2;
                int2 = int0;
            }
        }

        this.write("");
        this.write("MOST DRY RUN:");
        if (runInfo0 != null) {
            this.writeRunInfo(runInfo0, int1);
        }

        this.write("");
        this.write("MOST WET RUN:");
        if (runInfo1 != null) {
            this.writeRunInfo(runInfo1, int2);
        }
    }

    private void writeData() throws Exception {
        int int0 = 0;

        for (ClimMngrDebug.RunInfo runInfo : this.runs) {
            this.writeRunInfo(runInfo, ++int0);
        }
    }

    private void writePatterns() throws Exception {
        String string0 = "-";
        String string1 = "#";
        String string2 = "S";
        String string3 = "T";
        String string4 = "B";
        int int0 = 0;
        int int1 = 0;

        for (ClimMngrDebug.RunInfo runInfo : this.runs) {
            int1 = 0;

            for (ClimMngrDebug.RecordInfo recordInfo : runInfo.records) {
                int0 = (int)Math.ceil(recordInfo.durationHours / 24.0);
                String string5;
                if (recordInfo.isWeather && recordInfo.weatherType == 1) {
                    string5 = new String(new char[int0]).replace("\u0000", string2);
                } else if (recordInfo.isWeather && recordInfo.weatherType == 2) {
                    string5 = new String(new char[int0]).replace("\u0000", string3);
                } else if (recordInfo.isWeather && recordInfo.weatherType == 3) {
                    string5 = new String(new char[int0]).replace("\u0000", string4);
                } else if (int1 == 0 && !recordInfo.isWeather && int0 >= 2) {
                    string5 = new String(new char[int0 - 1]).replace("\u0000", string0);
                } else {
                    string5 = new String(new char[int0]).replace("\u0000", recordInfo.isWeather ? string1 : string0);
                }

                this.writer.write(string5);
                int1++;
            }

            this.writer.write(System.lineSeparator());
        }
    }

    private void printCountTable(FileWriter fileWriter, int[] ints) throws Exception {
        this.printCountTable(fileWriter, ints, 1);
    }

    private void printCountTable(FileWriter var1, int[] ints, int int3) throws Exception {
        if (ints != null && ints.length > 0) {
            int int0 = 0;

            for (int int1 = 0; int1 < ints.length; int1++) {
                if (ints[int1] > int0) {
                    int0 = ints[int1];
                }
            }

            this.write("    DAYS   COUNT GRAPH");
            float float0 = 50.0F / int0;
            if (int0 > 0) {
                for (int int2 = 0; int2 < ints.length; int2++) {
                    String string = "";
                    string = string + String.format("%1$8s", int2 * int3 + "-" + (int2 * int3 + int3));
                    int int4 = ints[int2];
                    string = string + String.format("%1$8s", int4);
                    string = string + " ";
                    int int5 = (int)(int4 * float0);
                    if (int5 > 0) {
                        string = string + new String(new char[int5]).replace("\u0000", "#");
                    } else if (int4 > 0) {
                        string = string + "*";
                    }

                    this.write(string);
                }
            }
        }
    }

    private String formatDuration(double double0) {
        int int0 = (int)(double0 / 24.0);
        int int1 = (int)(double0 - int0 * 24);
        return int0 + " days, " + int1 + " hours.";
    }

    private class RecordInfo {
        public boolean isWeather;
        public float strength;
        public int airType;
        public double durationHours;
        public int weatherType = 0;
    }

    private class RunInfo {
        public double seedA;
        public int durationDays;
        public double durationHours;
        public ArrayList<ClimMngrDebug.RecordInfo> records = new ArrayList<>();
        public double totalPeriodDuration = 0.0;
        public double longestPeriod = 0.0;
        public double shortestPeriod = 9.99999999E8;
        public int totalPeriods = 0;
        public double averagePeriod = 0.0;
        public double longestEmpty = 0.0;
        public double shortestEmpty = 9.99999999E8;
        public int totalEmpty = 0;
        public double averageEmpty = 0.0;
        public float highestStrength = 0.0F;
        public float lowestStrength = 1.0F;
        public float averageStrength = 0.0F;
        public float highestWarmStrength = 0.0F;
        public float lowestWarmStrength = 1.0F;
        public float averageWarmStrength = 0.0F;
        public float highestColdStrength = 0.0F;
        public float lowestColdStrength = 1.0F;
        public float averageColdStrength = 0.0F;
        public int countNormalWarm = 0;
        public int countNormalCold = 0;
        public int countStorm = 0;
        public int countTropical = 0;
        public int countBlizzard = 0;
        public int[] dayCountPeriod = new int[16];
        public int[] dayCountWarmPeriod = new int[16];
        public int[] dayCountColdPeriod = new int[16];
        public int[] dayCountEmpty = new int[75];
        public ArrayList<Integer> exceedingPeriods = new ArrayList<>();
        public ArrayList<Integer> exceedingEmpties = new ArrayList<>();
        public double mostWetPeriod = 0.0;
        public double mostDryPeriod = 9.99999999E8;
        public int[] totalDaysPeriod;

        public ClimMngrDebug.RecordInfo addRecord(double double0) {
            ClimMngrDebug.RecordInfo recordInfo = ClimMngrDebug.this.new RecordInfo();
            recordInfo.durationHours = double0;
            recordInfo.isWeather = false;
            this.records.add(recordInfo);
            return recordInfo;
        }

        public ClimMngrDebug.RecordInfo addRecord(int int0, double double0, float float0, boolean boolean0, boolean boolean1, boolean boolean2) {
            ClimMngrDebug.RecordInfo recordInfo = ClimMngrDebug.this.new RecordInfo();
            recordInfo.durationHours = double0;
            recordInfo.isWeather = true;
            recordInfo.airType = int0;
            recordInfo.strength = float0;
            recordInfo.weatherType = 0;
            if (boolean0) {
                recordInfo.weatherType = 1;
            } else if (boolean1) {
                recordInfo.weatherType = 2;
            } else if (boolean2) {
                recordInfo.weatherType = 3;
            }

            this.records.add(recordInfo);
            return recordInfo;
        }

        public void calculate() {
            double double0 = 0.0;
            double double1 = 0.0;
            float float0 = 0.0F;
            float float1 = 0.0F;
            float float2 = 0.0F;
            int int0 = 0;
            int int1 = 0;

            for (ClimMngrDebug.RecordInfo recordInfo : this.records) {
                int int2 = (int)(recordInfo.durationHours / 24.0);
                if (recordInfo.isWeather) {
                    this.totalPeriodDuration = this.totalPeriodDuration + recordInfo.durationHours;
                    if (recordInfo.durationHours > this.longestPeriod) {
                        this.longestPeriod = recordInfo.durationHours;
                    }

                    if (recordInfo.durationHours < this.shortestPeriod) {
                        this.shortestPeriod = recordInfo.durationHours;
                    }

                    this.totalPeriods++;
                    double0 += recordInfo.durationHours;
                    if (recordInfo.strength > this.highestStrength) {
                        this.highestStrength = recordInfo.strength;
                    }

                    if (recordInfo.strength < this.lowestStrength) {
                        this.lowestStrength = recordInfo.strength;
                    }

                    float0 += recordInfo.strength;
                    if (recordInfo.airType == 1) {
                        int0++;
                        if (recordInfo.strength > this.highestWarmStrength) {
                            this.highestWarmStrength = recordInfo.strength;
                        }

                        if (recordInfo.strength < this.lowestWarmStrength) {
                            this.lowestWarmStrength = recordInfo.strength;
                        }

                        float1 += recordInfo.strength;
                        if (recordInfo.weatherType == 1) {
                            this.countStorm++;
                        } else if (recordInfo.weatherType == 2) {
                            this.countTropical++;
                        } else if (recordInfo.weatherType == 3) {
                            this.countBlizzard++;
                        } else {
                            this.countNormalWarm++;
                        }

                        if (int2 < this.dayCountWarmPeriod.length) {
                            this.dayCountWarmPeriod[int2]++;
                        }
                    } else {
                        int1++;
                        if (recordInfo.strength > this.highestColdStrength) {
                            this.highestColdStrength = recordInfo.strength;
                        }

                        if (recordInfo.strength < this.lowestColdStrength) {
                            this.lowestColdStrength = recordInfo.strength;
                        }

                        float2 += recordInfo.strength;
                        this.countNormalCold++;
                        if (int2 < this.dayCountColdPeriod.length) {
                            this.dayCountColdPeriod[int2]++;
                        }
                    }

                    if (int2 < this.dayCountPeriod.length) {
                        this.dayCountPeriod[int2]++;
                    } else {
                        DebugLog.log("Period is longer than allowed array, days = " + int2);
                        this.exceedingPeriods.add(int2);
                    }
                } else {
                    if (recordInfo.durationHours > this.longestEmpty) {
                        this.longestEmpty = recordInfo.durationHours;
                    }

                    if (recordInfo.durationHours < this.shortestEmpty) {
                        this.shortestEmpty = recordInfo.durationHours;
                    }

                    this.totalEmpty++;
                    double1 += recordInfo.durationHours;
                    if (int2 < this.dayCountEmpty.length) {
                        this.dayCountEmpty[int2]++;
                    } else {
                        DebugLog.log("No-Weather period is longer than allowed array, days = " + int2);
                        this.exceedingEmpties.add(int2);
                    }
                }
            }

            if (this.totalPeriods > 0) {
                this.averagePeriod = double0 / this.totalPeriods;
                this.averageStrength = float0 / this.totalPeriods;
                if (int0 > 0) {
                    this.averageWarmStrength = float1 / int0;
                }

                if (int1 > 0) {
                    this.averageColdStrength = float2 / int1;
                }
            }

            if (this.totalEmpty > 0) {
                this.averageEmpty = double1 / this.totalEmpty;
            }
        }
    }
}
