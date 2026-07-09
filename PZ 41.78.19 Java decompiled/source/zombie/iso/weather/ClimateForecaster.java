// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import zombie.GameTime;

/**
 * TurboTuTone.
 */
public class ClimateForecaster {
    private static final int OffsetToday = 10;
    private ClimateValues climateValues;
    private ClimateForecaster.DayForecast[] forecasts = new ClimateForecaster.DayForecast[40];
    private ArrayList<ClimateForecaster.DayForecast> forecastList = new ArrayList<>(40);

    public ArrayList<ClimateForecaster.DayForecast> getForecasts() {
        return this.forecastList;
    }

    public ClimateForecaster.DayForecast getForecast() {
        return this.getForecast(0);
    }

    public ClimateForecaster.DayForecast getForecast(int offset) {
        int int0 = 10 + offset;
        return int0 >= 0 && int0 < this.forecasts.length ? this.forecasts[int0] : null;
    }

    private void populateForecastList() {
        this.forecastList.clear();

        for (int int0 = 0; int0 < this.forecasts.length; int0++) {
            this.forecastList.add(this.forecasts[int0]);
        }
    }

    protected void init(ClimateManager climateManager) {
        this.climateValues = climateManager.getClimateValuesCopy();

        for (int int0 = 0; int0 < this.forecasts.length; int0++) {
            int int1 = int0 - 10;
            ClimateForecaster.DayForecast dayForecast = new ClimateForecaster.DayForecast();
            dayForecast.weatherPeriod = new WeatherPeriod(climateManager, climateManager.getThunderStorm());
            dayForecast.weatherPeriod.setDummy(true);
            dayForecast.indexOffset = int1;
            dayForecast.airFront = new ClimateManager.AirFront();
            this.sampleDay(climateManager, dayForecast, int1);
            this.forecasts[int0] = dayForecast;
        }

        this.populateForecastList();
    }

    protected void updateDayChange(ClimateManager climateManager) {
        ClimateForecaster.DayForecast dayForecast = this.forecasts[0];

        for (int int0 = 0; int0 < this.forecasts.length; int0++) {
            if (int0 > 0 && int0 < this.forecasts.length) {
                this.forecasts[int0].indexOffset = int0 - 1 - 10;
                this.forecasts[int0 - 1] = this.forecasts[int0];
            }
        }

        dayForecast.reset();
        this.sampleDay(climateManager, dayForecast, this.forecasts.length - 1 - 10);
        dayForecast.indexOffset = this.forecasts.length - 1 - 10;
        this.forecasts[this.forecasts.length - 1] = dayForecast;
        this.populateForecastList();
    }

