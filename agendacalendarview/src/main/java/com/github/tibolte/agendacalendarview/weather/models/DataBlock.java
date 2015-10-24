package com.github.tibolte.agendacalendarview.weather.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataBlock {
    // region Attributes

    @SerializedName("summary")
    private String mSummary;

    @SerializedName("icon")
    private String mIcon;

    @SerializedName("data")
    private List<DataPoint> mData;

    // endregion

    // region Getters

    public String getIcon() {
        return mIcon;
    }

    public String getSummary() {
        return mSummary;
    }

    public List<DataPoint> getData() {
        return mData;
    }

    // endregion
}
