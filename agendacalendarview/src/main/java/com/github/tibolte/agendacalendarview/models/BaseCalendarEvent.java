package com.github.tibolte.agendacalendarview.models;

import java.util.Calendar;

/**
 * Event model class containing the information to be displayed on the agenda view.
 */
public class BaseCalendarEvent implements CalendarEvent {

    /**
     * Id of the event.
     */
    private long mId;
    /**
     * Color to be displayed in the agenda view.
     */
    private int mColor;
    /**
     * Title of the event.
     */
    private String mTitle;
    /**
     * Description of the event.
     */
    private String mDescription;
    /**
     * Where the event takes place.
     */
    private String mLocation;
    /**
     * Calendar instance helping sorting the events per section in the agenda view.
     */
    private Calendar mInstanceDay;
    /**
     * Start time of the event.
     */
    private Calendar mStartTime;
    /**
     * End time of the event.
     */
    private Calendar mEndTime;
    /**
     * Indicates if the event lasts all day.
     */
    private boolean mAllDay;
    /**
     * Tells if this BaseCalendarEvent instance is used as a placeholder in the agenda view, if there's
     * no event for that day.
     */
    private boolean mPlaceHolder;
    /**
     * Tells if this BaseCalendarEvent instance is used as a forecast information holder in the agenda
     * view.
     */
    private boolean mWeather;
    /**
     * Duration of the event.
     */
    private String mDuration;
    /**
     * References to a DayItem instance for that event, used to link interaction between the
     * calendar view and the agenda view.
     */
    private DayItem mDayReference;
    /**
     * References to a WeekItem instance for that event, used to link interaction between the
     * calendar view and the agenda view.
     */
    private WeekItem mWeekReference;
    /**
     * Weather icon string returned by the Dark Sky API.
     */
    private String mWeatherIcon;
    /**
     * Temperature value returned by the Dark Sky API.
     */
    private double mTemperature;

    // region Constructor

    /**
     * Initializes the event
     *
     * @param id          The id of the event.
     * @param color       The color of the event.
     * @param title       The title of the event.
     * @param description The description of the event.
     * @param location    The location of the event.
     * @param dateStart   The start date of the event.
     * @param dateEnd     The end date of the event.
     * @param allDay      Int that can be equal to 0 or 1.
     * @param duration    The duration of the event in RFC2445 format.
     */
    public BaseCalendarEvent(long id, int color, String title, String description, String location, long dateStart, long dateEnd, int allDay, String duration) {
        this.mId = id;
        this.mColor = color;
        this.mAllDay = (allDay == 1) ? true : false;
        this.mDuration = duration;
        this.mTitle = title;
        this.mDescription = description;
        this.mLocation = location;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.setTimeInMillis(dateStart);
        this.mEndTime = Calendar.getInstance();
        this.mEndTime.setTimeInMillis(dateEnd);
    }

    /**
     * Initializes the event
     * @param title The title of the event.
     * @param description The description of the event.
     * @param location The location of the event.
     * @param color The color of the event (for display in the app).
     * @param startTime The start time of the event.
     * @param endTime The end time of the event.
     * @param allDay Indicates if the event lasts the whole day.
     */
    public BaseCalendarEvent(String title, String description, String location, int color, Calendar startTime, Calendar endTime, boolean allDay) {
        this.mTitle = title;
        this.mDescription = description;
        this.mLocation = location;
        this.mColor = color;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mAllDay = allDay;
    }

    public BaseCalendarEvent(BaseCalendarEvent calendarEvent) {
        this.mId = calendarEvent.getId();
        this.mColor = calendarEvent.getColor();
        this.mAllDay = calendarEvent.isAllDay();
        this.mDuration = calendarEvent.getDuration();
        this.mTitle = calendarEvent.getTitle();
        this.mDescription = calendarEvent.getDescription();
        this.mLocation = calendarEvent.getLocation();
        this.mStartTime = calendarEvent.getStartTime();
        this.mEndTime = calendarEvent.getEndTime();
    }

    /**
     * Constructor for placeholder events, used if there are no events during one dat
     *
     * @param day   The instance day of the event.
     * @param title The title of the event.
     */
    public BaseCalendarEvent(Calendar day, String title) {
        this.mPlaceHolder = true;
        this.mTitle = title;
        this.mLocation = "";
        setInstanceDay(day);
    }

    // endregion

    // region Getters/Setters

    public int getColor() {
        return mColor;
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public String getDescription() {
        return mDescription;
    }

    public boolean isAllDay() {
        return mAllDay;
    }

    public void setAllDay(boolean allDay) {
        this.mAllDay = allDay;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Calendar getInstanceDay() {
        return mInstanceDay;
    }

    public void setInstanceDay(Calendar mInstanceDay) {
        this.mInstanceDay = mInstanceDay;
        this.mInstanceDay.set(Calendar.HOUR, 0);
        this.mInstanceDay.set(Calendar.MINUTE, 0);
        this.mInstanceDay.set(Calendar.SECOND, 0);
        this.mInstanceDay.set(Calendar.MILLISECOND, 0);
        this.mInstanceDay.set(Calendar.AM_PM, 0);
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar mEndTime) {
        this.mEndTime = mEndTime;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public boolean isPlaceHolder() {
        return mPlaceHolder;
    }

    public void setPlaceHolder(boolean mPlaceHolder) {
        this.mPlaceHolder = mPlaceHolder;
    }

    public boolean isWeather() {
        return mWeather;
    }

    public void setWeather(boolean mWeather) {
        this.mWeather = mWeather;
    }

    public DayItem getDayReference() {
        return mDayReference;
    }

    public void setDayReference(DayItem mDayReference) {
        this.mDayReference = mDayReference;
    }

    public WeekItem getWeekReference() {
        return mWeekReference;
    }

    public void setWeekReference(WeekItem mWeekReference) {
        this.mWeekReference = mWeekReference;
    }

    public String getWeatherIcon() {
        return mWeatherIcon;
    }

    public void setWeatherIcon(String mWeatherIcon) {
        this.mWeatherIcon = mWeatherIcon;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double mTemperature) {
        this.mTemperature = mTemperature;
    }

    @Override
    public CalendarEvent copy() {
        return new BaseCalendarEvent(this);
    }

    // endregion

    @Override
    public String toString() {
        return "BaseCalendarEvent{"
                + "title='"
                + mTitle
                + ", instanceDay= "
                + mInstanceDay.getTime()
                + "}";
    }
}
