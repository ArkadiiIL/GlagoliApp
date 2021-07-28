package com.arkadii.glagoli.calendar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.arkadii.glagoli.R
import java.util.*

class SimpleCalendarAdapter(private val context: Context,
                            private val listener: (CalendarViewHolder) -> Unit):
        RecyclerView.Adapter<CalendarViewHolder>(),
        CalendarAdapter {
    private val days = mutableListOf<Day>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        Log.v(TAG, "OnCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        return CalendarViewHolder(inflater.inflate(
                R.layout.calendar_cell, parent, false))
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        Log.v(TAG, "onBindViewHolder")
        val day = days[position]
        holder.day = day
        holder.cellDayText.text = day.day
        if(day.isReal) {
            if(isDayPassed(day)) {
                holder.cellDayText.setTextColor(ContextCompat.getColor(context, R.color.grey))
                holder.cellDayText.isClickable = false
            } else {
                listener(holder)
            }
        }
    }

    private fun isDayPassed(day: Day): Boolean {
        val calendar = Calendar.getInstance()
        val time = calendar.timeInMillis
        Log.d(TAG,"day = ${day.day} month = ${day.month.toInt() - 1} year = ${day.year}")
        calendar.set(Calendar.YEAR, day.year.toInt())
        calendar.set(Calendar.MONTH, day.month.toInt() - 1)
        calendar.set(Calendar.DAY_OF_MONTH, day.day.toInt())
        Log.d(TAG, "isDayPassed = ${time > calendar.timeInMillis}")
        return time > calendar.timeInMillis
    }

    override fun getItemCount(): Int = days.size

   override fun updateCalendar(days: MutableList<Day>) {
        Log.v(TAG, "updateCalendar")
        this.days.clear()
        this.days.addAll(days)
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "SimpleCalendarAdapter"
    }
}