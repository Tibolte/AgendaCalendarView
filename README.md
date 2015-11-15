# AgendaCalendarView
Finalizing details..., small layout changes and other functionalities to come. Feel free to try the sample app for now.  

![](https://raw.githubusercontent.com/Tibolte/AgendaCalendarView/master/demo.gif)  

Usage
===============================

Grab it from maven:

```groovy
    compile 'com.github.tibolte:agendacalendarview:1.0.0'
````  

Declare this view in your layout like below, providing your own theme and colors if you want.

```java
    <com.github.tibolte.agendacalendarview.AgendaCalendarView
        android:id="@+id/agenda_calendar_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        agendaCalendar:agendaCurrentDayTextColor="@color/theme_primary"
        agendaCalendar:calendarColor="@color/theme_primary"
        agendaCalendar:calendarCurrentDayTextColor="@color/calendar_text_current_day"
        agendaCalendar:calendarDayTextColor="@color/theme_text_icons"
        agendaCalendar:calendarHeaderColor="@color/theme_primary_dark"
        agendaCalendar:calendarPastDayTextColor="@color/theme_light_primary"
        agendaCalendar:fabColor="@color/theme_accent" />
````  

Then configure it in your code with a start and end date associated with a list of events:  
```java
        // minimum and maximum date of our calendar
        // 2 month behind, one year ahead, example: March 2015 <-> May 2015 <-> May 2016
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        minDate.add(Calendar.MONTH, -2);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

        List<CalendarEvent> eventList = new ArrayList<>();
        mockList(eventList);

        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
````  

The event list contains CalendarEvent instances, see the description of the parameters:
```java
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
    public CalendarEvent(String title, String description, String location, int color, Calendar startTime, Calendar endTime, boolean allDay) {
        this.mTitle = title;
        this.mDescription = description;
        this.mLocation = location;
        this.mColor = color;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mAllDay = allDay;
    }
````
Here is a quick (and very simple) to provide a list of events:

```java
    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        endTime1.add(Calendar.MONTH, 1);
        CalendarEvent event1 = new CalendarEvent("Thibault travels in Iceland", "A wonderful journey!", "Iceland",
                ContextCompat.getColor(this, R.color.orange_dark), startTime1, endTime1, true);
        eventList.add(event1);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar endTime2 = Calendar.getInstance();
        endTime2.add(Calendar.DAY_OF_YEAR, 3);
        CalendarEvent event2 = new CalendarEvent("Visit to Dalvík", "A beautiful small town", "Dalvík",
                ContextCompat.getColor(this, R.color.yellow), startTime2, endTime2, true);
        eventList.add(event2);

        Calendar startTime3 = Calendar.getInstance();
        Calendar endTime3 = Calendar.getInstance();
        startTime3.set(Calendar.HOUR_OF_DAY, 14);
        startTime3.set(Calendar.MINUTE, 0);
        endTime3.set(Calendar.HOUR_OF_DAY, 15);
        endTime3.set(Calendar.MINUTE, 0);
        CalendarEvent event3 = new CalendarEvent("Visit of Harpa", "", "Dalvík",
                ContextCompat.getColor(this, R.color.blue_dark), startTime3, endTime3, false);
        eventList.add(event3);
    }
````  

# Participating?
Make your pull requests on feature or bugfix branches.  

License
-----------

    Copyright 2015 Thibault Guégan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
