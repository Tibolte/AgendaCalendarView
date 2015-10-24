package com.github.tibolte.agendacalendarview.agenda;

import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.R;
import com.github.tibolte.agendacalendarview.utils.DateHelper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Header view for the StickyHeaderListView of the agenda view
 */
public class AgendaHeaderView extends LinearLayout {

    public static AgendaHeaderView inflate(ViewGroup parent) {
        AgendaHeaderView agendaHeaderView = (AgendaHeaderView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_agenda_header, parent, false);
        return agendaHeaderView;
    }

    // region Constructors

    public AgendaHeaderView(Context context) {
        super(context);
    }

    public AgendaHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AgendaHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // endregion

    // region Public methods

    public void setDay(Calendar day) {
        TextView txtDay = (TextView) findViewById(R.id.view_agenda_label);
        TextView txtTodayTomorrow = (TextView) findViewById(R.id.view_agenda_is_today);
        TextView txtSpacer = (TextView) findViewById(R.id.view_agenda_label_spacer);

        Calendar today = CalendarManager.getInstance().getToday();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(today.getTime());
        tomorrow.add(Calendar.DATE, 1);

        // Construct a day formatter without any year displayed
        String yearLessDate = DateHelper.getYearLessLocalizedDate(day, CalendarManager.getInstance().getLocale());

        txtDay.setTextColor(getResources().getColor(R.color.calendar_text_default));
        txtTodayTomorrow.setVisibility(GONE);
        txtSpacer.setVisibility(GONE);

        if (DateHelper.sameDate(day, today)) {
            txtDay.setTextColor(getResources().getColor(R.color.blue_selected));
            txtTodayTomorrow.setText(getResources().getString(R.string.today).toUpperCase());
            txtTodayTomorrow.setTextColor(getResources().getColor(R.color.blue_selected));
            txtTodayTomorrow.setVisibility(VISIBLE);
            txtTodayTomorrow.setTypeface(null, Typeface.BOLD);
            txtSpacer.setTextColor(getResources().getColor(R.color.blue_selected));
            txtSpacer.setVisibility(VISIBLE);
        } else if (DateHelper.sameDate(day, tomorrow)) {
            txtTodayTomorrow.setVisibility(VISIBLE);
            txtTodayTomorrow.setTypeface(null, Typeface.NORMAL);
            txtTodayTomorrow.setTextColor(getResources().getColor(R.color.calendar_text_default));
            txtTodayTomorrow.setText(getResources().getString(R.string.tomorrow).toUpperCase());
            txtSpacer.setTextColor(getResources().getColor(R.color.calendar_text_default));
            txtSpacer.setVisibility(VISIBLE);
        }

        txtDay.setText(yearLessDate);
    }

    // endregion
}
