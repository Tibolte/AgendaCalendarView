package com.github.tibolte.agendacalendarview.models;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.utils.DateHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Day model class.
 */
public class DayItem implements IDayItem {
    private Date mDate;
    private int mValue;
    private int mDayOfTheWeek;
    private boolean mToday;
    private boolean mFirstDayOfTheMonth;
    private boolean mSelected;
    private String mMonth;

    // region Constructor

    public DayItem(Date date, int value, boolean today, String month) {
        this.mDate = date;
        this.mValue = value;
        this.mToday = today;
        this.mMonth = month;
    }
    // only for cleanDay
    public DayItem() {

    }
    public DayItem(DayItem original) {

        this.mDate = original.getDate();
        this.mValue = original.getValue();
        this.mToday = original.isToday();
        this.mDayOfTheWeek = original.getDayOftheWeek();
        this.mFirstDayOfTheMonth = original.isFirstDayOfTheMonth();
        this.mSelected = original.isSelected();
        this.mMonth = original.getMonth();
    }
    // endregion

    // region Getters/Setters

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        this.mValue = value;
    }

    public boolean isToday() {
        return mToday;
    }

    public void setToday(boolean today) {
        this.mToday = today;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }

    public boolean isFirstDayOfTheMonth() {
        return mFirstDayOfTheMonth;
    }

    public void setFirstDayOfTheMonth(boolean firstDayOfTheMonth) {
        this.mFirstDayOfTheMonth = firstDayOfTheMonth;
    }

    public String getMonth() {
        return mMonth;
    }

    public void setMonth(String month) {
        this.mMonth = month;
    }

    public int getDayOftheWeek() {
        return mDayOfTheWeek;
    }

    public void setDayOftheWeek(int mDayOftheWeek) {
        this.mDayOfTheWeek = mDayOftheWeek;
    }

    // region Public methods

    public void buildDayItemFromCal(Calendar calendar) {
        Date date = calendar.getTime();
        this.mDate = date;

        this.mValue = calendar.get(Calendar.DAY_OF_MONTH);
        this.mToday = DateHelper.sameDate(calendar, CalendarManager.getInstance().getToday());
        this.mMonth = CalendarManager.getInstance().getMonthHalfNameFormat().format(date);
        if (this.mValue == 1) {
            this.mFirstDayOfTheMonth = true;
        }
    }

    // endregion

    @Override
    public String toString() {
        return "DayItem{"
                + "Date='"
                + mDate.toString()
                + ", value="
                + mValue
                + '}';
    }

    @Override
    public IDayItem copy() {
        return new DayItem(this);
    }

    // endregion
}
