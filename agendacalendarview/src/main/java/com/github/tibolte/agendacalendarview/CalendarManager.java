package com.github.tibolte.agendacalendarview;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.github.tibolte.agendacalendarview.models.MonthItem;
import com.github.tibolte.agendacalendarview.models.WeekItem;
import com.github.tibolte.agendacalendarview.utils.BusProvider;
import com.github.tibolte.agendacalendarview.utils.DateHelper;
import com.github.tibolte.agendacalendarview.utils.Events;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class manages information about the calendar. (Events, weather info...)
 * Holds reference to the days list of the calendar.
 * As the app is using several views, we want to keep everything in one place.
 */
public class CalendarManager {

    private static final String LOG_TAG = CalendarManager.class.getSimpleName();

    private static CalendarManager mInstance;

    private Context mContext;
    private Locale mLocale;
    private Calendar mToday = Calendar.getInstance();
    private SimpleDateFormat mWeekdayFormatter;
    private SimpleDateFormat mMonthHalfNameFormat;

    /**
     * List of days used by the calendar
     */
    private List<DayItem> mDays = new ArrayList<>();
    /**
     * List of weeks used by the calendar
     */
    private List<WeekItem> mWeeks = new ArrayList<>();
    /**
     * List of months used by the calendar
     */
    private List<MonthItem> mMonths = new ArrayList<>();
    /**
     * List of events instances
     */
    private List<CalendarEvent> mEvents = new ArrayList<>();
    /**
     * Helper to build our list of weeks
     */
    private Calendar mWeekCounter;
    /**
     * The start date given to the calendar view
     */
    private Calendar mMinCal;
    /**
     * The end date given to the calendar view
     */
    private Calendar mMaxCal;

    // region Constructors

    public CalendarManager(Context context) {
        this.mContext = context;
    }

