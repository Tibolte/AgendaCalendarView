package com.github.tibolte.agendacalendarview;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.github.tibolte.agendacalendarview.models.MonthItem;
import com.github.tibolte.agendacalendarview.models.WeekItem;
import com.github.tibolte.agendacalendarview.utils.BusProvider;
import com.github.tibolte.agendacalendarview.utils.DateHelper;
import com.github.tibolte.agendacalendarview.utils.Events;
import com.github.tibolte.agendacalendarview.utils.Utils;
import com.github.tibolte.agendacalendarview.weather.WeatherRepository;
import com.github.tibolte.agendacalendarview.weather.models.DataPoint;
import com.github.tibolte.agendacalendarview.weather.models.Forecast;
import com.github.tibolte.agendacalendarview.weather.models.Request;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

        // TODO: remove weather part from the project
        // we finished setting up events, check for forecast now
        /*ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getContext());
        locationProvider.getLastKnownLocation()
                .subscribe(location->{
                    // we have a location now, ask for forecast then
                    Request request = new Request();
                    request.setLat(String.valueOf(location.getLatitude()));
                    request.setLng(String.valueOf(location.getLongitude()));
                    request.setUnits((getLocale().equals(Locale.US) || getLocale().equals(Locale.CANADA)) ? Request.Units.US : Request.Units.UK);
                    request.setLanguage(Request.Language.ENGLISH);
                    request.addExcludeBlock(Request.Block.CURRENTLY);

                    new WeatherRepository().getForecast(request)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(forecast->sortForecast(forecast))
                            .subscribe(events->{
                                Log.d(LOG_TAG, "Received Forecast");
                                BusProvider.getInstance().send(new Events.ForecastFetched());
                            }, error->Log.w(LOG_TAG, "Forecast Error :" + error.getMessage()));
                });*/
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

    /**
     * Asynchronous task to retrieve the events on the phone.
     * For the purpose of this project, if we find at list one account,
     * we retrieve all events found between mMinCal and mMaxCal time range.
     * Build a list of pair of day/events. Each pair is a representation of a day with all its
     * events.
     */
    private class CalendarLoadTask extends AsyncTask<String, Void, Void> {

        private Context mContext;
        private List<CalendarEvent> mEvents;

        @Override
        protected Void doInBackground(String... params) {
            Account account = Utils.getAccount(mContext);
            if (account != null) {
                // fetch calendar colors
                Map<String, Integer> colors = new HashMap<>();
                ContentResolver contentResolver = mContext.getContentResolver();
                Cursor cursor = contentResolver
                        .query(CalendarContract.Calendars.CONTENT_URI,
                                new String[]{
                                        CalendarContract.Calendars._ID,
                                        CalendarContract.Calendars.CALENDAR_COLOR}, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        colors.put(cursor.getString(0), cursor.getInt(1));
                    }
                    cursor.close();
                }

                // load events
                boolean eventsFound = false;
                cursor = contentResolver.query(
                        CalendarContract.Events.CONTENT_URI,
                        new String[]{
                                CalendarContract.Events._ID,
                                CalendarContract.Events.EVENT_LOCATION,
                                CalendarContract.Events.TITLE,
                                CalendarContract.Events.DESCRIPTION,
                                CalendarContract.Events.DTSTART,
                                CalendarContract.Events.DTEND,
                                CalendarContract.Events.SYNC_DATA10,
                                CalendarContract.Events.DIRTY,
                                CalendarContract.Events.DELETED,
                                CalendarContract.Events.DELETED,
                                CalendarContract.Events.CALENDAR_ID,
                                CalendarContract.Events.ALL_DAY,
                                CalendarContract.Events.DURATION,
                                CalendarContract.Events._SYNC_ID},
                        CalendarContract.Events.DTEND
                                + " >= ? and "
                                + CalendarContract.Events.DTSTART
                                + " <= ? and "
                                + CalendarContract.Events.DELETED
                                + " != 1", new String[]{Long.toString(mMinCal.getTimeInMillis()), Long.toString(mMaxCal.getTimeInMillis())},
                        CalendarContract.Events.DTSTART + " ASC");

                if (cursor != null) {
                    // check if c is empty
                    eventsFound = cursor.moveToNext();

                    // restore cursor position
                    cursor.moveToFirst();
                }

                if (!eventsFound) {
                    Log.w(LOG_TAG, "no events loaded");
                    return null;
                }

                Log.d(LOG_TAG, String.format("Found %d events", cursor.getCount()));
                while (cursor.moveToNext()) {
                    BaseCalendarEvent event = new BaseCalendarEvent(
                            cursor.getLong(0),
                            colors.get(cursor.getString(10)),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(1),
                            cursor.getLong(4),
                            cursor.getLong(5),
                            cursor.getInt(11),
                            cursor.getString(12));
                    mEvents.add(event);
                }
                cursor.close();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mContext = CalendarManager.getInstance().getContext();
            if (mContext == null) {
                Log.e(LOG_TAG, "context is null");
            }
            mEvents = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            for (WeekItem weekItem : getWeeks()) {
                for (DayItem dayItem : weekItem.getDayItems()) {
                    boolean isEventForDay = false;
                    for (CalendarEvent event : mEvents) {
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

            // we finished setting up events, check for forecast now
            ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getContext());
            locationProvider.getLastKnownLocation()
                    .subscribe(location -> {
                        // we have a location now, ask for forecast then
                        Request request = new Request();
                        request.setLat(String.valueOf(location.getLatitude()));
                        request.setLng(String.valueOf(location.getLongitude()));
                        request.setUnits((getLocale().equals(Locale.US) || getLocale().equals(Locale.CANADA)) ? Request.Units.US : Request.Units.UK);
                        request.setLanguage(Request.Language.ENGLISH);
                        request.addExcludeBlock(Request.Block.CURRENTLY);

                        new WeatherRepository().getForecast(request)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(forecast -> sortForecast(forecast))
                                .subscribe(events -> {
                                    Log.d(LOG_TAG, "Received Forecast");
                                    BusProvider.getInstance().send(new Events.ForecastFetched());
                                }, error -> Log.w(LOG_TAG, "Forecast Error :" + error.getMessage()));
                    });
        }
    }

    private List<CalendarEvent> sortForecast(Forecast forecast) {
        Log.d(LOG_TAG, String.format("hourly data size: %d", forecast.getHourly().getData().size()));
        List<CalendarEvent> filteredCalendarList = new ArrayList<>();

        for (DataPoint dataPoint : forecast.getHourly().getData()) {
            CalendarEvent calendarEvent = new BaseCalendarEvent(dataPoint);

            Calendar tomorrow = Calendar.getInstance();
            tomorrow.setTime(getToday().getTime());
            tomorrow.add(Calendar.DATE, 1);

            // today's and tomorrow's forecast
            if (DateHelper.sameDate(getToday(), calendarEvent.getStartTime()) || DateHelper.sameDate(tomorrow, calendarEvent.getStartTime())) {
                // search for morning
                if (calendarEvent.getStartTime().get(Calendar.HOUR_OF_DAY) == 9) {
                    filteredCalendarList.add(calendarEvent);
                }

                // search for afternoon
                if (calendarEvent.getStartTime().get(Calendar.HOUR_OF_DAY) == 15) {
                    filteredCalendarList.add(calendarEvent);
                }

                // search for evening
                if (calendarEvent.getStartTime().get(Calendar.HOUR_OF_DAY) == 21) {
                    filteredCalendarList.add(calendarEvent);
                }
            }
        }

        // add weather info to our list of events so that we display it in the calendar view
        for (CalendarEvent weatherEvent : filteredCalendarList) {
            for (int i = 0; i < getEvents().size(); i++) {
                CalendarEvent calendarEvent = getEvents().get(i);

                Calendar dayBefore = Calendar.getInstance();
                dayBefore.setTime(calendarEvent.getInstanceDay().getTime());
                dayBefore.add(Calendar.DATE, -1);

                if (DateHelper.sameDate(weatherEvent.getStartTime(), dayBefore)) {
                    weatherEvent.setDayReference(getEvents().get(i - 1).getDayReference());
                    getEvents().add(i, weatherEvent);
                    break;
                }
            }
        }
        return filteredCalendarList;
    }

    // endregion
}
