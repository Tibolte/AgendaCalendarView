package com.github.tibolte.agendacalendarview.agenda;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.R;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.utils.BusProvider;
import com.github.tibolte.agendacalendarview.utils.DateHelper;
import com.github.tibolte.agendacalendarview.utils.Events;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.Calendar;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * StickyListHeadersListView to scroll chronologically through events.
 */
public class AgendaListView extends StickyListHeadersListView {

    // region Constructors

    public AgendaListView(Context context) {
        super(context);
    }

    public AgendaListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AgendaListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // endregion

    // region Class - View

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        BusProvider.getInstance().toObserverable()
                .subscribe(event -> {
                    if (event instanceof Events.DayClickedEvent) {
                        Events.DayClickedEvent clickedEvent = (Events.DayClickedEvent) event;
                        scrollToCurrentDate(clickedEvent.getCalendar());
                    } else if (event instanceof Events.CalendarScrolledEvent) {
                        int offset = (int) (3 * getResources().getDimension(R.dimen.day_cell_height));
                        translateList(offset);
                    } else if (event instanceof Events.EventsFetched) {
                        ((AgendaAdapter) getAdapter()).updateEvents(CalendarManager.getInstance().getEvents());

                        getViewTreeObserver().addOnGlobalLayoutListener(
                                new ViewTreeObserver.OnGlobalLayoutListener() {
                                    @Override
                                    public void onGlobalLayout() {
                                        if (getWidth() != 0 && getHeight() != 0) {
                                            // display only two visible rows on the calendar view
                                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
                                            int height = getHeight();
                                            int margin = (int) (getContext().getResources().getDimension(R.dimen.calendar_header_height) + 2 * getContext().getResources().getDimension(R.dimen.day_cell_height));
                                            layoutParams.height = height - margin;
                                            layoutParams.setMargins(0, margin, 0, 0);
                                            setLayoutParams(layoutParams);

                                            scrollToCurrentDate(CalendarManager.getInstance().getToday());

                                            getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                        }
                                    }
                                }
                        );
                    } else if (event instanceof Events.ForecastFetched) {
                        ((AgendaAdapter) getAdapter()).updateEvents(CalendarManager.getInstance().getEvents());
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                // if the user touches the listView, we put it back to the top
                translateList(0);
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    // endregion

    // region Public methods

    public void scrollToCurrentDate(Calendar today) {
        List<CalendarEvent> events = CalendarManager.getInstance().getEvents();

        int toIndex = 0;
        for (int i = 0; i < events.size(); i++) {
            if (DateHelper.sameDate(today, events.get(i).getInstanceDay())) {
                toIndex = i;
                break;
            }
        }

        final int finalToIndex = toIndex;
        post(()->setSelection(finalToIndex));
    }

    public void translateList(int targetY) {
        ObjectAnimator mover = ObjectAnimator.ofFloat(this, "translationY", targetY);
        mover.setDuration(150);
        if (targetY == 0) {
            mover.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    BusProvider.getInstance().send(new Events.AgendaListViewTouchedEvent());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        mover.start();
    }

    // endregion
}
