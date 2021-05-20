package com.arkadii.glagoli.calendar

import java.text.SimpleDateFormat
import java.util.*

class CalendarController(private val calendarEngine: CalendarEngine,
                         private val adapter: CalendarAdapter) {
    private val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    init {
        adapter.updateCalendar(calendarEngine.getCurrentDays())
    }

    fun plusMonth() {
        adapter.updateCalendar(calendarEngine.plusMonth())
    }
    fun minusMonth() {
        adapter.updateCalendar(calendarEngine.minusMonth())
    }
    fun plusYear() {
        adapter.updateCalendar(calendarEngine.plusYear())
    }

    fun minusYear() {
        adapter.updateCalendar(calendarEngine.minusYear())
    }

    fun getDateText(): String {
        return formatter.format(calendarEngine.getCalendar().time)
    }

    companion object {
        const val TAG = "CalendarControllerCHECKTAG"
    }
}