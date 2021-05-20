package com.arkadii.glagoli.calendar

import java.util.Calendar

interface CalendarEngine {
    fun plusMonth():MutableList<Day>

    fun minusMonth():MutableList<Day>

    fun plusYear():MutableList<Day>

    fun minusYear():MutableList<Day>

    fun getCurrentDays(): MutableList<Day>

    fun getCalendar(): Calendar
}