    protected void sampleDay(ClimateManager climateManager, ClimateForecaster.DayForecast dayForecast1, int int3) {
        GameTime gameTime = GameTime.getInstance();
        int int0 = gameTime.getYear();
        int int1 = gameTime.getMonth();
        int int2 = gameTime.getDayPlusOne();
        GregorianCalendar gregorianCalendar = new GregorianCalendar(int0, int1, int2, 0, 0);
        gregorianCalendar.add(5, int3);
        boolean boolean0 = true;
        ClimateForecaster.DayForecast dayForecast0 = this.getWeatherOverlap(int3 + 10, 0.0F);
        dayForecast1.weatherOverlap = dayForecast0;
        dayForecast1.weatherPeriod.stopWeatherPeriod();
        dayForecast1.name = "day: " + gregorianCalendar.get(1) + " - " + (gregorianCalendar.get(2) + 1) + " - " + gregorianCalendar.get(5);

        for (int int4 = 0; int4 < 24; int4++) {
            if (int4 != 0) {
                gregorianCalendar.add(11, 1);
            }

            this.climateValues.pollDate(gregorianCalendar);
            if (int4 == 0) {
                boolean0 = this.climateValues.getNoiseAirmass() >= 0.0F;
                dayForecast1.airFrontString = boolean0 ? "WARM" : "COLD";
                dayForecast1.dawn = this.climateValues.getDawn();
                dayForecast1.dusk = this.climateValues.getDusk();
                dayForecast1.dayLightHours = dayForecast1.dusk - dayForecast1.dawn;
            }

            if (!dayForecast1.weatherStarts
                && (boolean0 && this.climateValues.getNoiseAirmass() < 0.0F || !boolean0 && this.climateValues.getNoiseAirmass() >= 0.0F)) {
                int int5 = this.climateValues.getNoiseAirmass() >= 0.0F ? -1 : 1;
                dayForecast1.airFront.setFrontType(int5);
                climateManager.CalculateWeatherFrontStrength(
                    gregorianCalendar.get(1), gregorianCalendar.get(2), gregorianCalendar.get(5), dayForecast1.airFront
                );
                dayForecast1.airFront.setFrontWind(this.climateValues.getWindAngleDegrees());
                if (dayForecast1.airFront.getStrength() >= 0.1F) {
                    ClimateForecaster.DayForecast dayForecast2 = this.getWeatherOverlap(int3 + 10, int4);
                    float float0 = dayForecast2 != null ? dayForecast2.weatherPeriod.getTotalStrength() : -1.0F;
                    if (float0 < 0.1F) {
                        dayForecast1.weatherStarts = true;
                        dayForecast1.weatherStartTime = int4;
                        dayForecast1.weatherPeriod
                            .init(
                                dayForecast1.airFront,
                                this.climateValues.getCacheWorldAgeHours(),
                                gregorianCalendar.get(1),
                                gregorianCalendar.get(2),
                                gregorianCalendar.get(5)
                            );
                    }
                }

                if (!dayForecast1.weatherStarts) {
                    boolean0 = !boolean0;
                }
            }

            boolean boolean1 = int4 > this.climateValues.getDawn() && int4 <= this.climateValues.getDusk();
            float float1 = this.climateValues.getTemperature();
            float float2 = this.climateValues.getHumidity();
            float float3 = this.climateValues.getWindAngleDegrees();
            float float4 = this.climateValues.getWindIntensity();
            float float5 = this.climateValues.getCloudIntensity();
            if (dayForecast1.weatherStarts || dayForecast1.weatherOverlap != null) {
                WeatherPeriod weatherPeriod = dayForecast1.weatherStarts ? dayForecast1.weatherPeriod : dayForecast1.weatherOverlap.weatherPeriod;
                if (weatherPeriod != null) {
                    float3 = weatherPeriod.getWindAngleDegrees();
                    WeatherPeriod.WeatherStage weatherStage = weatherPeriod.getStageForWorldAge(this.climateValues.getCacheWorldAgeHours());
                    if (weatherStage != null) {
                        if (!dayForecast1.weatherStages.contains(weatherStage.getStageID())) {
                            dayForecast1.weatherStages.add(weatherStage.getStageID());
                        }

                        switch (weatherStage.getStageID()) {
                            case 1:
                                dayForecast1.hasHeavyRain = true;
                            case 4:
                            case 5:
                            case 6:
                            default:
                                float1 -= WeatherPeriod.getMaxTemperatureInfluence() * 0.25F;
                                float5 = 0.35F + 0.5F * weatherPeriod.getTotalStrength();
                                break;
                            case 2:
                                float4 = 0.5F * weatherPeriod.getTotalStrength();
                                float1 -= WeatherPeriod.getMaxTemperatureInfluence() * float4;
                                float5 = 0.5F + 0.5F * float4;
                                dayForecast1.hasHeavyRain = true;
                                break;
                            case 3:
                                float4 = 0.2F + 0.5F * weatherPeriod.getTotalStrength();
                                float1 -= WeatherPeriod.getMaxTemperatureInfluence() * float4;
                                float5 = 0.5F + 0.5F * float4;
                                dayForecast1.hasStorm = true;
                                break;
                            case 7:
                                dayForecast1.chanceOnSnow = true;
                                float4 = 0.75F + 0.25F * weatherPeriod.getTotalStrength();
                                float1 -= WeatherPeriod.getMaxTemperatureInfluence() * float4;
                                float5 = 0.5F + 0.5F * float4;
                                dayForecast1.hasBlizzard = true;
                                break;
                            case 8:
                                float4 = 0.4F + 0.6F * weatherPeriod.getTotalStrength();
                                float1 -= WeatherPeriod.getMaxTemperatureInfluence() * float4;
                                float5 = 0.5F + 0.5F * float4;
                                dayForecast1.hasTropicalStorm = true;
                        }
                    } else if (dayForecast1.weatherOverlap != null && int4 < dayForecast1.weatherEndTime) {
                        dayForecast1.weatherEndTime = int4;
                    }
                }

                if (float1 < 0.0F) {
                    dayForecast1.chanceOnSnow = true;
                }
            }

            dayForecast1.temperature.add(float1, boolean1);
            dayForecast1.humidity.add(float2, boolean1);
            dayForecast1.windDirection.add(float3, boolean1);
            dayForecast1.windPower.add(float4, boolean1);
            dayForecast1.cloudiness.add(float5, boolean1);
        }

        dayForecast1.temperature.calculate();
        dayForecast1.humidity.calculate();
        dayForecast1.windDirection.calculate();
        dayForecast1.windPower.calculate();
        dayForecast1.cloudiness.calculate();
        dayForecast1.hasFog = this.climateValues.isDayDoFog();
        dayForecast1.fogStrength = this.climateValues.getDayFogStrength();
        dayForecast1.fogDuration = this.climateValues.getDayFogDuration();
    }

