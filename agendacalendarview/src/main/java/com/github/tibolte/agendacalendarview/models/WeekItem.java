package com.github.tibolte.agendacalendarview.models;

import java.util.Date;
import java.util.List;

/**
 * Week model class.
 */
public class WeekItem {
    private int mWeekInYear;
    private int mYear;
    private int mMonth;
    private Date mDate;
    private String mLabel;
    private List<DayItem> mDayItems;

    // region Constructor

    public WeekItem(int weekInYear, int year, Date date, String label, int month) {
        this.mWeekInYear = weekInYear;
        this.mYear = year;
        this.mDate = date;
        this.mLabel = label;
        this.mMonth = month;
    }

    // endregion

    // region Getters/Setters

    public int getWeekInYear() {
        return mWeekInYear;
    }

    public void setWeekInYear(int weekInYear) {
        this.mWeekInYear = weekInYear;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        this.mYear = year;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        this.mMonth = month;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        this.mLabel = label;
    }

    public List<DayItem> getDayItems() {
        return mDayItems;
    }

    public void setDayItems(List<DayItem> dayItems) {
        this.mDayItems = dayItems;
    }

    // endregion

    @Override
    public String toString() {
        return "WeekItem{"
                + "label='"
                + mLabel
                + '\''
                + ", weekInYear="
                + mWeekInYear
                + ", year="
                + mYear
                + '}';
    }
}
