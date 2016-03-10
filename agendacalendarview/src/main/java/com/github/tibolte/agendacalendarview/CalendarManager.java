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
import android.util.SparseArray;
import android.util.SparseIntArray;

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

    /*
        * Map of weeks for faster search
        */
    private SparseIntArray weekMap = new SparseIntArray();
    /**
     * List of days used by the calendar
     */
    private List<DayItem> mDays = new ArrayList<>();
    /**
     * List of weeks used by the calendar
     */
    private List<WeekItem> mWeeks = new ArrayList<>();
    /**
     * List of events instances
     */
    private List<CalendarEvent> mEvents = new ArrayList<>();

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

    public Locale getLocale() {
        return mLocale;
    }

    public Context getContext() {
        return mContext;
    }

    public List<WeekItem> getWeeks() {
        return mWeeks;
    }

    public List<CalendarEvent> getEvents() {
        return mEvents;
    }



    public SparseIntArray getWeekMap() {
        return weekMap;
    }

    public List<DayItem> getDays() {
        return mDays;
    }

    public SimpleDateFormat getWeekdayFormatter() {
        return mWeekdayFormatter;
    }

    public SimpleDateFormat getMonthHalfNameFormat() {
        return mMonthHalfNameFormat;
    }

    public Calendar getToday() {
        return mToday;
    }
    public void setToday(Calendar today) {
        this.mToday = today;
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

        mDays.clear();
        mWeeks.clear();
        weekMap.clear();
        //mMonths.clear();
        mEvents.clear();

        Calendar mMinCal = Calendar.getInstance(mLocale);
        Calendar mMaxCal = Calendar.getInstance(mLocale);
        Calendar mWeekCounter = Calendar.getInstance(mLocale);

        mMinCal.setTime(minDate.getTime());
        mMaxCal.setTime(maxDate.getTime());

        // maxDate is exclusive, here we bump back to the previous day, as maxDate if December 1st, 2020,
        // we don't include that month in our list
        mMaxCal.add(Calendar.MINUTE, -1);

        // Now iterate we iterate between mMinCal and mMaxCal so we build our list of weeks
        mWeekCounter.setTime(mMinCal.getTime());
        int maxMonth = mMaxCal.get(Calendar.MONTH);
        int maxYear = mMaxCal.get(Calendar.YEAR);

        int currentMonth = mWeekCounter.get(Calendar.MONTH);
        int currentYear = mWeekCounter.get(Calendar.YEAR);

        int position = 0;
        // Loop through the weeks
        while ((currentMonth <= maxMonth // Up to, including the month.
                || currentYear < maxYear) // Up to the year.
                && currentYear < maxYear + 1) { // But not > next yr.

            Date date = mWeekCounter.getTime();
            // Build our week list
            int currentWeekOfYear = mWeekCounter.get(Calendar.WEEK_OF_YEAR);

            /////
            int uniqueWeekId = (currentYear * 100) + currentWeekOfYear;
            ////


            WeekItem weekItem = new WeekItem(currentWeekOfYear, currentYear, date, mMonthHalfNameFormat.format(date), currentMonth);
            List<DayItem> dayItems = getDayCells(mWeekCounter); // gather days for the built week
            weekItem.setDayItems(dayItems);
            mWeeks.add(weekItem);

            ///////
            weekMap.put(uniqueWeekId, position);
            position++;
            ///////

            //Log.d(LOG_TAG, String.format("Adding week: %s", weekItem));

            mWeekCounter.add(Calendar.WEEK_OF_YEAR, 1);

            currentMonth = mWeekCounter.get(Calendar.MONTH);
            currentYear = mWeekCounter.get(Calendar.YEAR);
        }
    }

    public void loadEvents(List<CalendarEvent> eventList) {

        SparseArray<List<CalendarEvent>> mapEvents = new SparseArray<>();
        for (CalendarEvent event : eventList) {
            Calendar startTime = event.getStartTime();
            int eWeekOfYear = startTime.get(Calendar.WEEK_OF_YEAR);
            int eYear = startTime.get(Calendar.YEAR);
            int eventWeekId = (eYear * 100) + eWeekOfYear;

            int weekPosition = weekMap.get(eventWeekId);
            WeekItem week = mWeeks.get(weekPosition);
            int dayOfWeek = startTime.get(Calendar.DAY_OF_WEEK) - 1;
                DayItem dayItem = week.getDayItems().get(dayOfWeek);

                    CalendarEvent copy = event.copy();

                    Calendar dayInstance = Calendar.getInstance();
                    dayInstance.setTime(dayItem.getDate());
                    copy.setInstanceDay(dayInstance);
                    copy.setDayReference(dayItem);
                    copy.setWeekReference(week);
                    // add instances in chronological order
                    /////
                    int dDayOfyear = dayInstance.get(Calendar.DAY_OF_YEAR);
                    int dYear = dayInstance.get(Calendar.YEAR);
                    int dayId = (dYear * 1000) + dDayOfyear;

                    if (mapEvents.indexOfKey(dayId)<0) {
                        List<CalendarEvent> newList = new ArrayList<>();
                        newList.add(copy);
                        mapEvents.put(dayId, newList);
                    } else {
                        List<CalendarEvent> oldList = mapEvents.get(dayId);
                        oldList.add(copy);
                        mapEvents.put(dayId, oldList);
                    }
        }
        for (WeekItem weekItem : mWeeks) {
            for (DayItem dayItem : weekItem.getDayItems()) {


                Calendar dayInstance = Calendar.getInstance();
                dayInstance.setTime(dayItem.getDate());

                int eDayOfyear = dayInstance.get(Calendar.DAY_OF_YEAR);
                int eYear = dayInstance.get(Calendar.YEAR);
                int dayId = (eYear * 1000) + eDayOfyear;

                if(mapEvents.indexOfKey(dayId)<0) {
                    BaseCalendarEvent event = new BaseCalendarEvent(dayInstance, getContext().getResources().getString(R.string.agenda_event_no_events));
                    event.setDayReference(dayItem);
                    event.setWeekReference(weekItem);
                    mEvents.add(event);
                } else {
                    List<CalendarEvent> listEvents = mapEvents.get(dayId);
                    mEvents.addAll(listEvents);
                }
            }
        }

        BusProvider.getInstance().send(new Events.EventsFetched());
        Log.d(LOG_TAG, "CalendarEventTask finished");
    }

    public void loadCal (Locale locale, SparseIntArray lWeekMap, List<WeekItem> lWeeks, List<DayItem> lDays, List<CalendarEvent> lEvents) {
        weekMap = lWeekMap;
        mWeeks = lWeeks;
        mDays = lDays;
        mEvents = lEvents;
        setLocale(locale);

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

        mDays.addAll(dayItems);
        return dayItems;
    }

    private void setLocale(Locale locale) {
        this.mLocale = locale;
        setToday(Calendar.getInstance(mLocale));
        mWeekdayFormatter = new SimpleDateFormat(getContext().getString(R.string.day_name_format), mLocale);
        mMonthHalfNameFormat = new SimpleDateFormat(getContext().getString(R.string.month_half_name_format), locale);
    }

    // endregion
}
