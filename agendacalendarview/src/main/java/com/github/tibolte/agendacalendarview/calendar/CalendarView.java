package com.github.tibolte.agendacalendarview.calendar;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.R;
import com.github.tibolte.agendacalendarview.calendar.weekslist.WeekListView;
import com.github.tibolte.agendacalendarview.calendar.weekslist.WeeksAdapter;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.github.tibolte.agendacalendarview.models.WeekItem;
import com.github.tibolte.agendacalendarview.utils.BusProvider;
import com.github.tibolte.agendacalendarview.utils.DateHelper;
import com.github.tibolte.agendacalendarview.utils.Events;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * The calendar view is a freely scrolling view that allows the user to browse between days of the
 * year.
 */
public class CalendarView extends LinearLayout {

    private static final String LOG_TAG = CalendarView.class.getSimpleName();

    /**
     * Top of the calendar view layout, the week days list
     */
    private LinearLayout mDayNamesHeader;
    /**
     * Part of the calendar view layout always visible, the weeks list
     */
    private WeekListView mListViewWeeks;
    /**
     * The adapter for the weeks list
     */
    private WeeksAdapter mWeeksAdapter;
    /**
     * The current highlighted day in blue
     */
    private DayItem mSelectedDay;
    /**
     * The current row displayed at top of the list
     */
    private int mCurrentListPosition;

    // region Constructors

    public CalendarView(Context context) {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_calendar, this, true);

