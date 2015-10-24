package com.github.tibolte.agendacalendarview.agenda;

import com.github.tibolte.agendacalendarview.R;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.utils.DateHelper;
import com.github.tibolte.agendacalendarview.weather.WeatherIconView;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        View circleView = findViewById(R.id.view_agenda_event_circle);
        TextView txtTime = (TextView) findViewById(R.id.view_agenda_event_time);
        TextView txtDuration = (TextView) findViewById(R.id.view_agenda_event_duration);
        TextView txtTitle = (TextView) findViewById(R.id.view_agenda_event_title);
        TextView txtLocation = (TextView) findViewById(R.id.view_agenda_event_location);
        TextView txtWeather = (TextView) findViewById(R.id.view_agenda_event_weather_temperature);
        WeatherIconView weatherIconView = (WeatherIconView) findViewById(R.id.view_agenda_event_weather_icon);
        LinearLayout timeContainer = (LinearLayout) findViewById(R.id.view_agenda_event_time_container);
        LinearLayout descriptionContainer = (LinearLayout) findViewById(R.id.view_agenda_event_description_container);
        LinearLayout locationContainer = (LinearLayout) findViewById(R.id.view_agenda_event_location_container);
        LinearLayout weatherContainer = (LinearLayout) findViewById(R.id.view_agenda_event_weather_container);

        timeContainer.setVisibility(VISIBLE);
        txtDuration.setVisibility(VISIBLE);
        descriptionContainer.setVisibility(VISIBLE);
        weatherContainer.setVisibility(GONE);
        txtTitle.setTextColor(getResources().getColor(android.R.color.black));
        txtTime.setTextColor(getResources().getColor(android.R.color.black));

        txtTitle.setText(event.getTitle());
        txtLocation.setText(event.getLocation());
        if (event.getLocation().length() > 0) {
            locationContainer.setVisibility(VISIBLE);
            txtLocation.setText(event.getLocation());
        } else {
            locationContainer.setVisibility(GONE);
        }
        GradientDrawable circleBgShape = (GradientDrawable) circleView.getBackground();
        circleBgShape.setColor(event.getColor());

        if (event.isAllDay()) {
            txtTime.setText(getResources().getString(R.string.agenda_event_all_day).toUpperCase());
            // calculate remaining days
            txtDuration.setText(DateHelper.getDuration(getContext(), event.getEndTime().getTimeInMillis() - event.getInstanceDay().getTimeInMillis()));
        } else if (!event.isPlaceHolder() && !event.isWeather()) {
            txtDuration.setText(DateHelper.getDuration(getContext(), event.getEndTime().getTimeInMillis() - event.getStartTime().getTimeInMillis()));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.agenda_event_hour_format));
            txtTime.setText(simpleDateFormat.format(event.getStartTime().getTime()));
        } else if (event.isWeather()) {
            switch (event.getStartTime().get(Calendar.HOUR_OF_DAY)) {
                case 9:
                    txtTime.setText(getResources().getString(R.string.morning));
                    break;
                case 15:
                    txtTime.setText(getResources().getString(R.string.afternoon));
                    break;
                case 21:
                    txtTime.setText(getResources().getString(R.string.evening));
                    break;
            }
            txtTime.setTextColor(getResources().getColor(R.color.calendar_text_first_day_of_month));
            txtDuration.setVisibility(GONE);
            descriptionContainer.setVisibility(GONE);
            weatherContainer.setVisibility(VISIBLE);
            txtWeather.setText(String.format("%d °", Math.round(event.getTemperature())));
            weatherIconView.setIconForForecast(event.getWeatherIcon());
        } else {
            txtDuration.setText("");
            txtTime.setText("");
            timeContainer.setVisibility(GONE);
            txtTitle.setTextColor(getResources().getColor(R.color.calendar_text_default));
        }
    }

    // endregion
}
