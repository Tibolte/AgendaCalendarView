package com.github.tibolte.agendacalendarview.weather.models;

import com.google.gson.annotations.SerializedName;

public class DataPoint {
    // region Attributes

    @SerializedName("time")
    private long mTime;

    @SerializedName("summary")
    private String mSummary;

    @SerializedName("icon")
    private String mIcon;

    @SerializedName("sunriseTime")
    private String mSunriseTime;

    @SerializedName("moonPhase")
    private String mMoonPhase;

    @SerializedName("nearestStormDistance")
    private String mNearestStormDistance;

    @SerializedName("nearestStormBearing")
    private String mNearestStormBearing;

    @SerializedName("precipIntensity")
    private String mPrecipIntensity;

    @SerializedName("precipIntensityMax")
    private String mPrecipIntensityMax;

    @SerializedName("precipIntensityMaxTime")
    private String mPrecipIntensityMaxTime;

    @SerializedName("precipProbability")
    private String mPrecipProbability;

    @SerializedName("precipType")
    private String mPrecipType;

    @SerializedName("precipAccumulation")
    private String mPrecipAccumulation;

    @SerializedName("temperature")
    private double mTemperature;

    @SerializedName("temperatureMin")
    private double mTemperatureMin;

    @SerializedName("temperatureMinTime")
    private double mTemperatureMinTime;

    @SerializedName("temperatureMax")
    private double mTemperatureMax;

    @SerializedName("temperatureMaxTime")
    private double mTemperatureMaxTime;

    @SerializedName("apparentTemperature")
    private double mApparentTemperature;

    @SerializedName("apparentTemperatureMin")
    private double mApparentTemperatureMin;

    @SerializedName("apparentTemperatureMinTime")
    private double mApparentTemperatureMinTime;

    @SerializedName("apparentTemperatureMax")
    private double mApparentTemperatureMax;

    @SerializedName("apparentTemperatureMaxTime")
    private double mApparentTemperatureMaxTime;

    @SerializedName("dewPoint")
    private String mDewPoint;

    @SerializedName("windSpeed")
    private String mWindSpeed;

    @SerializedName("windBearing")
    private String mWindBearing;

    @SerializedName("cloudClover")
    private String mCloudClover;

    @SerializedName("humidity")
    private String mHumidity;

    @SerializedName("pressure")
    private String mPressure;

    @SerializedName("visibility")
    private String mVisibility;

    @SerializedName("ozone")
    private String mOzone;

    // endregion

    // region Getters

    public double getApparentTemperature() {
        return mApparentTemperature;
    }

    public double getApparentTemperatureMax() {
        return mApparentTemperatureMax;
    }

    public double getApparentTemperatureMaxTime() {
        return mApparentTemperatureMaxTime;
    }

    public double getApparentTemperatureMin() {
        return mApparentTemperatureMin;
    }

    public double getApparentTemperatureMinTime() {
        return mApparentTemperatureMinTime;
    }

    public String getCloudClover() {
        return mCloudClover;
    }

    public String getDewPoint() {
        return mDewPoint;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getMoonPhase() {
        return mMoonPhase;
    }

    public String getNearestStormBearing() {
        return mNearestStormBearing;
    }

    public String getNearestStormDistance() {
        return mNearestStormDistance;
    }

    public String getOzone() {
        return mOzone;
    }

    public String getPrecipAccumulation() {
        return mPrecipAccumulation;
    }

    public String getPrecipIntensity() {
        return mPrecipIntensity;
    }

    public String getPrecipIntensityMax() {
        return mPrecipIntensityMax;
    }

    public String getPrecipIntensityMaxTime() {
        return mPrecipIntensityMaxTime;
    }

    public String getPrecipProbability() {
        return mPrecipProbability;
    }

    public String getPrecipType() {
        return mPrecipType;
    }

    public String getPressure() {
        return mPressure;
    }

    public String getSummary() {
        return mSummary;
    }

    public String getSunriseTime() {
        return mSunriseTime;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public double getTemperatureMax() {
        return mTemperatureMax;
    }

    public double getTemperatureMaxTime() {
        return mTemperatureMaxTime;
    }

    public double getTemperatureMin() {
        return mTemperatureMin;
    }

    public double getTemperatureMinTime() {
        return mTemperatureMinTime;
    }

    public long getTime() {
        return mTime;
    }

    public String getVisibility() {
        return mVisibility;
    }

    public String getWindBearing() {
        return mWindBearing;
    }

    public String getWindSpeed() {
        return mWindSpeed;
    }

    // endregion
}
