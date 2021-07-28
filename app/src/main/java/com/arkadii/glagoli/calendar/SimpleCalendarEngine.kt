package com.arkadii.glagoli.calendar

import android.util.Log
import java.util.*


class SimpleCalendarEngine: CalendarEngine {

    private var currentCalendar = Calendar.getInstance()


    private fun getDays(): MutableList<Day> {
        val calendar = Calendar.getInstance()
        calendar.time = currentCalendar.time
        val daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstOfMonth =
            if(calendar.get(Calendar.DAY_OF_WEEK) == 1) 6
            else calendar.get(Calendar.DAY_OF_WEEK) - 2
        val allDays = daysOfMonth + firstOfMonth
        Log.d(TAG, "daysOfMonth = $daysOfMonth firstOfMonth = $firstOfMonth allDays = $allDays")

        return fillDays(calendar, firstOfMonth, allDays)
    }

    private fun fillDays(calendar: Calendar, firstOfMonth: Int, allDays: Int): MutableList<Day> {
        val days = mutableListOf<Day>()
        for (x in 1..allDays) {
            if(x > firstOfMonth) {
                days.add(Day(
                        true,
                    (x - firstOfMonth).toString(),
                    (calendar.get(Calendar.MONTH) + 1).toString(),
                    calendar.get(Calendar.YEAR).toString()
                ))
            } else {
                days.add(Day(false, "", "", ""))
            }
        }
        return days
    }

    override fun plusMonth(): MutableList<Day> {
        currentCalendar.add(Calendar.MONTH, 1)
        return getDays()

    }

    override fun minusMonth(): MutableList<Day> {
        currentCalendar.add(Calendar.MONTH, -1)
        return getDays()
    }

    override fun plusYear(): MutableList<Day> {
        currentCalendar.add(Calendar.YEAR, 1)
        return getDays()
    }

    override fun minusYear(): MutableList<Day> {
        currentCalendar.add(Calendar.YEAR, -1)
        return getDays()
    }

    override fun getCurrentDays(): MutableList<Day> {
        return getDays()
    }

    override fun getCalendar(): Calendar {
        return currentCalendar
    }

    companion object {
        const val TAG = "SimpleCalendarEngine"
    }
}