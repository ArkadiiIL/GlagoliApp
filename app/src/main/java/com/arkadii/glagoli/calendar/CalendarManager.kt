package com.arkadii.glagoli.calendar

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class CalendarManager(private val context: FragmentActivity,
                      private val rv: RecyclerView) {

    private var currentCalendar = Calendar.getInstance()
    private val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    private var adapter: CalendarAdapter

    init {
        Log.v(TAG, "Init CalendarManager")
        adapter = CalendarAdapter(getDays())
        rv.layoutManager = getLayoutManager()
        rv.adapter = adapter
    }


    private fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(context, 7)
    }

    private fun getDays(): MutableList<Day> {
        val calendar = Calendar.getInstance()
        calendar.time = currentCalendar.time
        val daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - (3 - calendar.firstDayOfWeek)
        val allDays = daysOfMonth + firstOfMonth
        val days = mutableListOf<Day>()
        for (x in 1..allDays) {
            if(x > firstOfMonth) {
                days.add(Day(
                        true,
                        (x - firstOfMonth).toString(),
                        calendar.get(Calendar.MONTH).toString(),
                        calendar.get(Calendar.YEAR).toString()
                ))
            } else {
                days.add(Day(false, "", "", ""))
            }
        }
        return days
    }

    fun plusMonth() {
        currentCalendar.add(Calendar.MONTH, 1)
        adapter.updateCalendar(getDays())

    }

    fun minusMonth() {
        currentCalendar.add(Calendar.MONTH, -1)
        adapter.updateCalendar(getDays())
    }

    fun plusYear() {
        currentCalendar.add(Calendar.YEAR, 1)
        adapter.updateCalendar(getDays())
    }

    fun minusYear() {
        currentCalendar.add(Calendar.YEAR, -1)
        adapter.updateCalendar(getDays())
    }

    fun getDateText(): String {
        return formatter.format(currentCalendar.time)
    }

    private fun updateCalendar() {
        currentCalendar.time = Date()
    }

    companion object {
        const val TAG = "CalendarManagerCHECKTAG"
    }
}