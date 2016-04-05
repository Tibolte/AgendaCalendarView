package com.github.tibolte.agendacalendarview.models;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.utils.DateHelper;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Day model class.
 */
public class DayItem implements Parcelable {
    private Date mDate;
    private int mValue;
    private int mDayOfTheWeek;
    private boolean mToday;
    private boolean mFirstDayOfTheMonth;
    private boolean mSelected;
    private String mMonth;
    private boolean mHasEvents;

    // region Constructor

    public DayItem(Date date, int value, boolean today, String month, Calendar calendar, List<CalendarEvent> events) {
        this.mDate = date;
        this.mValue = value;
        this.mToday = today;
        this.mMonth = month;
		if(hasEventForDate(calendar,events)) {
            mHasEvents = true;
        } else {
            mHasEvents = false;
        }
    }

    // endregion

    // region Getters/Setters

	public boolean hasEvent() {
        return mHasEvents;
    }

    public void setHasEvents(boolean mHasEvents) {
        this.mHasEvents = mHasEvents;
    }
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

    public static DayItem buildDayItemFromCal(Calendar calendar, List<CalendarEvent> events) {
        Date date = calendar.getTime();
        boolean isToday = DateHelper.sameDate(calendar, CalendarManager.getInstance().getToday());
        int value = calendar.get(Calendar.DAY_OF_MONTH);
        DayItem dayItem = new DayItem(date, value, isToday, CalendarManager.getInstance().getMonthHalfNameFormat().format(date), calendar, events);
        if (value == 1) {
            dayItem.setFirstDayOfTheMonth(true);
        }
        dayItem.setToday(isToday);
        return dayItem;
    }

    /**
     *
     * @param calendar
     * @param events
     * @return true if day has a event, false if not
     */
	private boolean hasEventForDate(Calendar calendar,List<CalendarEvent> events) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
       for(int i=0;i<events.size();i++) {
            Date current = calendar.getTime();

            Date StartDate = events.get(i).getStartTime().getTime();
            Date EndDate = events.get(i).getEndTime().getTime();
            if((current.after(StartDate) && current.before(EndDate)) ||
              (current.compareTo(StartDate)==0  || current.compareTo(EndDate)==0)) {
                return true;
            }
       }
        return false;
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

    // region Interface - Parcelable

    protected DayItem(Parcel in) {
        long tmpMDate = in.readLong();
        mDate = tmpMDate != -1 ? new Date(tmpMDate) : null;
        mValue = in.readInt();
        mDayOfTheWeek = in.readInt();
        mToday = in.readByte() != 0x00;
        mFirstDayOfTheMonth = in.readByte() != 0x00;
        mSelected = in.readByte() != 0x00;
        mMonth = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mDate != null ? mDate.getTime() : -1L);
        dest.writeInt(mValue);
        dest.writeInt(mDayOfTheWeek);
        dest.writeByte((byte) (mToday ? 0x01 : 0x00));
        dest.writeByte((byte) (mFirstDayOfTheMonth ? 0x01 : 0x00));
        dest.writeByte((byte) (mSelected ? 0x01 : 0x00));
        dest.writeString(mMonth);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DayItem> CREATOR = new Parcelable.Creator<DayItem>() {
        @Override
        public DayItem createFromParcel(Parcel in) {
            return new DayItem(in);
        }

        @Override
        public DayItem[] newArray(int size) {
            return new DayItem[size];
        }
    };

    // endregion
}
