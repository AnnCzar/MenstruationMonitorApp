package com.example.project

import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.Date
import java.time.LocalDate
import java.time.ZoneId


/**
 * Class responsible for decorating the CompactCalendarView with events.
 *
 * @param calendarView The CompactCalendarView instance to decorate.
 */
class EventDecorator(
    private val calendarView: CompactCalendarView,
) {


    /**
     * Marks a range of days (start to end) in the calendar as period days with a red color.
     *
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun markPeriodDays(startDate: Date, endDate: Date) {
        val startLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

        var currentDate = startLocalDate
        while (!currentDate.isAfter(endLocalDate)) {
            Log.d("MarkPeriodDays", "Przetwarzanie daty: $currentDate")

            val currentEpochMilli = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val event = Event(Color.RED, currentEpochMilli)
            calendarView.addEvent(event)

            currentDate = currentDate.plusDays(1)
        }
        calendarView.invalidate()
    }

    /**
     * Marks a specific day in the calendar as the ovulation day with a green color.
     *
     * @param ovulationDate The date of ovulation.
     */
    fun markOvulation( ovulationDate: Date) {
        val ovulationTime = ovulationDate.time
        val event = Event(Color.GREEN, ovulationTime)
        calendarView.addEvent(event)
        calendarView.invalidate()
    }

    /**
     * Marks a specific day in the calendar for a doctor visit with a blue color.
     *
     * @param doctorVisit The date of the doctor visit.
     */
    fun doctorVisits(doctorVisit: Date) {
        val doctorVisitTime = doctorVisit.time
        val event = Event(Color.BLUE, doctorVisitTime)
        calendarView.addEvent(event)
    }

    /**
     * Marks the fertile days around ovulation in the calendar with an orange color.
     *
     * Fertile days start 5 days before ovulation and end 1 day after ovulation.
     *
     * @param ovulationDate The date of ovulation.
     */
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

