package com.github.tibolte.agendacalendarview.agenda;

import com.github.tibolte.agendacalendarview.R;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * List item view for the StickyHeaderListView of the agenda view
 */
public class AgendaEventView extends LinearLayout {
    public static AgendaEventView inflate(ViewGroup parent) {
        AgendaEventView agendaEventView = (AgendaEventView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_agenda_event, parent, false);
        return agendaEventView;
    }

    // region Constructors

    public AgendaEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AgendaEventView(Context context) {
        super(context);
    }

    public AgendaEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // endregion

    // region Public methods

    public void setEvent(CalendarEvent event) {
        TextView txtTitle = (TextView) findViewById(R.id.view_agenda_event_title);
        TextView txtLocation = (TextView) findViewById(R.id.view_agenda_event_location);
        LinearLayout descriptionContainer = (LinearLayout) findViewById(R.id.view_agenda_event_description_container);
        LinearLayout locationContainer = (LinearLayout) findViewById(R.id.view_agenda_event_location_container);

        descriptionContainer.setVisibility(VISIBLE);
        txtTitle.setTextColor(getResources().getColor(android.R.color.black));

        txtTitle.setText(event.getTitle());
        txtLocation.setText(event.getLocation());
        if (event.getLocation().length() > 0) {
            locationContainer.setVisibility(VISIBLE);
            txtLocation.setText(event.getLocation());
        } else {
            locationContainer.setVisibility(GONE);
        }

        if (event.getTitle().equals(getResources().getString(R.string.agenda_event_no_events))) {
            txtTitle.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            txtTitle.setTextColor(getResources().getColor(R.color.theme_text_icons));
        }
        descriptionContainer.setBackgroundColor(event.getColor());
        txtLocation.setTextColor(getResources().getColor(R.color.theme_text_icons));
    }

    // endregion
}