    private ClimateForecaster.DayForecast getWeatherOverlap(int int1, float float3) {
        int int0 = Math.max(0, int1 - 10);
        if (int0 == int1) {
            return null;
        } else {
            for (int int2 = int0; int2 < int1; int2++) {
                if (this.forecasts[int2].weatherStarts) {
                    float float0 = (float)this.forecasts[int2].weatherPeriod.getDuration() / 24.0F;
                    float float1 = int2 + this.forecasts[int2].weatherStartTime / 24.0F;
                    float1 += float0;
                    float float2 = int1 + float3 / 24.0F;
                    if (float1 > float2) {
                        return this.forecasts[int2];
                    }
                }
            }

            return null;
        }
    }

    public int getDaysTillFirstWeather() {
        int int0 = -1;

        for (int int1 = 10; int1 < this.forecasts.length - 1; int1++) {
            if (this.forecasts[int1].weatherStarts && int0 < 0) {
                int0 = int1;
            }
        }

        return int0;
    }

    public static class DayForecast {
        private int indexOffset = 0;
        private String name = "Day x";
        private WeatherPeriod weatherPeriod;
        private ClimateForecaster.ForecastValue temperature = new ClimateForecaster.ForecastValue();
        private ClimateForecaster.ForecastValue humidity = new ClimateForecaster.ForecastValue();
        private ClimateForecaster.ForecastValue windDirection = new ClimateForecaster.ForecastValue();
        private ClimateForecaster.ForecastValue windPower = new ClimateForecaster.ForecastValue();
        private ClimateForecaster.ForecastValue cloudiness = new ClimateForecaster.ForecastValue();
        private boolean weatherStarts = false;
        private float weatherStartTime = 0.0F;
        private float weatherEndTime = 24.0F;
        private boolean chanceOnSnow = false;
        private String airFrontString = "";
        private boolean hasFog = false;
        private float fogStrength = 0.0F;
        private float fogDuration = 0.0F;
        private ClimateManager.AirFront airFront;
        private ClimateForecaster.DayForecast weatherOverlap;
        private boolean hasHeavyRain = false;
        private boolean hasStorm = false;
        private boolean hasTropicalStorm = false;
        private boolean hasBlizzard = false;
        private float dawn = 0.0F;
        private float dusk = 0.0F;
        private float dayLightHours = 0.0F;
        private ArrayList<Integer> weatherStages = new ArrayList<>();

        public int getIndexOffset() {
            return this.indexOffset;
        }

        public String getName() {
            return this.name;
        }

        public ClimateForecaster.ForecastValue getTemperature() {
            return this.temperature;
        }

        public ClimateForecaster.ForecastValue getHumidity() {
            return this.humidity;
        }

        public ClimateForecaster.ForecastValue getWindDirection() {
            return this.windDirection;
        }

        public ClimateForecaster.ForecastValue getWindPower() {
            return this.windPower;
        }

        public ClimateForecaster.ForecastValue getCloudiness() {
            return this.cloudiness;
        }

        public WeatherPeriod getWeatherPeriod() {
            return this.weatherPeriod;
        }

        public boolean isWeatherStarts() {
            return this.weatherStarts;
        }

        public float getWeatherStartTime() {
            return this.weatherStartTime;
        }

        public float getWeatherEndTime() {
            return this.weatherEndTime;
        }

        public boolean isChanceOnSnow() {
            return this.chanceOnSnow;
        }

        public String getAirFrontString() {
            return this.airFrontString;
        }

        public boolean isHasFog() {
            return this.hasFog;
        }

        public ClimateManager.AirFront getAirFront() {
            return this.airFront;
        }

        public ClimateForecaster.DayForecast getWeatherOverlap() {
            return this.weatherOverlap;
        }

        public String getMeanWindAngleString() {
            return ClimateManager.getWindAngleString(this.windDirection.getTotalMean());
        }

