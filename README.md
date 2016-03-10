# AgendaCalendarView

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-AgendaCalendarView-green.svg?style=true)](https://android-arsenal.com/details/1/2796)

This library replicates the basic features of the Calendar and Agenda views from the Sunrise Calendar (now Outlook) app, coupled with some small design touch from the Google Calendar app.  

![](https://raw.githubusercontent.com/Tibolte/AgendaCalendarView/master/demo.gif)  

Usage
===============================

Grab it from maven:

```groovy
    compile 'com.github.tibolte:agendacalendarview:1.0.4'
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

The event list contains BaseCalendarEvent instances, see the description of the parameters:
```java
    /**
     * Initializes the event
     *
     * @param id          The id of the event.
     * @param color       The color of the event.
     * @param title       The title of the event.
     * @param description The description of the event.
     * @param location    The location of the event.
     * @param dateStart   The start date of the event.
     * @param dateEnd     The end date of the event.
     * @param allDay      Int that can be equal to 0 or 1.
     * @param duration    The duration of the event in RFC2445 format.
     */
    public BaseCalendarEvent(long id, int color, String title, String description, String location, long dateStart, long dateEnd, int allDay, String duration) {
        this.mId = id;
        this.mColor = color;
        this.mAllDay = (allDay == 1) ? true : false;
        this.mDuration = duration;
        this.mTitle = title;
        this.mDescription = description;
        this.mLocation = location;

        this.mStartTime = Calendar.getInstance();
        this.mStartTime.setTimeInMillis(dateStart);
        this.mEndTime = Calendar.getInstance();
        this.mEndTime.setTimeInMillis(dateEnd);
    }
````
Here is a quick (and very simple) example providing a list of events, you can provide several layouts too:

```java
    private void mockList(List<CalendarEvent> eventList) {
        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        endTime1.add(Calendar.MONTH, 1);
        BaseCalendarEvent event1 = new BaseCalendarEvent("Thibault travels in Iceland", "A wonderful journey!", "Iceland",
                ContextCompat.getColor(this, R.color.orange_dark), startTime1, endTime1, true);
        eventList.add(event1);

        Calendar startTime2 = Calendar.getInstance();
        startTime2.add(Calendar.DAY_OF_YEAR, 1);
        Calendar endTime2 = Calendar.getInstance();
        endTime2.add(Calendar.DAY_OF_YEAR, 3);
        BaseCalendarEvent event2 = new BaseCalendarEvent("Visit to Dalvík", "A beautiful small town", "Dalvík",
                ContextCompat.getColor(this, R.color.yellow), startTime2, endTime2, true);
        eventList.add(event2);

        // Example on how to provide your own layout
        Calendar startTime3 = Calendar.getInstance();
        Calendar endTime3 = Calendar.getInstance();
        startTime3.set(Calendar.HOUR_OF_DAY, 14);
        startTime3.set(Calendar.MINUTE, 0);
        endTime3.set(Calendar.HOUR_OF_DAY, 15);
        endTime3.set(Calendar.MINUTE, 0);
        DrawableCalendarEvent event3 = new DrawableCalendarEvent("Visit of Harpa", "", "Dalvík",
                ContextCompat.getColor(this, R.color.blue_dark), startTime3, endTime3, false, R.drawable.common_ic_googleplayservices);
        eventList.add(event3);
    }
````  

# Roadmap (feel free to suggest any other improvement ideas)

. Parallax items like in Google Calendar  
. Easier way to provide your own list of events (i.e when receiving objects from a web API)

# Participating?
Make your pull requests on feature or bugfix branches.  

# Special thanks to these contributors
[FHellmann](https://github.com/FHellmann)

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