        setOrientation(VERTICAL);
    }

    // endregion

    public DayItem getSelectedDay() {
        return mSelectedDay;
    }

    public void setSelectedDay(DayItem mSelectedDay) {
        this.mSelectedDay = mSelectedDay;
    }

    public WeekListView getListViewWeeks() {
        return mListViewWeeks;
    }

    // region Class - View

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDayNamesHeader = (LinearLayout) findViewById(R.id.cal_day_names);
        mListViewWeeks = (WeekListView) findViewById(R.id.list_week);
        mListViewWeeks.setLayoutManager(new LinearLayoutManager(getContext()));
        mListViewWeeks.setHasFixedSize(true);
        mListViewWeeks.setItemAnimator(null);
        mListViewWeeks.setSnapEnabled(true);

        // display only two visible rows on the calendar view
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (getWidth() != 0 && getHeight() != 0) {
                            collapseCalendarView();
                            getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                }
        );
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        BusProvider.getInstance().toObserverable()
                .subscribe(event -> {
                    if (event instanceof Events.CalendarScrolledEvent) {
                        expandCalendarView();
                    } else if (event instanceof Events.AgendaListViewTouchedEvent) {
                        collapseCalendarView();
                    } else if (event instanceof Events.DayClickedEvent) {
                        Events.DayClickedEvent clickedEvent = (Events.DayClickedEvent) event;
                        updateSelectedDay(clickedEvent.getCalendar(), clickedEvent.getDay());
                    }
                });
    }

    // endregion

    // region Public methods

    public void init(CalendarManager calendarManager, int dayTextColor, int currentDayTextColor, int pastDayTextColor) {
        Calendar today = calendarManager.getToday();
        Locale locale = calendarManager.getLocale();
        SimpleDateFormat weekDayFormatter = calendarManager.getWeekdayFormatter();
        List<WeekItem> weeks = calendarManager.getWeeks();

        setUpHeader(today, weekDayFormatter, locale);
        setUpAdapter(today, weeks, dayTextColor, currentDayTextColor, pastDayTextColor);
        scrollToDate(today, weeks);
    }

    /**
     * Fired when the Agenda list view changes section.
     *
     * @param calendarEvent The event for the selected position in the agenda listview.
     */
    public void scrollToDate(final CalendarEvent calendarEvent) {
        mListViewWeeks.post(()->scrollToPosition(updateSelectedDay(calendarEvent.getInstanceDay(), calendarEvent.getDayReference())));
    }

    public void scrollToDate(Calendar today, List<WeekItem> weeks) {
        Integer currentWeekIndex = null;
        Calendar scrollToCal = today;

        for (int c = 0; c < weeks.size(); c++) {
            if (DateHelper.sameWeek(scrollToCal, weeks.get(c))) {
                currentWeekIndex = c;
                break;
            }
        }

        if (currentWeekIndex != null) {
            final Integer finalCurrentWeekIndex = currentWeekIndex;
            mListViewWeeks.post(() -> scrollToPosition(finalCurrentWeekIndex));
        }
    }

    public void setBackgroundColor(int color) {
        mListViewWeeks.setBackgroundColor(color);
    }

    // endregion

    // region Private methods

    private void scrollToPosition(int targetPosition) {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) mListViewWeeks.getLayoutManager());
        layoutManager.scrollToPosition(targetPosition);
    }

    private void updateItemAtPosition(int position) {
        WeeksAdapter weeksAdapter = (WeeksAdapter) mListViewWeeks.getAdapter();
        weeksAdapter.notifyItemChanged(position);
    }

    /**
     * Creates a new adapter if necessary and sets up its parameters.
     */
    private void setUpAdapter(Calendar today, List<WeekItem> weeks, int dayTextColor, int currentDayTextColor, int pastDayTextColor) {
        if (mWeeksAdapter == null) {
            Log.d(LOG_TAG, "Setting adapter with today's calendar: " + today.toString());
            mWeeksAdapter = new WeeksAdapter(getContext(), today, dayTextColor, currentDayTextColor, pastDayTextColor);
            mListViewWeeks.setAdapter(mWeeksAdapter);
        }
        mWeeksAdapter.updateWeeksItems(weeks);
    }

    private void setUpHeader(Calendar today, SimpleDateFormat weekDayFormatter, Locale locale) {
        int daysPerWeek = 7;
        String[] dayLabels = new String[daysPerWeek];
        Calendar cal = Calendar.getInstance(CalendarManager.getInstance(getContext()).getLocale());
        cal.setTime(today.getTime());
        int firstDayOfWeek = cal.getFirstDayOfWeek();
        for (int count = 0; count < 7; count++) {
            cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + count);
            if (locale.getLanguage().equals("en")) {
                dayLabels[count] = weekDayFormatter.format(cal.getTime()).toUpperCase(locale);
            } else {
                dayLabels[count] = weekDayFormatter.format(cal.getTime());
            }
        }

        for (int i = 0; i < mDayNamesHeader.getChildCount(); i++) {
            TextView txtDay = (TextView) mDayNamesHeader.getChildAt(i);
            txtDay.setText(dayLabels[i]);
        }
    }

    private void expandCalendarView() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        int height = (int) (getResources().getDimension(R.dimen.calendar_header_height) + 5 * getResources().getDimension(R.dimen.day_cell_height));
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    private void collapseCalendarView() {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        int height = (int) (getResources().getDimension(R.dimen.calendar_header_height) + 2 * getResources().getDimension(R.dimen.day_cell_height));
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

    /**
     * Update a selected cell day item.
     *
     * @param calendar The Calendar instance of the day selected.
     * @param dayItem  The DayItem information held by the cell item.
     * @return The selected row of the weeks list, to be updated.
     */
    private int updateSelectedDay(Calendar calendar, DayItem dayItem) {
        Integer currentWeekIndex = null;
        Calendar scrollToCal = calendar;

        // update highlighted/selected day
        if (!dayItem.equals(getSelectedDay())) {
            dayItem.setSelected(true);
            if (getSelectedDay() != null) {
                getSelectedDay().setSelected(false);
            }
            setSelectedDay(dayItem);
        }

        for (int c = 0; c < CalendarManager.getInstance().getWeeks().size(); c++) {
            if (DateHelper.sameWeek(scrollToCal, CalendarManager.getInstance().getWeeks().get(c))) {
                currentWeekIndex = c;
                break;
            }
        }

        if (currentWeekIndex != null) {
            // highlighted day has changed, update the rows concerned
            if (currentWeekIndex != mCurrentListPosition) {
                updateItemAtPosition(mCurrentListPosition);
            }
            mCurrentListPosition = currentWeekIndex;
            updateItemAtPosition(currentWeekIndex);
        }

        return mCurrentListPosition;
    }

    // endregion
}
