package com.example.project

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.Date
import java.time.LocalDate
import java.time.ZoneId

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
    @RequiresApi(Build.VERSION_CODES.O)
    fun markFertilityDays(ovulationDate: LocalDate) {
        val firstFertalDay = ovulationDate.minusDays(5)
        val firstFertalDayDate = Date.from(firstFertalDay.atStartOfDay(ZoneId.systemDefault()).toInstant()).time
        val lastFertalDay = ovulationDate.plusDays(1)
        val lastFertalDayDate = Date.from(lastFertalDay.atStartOfDay(ZoneId.systemDefault()).toInstant()).time
        var currentTime = firstFertalDayDate
        val ovulationDateInMillis = Date.from(ovulationDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).time

        while (currentTime < ovulationDateInMillis) {
            val event = Event(Color.parseColor("#FFA500"), currentTime)
            calendarView.addEvent(event)
            currentTime += 24 * 60 * 60 * 1000

        }
        val event = Event(Color.parseColor("#FFA500"), lastFertalDayDate)
        calendarView.addEvent(event)
        calendarView.invalidate()
    }
}

