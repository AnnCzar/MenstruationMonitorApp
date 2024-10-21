package com.example.project

import android.graphics.Color
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.Date

class EventDecorator(
    private val calendarView: CompactCalendarView,
) {

    fun markPeriodDays(startDate: Date, endDate: Date ) {
        val startDate = startDate.time
        val endDate= endDate.time
        var currentTime = startDate


        while (currentTime <= endDate) {
            val event = Event(Color.RED, currentTime)
            calendarView.addEvent(event)
            currentTime += 24 * 60 * 60 * 1000
        }
        calendarView.invalidate()
    }

    fun markOvulation( ovulationDate: Date) {
        val ovulationTime = ovulationDate.time
        val event = Event(Color.GREEN, ovulationTime)
        calendarView.addEvent(event)
        calendarView.invalidate()
    }

    fun doctorVisits(doctorVisit: Date) {
        val doctorVisitTime = doctorVisit.time
        val event = Event(Color.BLUE, doctorVisitTime)
        calendarView.addEvent(event)
    }
}
