package com.github.tibolte.agendacalendarview;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;

public interface CalendarPickerController {
    void onDaySelected(DayItem dayItem);

    void onEventSelected(CalendarEvent event);
}
