package com.github.tibolte.agendacalendarview;

import com.github.tibolte.agendacalendarview.agenda.AgendaAdapter;
import com.github.tibolte.agendacalendarview.agenda.AgendaListView;
import com.github.tibolte.agendacalendarview.calendar.CalendarView;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.utils.BusProvider;
import com.github.tibolte.agendacalendarview.utils.Events;
import com.github.tibolte.agendacalendarview.utils.ListViewScrollTracker;
import com.github.tibolte.agendacalendarview.widgets.FloatingActionButton;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * View holding the agenda and calendar view together.
 */
public class AgendaCalendarView extends FrameLayout implements StickyListHeadersListView.OnStickyHeaderChangedListener {

    private static final String LOG_TAG = AgendaCalendarView.class.getSimpleName();

    private CalendarView mCalendarView;
    private AgendaListView mAgendaListView;
    private FloatingActionButton mFloatingActionButton;

    private int mCalendarHeaderColor;
    private CalendarPickerController mCalendarPickerController;

    private ListViewScrollTracker mAgendaListViewScrollTracker;
    private AbsListView.OnScrollListener mAgendaScrollListener = new AbsListView.OnScrollListener() {
        int mCurrentAngle;
        int mMaxAngle = 85;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int scrollY = mAgendaListViewScrollTracker.calculateScrollY(firstVisibleItem, visibleItemCount);
            if (scrollY != 0) {
                mFloatingActionButton.show();
            }
            Log.d(LOG_TAG, String.format("Agenda listView scrollY: %d", scrollY));
            int toAngle = scrollY / 100;
            if (toAngle > mMaxAngle) {
                toAngle = mMaxAngle;
            } else if (toAngle < -mMaxAngle) {
                toAngle = -mMaxAngle;
            }
            RotateAnimation rotate = new RotateAnimation(mCurrentAngle, toAngle, mFloatingActionButton.getWidth() / 2, mFloatingActionButton.getHeight() / 2);
            rotate.setFillAfter(true);
            mCurrentAngle = toAngle;
            mFloatingActionButton.startAnimation(rotate);
        }
    };

    // region Constructors

    public AgendaCalendarView(Context context) {
        super(context);
    }

    public AgendaCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorOptionsView, 0, 0);
        mCalendarHeaderColor = a.getColor(R.styleable.ColorOptionsView_calendarHeaderColor, getResources().getColor(R.color.calendar_past_days_bg));

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_agendacalendar, this, true);

        setAlpha(0f);
    }

    // endregion

    // region Class - View

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCalendarView = (CalendarView) findViewById(R.id.calendar_view);
        mAgendaListView = (AgendaListView) findViewById(R.id.agenda_listview);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

        mCalendarView.findViewById(R.id.cal_day_names).setBackgroundColor(mCalendarHeaderColor);
        mAgendaListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            mCalendarPickerController.onEventSelected(CalendarManager.getInstance().getEvents().get(position));
        });

        BusProvider.getInstance().toObserverable()
                .subscribe(event -> {
                    if (event instanceof Events.DayClickedEvent) {
                        mCalendarPickerController.onDaySelected(((Events.DayClickedEvent) event).getDay());
                    } else if (event instanceof Events.EventsFetched) {
                        ObjectAnimator alphaAnimation = new ObjectAnimator().ofFloat(this, "alpha", getAlpha(), 1f).setDuration(500);
                        alphaAnimation.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                long fabAnimationDelay = 500;
                                // Just after setting the alpha from this view to 1, we hide the fab.
                                // It will reappear as soon as the user is scrolling the Agenda view.
                                new Handler().postDelayed(() -> {
                                    mFloatingActionButton.hide();
                                    mAgendaListViewScrollTracker = new ListViewScrollTracker(mAgendaListView);
                                    mAgendaListView.setOnScrollListener(mAgendaScrollListener);
                                    mFloatingActionButton.setOnClickListener((v) -> {
                                        mAgendaListView.translateList(0);
                                        mAgendaListView.scrollToCurrentDate(CalendarManager.getInstance().getToday());
                                        new Handler().postDelayed(() -> mFloatingActionButton.hide(), fabAnimationDelay);
                                    });
                                }, fabAnimationDelay);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        alphaAnimation.start();
                    }
                });
    }

    // endregion

    // region Interface - StickyListHeadersListView.OnStickyHeaderChangedListener

    @Override
    public void onStickyHeaderChanged(StickyListHeadersListView stickyListHeadersListView, View header, int position, long headerId) {
        Log.d(LOG_TAG, String.format("onStickyHeaderChanged, position = %d, headerId = %d", position, headerId));

        if (CalendarManager.getInstance().getEvents().size() > 0) {
            CalendarEvent event = CalendarManager.getInstance().getEvents().get(position);
            if (event != null) {
                mCalendarView.scrollToDate(event);
            }
        }
    }

    // endregion

    // region Public methods

    public void init(List<CalendarEvent> eventList, Calendar minDate, Calendar maxDate, Locale locale, CalendarPickerController calendarPickerController) {
        mCalendarPickerController = calendarPickerController;

        CalendarManager.getInstance(getContext()).buildCal(minDate, maxDate, locale);

        // Feed our views with weeks list and events
        mCalendarView.init(CalendarManager.getInstance(getContext()));

        // Load agenda events and scroll to current day
        AgendaAdapter agendaAdapter = new AgendaAdapter();
        mAgendaListView.setAdapter(agendaAdapter);
        mAgendaListView.setOnStickyHeaderChangedListener(this);
        CalendarManager.getInstance().loadEvents(eventList);
    }

    // endregion
}