    public static CalendarManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CalendarManager(context);
        }
        return mInstance;
    }

    public static CalendarManager getInstance() {
        return mInstance;
    }

    // endregion

    // region Getters/Setters

    /**
     * Sets the current mLocale
     *
     * @param locale to be set
     */
    public void setLocale(Locale locale) {
        this.mLocale = locale;

        //apply the same locale to all variables depending on that
        setToday(Calendar.getInstance(mLocale));
        mWeekdayFormatter = new SimpleDateFormat(getContext().getString(R.string.day_name_format), mLocale);
        mMonthHalfNameFormat = new SimpleDateFormat(getContext().getString(R.string.month_half_name_format), locale);
    }

    public Locale getLocale() {
        return mLocale;
    }

    public Context getContext() {
        return mContext;
    }

    public Calendar getToday() {
        return mToday;
    }

    public void setToday(Calendar today) {
        this.mToday = today;
    }

    public List<WeekItem> getWeeks() {
        return mWeeks;
    }

    public List<MonthItem> getMonths() {
        return mMonths;
    }

    public List<DayItem> getDays() {
        return mDays;
    }

    public List<CalendarEvent> getEvents() {
        return mEvents;
    }

    public SimpleDateFormat getWeekdayFormatter() {
        return mWeekdayFormatter;
    }

    public SimpleDateFormat getMonthHalfNameFormat() {
        return mMonthHalfNameFormat;
    }

    // endregion

    // region Public methods

    public void buildCal(Calendar minDate, Calendar maxDate, Locale locale) {
        if (minDate == null || maxDate == null) {
            throw new IllegalArgumentException(
                    "minDate and maxDate must be non-null.");
        }
        if (minDate.after(maxDate)) {
            throw new IllegalArgumentException(
                    "minDate must be before maxDate.");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Locale is null.");
        }

        setLocale(locale);

        getDays().clear();
        getWeeks().clear();
        getMonths().clear();
        getEvents().clear();

        mMinCal = Calendar.getInstance(mLocale);
        mMaxCal = Calendar.getInstance(mLocale);
        mWeekCounter = Calendar.getInstance(mLocale);

        mMinCal.setTime(minDate.getTime());
        mMaxCal.setTime(maxDate.getTime());

        // maxDate is exclusive, here we bump back to the previous day, as maxDate if December 1st, 2020,
        // we don't include that month in our list
        mMaxCal.add(Calendar.MINUTE, -1);

        // Now iterate we iterate between mMinCal and mMaxCal so we build our list of weeks
        mWeekCounter.setTime(mMinCal.getTime());
        int maxMonth = mMaxCal.get(Calendar.MONTH);
        int maxYear = mMaxCal.get(Calendar.YEAR);
        // Build another month item and add it to our list, if this value change when we loop through the weeks
        int tmpMonth = -1;
        setToday(Calendar.getInstance(mLocale));

        // Loop through the weeks
        while ((mWeekCounter.get(Calendar.MONTH) <= maxMonth // Up to, including the month.
                || mWeekCounter.get(Calendar.YEAR) < maxYear) // Up to the year.
                && mWeekCounter.get(Calendar.YEAR) < maxYear + 1) { // But not > next yr.
            Date date = mWeekCounter.getTime();

            if (tmpMonth != mWeekCounter.get(Calendar.MONTH)) {
                MonthItem monthItem = new MonthItem(mWeekCounter.get(Calendar.YEAR), mWeekCounter.get(Calendar.MONTH));
                getMonths().add(monthItem);
            }

            // Build our week list
            WeekItem weekItem = new WeekItem(mWeekCounter.get(Calendar.WEEK_OF_YEAR), mWeekCounter.get(Calendar.YEAR), date, mMonthHalfNameFormat.format(date), mWeekCounter.get(Calendar.MONTH));
            List<DayItem> dayItems = getDayCells(mWeekCounter); // gather days for the built week
            weekItem.setDayItems(dayItems);
            getWeeks().add(weekItem);
            addWeekToLastMonth(weekItem);

            Log.d(LOG_TAG, String.format("Adding week: %s", weekItem));
            tmpMonth = mWeekCounter.get(Calendar.MONTH);
            mWeekCounter.add(Calendar.WEEK_OF_YEAR, 1);
        }
    }

    public void loadEvents(List<CalendarEvent> eventList) {
        /*CalendarLoadTask calendarLoadTask = new CalendarLoadTask();
        calendarLoadTask.execute();*/

        for (WeekItem weekItem : getWeeks()) {
            for (DayItem dayItem : weekItem.getDayItems()) {
                boolean isEventForDay = false;
                for (CalendarEvent event : eventList) {
                    if (DateHelper.isBetweenInclusive(dayItem.getDate(), event.getStartTime(), event.getEndTime())) {
                        CalendarEvent copy = event.copy();

                        Calendar dayInstance = Calendar.getInstance();
                        dayInstance.setTime(dayItem.getDate());
                        copy.setInstanceDay(dayInstance);
                        copy.setDayReference(dayItem);
                        copy.setWeekReference(weekItem);
                        // add instances in chronological order
                        getEvents().add(copy);
                        isEventForDay = true;
                    }
                }
                if (!isEventForDay) {
                    Calendar dayInstance = Calendar.getInstance();
                    dayInstance.setTime(dayItem.getDate());
                    BaseCalendarEvent event = new BaseCalendarEvent(dayInstance, getContext().getResources().getString(R.string.agenda_event_no_events));
                    event.setDayReference(dayItem);
                    event.setWeekReference(weekItem);
                    getEvents().add(event);
                }
            }
        }

        BusProvider.getInstance().send(new Events.EventsFetched());
        Log.d(LOG_TAG, "CalendarEventTask finished");
    }

    // endregion

    // region Private methods

    private List<DayItem> getDayCells(Calendar startCal) {
        Calendar cal = Calendar.getInstance(mLocale);
        cal.setTime(startCal.getTime());
        List<DayItem> dayItems = new ArrayList<>();

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
        if (offset > 0) {
            offset -= 7;
        }
        cal.add(Calendar.DATE, offset);

        Log.d(LOG_TAG, String.format("Buiding row week starting at %s", cal.getTime()));
        for (int c = 0; c < 7; c++) {
            DayItem dayItem = DayItem.buildDayItemFromCal(cal);
            dayItem.setDayOftheWeek(c);
            dayItems.add(dayItem);
            cal.add(Calendar.DATE, 1);
        }

        getDays().addAll(dayItems);
        return dayItems;
    }

    private void addWeekToLastMonth(WeekItem weekItem) {
        getLastMonth().getWeeks().add(weekItem);
        getLastMonth().setMonth(mWeekCounter.get(Calendar.MONTH) + 1);
    }

    private MonthItem getLastMonth() {
        return getMonths().get(getMonths().size() - 1);
    }
    // endregion
}