        public float getFogStrength() {
            return this.fogStrength;
        }

        public float getFogDuration() {
            return this.fogDuration;
        }

        public boolean isHasHeavyRain() {
            return this.hasHeavyRain;
        }

        public boolean isHasStorm() {
            return this.hasStorm;
        }

        public boolean isHasTropicalStorm() {
            return this.hasTropicalStorm;
        }

        public boolean isHasBlizzard() {
            return this.hasBlizzard;
        }

        public ArrayList<Integer> getWeatherStages() {
            return this.weatherStages;
        }

        public float getDawn() {
            return this.dawn;
        }

        public float getDusk() {
            return this.dusk;
        }

        public float getDayLightHours() {
            return this.dayLightHours;
        }

        private void reset() {
            this.weatherPeriod.stopWeatherPeriod();
            this.temperature.reset();
            this.humidity.reset();
            this.windDirection.reset();
            this.windPower.reset();
            this.cloudiness.reset();
            this.weatherStarts = false;
            this.weatherStartTime = 0.0F;
            this.weatherEndTime = 24.0F;
            this.chanceOnSnow = false;
            this.hasFog = false;
            this.fogStrength = 0.0F;
            this.fogDuration = 0.0F;
            this.weatherOverlap = null;
            this.hasHeavyRain = false;
            this.hasStorm = false;
            this.hasTropicalStorm = false;
            this.hasBlizzard = false;
            this.weatherStages.clear();
        }
    }

    public static class ForecastValue {
        private float dayMin;
        private float dayMax;
        private float dayMean;
        private int dayMeanTicks;
        private float nightMin;
        private float nightMax;
        private float nightMean;
        private int nightMeanTicks;
        private float totalMin;
        private float totalMax;
        private float totalMean;
        private int totalMeanTicks;

        public ForecastValue() {
            this.reset();
        }

        public float getDayMin() {
            return this.dayMin;
        }

        public float getDayMax() {
            return this.dayMax;
        }

        public float getDayMean() {
            return this.dayMean;
        }

        public float getNightMin() {
            return this.nightMin;
        }

        public float getNightMax() {
            return this.nightMax;
        }

        public float getNightMean() {
            return this.nightMean;
        }

        public float getTotalMin() {
            return this.totalMin;
        }

        public float getTotalMax() {
            return this.totalMax;
        }

        public float getTotalMean() {
            return this.totalMean;
        }

        protected void add(float float0, boolean boolean0) {
            if (boolean0) {
                if (float0 < this.dayMin) {
                    this.dayMin = float0;
                }

                if (float0 > this.dayMax) {
                    this.dayMax = float0;
                }

                this.dayMean += float0;
                this.dayMeanTicks++;
            } else {
                if (float0 < this.nightMin) {
                    this.nightMin = float0;
                }

                if (float0 > this.nightMax) {
                    this.nightMax = float0;
                }

                this.nightMean += float0;
                this.nightMeanTicks++;
            }

            if (float0 < this.totalMin) {
                this.totalMin = float0;
            }

            if (float0 > this.totalMax) {
                this.totalMax = float0;
            }

            this.totalMean += float0;
            this.totalMeanTicks++;
        }

        protected void calculate() {
            if (this.totalMeanTicks <= 0) {
                this.totalMean = 0.0F;
            } else {
                this.totalMean = this.totalMean / this.totalMeanTicks;
            }

            if (this.dayMeanTicks <= 0) {
                this.dayMin = this.totalMin;
                this.dayMax = this.totalMax;
                this.dayMean = this.totalMean;
            } else {
                this.dayMean = this.dayMean / this.dayMeanTicks;
            }

            if (this.nightMeanTicks <= 0) {
                this.nightMin = this.totalMin;
                this.nightMax = this.totalMax;
                this.nightMean = this.totalMean;
            } else {
                this.nightMean = this.nightMean / this.nightMeanTicks;
            }
        }

        protected void reset() {
            this.dayMin = 10000.0F;
            this.dayMax = -10000.0F;
            this.dayMean = 0.0F;
            this.dayMeanTicks = 0;
            this.nightMin = 10000.0F;
            this.nightMax = -10000.0F;
            this.nightMean = 0.0F;
            this.nightMeanTicks = 0;
            this.totalMin = 10000.0F;
            this.totalMax = -10000.0F;
            this.totalMean = 0.0F;
            this.totalMeanTicks = 0;
        }
    }
}
