package com.github.tibolte.agendacalendarview;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

import java.util.Calendar;

public interface CalendarPickerController {
    void onDaySelected(DayItem dayItem);

    void onEventSelected(CalendarEvent event);

    void onScrollToDate(Calendar calendar);
}
