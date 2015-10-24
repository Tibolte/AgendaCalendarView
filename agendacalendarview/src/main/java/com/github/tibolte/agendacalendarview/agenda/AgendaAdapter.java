package com.github.tibolte.agendacalendarview.agenda;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Adapter for the agenda, implements StickyListHeadersAdapter.
 * Days as sections and CalendarEvents as list items.
 */
public class AgendaAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<CalendarEvent> mEvents = new ArrayList<>();

    // region Constructor

    public AgendaAdapter() {
    }

    // endregion

    // region Public methods

    public void updateEvents(List<CalendarEvent> events) {
        this.mEvents.clear();
        this.mEvents.addAll(events);
        notifyDataSetChanged();
    }

    // endregion

    // region Interface - StickyListHeadersAdapter

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        AgendaHeaderView agendaHeaderView = (AgendaHeaderView) convertView;
        if (agendaHeaderView == null) {
            agendaHeaderView = AgendaHeaderView.inflate(parent);
        }
        agendaHeaderView.setDay(getItem(position).getInstanceDay());
        return agendaHeaderView;
    }

    @Override
    public long getHeaderId(int position) {
        return mEvents.get(position).getInstanceDay().getTimeInMillis();
    }

    // endregion

    // region Class - BaseAdapter

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public CalendarEvent getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AgendaEventView agendaEventView = (AgendaEventView) convertView;
        if (agendaEventView == null) {
            agendaEventView = AgendaEventView.inflate(parent);
        }
        agendaEventView.setEvent(getItem(position));
        return agendaEventView;
    }

    // endregion
}
