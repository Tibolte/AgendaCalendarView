package com.github.tibolte.agendacalendarview.weather.models;

import com.google.gson.annotations.SerializedName;

/**
 * Model class containing the information returned by the Weather API.
 */
public class Forecast {
    // region Attributes

    /**
     * The requested latitude.
     */
    @SerializedName("latitude")
    private float mLatitude;

    /**
     * The requested longitude.
     */
    @SerializedName("longitude")
    private float mLongitude;

    /**
     * The IANA timezone name for the requested location (e.g. America/New_York).
     * This is the timezone used for text forecast summaries and for determining the exact start
     * time of daily data points.
     */
    @SerializedName("timezone")
    private String mTimeZone;

    /**
     * The current timezone offset in hours from GMT.
     */
    @SerializedName("offset")
    private int mOffset;

    /**
     * A data point containing the current weather conditions at the requested location.
     */
    @SerializedName("currently")
    private DataPoint mCurrently;

    /**
     * A data block containing the weather conditions minute-by-minute for the next hour.
     */
    @SerializedName("minutely")
    private DataBlock mMinutely;

    /**
     * A data block containing the weather conditions hour-by-hour for the next two days.
     */
    @SerializedName("hourly")
    private DataBlock mHourly;

    /**
     * A data block containing the weather conditions day-by-day for the next week.
     */
    @SerializedName("daily")
    private DataBlock mDaily;

    // endregion

    // region Getters

    public String getTimeZone() {
        return mTimeZone;
    }

    public float getLatitude() {
        return mLatitude;
    }

    public float getLongitude() {
        return mLongitude;
    }

    public int getOffset() {
        return mOffset;
    }

    public DataPoint getCurrently() {
        return mCurrently;
    }

    public DataBlock getDaily() {
        return mDaily;
    }

    public DataBlock getHourly() {
        return mHourly;
    }

    public DataBlock getMinutely() {
        return mMinutely;
    }

    // endregion
}
